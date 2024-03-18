package com.api.agendamentos.services;

import com.api.agendamentos.exceptions.BadRequestException;
import com.api.agendamentos.exceptions.CustomNotFoundException;
import com.api.agendamentos.models.Usuarios;
import com.api.agendamentos.repository.AgendamentosRepository;
import com.api.agendamentos.repository.UsuariosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuariosService {
    private final UsuariosRepository usuariosRepository;
    private final AgendamentosRepository agendamentosRepository;

    public UsuariosService(UsuariosRepository usuariosRepository, AgendamentosRepository agendamentosRepository) {
        this.usuariosRepository = usuariosRepository;
        this.agendamentosRepository = agendamentosRepository;
    }
    public List<Usuarios> getUsuarios(){

        return usuariosRepository.getUsuarios();
    }
    public Usuarios getUsuarioPorId(String id) {
        Usuarios usuario = usuariosRepository.getUsuarioPorId(id);
        if (usuario == null) {
            throw new CustomNotFoundException("Usuário não encontrado.");
        }
        return usuario;
    }

    public Usuarios createUsuarios(String nome, String email, String dataNascimento){

        if (existeUsuarioComEmail(email.trim())) {
            throw new BadRequestException("Email já cadastrado.");
        }

        validarCamposObrigatorios(nome, email, dataNascimento);
        validarNomeRegex(nome);
        validarEmailRegex(email);

        LocalDate dataNascimentoFormatada = (dataNascimento != null && !dataNascimento.trim().isEmpty()) ?
                validarFormatoDataNascimento(dataNascimento) : null;

        Usuarios novoUsuario = new Usuarios(nome.trim(), email.trim(), dataNascimentoFormatada.toString().trim());
        novoUsuario.setDataCriacao(LocalDateTime.now());

        usuariosRepository.salvarUsuarios(adicionarNovoUsuario(novoUsuario));

        return novoUsuario;
    }
    public Usuarios atualizarUsuarios(String id, String nome, String email, String dataNascimento) {

        List<Usuarios> usuarios = usuariosRepository.getUsuarios();
        Optional<Usuarios> usuarioExistente = usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();

        if (usuarioExistente.isPresent()) {
            Usuarios usuario = usuarioExistente.get();

            validarCamposObrigatorios(nome, email, dataNascimento);
            validarNomeRegex(nome);
            validarEmailRegex(email);

            if (!usuario.getEmail().equals(email.trim()) && existeUsuarioComEmail(email.trim())) {
                throw new BadRequestException("Email já cadastrado.");
            }
            if (nome != null) {
                usuario.setNome(nome.trim());
            }
            if (email != null) {
                usuario.setEmail(email.trim());
            }
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                LocalDate dataNascimentoFormatada = validarFormatoDataNascimento(dataNascimento);
                usuario.setDataNascimento(dataNascimentoFormatada.toString());
            }
            usuariosRepository.salvarUsuarios(usuarios);
            return usuario;
        } else {
            throw new CustomNotFoundException("Usuário não encontrado.");
        }
    }

    //validações 
    public List<Usuarios> adicionarNovoUsuario(Usuarios novoUsuario) {
        List<Usuarios> usuarios = usuariosRepository.getUsuarios();
        if (usuarios != null) {
            novoUsuario.setDataCriacao(LocalDateTime.now());
            usuarios.add(novoUsuario);
            return usuarios;
        } else {
            return List.of(novoUsuario);
        }
    }
    private LocalDate validarFormatoDataNascimento(String dataNascimento) {
        try {
            return LocalDate.parse(dataNascimento);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Formato inválido para a data de nascimento. Use o formato 'yyyy-MM-dd'.");
        }
    }
    private void validarCamposObrigatorios(String nome, String email, String dataNascimento) {
        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                dataNascimento == null || dataNascimento.trim().isEmpty()) {
            throw new BadRequestException("Nome, email e data de nascimento são campos obrigatórios.");
        }
    }
    private void validarNomeRegex(String nome) {
        if (nome.matches(".*\\s{2,}.*")) {
            throw new BadRequestException("Nome não pode conter espaços duplos entre as palavras.");
        }
        if (!nome.matches("^[ A-Za-z]+$")) {
            throw new BadRequestException("Nome deve conter apenas letras. Não é permitido utilizar acentos gráficos ou espaços duplos entre as palavras.");
        }
    }
    private void validarEmailRegex(String email) {
        if (email == null || !email.matches("^\\s*[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\s*$")) {
            throw new BadRequestException("Formato de email inválido e/ou contém espaços. Utilize o formato 'exemplo@exemplo.com'.");
        }
    }
    private boolean existeUsuarioComEmail(String email) {
        List<Usuarios> usuarios = usuariosRepository.getUsuarios();
        return usuarios != null && usuarios.stream().anyMatch(usuario -> usuario.getEmail().trim().equalsIgnoreCase(email));
    }
    public boolean usuarioExiste(String idUsuario) {
        List<Usuarios> usuarios = usuariosRepository.getUsuarios();
        return usuarios.stream()
                .anyMatch(usuario -> usuario.getId().equals(idUsuario));
    }
}
