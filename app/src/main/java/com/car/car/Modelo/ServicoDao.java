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
public class ServicoDao {
    private SQLiteDatabase db;

    public ServicoDao() {
        this.db = DbHelper.getInstance().getWritableDatabase();
    }

    public void insert(Servico servico) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("descrisao", servico.getDescrisao());
        contentValues.put("valor", servico.getValor());

        db.insert("Servico", "id", contentValues);
    }


    public List<Servico> getAll(Long idVeiculoDesconsiderado) {
        String sql = "SELECT id, descrisao, valor FROM Servico\n";

        if (idVeiculoDesconsiderado != null)
            sql += "WHERE NOT EXISTS (\n" +
                    "   SELECT idVeiculo FROM ServicosPrestado\n" +
                    "   WHERE idServico = Servico.id\n" +
                    "      AND idVeiculo = " + idVeiculoDesconsiderado + ")\n";
        sql += "ORDER BY descrisao";

        Cursor cursor = db.rawQuery(sql, null);
        List<Servico> servicos = new ArrayList<>();

        while (cursor.moveToNext())
            servicos.add(new Servico(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getDouble(2)));

        return servicos;
    }

    public List<Servico> getAllOrderUsed() {
        String sql = "SELECT id, descrisao, valor FROM Servico\n" +
                "ORDER BY descrisao";

        Cursor cursor = db.rawQuery(sql, null);
        List<Servico> servicos = new ArrayList<>();

        while (cursor.moveToNext())
            servicos.add(new Servico(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getDouble(2)));

        return servicos;
    }

}
