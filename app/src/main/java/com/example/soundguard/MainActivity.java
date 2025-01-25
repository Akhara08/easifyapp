package com.example.soundguard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final long SESSION_TIMEOUT = 1 * 60 * 1000; // 10 minutes in milliseconds
    private Handler sessionHandler;
    private Runnable sessionTimeoutRunnable;

    private ListView audioListView;
    private List<AudioFile> audioFiles = new ArrayList<>();
    private List<AudioFile> filteredAudioFiles = new ArrayList<>();
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private AudioFileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioListView = findViewById(R.id.audioListView);
        playerView = findViewById(R.id.playerView);

        // Initialize session timeout
        sessionHandler = new Handler();
        sessionTimeoutRunnable = this::handleSessionTimeout;
        resetSessionTimeout();

        // Fetch the audio file list from S3
        fetchAudioFiles();

        // Set up date picker button click listener
        findViewById(R.id.selectDateButton).setOnClickListener(v -> showDatePickerDialog());
    }

    private void resetSessionTimeout() {
        sessionHandler.removeCallbacks(sessionTimeoutRunnable);
        sessionHandler.postDelayed(sessionTimeoutRunnable, SESSION_TIMEOUT);
    }

    private void handleSessionTimeout() {
        // Redirect to login page after session timeout
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetSessionTimeout(); // Reset the timer on user interaction
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d %s %d", selectedDay, getMonthName(selectedMonth), selectedYear);
                    filterAudioFilesByDate(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private String getMonthName(int month) {
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }

    private void filterAudioFilesByDate(String selectedDate) {
        filteredAudioFiles.clear();
        for (AudioFile file : audioFiles) {
            if (file.formattedDate.contains(selectedDate)) {
                filteredAudioFiles.add(file);
            }
        }

        if (filteredAudioFiles.isEmpty()) {
            Toast.makeText(this, "No files found for the selected date", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    private void fetchAudioFiles() {
        runOnUiThread(() -> {
            findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
            findViewById(R.id.emptyStateTextView).setVisibility(View.GONE);
        });

        new Thread(() -> {
            try {
                ObjectListing objectListing = MyApp.s3Client.listObjects(new ListObjectsRequest()
                        .withBucketName("soundguard"));

                audioFiles.clear();
                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                    audioFiles.add(new AudioFile(objectSummary.getKey()));
                }

                runOnUiThread(() -> {
                    if (audioFiles.isEmpty()) {
                        findViewById(R.id.emptyStateTextView).setVisibility(View.VISIBLE);
                    } else {
                        filteredAudioFiles.clear();
                        filteredAudioFiles.addAll(audioFiles);
                        adapter = new AudioFileAdapter(this, filteredAudioFiles);
                        audioListView.setAdapter(adapter);
                    }

                    findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                });

            } catch (Exception e) {
                Log.e("S3Error", "Error fetching files", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to fetch files", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                    findViewById(R.id.emptyStateTextView).setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    private void deleteAudioFile(String fileName) {
        new Thread(() -> {
            try {
                MyApp.s3Client.deleteObject("soundguard", fileName);
                runOnUiThread(() -> {
                    Toast.makeText(this, fileName + " deleted", Toast.LENGTH_SHORT).show();
                    fetchAudioFiles();
                });
            } catch (Exception e) {
                Log.e("S3Error", "Error deleting file: " + fileName, e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to delete " + fileName, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void confirmDeletion(String fileName) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete " + fileName + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAudioFile(fileName))
                .setNegativeButton("No", null)
                .show();
    }

    private class AudioFile {
        String fileName;
        boolean isChecked;
        String formattedDate;

        AudioFile(String fileName) {
            this.fileName = fileName;
            this.isChecked = false;
            this.formattedDate = parseDateFromFileName(fileName);
        }

        private String parseDateFromFileName(String fileName) {
            String dateString = fileName.substring(fileName.indexOf('_') + 1, fileName.lastIndexOf('.'));
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            try {
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "Unknown Date";
            }
        }
    }

    private class AudioFileAdapter extends ArrayAdapter<AudioFile> {
        public AudioFileAdapter(MainActivity context, List<AudioFile> files) {
            super(context, 0, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AudioFile audioFile = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_audio, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.audioFileName);
            TextView dateTextView = convertView.findViewById(R.id.audioFileDate);
            CheckBox checkBox = convertView.findViewById(R.id.deleteCheckBox);

            textView.setText(audioFile.fileName);
            dateTextView.setText(audioFile.formattedDate);
            checkBox.setChecked(audioFile.isChecked);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                audioFile.isChecked = isChecked;
                if (isChecked) {
                    confirmDeletion(audioFile.fileName);
                    checkBox.setChecked(false);
                }
            });

            convertView.setOnClickListener(v -> playAudio(audioFile.fileName));

            return convertView;
        }
    }

    private void playAudio(String fileName) {
        if (player != null) {
            player.release();
        }
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        String audioUrl = "https://soundguard.s3.amazonaws.com/" + fileName;
        MediaItem mediaItem = MediaItem.fromUri(audioUrl);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
        sessionHandler.removeCallbacks(sessionTimeoutRunnable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        resetSessionTimeout();
    }
}
