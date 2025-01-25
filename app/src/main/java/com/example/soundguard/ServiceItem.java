package com.example.soundguard;

public class ServiceItem {
    private String title;
    private String description;
    private String price;
    private int imageResId;

    public ServiceItem(String title, String description, String price, int imageResId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}
