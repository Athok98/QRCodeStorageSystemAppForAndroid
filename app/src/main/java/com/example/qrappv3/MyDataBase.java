package com.example.qrappv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class MyDataBase extends SQLiteOpenHelper {

    public MyDataBase(Context context) {
        super(context, "name1.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tableimage (name text, image blob, qr_id text,image_uri test);");
        db.execSQL("create table tablebox (name text, qr_id text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists tableimage");
        db.execSQL("drop table if exists tablebox");
    }

    public boolean insertdata(String username, byte[] img, String qr_id, String image_uri){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", username);
        contentValues.put("image", img);
        contentValues.put("qr_id", qr_id);
        contentValues.put("image_uri", image_uri);
        long ins = MyDB.insert("tableimage", null, contentValues);
        if(ins ==-1) return false;
        else return true;

    }
    public boolean removeBox(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        long ins = MyDB.delete("tableimage","qr_id=?",new String[]{name});
        if(ins ==-1) return false;
        else return true;

    }
    public boolean removeItem(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        long ins = MyDB.delete("tableimage","name=?",new String[]{name});
        if(ins ==-1) return false;
        else return true;

    }
    public boolean checkIfBoxExiste(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        long ins = MyDB.rawQuery("Select * from tableimage where qr_id = ?", new String[]{name}).getCount();
        if(ins ==0) return false;
        else return true;

    }
    public String getName(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from tableimage where qr_id = ?", new String[]{name});
        cursor.moveToFirst();
        return cursor.getString(0);
    }
    public String getUri(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from tableimage where qr_id = ?", new String[]{name});
        cursor.moveToFirst();
        return cursor.getString(3);
    }
    public Integer getNumberUri(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from tableimage where qr_id = ?", new String[]{name});
        cursor.moveToFirst();
        return cursor.getCount();
    }
    public String[] getUris(String name){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from tableimage where qr_id = ?", new String[]{name});
        String[] tablica = new String[cursor.getCount()];
        cursor.moveToFirst();
        for(int i =0;i<cursor.getCount();i++){
            tablica[i] = cursor.getString(3);
        }
        return tablica;
    }

    public Bitmap getImage(String qr_id){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from tableimage where qr_id = ?", new String[]{qr_id});
        //        if(c.moveToNext())
        cursor.moveToFirst();
        byte[] bitmap = cursor.getBlob(1);
        Bitmap image = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        return image;
    }

    public boolean insertDataBox(String name, String qr_id){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("qr_id", qr_id);
        long ins = MyDB.insert("tablebox", null, contentValues);
        if(ins ==-1) return false;
        else return true;
    }


}