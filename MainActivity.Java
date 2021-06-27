package com.example.cowinprobe;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.*;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {



    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Button start = (Button)findViewById(R.id.startbtn);
        Button end = (Button)findViewById(R.id.endbtn);
        TextView lastchecked = (TextView) findViewById(R.id.tv_lastchecked);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String showlc = "Last checked at " + sharedPreferences.getString("LastChecked", "--Start searching--") + " HRS";
        lastchecked.setText(showlc);
        start.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.scheduleAtFixedRate(myTask(), 0, 1, HOURS);
                Boolean success = false;

//                LocalDateTime today1 = LocalDateTime.now();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//                String today = today1.format(formatter);
//
//                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
//                prefEditor.putString("LastChecked", today);
//                prefEditor.apply();
            }});

                end.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        start.setEnabled(true);
                        finish();
                    }
                });
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView lastchecked = (TextView) findViewById(R.id.tv_lastchecked);
        String showlc = "Last checked at " + sharedPreferences.getString("LastChecked", "--Start searching--") + " HRS";
        lastchecked.setText(showlc);
    }

            @RequiresApi(api = Build.VERSION_CODES.O)
            private Runnable myTask() {

//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//
//                LocalDateTime today1 = LocalDateTime.now();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//                String today = today1.format(formatter);

//                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
//                prefEditor.putString("LastChecked", today);
//                prefEditor.apply();

                return new Runnable() {


                    final TextView codes = findViewById(R.id.codestext);
                    final String codevals = codes.getText().toString();
                    final List<String> cvals = Arrays.asList(codevals.split(","));

                    @Override
                    public void run() {
                        try {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

                            LocalDateTime today1 = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                            String today = today1.format(formatter);

                            SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                            prefEditor.putString("LastChecked", today);
                            prefEditor.apply();

                            FindSlot fs = new FindSlot(cvals);
                            int flag = fs.flag[0];
                            if (flag == 1) {
                                List<String> centers = fs.availableSlots;
                                System.out.println("Arrived here...");
                                for (String ggg : centers) {
                                    System.out.println(ggg);
                                    System.out.println(" ");
                                }


                                NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel1");

                                Intent intent = new Intent(MainActivity.this, AvailableSlots.class);
                                intent.putExtra("Slots", centers.toString());
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                                stackBuilder.addNextIntentWithParentStack(intent);
                                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

                                Notification notification = notificationCompatBuilder
                                        .setSmallIcon(R.drawable.ic_stat_name)
                                        .setContentTitle("Vaccine Slots Available")
                                        .setContentText("Please check the booking slots.")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(pendingIntent)
                                        .build();


                                NotificationManagerCompat notificationmanager = NotificationManagerCompat.from(MainActivity.this);
                                notificationmanager.notify(1955, notification);

                            } else {
                                System.out.println("No vaccine centers available...");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
        }
