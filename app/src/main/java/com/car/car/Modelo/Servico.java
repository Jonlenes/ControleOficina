package com.car.car.Modelo;


/**
 * Created by asus on 04/08/2016.
 */
public class Servico {

    private Long id;
    private String descrisao;
    private Double valor;

    public Servico(Long id, String descrisao, Double valor) {
        this.id = id;
        this.descrisao = descrisao;
        this.valor = valor;
    }

    public Servico(String descrisao, Double valor) {
        this.descrisao = descrisao;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrisao() {
        return descrisao;
    }

    public void setDescrisao(String descrisao) {
        this.descrisao = descrisao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
