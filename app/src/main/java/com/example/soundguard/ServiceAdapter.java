package com.example.soundguard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> serviceList;

    public ServiceAdapter(List<ServiceItem> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        ServiceItem serviceItem = serviceList.get(position);
        holder.title.setText(serviceItem.getTitle());
        holder.description.setText(serviceItem.getDescription());
        holder.price.setText(serviceItem.getPrice());
        holder.imageView.setImageResource(serviceItem.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            // Handle item click, for example, show a toast
            Toast.makeText(v.getContext(), "You clicked on " + serviceItem.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, price;
        ImageView imageView;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
