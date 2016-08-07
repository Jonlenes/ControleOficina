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
public class ServicosPrestadoDao {
    private SQLiteDatabase db;

    public ServicosPrestadoDao() {
        this.db = DbHelper.getInstance().getWritableDatabase();
    }

    public void insert(ServicosPrestado servicosPrestado) {
        ContentValues contentValues = new ContentValues();

        contentValues.put("idVeiculo", servicosPrestado.getIdVeiculo());
        contentValues.put("idServico", servicosPrestado.getIdServico());

        db.insert("ServicosPrestado", "id", contentValues);
    }

    public void delete(Long idVeiculo){
        db.delete("ServicosPrestado", "idVeiculo = " + idVeiculo, null);
    }

    public List<Servico> getAllByCar(Long id) {
        String sql = "SELECT id, descrisao, valor FROM ServicosPrestado\n" +
                "INNER JOIN Servico\n" +
                "   ON Servico.id = ServicosPrestado.idServico\n" +
                "WHERE idVeiculo = " + id;

        Cursor cursor = db.rawQuery(sql, null);
        List<Servico> servicos = new ArrayList<>();

        while (cursor.moveToNext())
            servicos.add(new Servico(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getDouble(2)));

        return servicos;
    }

}
