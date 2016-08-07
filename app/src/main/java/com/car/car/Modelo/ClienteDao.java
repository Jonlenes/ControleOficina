package com.car.car.Modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.car.car.DataBase.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 04/08/2016.
 */
public class ClienteDao {
    private SQLiteDatabase db;

    public ClienteDao() {
        this.db = DbHelper.getInstance().getWritableDatabase();
    }

    public void insert(Cliente cliente) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("nome", cliente.getNome());
        contentValues.put("cpf", cliente.getCpf());
        contentValues.put("telefone", cliente.getTelefone());

        db.insert("Cliente", "id", contentValues);
    }

    public void update(Cliente cliente) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("nome", cliente.getNome());
        contentValues.put("cpf", cliente.getCpf());
        contentValues.put("telefone", cliente.getTelefone());

        db.update("Cliente", contentValues, "id = " + cliente.getId(), null);
    }

    public void delete(Long idVeiculo){
        db.delete("Cliente", "id = " + idVeiculo, null);
    }


    public Cliente getById(Long id) {
        String sql = "SELECT id, nome, cpf, telefone FROM Cliente\n" +
                "WHERE id = " + id;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToNext())
            return new Cliente(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));

        return null;
    }

    public Long getIdLast() {
        String sql = "SELECT MAX(id) id FROM Cliente\n";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToNext())
            return cursor.getLong(0);

        return -1L;
    }

    public List<Cliente> getAll() {
        String sql = "SELECT id, nome, cpf, telefone FROM Cliente\n" +
                "ORDER BY nome";

        Cursor cursor = db.rawQuery(sql, null);
        List<Cliente> clientes = new ArrayList<>();

        while (cursor.moveToNext())
            clientes.add(new Cliente(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)));

        return clientes;
    }

}
