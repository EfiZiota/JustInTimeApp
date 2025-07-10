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

public class SignUpActivity2 extends AppCompatActivity {

    EditText nameView;
    EditText surnameView;
    EditText phoneView;
    EditText mobileView;
    EditText emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        nameView = findViewById(R.id.name);
        surnameView = findViewById(R.id.surname);
        phoneView = findViewById(R.id.phone);
        mobileView = findViewById(R.id.mobile);
        emailView = findViewById(R.id.email);


    }


    //Επιλογή του κουμπιού "ΟΚ"
    public void goBack(View view) {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String name = nameView.getText().toString();
        String surname = surnameView.getText().toString();
        String phone = phoneView.getText().toString();
        String mobile = mobileView.getText().toString();
        String email = emailView.getText().toString();

        //Έλεγχος να έχουν συμπληρωθεί όλα τα πεδία
        if (name.isEmpty() || surname.isEmpty() || phone.isEmpty() || mobile.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία! ", Toast.LENGTH_SHORT).show();
            return;
        }

        //Δημιουργία του λογαριασμού του χρήστη
        NewAccount account = new NewAccount(name, surname, phone, mobile, email);

        //Διαδικασία προσθήκης του λογαριασμού του χρήστη στη βάση στο background thread
        new AddAccountTask().execute(account);
    }

    private class AddAccountTask extends android.os.AsyncTask<NewAccount, Void, Long> {
        @Override
        protected Long doInBackground(NewAccount... accounts) {
            MyDBHandler dbHandler = new MyDBHandler(SignUpActivity2.this, null, null, 1);
            return dbHandler.addAccount(accounts[0]); //Προσθήκη του λογαριασμού του χρήστη στη βάση και απόδοση id σε αυτόν
        }

        @Override
        protected void onPostExecute(Long userId) {

            //Ενημέρωση σε πιθανότητα αποτυχίας του συστήματος
            if (userId == -1) {
                Toast.makeText(SignUpActivity2.this, "Αποτυχία δημιουργίας λογαριασμού...", Toast.LENGTH_SHORT).show();
                return;
            }

            //Επιβεβαίωση υποβολής δεδομένων λογαριασμού
            SharedPreferences prefs = getSharedPreferences("SignUpPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasFilledPersonalData", true);
            editor.apply();

            //Καθαρισμός πεδίων
            nameView.setText("");
            surnameView.setText("");
            phoneView.setText("");
            mobileView.setText("");
            emailView.setText("");

            Intent i = new Intent(SignUpActivity2.this, SignUpActivity1.class);
            i.putExtra("USER_ID", userId); //μεταφορά του id που έχει δημιουργηθεί για τον χρήστη
            Toast.makeText(SignUpActivity2.this, "Τα στοιχεία σου προστέθηκαν!", Toast.LENGTH_SHORT).show();
            startActivity(i);
            finish();
        }
    }


}
