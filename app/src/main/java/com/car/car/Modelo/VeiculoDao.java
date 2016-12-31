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
public class VeiculoDao {
    private SQLiteDatabase db;

    public VeiculoDao() {
        this.db = DbHelper.getInstance().getWritableDatabase();
    }

    public void insert(Veiculo veiculo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("marca", veiculo.getMarca());
        contentValues.put("modelo", veiculo.getModelo());
        contentValues.put("placa", veiculo.getPlaca());
        contentValues.put("km", veiculo.getKm());
        contentValues.put("idCliente", veiculo.getCliente().getId());

        db.insert("Veiculo", "id", contentValues);
    }

    public void update(Veiculo veiculo) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("marca", veiculo.getMarca());
        contentValues.put("modelo", veiculo.getModelo());
        contentValues.put("placa", veiculo.getPlaca());
        contentValues.put("km", veiculo.getKm());
        contentValues.put("idCliente", veiculo.getCliente().getId());

        db.update("Veiculo", contentValues, "id = " + veiculo.getId(), null);
    }

    public Long getIdLast() {
        String sql = "SELECT MAX(id) id FROM Veiculo\n";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToNext())
            return cursor.getLong(0);

        return -1L;
    }

    public void delete(Long id){
        db.delete("Veiculo", "id = " + id, null);
    }

    public Veiculo getById(Long id) {
        String sql = "SELECT Veiculo.id AS idVeiculo, marca, modelo, placa, km, idCliente, nome, cpf, telefone FROM Veiculo\n" +
                "INNER JOIN CLiente\n" +
                "   ON CLiente.id = idCliente\n" +
                "WHERE Veiculo.id = " + id;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToNext())
            return new Veiculo(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    new Cliente(cursor.getLong(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)),
                    null);

        return null;
    }

    public List<Veiculo> getAllByClient(Long idCliente) {
        String sql = "SELECT id, marca, modelo, placa, km, idCliente FROM Veiculo\n" +
                "WHERE idCliente = " + idCliente;

        Cursor cursor = db.rawQuery(sql, null);
        List<Veiculo> veiculos = new ArrayList<>();

        while (cursor.moveToNext())
            veiculos.add(new Veiculo(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    null, null));

        return veiculos;
    }

}
