package com.example.justintimeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "usersDB.db";


    //Τα πεδία του πίνακα users που έχει τα δεδομένα του κάθε χρήστη
    public static final String TABLE_DATA = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_PHONENUMBER = "phoneNumber";
    public static final String COLUMN_MOBILENUMBER = "mobileNumber";
    public static final String COLUMN_EMAIL = "email";

    //Τα πεδία του πίνακα addresses που έχει τις διευθύνσεις του κάθε χρήστη
    public static final String TABLE_ADDRESSES = "addresses";
    public static final String COLUMN_USER_ID = "userid";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_COUNTY = "county";

    //Τα πεδία του πίνακα accesses που έχει τα στοιχεία πρόσβασης του κάθε χρήστη
    public static final String TABLE_ACCESSES = "accesses";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Δημιουργία των τριών πινάκων
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_DATA + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + //Αυτόματη δημιουργία των id
                COLUMN_NAME + " TEXT," +
                COLUMN_SURNAME + " TEXT," +
                COLUMN_PHONENUMBER + " TEXT," +
                COLUMN_MOBILENUMBER + " TEXT," +
                COLUMN_EMAIL + " TEXT" + ")";

        String CREATE_ADDRESSES_TABLE = "CREATE TABLE " +
                TABLE_ADDRESSES + "(" +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_CITY + " TEXT," +
                COLUMN_CODE + " TEXT," +
                COLUMN_COUNTY + " TEXT," +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_DATA + "(" + COLUMN_ID + "))"; //Αντιστοίχιση με τα id του πίνακα των users

        String CREATE_ACCESSES_TABLE = "CREATE TABLE " +
                TABLE_ACCESSES + "(" +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_PASSWORD+ " TEXT," +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_DATA + "(" + COLUMN_ID + "))"; //Αντιστοίχιση με τα id του πίνακα των users

        db.execSQL(CREATE_ACCESSES_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_ADDRESSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESSES);
        onCreate(db);
    }


    //Συνάρτηση προσθήκης των δεδομένων του χρήστη στη βάση
    public long addAccount(NewAccount account) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, account.getName());
        values.put(COLUMN_SURNAME, account.getSurname());
        values.put(COLUMN_PHONENUMBER, account.getPhone());
        values.put(COLUMN_MOBILENUMBER, account.getMobile());
        values.put(COLUMN_EMAIL, account.getEmail());
        SQLiteDatabase db = this.getWritableDatabase();

        long userId = db.insert(TABLE_DATA, null, values);
        db.close();
        return userId;
    }

    //Συνάρτηση προσθήκης των διευθύνσεων του χρήστη στη βάση
    public void addAddress(NewAddress address, long userId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId); //Χρησιμοποιείται το id που έχει δημιουργηθεί για τον χρήστη
        values.put(COLUMN_ADDRESS, address.getAddress());
        values.put(COLUMN_CITY, address.getCity());
        values.put(COLUMN_CODE, address.getCode());
        values.put(COLUMN_COUNTY, address.getCounty());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_ADDRESSES, null, values);

        db.close();

    }


    //Συνάρτηση προσθήκης των στοιχείων πρόσβασης του χρήστη στη βάση
    public boolean addAccess(NewAccess access, long userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        //Έλεγχος ύπαρξης ίδιου username στη βάση
        String usernameQuery = "SELECT 1 FROM " + TABLE_ACCESSES + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(usernameQuery, new String[] { access.getUsername() });

        boolean usernameExists = cursor.moveToFirst();
        cursor.close();

        if (usernameExists) {
            db.close();
            return false; //Επιστροφή της πληροφορίας ύπαρξης του username
        }

        //Αν δεν υπάρχει το username, γίνεται είσοδος των δεδομένων στη βάση
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_USERNAME, access.getUsername());
        values.put(COLUMN_PASSWORD, access.getPassword());

        db.insert(TABLE_ACCESSES, null, values);
        db.close();

        return true; //Επιστροφή επιτυχούς προσθήκης
    }


    //Συνάρτηση ελέγχου σωστών στοιχείων για σύνδεση του χρήστη
    public long checkCredentials(String username, String password) {
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_ACCESSES +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        long userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }

        cursor.close();
        db.close();
        return userId; //Επιστροφή του id στο οποίο αντιστοιχούν το username & το password
    }


    //Συνάρτηση εύρεσης των δεδομένων του χρήστη με βάση το id
    public List<String> getUserDetailsById(long id) {
        List<String> userDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        //Εύρεση των στοιχειών που αντιστοιχούν στο id και προσθήκη αυτών σε λίστα
        Cursor cursor = db.query(
                TABLE_DATA,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            userDetails.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            userDetails.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME)));
            userDetails.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONENUMBER)));
            userDetails.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOBILENUMBER)));
            userDetails.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
        }
        cursor.close();

        //Προσθήκη και των διεθύνσεων που αντιστοιχούν στο id
        Cursor addressCursor = db.query(
                TABLE_ADDRESSES,
                null,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (addressCursor.moveToFirst()) {
            do {
                userDetails.add(addressCursor.getString(addressCursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
                userDetails.add(addressCursor.getString(addressCursor.getColumnIndexOrThrow(COLUMN_CITY)));
                userDetails.add(addressCursor.getString(addressCursor.getColumnIndexOrThrow(COLUMN_CODE)));
                userDetails.add(addressCursor.getString(addressCursor.getColumnIndexOrThrow(COLUMN_COUNTY)));
            } while (addressCursor.moveToNext());
        }

        addressCursor.close();
        db.close();
        return userDetails; //Επιστροφή της λίστας με τα στοιχεία του id
    }


    //Συνάρτηση εύρεσης των ονομάτων των διευθύνσεων ενός χρήστη
    public List<String> getAddressesOfUser(long userId) {

        List<String> addressList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ADDRESSES,
                new String[]{COLUMN_ADDRESS}, //Προσθήκη στη λίστα μόνο των ονομάτων των διευθύνσεων ενός id
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                addressList.add(address);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return addressList;
    }


    //Συνάρτηση διαγραφής όλων των δεδομένων που αντιστοιχούν σε ένα id
    public void deleteUserById(long userId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DATA, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_ADDRESSES, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.delete(TABLE_ACCESSES, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

        db.close();
    }



}
