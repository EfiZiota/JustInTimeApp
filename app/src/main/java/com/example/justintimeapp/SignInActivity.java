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

public class SignInActivity extends AppCompatActivity {


    EditText usernameText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        usernameText = findViewById(R.id.TextUsername);
        passwordText = findViewById(R.id.TextPassword);


    }


    //Επιλογή του κουμπιού "Σύνδεση"
    public void SignIn(View view) {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        //Έλεγχος αν υπάρχει χρήστης με αυτά τα στοιχεία εισόδου στη βάση στο background thread
        new CheckCredentialsTask().execute(username, password);
    }


    private class CheckCredentialsTask extends AsyncTask<String, Void, Long> {
        String usernameInput;
        String passwordInput;

        @Override
        protected Long doInBackground(String... params) {
            usernameInput = params[0];
             passwordInput = params[1];

            MyDBHandler dbHandler = new MyDBHandler(SignInActivity.this, null, null, 1);
            return dbHandler.checkCredentials(usernameInput, passwordInput); //έλεγχος ύπαρξης των στοιχείων εισόδου και πειστροφή του id του
        }

        @Override
        protected void onPostExecute(Long userId) {
            super.onPostExecute(userId);

            //Είσοδος του χρήστη στο προφίλ του αν υπάρχουν τα στοιχεία εισόδου ή όχι
            if (userId != -1) {
                Intent i = new Intent(SignInActivity.this, InitialActivity.class);
                i.putExtra("USER_ID", userId);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Καθαρισμός του back stack για να μην επιστρέφει σε αυτό το σημείο ο χρήστης με το κουμπί πίσω του κινητού
                startActivity(i);
            } else {
                Toast.makeText(SignInActivity.this, "Το όνομα χρήστη ή ο κωδικός πρόσβασης δεν είναι έγκυρα!", Toast.LENGTH_SHORT).show();
            }

            //Καθαρισμός πεδίων
            usernameText.setText("");
            passwordText.setText("");
        }
    }

}