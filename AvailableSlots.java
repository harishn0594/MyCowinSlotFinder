package com.example.cowinprobe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvailableSlots extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_slots);

        TextView tv = (TextView) findViewById(R.id.tvslots);

//        ArrayList<String> slots = getIntent().getStringArrayListExtra("Slots");
        String slots = getIntent().getStringExtra("Slots");

        List<String> showtext = new ArrayList<String>(Arrays.asList(slots.split(",")));
        String t = "";
        for (String x : showtext){
            t = t + "\n" + x + "\n" + "-----------------";
        }

        tv.setText(t);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}