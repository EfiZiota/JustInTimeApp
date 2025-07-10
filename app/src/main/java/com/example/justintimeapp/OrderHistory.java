package com.example.justintimeapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity {

    long userId;
    ListView listView;
    OrderDBHandler dbHandler;
    ArrayAdapter<String> adapter;
    List<String> orderCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("USER_ID", -1);

        //Εντοπισμός της λίστας
        listView = findViewById(R.id.listView);

        dbHandler = new OrderDBHandler(this, null, null, 1);
        //Φόρτωση των κωδικών των παραγγελίων του χρήστη από την βάση και εμφάνιση τους στη λίστα στο background thread
        new LoadOrderCodesTask().execute(userId); // AsyncTask για λήψη παραγγελιών


    }


    private class LoadOrderCodesTask extends AsyncTask<Long, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Long... ids) {
            return dbHandler.getOrderCodesByUserId(ids[0]);//Επιστροφή των κωδικών των παραγγελιών που αντιστοιχούν στο id του χρήστη
        }

        @Override
        protected void onPostExecute(List<String> result) {
            //Εμφάνιση των κωδικών των παραγγελιών στην λίστα της οθόνης
            orderCodes = result != null ? result : new ArrayList<>();
            adapter = new ArrayAdapter<>(OrderHistory.this, android.R.layout.simple_list_item_1, orderCodes);
            listView.setAdapter(adapter);
        }
    }


    //Επιλογή του κουμπιού "Αξιολογήστε"
    public void goToEvaluation(View view) {
        if (userId != -1) {
            Intent i = new Intent(this, EvaluationActivity1.class);
            i.putExtra("USER_ID", userId);
            //Μεταφορά της λίστας με τους κωδικούς των παραγγελιών και στην επόμενη οθόνη
            i.putStringArrayListExtra("ORDER_CODES", new ArrayList<>(orderCodes));
            startActivity(i);
        }
    }

    //Επιστροφή στην αρχική οθόνη όταν πατίεται το κουμπί πίσω του κινητού ( απαραίτητο όταν έχει προηγηθεί αξιολόγηση)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrderHistory.this, InitialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
