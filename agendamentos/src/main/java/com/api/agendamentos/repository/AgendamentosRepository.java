package com.api.agendamentos.repository;

import com.api.agendamentos.models.Agendamentos;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Repository
public class AgendamentosRepository {

    private static final String FILE_PATH = "C:\\Users\\VICTO\\Documents\\crafters\\Victoria-Stalino\\api-agendamentos-victoria\\data\\agendamentos_farmacia.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    public AgendamentosRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    //carregar os agendamentos do arquivo json
    public List<Agendamentos> getAgendamentos() {
        try {
            return objectMapper.readValue(new File(FILE_PATH), new TypeReference<List<Agendamentos>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //escrever/salvar no arquivo json
    public void salvarAgendamentos(List<Agendamentos> agendamentos) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), agendamentos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}