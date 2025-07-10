package com.example.justintimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InitialActivity extends AppCompatActivity {

    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_initial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("USER_ID", -1);


    }


    //Επιλογή κάποιου από τα κουμπιά της αρχικής οθόνης της εφαρμογής για να γίνει μεταφορά του χρήστη στην αντίστοιχη οθόνη
    public void goToProfile(View view) {
        if(!(userId ==-1)){
            Intent i = new Intent(this, profileActivity.class);
            i.putExtra("USER_ID", userId);
            startActivity(i);
        }
    }

    public void goToAddOrder(View view) {
        if(!(userId ==-1)){
            Intent i = new Intent(this, AddOrderActivity.class);
            i.putExtra("USER_ID", userId);
            startActivity(i);
        }
    }

    public void goToSearchOrder(View view) {
        if(!(userId ==-1)){
            Intent i = new Intent(this, SearchOrderActivity.class);
            i.putExtra("USER_ID", userId);
            startActivity(i);
        }
    }

    public void goToHistoryOrder(View view) {
        if(!(userId ==-1)){
            Intent i = new Intent(this, OrderHistory.class);
            i.putExtra("USER_ID", userId);
            startActivity(i);
        }
    }


    //Επιλογή του κουμπιού "Αποσύνδεσης"
    public void SignOut(View view) {
        Intent i = new Intent(this, SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //για να μην μπορεί ο χρήστης να επιστρέψει σε αυτήν την οθόνη εφόσον αποσυνδέθηκε
        startActivity(i);
    }

    //Να μην γίνεται επιστροφή σε προηγούμενα activities του stack αλλά να βγαίνει από την εφαρμογή αν κάνει πίσω ο χρήστης σε αυτό το σημείο
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }



}