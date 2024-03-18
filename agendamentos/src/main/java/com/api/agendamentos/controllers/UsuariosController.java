package com.api.agendamentos.controllers;

import com.api.agendamentos.models.Usuarios;
import com.api.agendamentos.services.UsuariosService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuariosService usuariosService;

    @Autowired
    public UsuariosController(UsuariosService usuariosService) {
        this.usuariosService = usuariosService;
    }

    @Operation(summary = "Listar usuários", description = "Retorna a lista de todos os usuários.")
    @GetMapping
    public List<Usuarios> getUsuarios() {
        return usuariosService.getUsuarios();
    }

    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")})
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioPorId(@PathVariable String id) {
        Usuarios usuario = usuariosService.getUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário com base nos dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = {@io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Usuarios novoUsuario) {
        Usuarios createdUsuario = usuariosService.createUsuarios(
                novoUsuario.getNome(),
                novoUsuario.getEmail(),
                novoUsuario.getDataNascimento()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso.");
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza um usuário existente com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable String id, @RequestBody Usuarios usuarioAtualizado) {
        Usuarios updatedUsuario = usuariosService.atualizarUsuarios(
                id,
                usuarioAtualizado.getNome(),
                usuarioAtualizado.getEmail(),
                usuarioAtualizado.getDataNascimento()
        );
        return ResponseEntity.ok("Usuário atualizado com sucesso.");
    }
}
