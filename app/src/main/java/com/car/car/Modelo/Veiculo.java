package com.car.car.Modelo;

import java.util.List;

/**
 * Created by asus on 04/08/2016.
 */
public class Veiculo {
    private Long id;
    private String placa;
    private String descrisao;
    private Cliente cliente;
    private List<Servico> servicos;

    public Veiculo() {
    }

    public Veiculo(String placa, String descrisao, Cliente cliente, List<Servico> servicos) {
        this.placa = placa;
        this.descrisao = descrisao;
        this.cliente = cliente;
        this.servicos = servicos;
    }

    public Veiculo(Long id, String placa, String descrisao, Cliente cliente, List<Servico> servicos) {
        this.id = id;
        this.placa = placa;
        this.descrisao = descrisao;
        this.cliente = cliente;
        this.servicos = servicos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getDescrisao() {
        return descrisao;
    }

    public void setDescrisao(String descrisao) {
        this.descrisao = descrisao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }
}
