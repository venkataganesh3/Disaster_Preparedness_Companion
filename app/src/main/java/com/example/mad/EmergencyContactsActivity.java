package com.example.mad;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmergencyContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        // Example Emergency Contacts
        String contacts = "🚨 Emergency Contacts 🚨\n\n" +
                "📞 Police: 100\n" +
                "🚑 Ambulance: 108\n" +
                "🔥 Fire Brigade: 101\n" +
                "📡 Disaster Helpline: 112\n" +
                "🏥 Local Hospital: +91 98765 43210";

        // Set the contacts to TextView
        TextView contactTextView = findViewById(R.id.txtEmergencyContacts);
        contactTextView.setText(contacts);
    }
}
