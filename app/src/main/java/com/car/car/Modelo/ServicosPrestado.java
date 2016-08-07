package com.car.car.Modelo;

/**
 * Created by asus on 04/08/2016.
 */
public class ServicosPrestado {

    private Long idVeiculo ;
    private Long idServico;

    public ServicosPrestado(Long idVeiculo, Long idServico) {
        this.idVeiculo = idVeiculo;
        this.idServico = idServico;
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Long getIdServico() {
        return idServico;
    }

    public void setIdServico(Long idServico) {
        this.idServico = idServico;
    }
}
