package com.example.justintimeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity3 extends AppCompatActivity {

    EditText addressView;
    EditText codeView;
    EditText cityView;
    EditText countyView;
    long userId;
    private boolean isAddingNewAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        userId = getIntent().getLongExtra("USER_ID", -1);

        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        addressView = findViewById(R.id.AddressView);
        codeView = findViewById(R.id.codeView);
        cityView = findViewById(R.id.CityView);
        countyView = findViewById(R.id.countyView);


    }

    //Επιλογή του κουμπιού "ΟΚ"
    public void getData(View view) {
        //Δημιουργία διεύθυνσης αν έχει γίνει συμπλήρωση όλων των πεδίων
        if (!validateInputs()) return;
        isAddingNewAddress = false;
        NewAddress newaddress = createNewAddressFromInputs();

        //Διαδικασία προσθήκης της διεύθυνσης του χρήστη στη βάση στο background thread
        new AddAddressTask().execute(newaddress);
    }


    //Επιλογή του κουμπιού "+"
    public void newAddressData(View view) {
        //Ακολουθείτε η ίδια διαδικασία με πριν για προσθήκη περισσότερων διεύθυνσεων
        if (!validateInputs()) return;
        isAddingNewAddress = true;
        NewAddress newaddress = createNewAddressFromInputs();
        new AddAddressTask().execute(newaddress);
    }

    //Έλεγχος να έχουν συμπληρωθεί όλα τα πεδία
    private boolean validateInputs() {
        if (addressView.getText().toString().isEmpty() ||
                codeView.getText().toString().isEmpty() ||
                cityView.getText().toString().isEmpty() ||
                countyView.getText().toString().isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία! ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
    private NewAddress createNewAddressFromInputs() {
        return new NewAddress(
                addressView.getText().toString(),
                codeView.getText().toString(),
                cityView.getText().toString(),
                countyView.getText().toString()
        );
    }


    private class AddAddressTask extends android.os.AsyncTask<NewAddress, Void, Boolean> {
        @Override
        protected Boolean doInBackground(NewAddress... addresses) {
            MyDBHandler dbHandler = new MyDBHandler(SignUpActivity3.this, null, null, 1);
            dbHandler.addAddress(addresses[0], userId); //Προσθήκη της διεύθυνσης του χρήστη στη βάση με αντιστοίχιση στο ίδιο id
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            //Επιβεβαίωση υποβολής δεδομένων λογαριασμού
            SharedPreferences prefs = getSharedPreferences("SignUpPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasFilledAddress", true);
            editor.apply();

            //Καθαρισμός πεδίων
            addressView.setText("");
            codeView.setText("");
            cityView.setText("");
            countyView.setText("");


            //Αναλόγως το ποιο κουμπί έχει πατηθεί ο χρήστης οδηγείται στο αντίστοιχο activity
            if (isAddingNewAddress) {
                Intent i = new Intent(SignUpActivity3.this, SignUpActivity3.class);
                i.putExtra("USER_ID", userId);
                Toast.makeText(SignUpActivity3.this, "Η διεύθυνση σου προστέθηκε!", Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SignUpActivity3.this, SignUpActivity1.class);
                i.putExtra("USER_ID", userId);
                Toast.makeText(SignUpActivity3.this, "Η διεύθυνση σου προστέθηκε!", Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();
            }
        }
    }

}
