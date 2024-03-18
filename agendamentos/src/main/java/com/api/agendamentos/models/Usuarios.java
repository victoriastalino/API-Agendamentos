package com.api.agendamentos.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Usuarios {
    private String id;
    private String nome;
    private String email;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String dataNascimento;
    private LocalDateTime dataCriacao;
    public Usuarios() {
    }

    public Usuarios(String nome, String email, String dataNascimento) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.dataCriacao = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
