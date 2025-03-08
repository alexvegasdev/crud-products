package com.example.crudproductos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.crudproductos.Modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ventas.db";
    private static final int DATABASE_VERSION = 5;
    private static final String TABLE_PRODUCTOS = "productos";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_DESCRIP = "descrip";
    private static final String COLUMN_PRECIO = "precio";
    private static final String COLUMN_STOCK = "stock";
    private static final String COLUMN_URL = "url";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_PRODUCTOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_DESCRIP + " TEXT, " +
                COLUMN_PRECIO + " REAL, " +
                COLUMN_STOCK + " INTEGER, " +
                COLUMN_URL + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
        onCreate(db);
    }

    public void insertarProducto(String nom,String descrip, double precio, int stock, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nom);
        values.put(COLUMN_DESCRIP, descrip);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_STOCK, stock);
        values.put(COLUMN_URL, url);
        db.insert(TABLE_PRODUCTOS, null, values);
        db.close();
    }

    public List<Producto> obtenerProductos() {
        List<Producto> listaProductos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String descrip = cursor.getString(2);
                double precio = cursor.getDouble(3);
                int stock = cursor.getInt(4);
                String url = cursor.getString(5);
                listaProductos.add(new Producto(id, nombre, descrip, precio, stock, url));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listaProductos;
    }
    public int actualizarProducto(int id, String nom, String descrip, double precio, int stock, String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nom);
        values.put("descrip", descrip);
        values.put("precio", precio);
        values.put("stock", stock);
        values.put("url", url);
        return db.update("productos", values, "id = ?", new String[]{String.valueOf(id)});
    }
    public int eliminarProducto(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("productos", "id=?", new String[]{String.valueOf(id)});
    }
    public int contarRegistros() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM productos", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}
