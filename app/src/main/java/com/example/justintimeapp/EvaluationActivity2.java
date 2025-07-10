package com.example.justintimeapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class EvaluationActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation2); // The FrameLayout layout

        long userId = getIntent().getLongExtra("USER_ID", -1);
        String orderCode = getIntent().getStringExtra("ORDER_CODE");

        //Φόρτωση του fragment αν δεν υπάρχει ήδη με τα δεδομένα που έλαβε από την προηγούμενη οθόνη
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.evaluation_fragment_container, EvaluationFragment.newInstance(userId, orderCode))
                    .commit();
        }
    }
}



















/*package com.example.justintimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EvaluationActivity2 extends AppCompatActivity {


    long userId;
    RatingBar stars;
    TextView codeTextView;
    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evaluation2);

        userId = getIntent().getLongExtra("USER_ID", -1);
        Toast.makeText(this, "ID IS: " + userId, Toast.LENGTH_SHORT).show();


        String orderCode = getIntent().getStringExtra("ORDER_CODE");

        codeTextView = findViewById(R.id.orderTextView);
        codeTextView.setText(orderCode);

        commentEditText = findViewById(R.id.commentEditText);
        stars = findViewById(R.id.ratingBar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void AddEvaluation(View view) {
        String code = codeTextView.getText().toString();
        String comment = commentEditText.getText().toString();
        String rating = String.valueOf(stars.getRating());

        if(rating.isEmpty()){
            Toast.makeText(this, "Please give some stars" , Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Tha stars are: " + rating , Toast.LENGTH_SHORT).show();

            OrderDBHandler orderdbHandler = new OrderDBHandler(this, null, null, 1);

            orderdbHandler.addEvaluation(code,rating,comment);

            if(!(userId ==-1)){
                Intent i = new Intent(this, OrderHistory.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
            }
        }

    }
}*/