package com.example.justintimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;

public class EvaluationFragment extends Fragment {

    private long userId;
    private RatingBar stars;
    private TextView codeTextView;
    private EditText commentEditText;

    private String orderCode;

    public EvaluationFragment() {

    }

    public static EvaluationFragment newInstance(long userId, String orderCode) {
        EvaluationFragment fragment = new EvaluationFragment();
        Bundle args = new Bundle();
        args.putLong("USER_ID", userId);
        args.putString("ORDER_CODE", orderCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_evaluation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getArguments() != null) {
            userId = getArguments().getLong("USER_ID", -1);
            orderCode = getArguments().getString("ORDER_CODE", "");
        }


        //Φόρτωση του κωδικόυ της παραγγελίας που επέλεξε ο χρήστης
        codeTextView = view.findViewById(R.id.orderTextView);
        codeTextView.setText(orderCode);

        //Εντοπισμός των πεδίων από τα οποία θέλουμε τις πληροφορίες
        commentEditText = view.findViewById(R.id.commentEditText);
        stars = view.findViewById(R.id.ratingBar);

        //Επιλογή κουμπιού "ΟΚ"
        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(v -> addEvaluation());
    }

    //Επιλογή κουμπιού "ΟΚ"
    private void addEvaluation() {

        //Παίρνω τις τιμές που εισήγαγε ο χρήστης στα ανάλογα πεδία
        String code = codeTextView.getText().toString();
        String comment = commentEditText.getText().toString();
        String rating = String.valueOf(stars.getRating());

        //Έλεγχος συμπλήρωσης τουλάχιστον των "αστεριών"
        if (rating.isEmpty() || stars.getRating() == 0.0f) {
            Toast.makeText(getContext(), "Παρακαλώ δώστε μία βαθμολογία!", Toast.LENGTH_SHORT).show();
        } else {

            OrderDBHandler orderdbHandler = new OrderDBHandler(getContext(), null, null, 1);

            //Προσθήκη της αξιολόγησης του χρήστη στα στοιχεία της παραγγελίας
            orderdbHandler.addEvaluation(code, rating, comment);

            if (userId != -1) {
                requireActivity().getSupportFragmentManager().popBackStack();
                Intent i = new Intent(getContext(), OrderHistory.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                requireActivity().finish();
            }

        }
    }
}
