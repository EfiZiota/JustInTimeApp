package com.example.justintimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderStateActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_state);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Παραλαβή δεδομένων κωδικού και κατάστασης παραγγελίας από την προηγούμενη οθόνη
        Intent intent = getIntent();
        String orderCode = intent.getStringExtra("orderCode");
        String orderState = intent.getStringExtra("orderState");

        //Εντοπισμός των πεδίων στα οποία θέλουμε να εισάγουμε τις πληροφορίες της παραγγελίας
        TextView codeText = findViewById(R.id.codeStateTextView);
        TextView stateText = findViewById(R.id.orderStateTextView);


        //Διαδικασία ώστε να εισαχθούν οι πληροφορίες της παραγγελίας μαζί με τα υπάρχοντα κείμενα που υφίσταται στην οθόνη
        String existingStateText = stateText.getText().toString();
        String existingCodeText = codeText.getText().toString();

        String updatedStateText = existingStateText + "\n" + orderState;
        String updatedCodeText = existingCodeText + "\n" + orderCode;

        stateText.setText(updatedStateText);
        codeText.setText(updatedCodeText);

        //Εμφάνιση συγκεκριμένης εικόνα αναλόγως την κατάσταση της παραγγελίας
        ImageView orderStateImage = findViewById(R.id.orderStateImage);

        if ("Παραδόθηκε".equalsIgnoreCase(orderState)) {
            orderStateImage.setImageResource(R.drawable.delivered_icon);
        } else if ("Προς Παράδοση".equalsIgnoreCase(orderState)) {
            orderStateImage.setImageResource(R.drawable.fordelivery_icon);
        } else if ("Σε εκκρεμότητα".equalsIgnoreCase(orderState)) {
            orderStateImage.setImageResource(R.drawable.pend_icon);
        }



    }


    //Επιλογή κουμπιού "Πότε;"
    public void goToAvailability(View view) {

        Intent intent = getIntent();
        String orderCode = intent.getStringExtra("orderCode");
        String orderState = intent.getStringExtra("orderState");
        long userId = intent.getLongExtra("USER_ID", -1);
        String courier = intent.getStringExtra("courier");

        //Εμφάνιση του fragment με αποστολή σε αυτό των παραπάνω στοιχείων
        AvailabilityFragment fragment = AvailabilityFragment.newInstance(userId, orderCode, orderState, courier);

        View container = findViewById(R.id.fragment_container);
        container.setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();


    }




}