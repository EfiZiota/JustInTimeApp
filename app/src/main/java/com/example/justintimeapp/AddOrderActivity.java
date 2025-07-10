package com.example.justintimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddOrderActivity extends AppCompatActivity {

    long userId;
    EditText orderCodeText;
    EditText courierText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("USER_ID", -1);


        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        orderCodeText = findViewById(R.id.OrderCodeText);
        courierText = findViewById(R.id.CourierText);



    }

    //Επιλογή του κουμπιού "Καταχώρηση"
    public void AddOrder(View view) {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String orderCode = orderCodeText.getText().toString();
        String courier = courierText.getText().toString();

        //Έλεγχος να έχουν συμπληρωθεί όλα τα πεδία
        if (orderCode.isEmpty() || courier.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία!", Toast.LENGTH_SHORT).show();
            return;
        }


        //Μεταφορά των στοιχείων της παραγγελίας που εισήγαγε ο χρήστης μάζι με το id του στην επόμενη οθόνη, έτσι ώστε να ολοκληρωθεί εκεί η καταχώρηση παραγγελίας στη βάση
        if(!(userId ==-1)){
            Intent i = new Intent(this, UserAvailability.class);
            i.putExtra("USER_ID", userId);
            i.putExtra("ORDERCODE", orderCode);
            i.putExtra("COURIER", courier);
            i.putExtra("IS_UPDATE", false); //μεταβλητή που δηλώνει ότι γίνεται καινούργια προσθήκη στη βάση
            startActivity(i);
        }

        //Καθαρισμός πεδίων
        orderCodeText.setText("");
        courierText.setText("");

    }
}