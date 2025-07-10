package com.example.justintimeapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class profileActivity extends AppCompatActivity {

    long userId;
    ListView userDetailsListView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("USER_ID", -1);

        //Εντοπισμός της λίστας
        userDetailsListView = findViewById(R.id.userDetailsListView);

        //Φόρτωση των στοιχείων του χρήστη από την βάση και εμφάνιση τους στη λίστα στο background thread
        new LoadUserDetailsTask().execute(userId);

    }

    // AsyncTask για φόρτωση στοιχείων χρήστη
    private class LoadUserDetailsTask extends AsyncTask<Long, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Long... ids) {
            MyDBHandler dbHandler = new MyDBHandler(profileActivity.this, null, null, 1);
            return dbHandler.getUserDetailsById(ids[0]); //Φόρτωση των δεδομένων του χρήστη με βάση το id του
        }

        @Override
        protected void onPostExecute(List<String> userDetails) {
            //Εμφάνιση των στοιχείων από τη βάση στη λίστα
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    profileActivity.this,
                    android.R.layout.simple_list_item_1,
                    userDetails
            );
            userDetailsListView.setAdapter(adapter);
        }
    }


    //Επιλογή του κουμπιού "Διαγραφή Λογαριασμού"
    public void DeleteAccount(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Επιβεβαίωση Διαγραφής Λογαριασμού")
                .setMessage("Είστε σίγουροι ότι θέλετε να διαγράψετε τον λογαριασμό σας; Αυτή η ενέργεια δεν μπορεί να αναιρεθεί.")
                .setPositiveButton("Διαγραφή", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteUserTask().execute(userId); //Διαγραφή όλων των στοιχείων του χρήστη στην βάση στο background thread
                    }
                })
                .setNegativeButton("Ακύρωση", null) // Ακύρωση διαλόγου
                .show();
    }


    private class DeleteUserTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... ids) {
            MyDBHandler dbHelper = new MyDBHandler(profileActivity.this, null, null, 1);
            dbHelper.deleteUserById(ids[0]);//Διάγραφη όλων των στοιχείων στη βάση που έχουν το συγκεκριμένο id
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent(profileActivity.this, MainActivity.class);
            //Καθαρισμός του stack για να μην μπορεί να επιστρέψει ο χρήστης σε αυτή την οθόνη εφόσον ο λογαριασμός του δεν υπάρχει πια
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
}
