package com.example.justintimeapp;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class UserAvailability extends AppCompatActivity {

    long userId;
    String orderCode;
    String courier;
    EditText dateText;
    EditText flexibleDateText;
    EditText hourText;
    RadioGroup radioGroup;
    boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_availability);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Τα δεδομένα που συμπλήρωσε ο χρήστης στην προηγούμενη οθόνη
        userId = getIntent().getLongExtra("USER_ID", -1);
        orderCode = getIntent().getStringExtra("ORDERCODE");
        courier = getIntent().getStringExtra("COURIER");
        isUpdate = getIntent().getBooleanExtra("IS_UPDATE", false);


        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        dateText = findViewById(R.id.dateEditText);
        flexibleDateText = findViewById(R.id.flexibleEditText);
        hourText = findViewById(R.id.hourEditText);
        radioGroup = findViewById(R.id.radioGroupAddresses);


        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 2);

        //Επιστροφή των διευθύνσεων που αντιστοιχούν στο συγκεκριμένο χρήστη από τη βάση
        List<String> addressList = dbHandler.getAddressesOfUser(userId);

        //Εμφάνιση των διευθύνσεων σε radiobuttons
        for (String address : addressList) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(address);
            radioButton.setId(View.generateViewId());

            //Τροποποίηση της εμφάνισης των κουμπιών
            radioButton.setTextSize(18);
            radioButton.setTextColor(ContextCompat.getColor(this, R.color.black));
            radioButton.setPadding(48, 32, 48, 32);
            radioButton.setButtonTintList(
                    ContextCompat.getColorStateList(this, R.color.purple)
            );

            radioGroup.addView(radioButton);

        }

    }

    //Επιλογή του κουμπιού "ΟΚ"
    public void getDateData(View view) {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String date = dateText.getText().toString();
        String flexible = flexibleDateText.getText().toString();
        String hour = hourText.getText().toString();

        //Παίρνω την διεύθυνση που διάλεξε ο χρήστης
        int selectedId = radioGroup.getCheckedRadioButtonId();

        //Έλεγχος αν δεν επέλεξε διεύθυνση
        if (selectedId == -1) {
            Toast.makeText(this, "Παρακαλώ επιλέξτε διεύθυνση.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String addressText = selectedRadioButton.getText().toString();

        //Έλεγχος να έχουν συμπληρωθεί όλα τα πεδία ή τουλάχιστον μία από τις δύο επιλογές ημερομηνιών
        if (date.isEmpty() && flexible.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ επιλέξτε τουλάχιστον μία μορφή ημερομηνίας.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hour.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε το χρονικό διάστημα διαθεσιμότητάς σας", Toast.LENGTH_SHORT).show();
            return;
        }

        //Δημιουργία της παραγγελίας του χρήστη
        NewOrder order = new NewOrder(userId, orderCode, courier, date, flexible, hour, addressText);

       //Διαδικασία προσθήκης της παραγγελίας του χρήστη στη βάση στο background thread
        new SaveOrderTask().execute(order);

    }

    private class SaveOrderTask extends AsyncTask<NewOrder, Void, Void> {
        @Override
        protected Void doInBackground(NewOrder... orders) {
            OrderDBHandler dbHandler = new OrderDBHandler(UserAvailability.this, null, null, 1);
            NewOrder order = orders[0];

            //Προσθήκη ή ανανέωση της παραγγελίας του χρήστη στη βάση αναλόγως την περίπτωση
            if (isUpdate) {
                dbHandler.updateOrder(order);  //Αν ο χρήστης αλλάζει την διαθεσιμότητα του
            } else {
                dbHandler.addOrder(order);   //Αν ο χρήστης εισάγει την διαθεσιμότητα του πρώτη φορά
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            //Ανάλογο μήνυμα ενημέρωσης
            Toast.makeText(UserAvailability.this, isUpdate ? "Η διαθεσιμότητα σας ενημερώθηκε!" : "Η παραγγελία σας καταχωρήθηκε!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(UserAvailability.this, InitialActivity.class);
            i.putExtra("USER_ID", userId);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//Καθαρισμός του back stack για να μην επιστρέφει σε αυτό το σημείο ο χρήστης με το κουμπί πίσω του κινητού
            startActivity(i);
            finish();
        }
    }

}
