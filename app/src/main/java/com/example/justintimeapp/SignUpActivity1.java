package com.example.justintimeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity1 extends AppCompatActivity {
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Για να λαμβάνει το id με το οποίο καταχωρείται ο χρήστης στο σύστημα
        userId = getIntent().getLongExtra("USER_ID", -1);

        //Διαδικασία με την οποία ελέγχεται ότι ο χρήστης έχει συμπληρώσει τα στοιχεία του πριν μεταβεί στην επόμενη κατάσταση
        if (userId==-1){
            SharedPreferences prefs = getSharedPreferences("SignUpPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasFilledPersonalData", false);
            editor.putBoolean("hasFilledAddress", false);
            editor.apply();
        }


    }


    //Επιλογή του κουμπιού για τα "Προσωπικά Δεδομένα"
    public void FillInData(View view) {
        Intent i = new Intent(this, SignUpActivity2.class);
        startActivity(i);
    }


    //Επιλογή του κουμπιού για τις "Διευθύνσεις"
    public void FillInAddress(View view) {
        Intent i = new Intent(this, SignUpActivity3.class);
        i.putExtra("USER_ID", userId); //μεταφορά του id που έχει δημιουργηθεί για τον χρήστη έτσι ώστε να γίνει αντιστοίχιση της διεύθυνσης με αυτόν
        startActivity(i);
    }


    //Επιλογή του κουμπιού "Όλα έτοιμα"
    public void CreateAccount(View view) {

        //Διαδικασία ελέγχου ότι έχει πρώτα συμπλήρωσει τα στοιχεία του ο χρήστης και έχει δημιουργήσει προφίλ
        SharedPreferences prefs = getSharedPreferences("SignUpPrefs", MODE_PRIVATE);
        boolean hasFilledPersonalData = prefs.getBoolean("hasFilledPersonalData", false);
        boolean hasFilledAddress = prefs.getBoolean("hasFilledAddress", false);

        if (!hasFilledPersonalData || !hasFilledAddress || userId==-1) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα στοιχεία σας για να δημιουργηθεί ο λογαριασμός σας.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent i = new Intent(this, SignUpActivity4.class);
        i.putExtra("USER_ID", userId); //ξαναγίνεται μεταφορά του id για να αντιστοιχηθούν και τα username & password με τον χρήστη
        startActivity(i);
    }
}