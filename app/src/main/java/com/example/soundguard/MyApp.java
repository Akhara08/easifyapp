package com.example.soundguard;

import android.app.Application;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

public class MyApp extends Application {

    public static AmazonS3Client s3Client;
    public static TransferUtility transferUtility;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the AWS Cognito Identity Pool
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-southeast-2:e24a08c7-9a57-4fd3-9af5-6137db952113",  // Replace with your identity pool ID
                Regions.AP_SOUTHEAST_2 // Replace with your region
        );

        s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = TransferUtility.builder()
                .s3Client(s3Client)
                .context(getApplicationContext())
                .build();
    }
}
