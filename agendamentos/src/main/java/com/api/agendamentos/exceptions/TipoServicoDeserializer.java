package com.api.agendamentos.exceptions;

import com.api.agendamentos.models.Agendamentos;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


import java.io.IOException;

public class TipoServicoDeserializer extends JsonDeserializer<Agendamentos.TipoServico> {
    @Override
    public Agendamentos.TipoServico deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString().toUpperCase();
        if (Agendamentos.TipoServico.contains(value)) {
            return Agendamentos.TipoServico.valueOf(value);
        } else {
            throw new IllegalArgumentException("Serviço inválido. Escolha um serviço válido.");
        }
    }
}
