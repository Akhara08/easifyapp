package com.example.soundguard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ServiceListingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;
    private List<ServiceItem> serviceList;
    private List<ServiceItem> filteredServiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_listing);

        recyclerView = findViewById(R.id.recyclerView);
        // Set GridLayoutManager with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        serviceList = new ArrayList<>();
        // Add some sample services
        serviceList.add(new ServiceItem("Plumbing", "Fix your pipes and faucets", "$50", R.drawable.plumbing));
        serviceList.add(new ServiceItem("Electrician", "Electrical repairs and installations", "$60", R.drawable.electrician));
        serviceList.add(new ServiceItem("Cleaning", "Home and office cleaning services", "$40", R.drawable.cleaning));
        serviceList.add(new ServiceItem("Carpentry", "Custom furniture and repairs", "$80", R.drawable.capentary));
        serviceList.add(new ServiceItem("Painting", "Interior and exterior painting services", "$120", R.drawable.painting));
        serviceList.add(new ServiceItem("Gardening", "Landscaping and garden maintenance", "$70", R.drawable.gardening));
        serviceList.add(new ServiceItem("AC Repair", "Air conditioner maintenance and repair", "$100", R.drawable.acrepair));
        serviceList.add(new ServiceItem("Maid Service", "Full-time or part-time cleaning assistance", "$30", R.drawable.maid_service));
        serviceList.add(new ServiceItem("Pest Control", "Eliminate pests from your home", "$90", R.drawable.pest_control));
        serviceList.add(new ServiceItem("Moving", "Pack and move your belongings", "$150", R.drawable.moving));

        // Copy the original list to the filtered list
        filteredServiceList = new ArrayList<>(serviceList);

        serviceAdapter = new ServiceAdapter(filteredServiceList);
        recyclerView.setAdapter(serviceAdapter);

        // Correct SearchView initialization
        SearchView searchView = findViewById(R.id.searchView);  // No casting required
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
