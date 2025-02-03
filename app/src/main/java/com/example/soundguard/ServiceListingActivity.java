package com.example.soundguard;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceListingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;
    private List<ServiceItem> serviceList = new ArrayList<>();
    private List<ServiceItem> filteredServiceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_listing);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        serviceAdapter = new ServiceAdapter(filteredServiceList);
        recyclerView.setAdapter(serviceAdapter);

        fetchServices(); // Fetch data from API

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterServices(newText);
                return false;
            }
        });
    }

    private void fetchServices() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ServiceItem>> call = apiService.getServices();

        call.enqueue(new Callback<List<ServiceItem>>() {
            @Override
            public void onResponse(Call<List<ServiceItem>> call, Response<List<ServiceItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceList.clear();
                    for (ServiceItem service : response.body()) {
                        // Assign the corresponding drawable image resource ID
                        switch (service.getTitle().toLowerCase()) {
                            case "plumbing":
                                service.setImageResId(R.drawable.plumbing); // Example image name
                                break;
                            case "electrician":
                                service.setImageResId(R.drawable.electrician);
                                break;
                            case "cleaning":
                                service.setImageResId(R.drawable.cleaning);
                                break;
                            case "carpentry":
                                service.setImageResId(R.drawable.capentary);
                                break;
                            case "painting":
                                service.setImageResId(R.drawable.painting);
                                break;
                            default:
                                service.setImageResId(R.drawable.moving); // Fallback image
                                break;
                        }
                        serviceList.add(service);
                    }
                    filteredServiceList.clear();
                    filteredServiceList.addAll(serviceList);
                    serviceAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ServiceListingActivity.this, "No services found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceItem>> call, Throwable t) {
                Toast.makeText(ServiceListingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterServices(String query) {
        filteredServiceList.clear();
        if (query.isEmpty()) {
            filteredServiceList.addAll(serviceList);
        } else {
            for (ServiceItem service : serviceList) {
                if (service.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredServiceList.add(service);
                }
            }
        }
        serviceAdapter.notifyDataSetChanged();
    }
}
