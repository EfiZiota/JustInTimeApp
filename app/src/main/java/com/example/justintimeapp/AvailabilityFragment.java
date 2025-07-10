package com.example.justintimeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Map;

public class AvailabilityFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_ORDER_CODE = "orderCode";
    private static final String ARG_ORDER_STATE = "orderState";
    private static final String ARG_COURIER = "courier";

    private long userId;
    private String orderCode;
    private String orderState;
    private String courier;
    TextView dateText;
    TextView timeText;
    TextView locationText;

    public static AvailabilityFragment newInstance(long userId, String orderCode, String orderState, String courier) {
        AvailabilityFragment fragment = new AvailabilityFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_USER_ID, userId);
        args.putString(ARG_ORDER_CODE, orderCode);
        args.putString(ARG_ORDER_STATE, orderState);
        args.putString(ARG_COURIER, courier);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability, container, false);

        if (getArguments() != null) {
            userId = getArguments().getLong(ARG_USER_ID);
            orderCode = getArguments().getString(ARG_ORDER_CODE);
            orderState = getArguments().getString(ARG_ORDER_STATE);
            courier = getArguments().getString(ARG_COURIER);
        }

        //Εντοπισμός των πεδίων στα οποία θέλουμε να εισάγουμε πληροφορίες
        dateText = view.findViewById(R.id.deliveryDateText);
        timeText = view.findViewById(R.id.deliveryTimeText);
        locationText = view.findViewById(R.id.locationText);

        //Φόρτωση των δεδομένων της διαθεσιμότητας του χρήστη από τη βάση με εκτέλεση στο background thread
        new LoadDeliveryInfoTask(orderCode, userId).execute();


        Button yesButton = view.findViewById(R.id.yesButton);
        Button noButton = view.findViewById(R.id.noButton);

        //Επιλογή κουμπιού "Ναι"
        yesButton.setOnClickListener(v -> {
            //Μεατφορά όλων των απαραίτητων στοιχείων στην επόμενη οθόνη για να γίνει η ανανέωση
            Intent intent = new Intent(getActivity(), UserAvailability.class);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("ORDERCODE", orderCode);
            intent.putExtra("ORDERSTATE", orderState);
            intent.putExtra("COURIER", courier);
            intent.putExtra("IS_UPDATE", true); //Δήλωση ότι πρόκειται για ανανέωση διαθεσιμότητας
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//Καθαρισμός του back stack για να μην επιστρέφει σε αυτό το σημείο ο χρήστης με το κουμπί πίσω του κινητού
            startActivity(intent);
        });

        //Επιλογή κουμπιού "Όχι"
        noButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InitialActivity.class);
            intent.putExtra("USER_ID", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);//Καθαρισμός του back stack για να μην επιστρέφει σε αυτό το σημείο ο χρήστης με το κουμπί πίσω του κινητού
            startActivity(intent);
            requireActivity().finish();
        });


        return view;
    }

    private class LoadDeliveryInfoTask extends AsyncTask<Void, Void, Map<String, String>> {
        private String orderCode;
        private long userId;

        public LoadDeliveryInfoTask(String orderCode, long userId) {
            this.orderCode = orderCode;
            this.userId = userId;
        }

        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            OrderDBHandler dbHandler = new OrderDBHandler(getContext(), null, null, 1);
            return dbHandler.getCourierDeliveryInfo(orderCode, userId); //Επιστροφή στοιχείων διαθεσιμότητας της παραγγελίας με βάση τον κωδικό της
        }

        @Override
        protected void onPostExecute(Map<String, String> data) {
            //Εμφάνιση των στοιχείων που ανακτήθηκαν από τη βάση στα ανάλογα πεδία
            if (!data.isEmpty()) {
                dateText.setText(data.get("date"));
                timeText.setText(data.get("time"));
                locationText.setText(data.get("address"));
            } else {
                //Ενημέρωση για αποτυχία εμφάνισης δεδομένων
                dateText.setText("-");
                timeText.setText("-");
                locationText.setText("-");
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Δεν υπάρχουν δεδομένα!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}
