package com.example.justintimeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity4 extends AppCompatActivity {

    long userId;
    EditText usernameText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        usernameText = findViewById(R.id.TextUsername);
        passwordText = findViewById(R.id.TextPassword);


        userId = getIntent().getLongExtra("USER_ID", -1);

    }


    //Επιλογή του κουμπιού "Επιβεβαίωση"
    public void Confirm(View view) {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String username = usernameText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        //Έλεγχος να έχουν συμπληρωθεί όλα τα πεδία
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία! ", Toast.LENGTH_SHORT).show();
            return;
        }

        //Δημιουργία στοιχείων εισόδου του χρήστη
        NewAccess access = new NewAccess(username, password);

        //Διαδικασία προσθήκης των στοιχείων εισόδου του χρήστη στη βάση στο background thread
        new AddAccessTask().execute(access);
    }


    private class AddAccessTask extends AsyncTask<NewAccess, Void, Boolean> {

        @Override
        protected Boolean doInBackground(NewAccess... accesses) {
            MyDBHandler dbHandler = new MyDBHandler(SignUpActivity4.this, null, null, 1);
            return dbHandler.addAccess(accesses[0], userId); //Προσθήκη των στοιχείων εισόδου του χρήστη στη βάση με αντιστοίχιση στο ίδιο id
            //και επιστροφή επιτυχούς ή όχι προσθήκης αναλόγως με το αν υπάρχει άλλος χρήστης με ίδιο username
        }

        @Override
        protected void onPostExecute(Boolean success) {

            //Έλεγχος ύπαρξης ίδιου username από άλλο χρήστη
            if (!success) {
                Toast.makeText(SignUpActivity4.this, "Αυτό το username υπάρχει ήδη...", Toast.LENGTH_SHORT).show();
            } else {
                //Καθαρισμός πεδίων
                usernameText.setText("");
                passwordText.setText("");

                Intent i = new Intent(SignUpActivity4.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
}

