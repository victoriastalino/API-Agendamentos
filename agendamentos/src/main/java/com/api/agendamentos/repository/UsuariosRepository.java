package com.api.agendamentos.repository;

import com.api.agendamentos.models.Usuarios;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuariosRepository {

    private static final String FILE_PATH = "C:\\Users\\VICTO\\Documents\\crafters\\Victoria-Stalino\\api-agendamentos-victoria\\data\\usuarios_farmacia.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    public UsuariosRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // carregar os usuarios do arquivo json
    public List<Usuarios> getUsuarios() {
        try {
            return objectMapper.readValue(new File(FILE_PATH), new TypeReference<List<Usuarios>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Usuarios getUsuarioPorId(String id) {
        List<Usuarios> usuarios = getUsuarios();
        return usuarios.stream()
                .filter(usuario -> usuario.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // salva os usuarios no arquivo json
    public void salvarUsuarios(List<Usuarios> usuarios) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
