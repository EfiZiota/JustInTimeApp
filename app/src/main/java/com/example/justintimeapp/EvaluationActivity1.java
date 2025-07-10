package com.example.justintimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EvaluationActivity1 extends AppCompatActivity {

    long userId;
    ArrayList<String> orderCodes;
    ListView orderListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evaluation1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("USER_ID", -1);

        //Εντοπισμός της λίστας
        orderListView = findViewById(R.id.orderListView);

        //Φόρτωση των κωδικών των παραγγελίων του χρήστη από την προηγούμενη οθόνη
        orderCodes = getIntent().getStringArrayListExtra("ORDER_CODES");

        //Εμφάνιση των κωδικών των παραγγελίων στη λίστα
        if (orderCodes != null && !orderCodes.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    orderCodes
            );
            orderListView.setAdapter(adapter);
        }
        else{
            Toast.makeText(this, "Δεν υπάρχουν δεδομένα!", Toast.LENGTH_SHORT).show();
        }


       //Επιλογή μίας παραγγελίας από τη λίστα και μεταφορά του κωδικού της στην επόμενη οθόνη
        orderListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOrderCode = orderCodes.get(position);

            Intent intent = new Intent(EvaluationActivity1.this, EvaluationActivity2.class);
            intent.putExtra("ORDER_CODE", selectedOrderCode);
            if(!(userId ==-1)){
                intent.putExtra("USER_ID", userId);
            }
            startActivity(intent);
        });


    }
}