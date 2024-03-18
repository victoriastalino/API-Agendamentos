package com.api.agendamentos.controllers;

import com.api.agendamentos.exceptions.BadRequestException;
import com.api.agendamentos.exceptions.InvalidFormatException;
import com.api.agendamentos.models.Agendamentos;
import com.api.agendamentos.services.AgendamentosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentosController {

    private final AgendamentosService agendamentosService;

    @Autowired
    public AgendamentosController(AgendamentosService agendamentosService) {
        this.agendamentosService = agendamentosService;
    }

    @Operation(summary = "Buscar agendamentos", description = "Retorna uma lista com todos os agendamentos feitos.")
    @GetMapping
    public ResponseEntity<List<Agendamentos>> getAgendamentos() {
        List<Agendamentos> agendamentos = agendamentosService.getAgendamentos();
        return ResponseEntity.ok(agendamentos);
    }

    @Operation(summary = "Buscar agendamentos do usuário", description = "Retorna uma lista com todos os agendamentos feitos por um usuário recebendo como parâmetro seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamentos encontrados", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Agendamentos.class))) }),
            @ApiResponse(responseCode = "400", description = "ID de usuário inválido"),
            @ApiResponse(responseCode = "404", description = "Nenhum agendamento encontrado para o usuário")
    })
    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> getAgendamentosPorUsuario(@PathVariable String idUsuario) {
            List<Agendamentos> agendamentosUsuario = agendamentosService.getAgendamentosPorUsuario(idUsuario);
            return ResponseEntity.ok(agendamentosUsuario);
    }
    @Operation(summary = "Buscar horários disponíveis", description = "Lista os horários disponíveis para agendamento em uma data especificada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horários disponíveis encontrados", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LocalDateTime.class))) }),
            @ApiResponse(responseCode = "400", description = "Formato de data inválido"),
            @ApiResponse(responseCode = "404", description = "Nenhum horário disponível para a data especificada")
    })
    @GetMapping("/disponiveis")
    public ResponseEntity<?> getHorariosDisponiveis(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
            List<LocalDateTime> horariosDisponiveis = agendamentosService.getHorariosDisponiveis(data);

            if (horariosDisponiveis.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Não há horários disponíveis para a data especificada.");
            }
            return ResponseEntity.ok(horariosDisponiveis);
    }

    @Operation(summary = "Criar agendamento", description = "Cria um novo agendamento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Agendamentos.class)) }),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
    })
    @PostMapping
    public ResponseEntity<?> createAgendamento(@RequestBody Agendamentos novoAgendamento) {
            Agendamentos createdAgendamento = agendamentosService.createAgendamento(
                    novoAgendamento.getIdUsuario(),
                    novoAgendamento.getServico(),
                    novoAgendamento.getDataHora()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("Agendamento criado com sucesso.");
        }
    @Operation(summary = "Cancelar agendamento", description = "Realiza o cancelamento do agendamento de id especificado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Não é possível cancelar um agendamento passado ou já cancelado.")
    })
    @DeleteMapping("/{idAgendamento}")
        public ResponseEntity<String> cancelarAgendamento(@PathVariable String idAgendamento) {

        agendamentosService.cancelarAgendamento(idAgendamento);
        return ResponseEntity.ok("Agendamento cancelado com sucesso.");
    }
}

