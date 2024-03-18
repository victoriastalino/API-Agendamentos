package com.api.agendamentos.models;

import com.api.agendamentos.exceptions.TipoServicoDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.UUID;

public class Agendamentos {

    public enum statusAgendamento{
        AGENDADO, CANCELADO
    }
    public enum TipoServico {
        SERVICO1, SERVICO2, SERVICO3, SERVICO4, servico1, servico2, servico3, servico4;

        public static boolean contains(String value) {
            for (TipoServico tipoServico : values()) {
                if (tipoServico.name().equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
    private String idAgendamento;
    private String idUsuario;
    @JsonDeserialize(using = TipoServicoDeserializer.class)
    private TipoServico servico;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private String dataHora;
    private statusAgendamento status;

    public Agendamentos() {}
    public Agendamentos(String idUsuario, TipoServico servico, String dataHora) {
        this.idAgendamento = UUID.randomUUID().toString();
        this.idUsuario = idUsuario;
        this.servico = servico;
        this.dataHora = dataHora;
        this.status = statusAgendamento.AGENDADO;
    }
    public String getIdAgendamento() {
        return idAgendamento;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public TipoServico getServico() {
        return servico;
    }

    public void setServico(TipoServico servico) {
        this.servico = servico;
    }

    public String getDataHora() {return dataHora;}

    public void setDataHora(String dataHora) {this.dataHora = dataHora;
    }

    public statusAgendamento getStatus() {
        return status;
    }

    public void setStatus(statusAgendamento status) {
        this.status = status;
    }
}
