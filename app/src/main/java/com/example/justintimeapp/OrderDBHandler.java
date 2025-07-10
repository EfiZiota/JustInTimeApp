package com.example.justintimeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class OrderDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ordersDB.db";


    //Τα πεδία του πίνακα courier_data που έχει τα δεδομένα κάθε παραγγελίας
    public static final String TABLE_COURIER_DATA = "courier_data";
    public static final String COLUMN_ID = "userid";
    public static final String COLUMN_ORDERCODE_C = "ordercode";
    public static final String COLUMN_COURIER = "courier";
    public static final String COLUMN_ORDERSTATE = "state";
    public static final String COLUMN_DELIVERY_DATE = "delivery_date";
    public static final String COLUMN_FLEXIBLE_DATE = "flexible_date";
    public static final String COLUMN_HOUR = "hours";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_RATING = "stars";
    public static final String COLUMN_COMMENT = "comments";




    public OrderDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Δημιουργία του πίνακα
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURIER_DATA_TABLE = "CREATE TABLE " +
                TABLE_COURIER_DATA + "(" +
                COLUMN_ID + " INTEGER," +
                COLUMN_ORDERCODE_C + " TEXT," +
                COLUMN_ORDERSTATE + " TEXT," +
                COLUMN_COURIER + " TEXT," +
                COLUMN_DELIVERY_DATE + " TEXT," +
                COLUMN_FLEXIBLE_DATE + " TEXT," +
                COLUMN_HOUR + " TEXT," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_RATING + " TEXT," +
                COLUMN_COMMENT + " TEXT" + ")";

        db.execSQL(CREATE_COURIER_DATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURIER_DATA);
        onCreate(db);
    }


    //Συνάρτηση προσθήκης μίας παραγγελίας στη βάση
    public void addOrder(NewOrder order) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues courierValues = new ContentValues();
        courierValues.put(COLUMN_ID, order.getId());
        courierValues.put(COLUMN_ORDERCODE_C, order.getOrderCode());
        courierValues.put(COLUMN_COURIER, order.getCourier());
        courierValues.put(COLUMN_DELIVERY_DATE, order.getDate());
        courierValues.put(COLUMN_FLEXIBLE_DATE, order.getFlexibleDate());
        courierValues.put(COLUMN_HOUR, order.getHours());
        courierValues.put(COLUMN_ADDRESS, order.getAddress());

        //Δημιουργία μίας τυχαίας τιμής "κατάστασης παραγγελίας" για τις ανάγκες της έργασίας(στην πραγματικότητα αυτή η πληροφορία θα προστίθεται από την εταιρεία κούριερ)
        String[] states = {"Παραδόθηκε", "Προς Παράδοση", "Σε εκκρεμότητα"};
        String randomState = states[new Random().nextInt(states.length)];
        courierValues.put(COLUMN_ORDERSTATE, randomState);

        db.insert(TABLE_COURIER_DATA, null, courierValues);

        db.close();
    }

    //Συνάρτηση προσθήκης της αξιολόγησης μίας παραγγελίας
    public void addEvaluation(String orderCode, String rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RATING, rating);
        values.put(COLUMN_COMMENT, comment);

        //Ενημέρωση της υπάρχουσας παραγγελίας με τα στοιχεία της αξιολόγησης
        db.update(
                TABLE_COURIER_DATA,
                values,
                COLUMN_ORDERCODE_C + " = ?",
                new String[]{orderCode}
        );

        db.close();
    }


    //Συνάρτηση εύρεσης μίας παραγγελίας με βάση τον κωδικό της και το id του χρήστη
    public Map<String, String> searchOrder(String orderCode, long userId) {
        String query = "SELECT " + COLUMN_ORDERCODE_C + ", " + COLUMN_ORDERSTATE + ", " + COLUMN_COURIER + ", " + COLUMN_ID +
                " FROM " + TABLE_COURIER_DATA +
                " WHERE " + COLUMN_ORDERCODE_C + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{orderCode});

        Map<String, String> result = new HashMap<>();

        if (cursor.moveToFirst()) {
            long dbUserId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));

            //Αν η παραγγελία αντιστοιχεί σε αυτόν τον χρήστη γίνεται προσθήκη στο map των στοιχείων της παραγγελίας που χρειάζονται
            if (dbUserId == userId) {
                result.put("code", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDERCODE_C)));
                result.put("state", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDERSTATE)));
                result.put("courier", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURIER)));
            } else {
                //Αν ο κωδικός παραγγελίας δεν αντιστοιχεί στον χρήστη δεν μπορεί να του επιστρέφει τα στοιχεία της παραγγελίας
                result.put("error", "Αυτή η παραγγελίά δεν αντιστοιχεί σε εσάς...");
            }
        } else {
            //Αν δεν υπάρχει ο κωδικός παραγγελίας
            result.put("error", "Η παραγγελία δεν υπάρχει.");
        }

        cursor.close();
        db.close();
        return result; //Επιστροφή του map με τα ανάλογα στοιχεία
    }



    //Συνάρτηση εύρεσης των παραγγελιών που αντιστοιχούν σε ένα id
    public List<String> getOrderCodesByUserId(long userId) {
        List<String> orderCodes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        //Εντοπισμός των κωδικών των παραγγελιών ενός συγκεκριμένου id
        String query = "SELECT " + COLUMN_ORDERCODE_C + " FROM " + TABLE_COURIER_DATA +
                " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDERCODE_C));
                orderCodes.add(code); //Προσθήκη των κωδικών των παραγγελίων του συγκεκριμένου id στη λίστα
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return orderCodes; //Επιστροφή της λίστας
    }


    //Συνάρτηση εύρεσης στοιχείων διαθεσιμότητας για μία παραγγελία
    public Map<String, String> getCourierDeliveryInfo(String orderCode, long userId) {
        Map<String, String> result = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_DELIVERY_DATE + ", " +
                        COLUMN_HOUR + ", " +
                        COLUMN_ADDRESS +
                        " FROM " + TABLE_COURIER_DATA +
                        " WHERE " + COLUMN_ORDERCODE_C + " = ? AND " +
                        COLUMN_ID + " = ?",
                new String[]{orderCode, String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            result.put("date", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELIVERY_DATE)));
            result.put("time", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HOUR)));
            result.put("address", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
        }

        cursor.close();
        db.close();
        return result; //Επιστροφή του map με τα στοιχεία διαθεσιμότητας που υπάρχουν στη βάση για μία παραγγελία
    }

    //Συνάρτηση ενημέρωσης στοιχείων διαθεσιμότητας μίας παραγγελίας
    public void updateOrder(NewOrder order) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_DELIVERY_DATE, order.getDate());
        values.put(COLUMN_FLEXIBLE_DATE, order.getFlexibleDate());
        values.put(COLUMN_HOUR, order.getHours());
        values.put(COLUMN_ADDRESS, order.getAddress());


        String whereClause = COLUMN_ORDERCODE_C + " = ? AND " + COLUMN_ID + " = ?";
        String[] whereArgs = { order.getOrderCode(), String.valueOf(order.getId()) };

        //Ενημέρωση της παραγγελίας με τα καινούργια στοιχεία διαθεσιμότητας
        db.update(TABLE_COURIER_DATA, values, whereClause, whereArgs);
        db.close();

    }


}
