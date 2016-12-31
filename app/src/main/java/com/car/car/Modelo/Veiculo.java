package com.car.car.Modelo;

import java.util.List;

/**
 * Created by asus on 04/08/2016.
 */
public class Veiculo {
    private Long id;
    private String marca;
    private String modelo;
    private String placa;
    private Long km;
    private Cliente cliente;
    private List<Servico> servicos;

    public Veiculo() {
    }

    public Veiculo(String marca, String modelo, String placa, Long km, Cliente cliente, List<Servico> servicos) {
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.km = km;
        this.cliente = cliente;
        this.servicos = servicos;
    }

    public Veiculo(Long id, String marca, String modelo, String placa, Long km, Cliente cliente, List<Servico> servicos) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.km = km;
        this.cliente = cliente;
        this.servicos = servicos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Long getKm() {
        return km;
    }

    public void setKm(Long km) {
        this.km = km;
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
