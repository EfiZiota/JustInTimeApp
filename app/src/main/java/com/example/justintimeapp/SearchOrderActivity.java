package com.example.justintimeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class SearchOrderActivity extends AppCompatActivity {

    EditText orderCodeText;
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getLongExtra("USER_ID", -1);

        //Εντοπισμός του πεδίου από το οποίο θέλουμε τις πληροφορίες
        orderCodeText = findViewById(R.id.OrderCodeText);


    }

    //το κουμπι αναζητησης με το οποιο βρισκει την παραγγελια στη βαση και σε παει στην πεομη οθονη
    //Επιλογή του κουμπιού "Αναζήτηση"
    public void SearchOrder(View view) {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String orderCode = orderCodeText.getText().toString().trim();

        //Έλεγχος να έχει συμπληρωθεί το πεδίο
        if (orderCode.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε κάποιον κωδικό παραγγελίας.", Toast.LENGTH_SHORT).show();
            return;
        }


        // Αναζήτηση της παραγγελίας στη βάση στο background thread
        new SearchOrderTask().execute(orderCode);
    }


    private class SearchOrderTask extends AsyncTask<String, Void, Map<String, String>> {
        String searchedOrderCode;

        @Override
        protected Map<String, String> doInBackground(String... codes) {
            searchedOrderCode = codes[0];
            OrderDBHandler orderdbHandler = new OrderDBHandler(SearchOrderActivity.this, null, null, 1);
            return orderdbHandler.searchOrder(searchedOrderCode, userId); //Εύρεση της παραγγελίας με βάση τον κωδικό της και επιστροφή των στοιχείων του κωδικού,της κατάστασης και της κούριερ της
        }

        @Override
        protected void onPostExecute(Map<String, String> order) {
            //Μήνυμα σφάλματος σε περίπτωση μη εντοπισμού παραγγελίας
            if (order == null || order.containsKey("error")) {
                String errorMessage = (order != null) ? order.get("error") : "Δεν βρέθηκε η παραγγελία.";
                Toast.makeText(SearchOrderActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                //Προσθήκη σε μεταβλητές των στοιχείων που επιστράφηκαν από την βάση αν η παραγγελία υπάρχει
                String code = order.get("code");
                String state = order.get("state");
                String courier = order.get("courier");

                //Αποστολή των στοιχείων στην επόμενη οθόνη για εμφάνιση
                Intent i = new Intent(SearchOrderActivity.this, OrderStateActivity.class);
                i.putExtra("USER_ID", userId);
                i.putExtra("orderCode", code);
                i.putExtra("orderState", state);
                i.putExtra("courier", courier);
                startActivity(i);
            }

            //Καθαρισμός πεδίου
            orderCodeText.setText("");
        }

    }
}
