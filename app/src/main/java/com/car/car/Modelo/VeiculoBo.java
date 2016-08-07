package com.car.car.Modelo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asus on 04/08/2016.
 */
public class VeiculoBo {

    public void insertOrUpdate(Veiculo veiculo, boolean newInsert, boolean newClient) {

        if (newClient) {
            ClienteDao clienteDao = new ClienteDao();
            clienteDao.insert(veiculo.getCliente());
            veiculo.getCliente().setId(clienteDao.getIdLast());
        } else {
            new ClienteDao().update(veiculo.getCliente());
        }

        if (newInsert) {

            VeiculoDao veiculoDao = new VeiculoDao();
            veiculoDao.insert(veiculo);
            veiculo.setId(veiculoDao.getIdLast());

        } else {

            new  VeiculoDao().update(veiculo);
            new ServicosPrestadoDao().delete(veiculo.getId());

        }

        ServicosPrestadoDao servicosPrestadoDao = new ServicosPrestadoDao();
        for (Servico servico : veiculo.getServicos()) {
            servicosPrestadoDao.insert(new ServicosPrestado(veiculo.getId(), servico.getId()));
        }
    }

    public void delete(Long id) {
        Long idClient = new VeiculoDao().getById(id).getCliente().getId();
        VeiculoDao veiculoDao = new VeiculoDao();

        new ServicosPrestadoDao().delete(id);
        veiculoDao.delete(id);
        if (veiculoDao.getAllByClient(idClient).size() == 0)
            new ClienteDao().delete(idClient);

    }

    public Map<Cliente, List<Veiculo> > getMapForView() {

        Map<Cliente, List<Veiculo> > map = new LinkedHashMap<>();
        List<Cliente> clientes = new ClienteDao().getAll();
        VeiculoDao veiculoDao = new VeiculoDao();

        for (Cliente cliente : clientes) {
            map.put(cliente, veiculoDao.getAllByClient(cliente.getId()));
        }

        return map;

    }
}
