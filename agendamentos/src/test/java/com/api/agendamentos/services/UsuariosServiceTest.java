package com.api.agendamentos.services;

import com.api.agendamentos.exceptions.BadRequestException;
import com.api.agendamentos.exceptions.CustomNotFoundException;
import com.api.agendamentos.models.Usuarios;
import com.api.agendamentos.repository.UsuariosRepository;
import com.api.agendamentos.repository.AgendamentosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UsuariosServiceTest {

    @Mock
    private UsuariosRepository usuariosRepository;

    @Mock
    private AgendamentosRepository agendamentosRepository;

    private UsuariosService usuariosService;

    @BeforeEach
    void setUp(){
        usuariosService = new UsuariosService(usuariosRepository,agendamentosRepository);
    }
    @Test
    public void GetUsuariosComSucesso() {
        Usuarios usuario1 = new Usuarios("Usuario1", "usuario1@example.com", "2000-01-01");
        Usuarios usuario2 = new Usuarios("Usuario2", "usuario2@example.com", "1990-02-02");
        List<Usuarios> usuariosMock = Arrays.asList(usuario1, usuario2);

        when(usuariosRepository.getUsuarios()).thenReturn(usuariosMock);

        List<Usuarios> result = usuariosService.getUsuarios();

        assertEquals(usuariosMock, result);
    }
    @Test
    public void GetUsuarioPorIdComSucesso() {
        Usuarios mockUsuario = new Usuarios("Teste", "teste@example.com", "1990-01-01");
        when(usuariosRepository.getUsuarioPorId("1")).thenReturn(mockUsuario);

        Usuarios result = usuariosService.getUsuarioPorId("1");

        verify(usuariosRepository, times(1)).getUsuarioPorId("1");

        assertEquals(mockUsuario, result);
    }
    @Test
    public void GetUsuarioPorIdNaoExistente() {
        when(usuariosRepository.getUsuarios()).thenReturn(Arrays.asList());
        assertThrows(CustomNotFoundException.class, () -> usuariosService.getUsuarioPorId("id_inexistente"));
    }
    @Test
    public void CriarUsuarioComSucesso() {
        when(usuariosRepository.getUsuarios()).thenReturn(new ArrayList<>(List.of()));

        Usuarios novoUsuario = usuariosService.createUsuarios("NovoUsuario", "novo_usuario@example.com", "1990-01-01");

        assertNotNull(novoUsuario.getId());
        assertEquals("NovoUsuario", novoUsuario.getNome());
        assertEquals("novo_usuario@example.com", novoUsuario.getEmail());
        assertEquals("1990-01-01", novoUsuario.getDataNascimento());
        assertNotNull(novoUsuario.getDataCriacao());
    }
    @Test
    public void CriarUsuarioComEmailJaExistente() {
        Usuarios usuarioExistente = new Usuarios("UsuarioExistente", "usuario_existente@example.com", "1980-01-01");

        when(usuariosRepository.getUsuarios()).thenReturn(Arrays.asList(usuarioExistente));

        assertThrows(BadRequestException.class, () ->
                usuariosService.createUsuarios("NovoUsuario", "usuario_existente@example.com", "1990-01-01"));
    }
    @Test
    public void AtualizarUsuarioComSucesso() {
        Usuarios usuarioExistente = new Usuarios("UsuarioExistente", "usuario_existente@example.com", "1980-01-01");
        List<Usuarios> usuariosMock = Arrays.asList(usuarioExistente);

        when(usuariosRepository.getUsuarios()).thenReturn(usuariosMock);

        Usuarios usuarioAtualizado = usuariosService.atualizarUsuarios(
                usuarioExistente.getId(),
                "NovoNome",
                "novo_email@example.com",
                "1990-01-01"
        );

        assertEquals("NovoNome", usuarioAtualizado.getNome());
        assertEquals("novo_email@example.com", usuarioAtualizado.getEmail());
        assertEquals("1990-01-01", usuarioAtualizado.getDataNascimento());
        assertNotNull(usuarioAtualizado.getDataCriacao());
    }
    @Test
    public void AtualizarUsuarioComIdInexistente() {
        when(usuariosRepository.getUsuarios()).thenReturn(Arrays.asList());

        assertThrows(CustomNotFoundException.class, () ->
                usuariosService.atualizarUsuarios("id_inexistente", "NovoNome", "novo_email@example.com", "1990-01-01"));
    }
    @Test
    public void CriarUsuarioComCampoObrigatorioAusente() {
        assertThrows(BadRequestException.class, () ->
                usuariosService.createUsuarios(null, "email@example.com", "1990-01-01"));
    }
    @Test
    public void CriarUsuarioComEspacosDesnecessarios() {
        assertThrows(BadRequestException.class, () ->
                usuariosService.createUsuarios("Nome  Com  Espacos", "email@example.com", "1990-01-01"));
    }
    @Test
    public void CriarUsuarioComFormatoDeEmailInvalido() {
        assertThrows(BadRequestException.class, () ->
                usuariosService.createUsuarios("NovoUsuario", "email_invalido", "1990-01-01"));
    }

    @Test
    public void AtualizarUsuarioComCampoObrigatorioAusente() {
        Usuarios usuarioExistente = new Usuarios("UsuarioExistente", "usuario_existente@example.com", "1980-01-01");
        List<Usuarios> usuariosMock = Arrays.asList(usuarioExistente);

        when(usuariosRepository.getUsuarios()).thenReturn(new ArrayList<>(usuariosMock));

        assertThrows(BadRequestException.class, () ->
                usuariosService.atualizarUsuarios(usuarioExistente.getId(), null, "novo_email@example.com", "1990-01-01"));
    }

    @Test
    public void AtualizarUsuarioComEmailDuplicado() {
        Usuarios usuarioExistente1 = new Usuarios("UsuarioExistente1", "usuario_existente1@example.com", "1980-01-01");
        Usuarios usuarioExistente2 = new Usuarios("UsuarioExistente2", "usuario_existente2@example.com", "1985-01-01");
        List<Usuarios> usuariosMock = Arrays.asList(usuarioExistente1, usuarioExistente2);

        when(usuariosRepository.getUsuarios()).thenReturn(new ArrayList<>(usuariosMock));

        assertThrows(BadRequestException.class, () ->
                usuariosService.atualizarUsuarios(usuarioExistente1.getId(), "NovoNome", "usuario_existente2@example.com", "1990-01-01"));
    }

}


