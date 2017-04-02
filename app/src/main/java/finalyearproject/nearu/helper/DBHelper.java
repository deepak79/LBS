package finalyearproject.nearu.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    public static final String CONTACTS_COLUMN_ADDRESS = "address";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_PROFILE = "profile";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table contacts " + "(id integer primary key, name text,phone text,address text,email text,profile text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CONTACTS_TABLE_NAME, null, null);
        return true;
    }

    public boolean insertContact(String name, String phone, String address, String email, String profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("address", address);
        contentValues.put("email", email);
        contentValues.put("profile", profile);
        db.insert("contacts", null, contentValues);
        return true;
    }

//    public AddressStruct getData(int id) {
//        AddressStruct addressStruct = new AddressStruct();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select * from contacts where id=" + id + "", null);
//        try {
//            if (res.getCount() > 0) {
//
//                res.moveToFirst();
//                addressStruct.setId(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
//                addressStruct.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//                addressStruct.setMobileno(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHONE)));
//                addressStruct.setAddress(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ADDRESS)));
//                addressStruct.setEmail(res.getString(res.getColumnIndex(CONTACTS_COLUMN_EMAIL)));
//                addressStruct.setProfile(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PROFILE)));
//            }
//        } finally {
//            res.close();
//        }
//
//        return addressStruct;
//    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String name, String phone, String address, String email, String profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("address", address);
        contentValues.put("email", email);
        contentValues.put("profile", profile);
        db.update("contacts", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

//    public ArrayList<AddressStruct> getAllContacts() {
//        ArrayList<AddressStruct> array_list = new ArrayList<AddressStruct>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select * from contacts", null);
//        res.moveToFirst();
//
//        while (res.isAfterLast() == false) {
//            AddressStruct addressStruct = new AddressStruct();
//            addressStruct.setId(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
//            addressStruct.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//            addressStruct.setMobileno(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHONE)));
//            addressStruct.setAddress(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ADDRESS)));
//            addressStruct.setEmail(res.getString(res.getColumnIndex(CONTACTS_COLUMN_EMAIL)));
//            addressStruct.setProfile(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PROFILE)));
//
//            array_list.add(addressStruct);
//            res.moveToNext();
//        }
//        return array_list;
//    }
}