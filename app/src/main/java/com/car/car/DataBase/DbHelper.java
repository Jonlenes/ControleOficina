package com.car.car.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	
    private final static String DATABASE_NAME = "CitCar";
    private final static int DATABASE_VERSION = 6;
    private static DbHelper ourInstance = null;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DbHelper getInstance() {
        return ourInstance;
    }

    public static void newInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DbHelper(context.getApplicationContext());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableCliente(db);
        createTableVeiculo(db);
        createTableServico(db);
        createTableServicosPrestados(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE Cliente");
        db.execSQL("DROP TABLE Veiculo");
        db.execSQL("DROP TABLE Servico");
        db.execSQL("DROP TABLE ServicosPrestado");

        onCreate(db);
    }

    public void createTableCliente(SQLiteDatabase db){
        String sql = "CREATE TABLE IF NOT EXISTS Cliente\n" +
                "(\n" +
                "\tid               INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tnome             VARCHAR(100) NOT NULL,\n" +
                "\tcpf              VARCHAR(14) NOT NULL,\n" +
                "\ttelefone         VARCHAR(20) NOT NULL\n" +
                ")";
        db.execSQL(sql);
		
    }

    public void createTableVeiculo(SQLiteDatabase db){
        String sql = "CREATE TABLE IF NOT EXISTS Veiculo\n" +
                "(\n" +
                "\tid            INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tplaca         VARCHAR(8) NOT NULL,\n" +
                "\tdescrisao     VARCHAR(100) NOT NULL,\n" +
                "\tidCliente     INTEGER NOT NULL\n" +
                ")\n";
        db.execSQL(sql);
    }

    public void createTableServico(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS Servico\n" +
                "(\n" +
                "\tid            INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tdescrisao     VARCHAR(100) NOT NULL,\n" +
                "\tvalor         REAL NOT NULL\n" +
                ")";
        db.execSQL(sql);
    }

    public void createTableServicosPrestados(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS ServicosPrestado\n" +
                "(\n" +
                "\tidVeiculo     INTEGER NOT NULL,\n" +
                "\tidServico     INTEGER NOT NULL\n" +
                ")";
        db.execSQL(sql);
    }
}
