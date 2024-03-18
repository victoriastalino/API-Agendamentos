package com.api.agendamentos.services;

import com.api.agendamentos.exceptions.BadRequestException;
import com.api.agendamentos.exceptions.CustomNotFoundException;
import com.api.agendamentos.exceptions.HttpMessageNotReadableException;
import com.api.agendamentos.models.Agendamentos;
import com.api.agendamentos.repository.AgendamentosRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgendamentosService {
    private final AgendamentosRepository agendamentosRepository;
    private final UsuariosService usuariosService;

    public AgendamentosService(AgendamentosRepository agendamentosRepository, UsuariosService usuariosService) {
        this.agendamentosRepository = agendamentosRepository;
        this.usuariosService = usuariosService;
    }
    public List<Agendamentos> getAgendamentos(){
        return agendamentosRepository.getAgendamentos();
    }

    public  List<Agendamentos> getAgendamentosPorUsuario(String idUsuario){
        if (!usuariosService.usuarioExiste(idUsuario)) {
            throw new CustomNotFoundException("Usuário não encontrado. Certifique-se de que o ID do usuário está correto.");
        }
        List<Agendamentos> agendamentosUsuario = agendamentosRepository.getAgendamentos().stream()
                .filter(agendamento -> agendamento.getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());

        if (agendamentosUsuario.isEmpty()) {
            throw new CustomNotFoundException("Nenhum agendamento encontrado para este usuário.");
        }

        return agendamentosUsuario;
    }
    public Agendamentos createAgendamento(String idUsuario, Agendamentos.TipoServico servico, String dataHora){

        if (!usuariosService.usuarioExiste(idUsuario)) {
            throw new CustomNotFoundException("Usuário não encontrado. Certifique-se de que o ID do usuário está correto.");
        }

        validarCamposObrigatorios(idUsuario, servico, dataHora);
        validarServico(servico);
        LocalDateTime dataHoraFormatada = validarFormatoDataHora(dataHora);
        validarHorarioFuturo(dataHoraFormatada);
        validarHorarioComercial(dataHoraFormatada);
        validarHoraCheia(dataHoraFormatada);
        validarHorarioDisponivel(dataHoraFormatada);

        Agendamentos novoAgendamento = new Agendamentos(idUsuario.trim(), servico, dataHoraFormatada.toString().trim());
        agendamentosRepository.salvarAgendamentos(adicionarNovoAgendamento(novoAgendamento));

        return novoAgendamento;
    }
    public void cancelarAgendamento(String idAgendamento) {
        List<Agendamentos> agendamentos = agendamentosRepository.getAgendamentos();

        Optional<Agendamentos> agendamentoOptional = agendamentos.stream()
                .filter(agendamento -> agendamento.getIdAgendamento().equals(idAgendamento))
                .findFirst();

        if (agendamentoOptional.isPresent()) {
            Agendamentos agendamento = agendamentoOptional.get();
            validarCancelarAgendamento(agendamento);
            agendamentosRepository.salvarAgendamentos(agendamentos);
        } else {
            throw new CustomNotFoundException("Agendamento não encontrado.");
        }
    }
    public List<LocalDateTime> getHorariosDisponiveis(LocalDate data) {
        validarData(data);

        List<Agendamentos> agendamentos = agendamentosRepository.getAgendamentos();
        List<LocalDateTime> horariosDia = obterHorariosDia(data);
        LocalDateTime agora = LocalDateTime.now();

        List<LocalDateTime> horariosDisponiveis = horariosDia.stream()
                .filter(horario ->
                        horario.isEqual(data.atStartOfDay()) || horario.isAfter(agora))
                .filter(horario -> agendamentos.stream()
                        .noneMatch(agendamento ->
                                agendamento.getDataHora().equals(horario.toString())
                                && agendamento.getStatus() == Agendamentos.statusAgendamento.AGENDADO))
                .collect(Collectors.toList());

        return horariosDisponiveis;
    }
    private List<LocalDateTime> obterHorariosDia(LocalDate data) {
        LocalDateTime inicioDia = data.atTime(9, 0);
        LocalDateTime fimDia = data.atTime(17, 0);

        List<LocalDateTime> horariosDia = new ArrayList<>();
        LocalDateTime horarioAtual = inicioDia;

        while (!horarioAtual.isAfter(fimDia)) {
            horariosDia.add(horarioAtual);
            horarioAtual = horarioAtual.plusMinutes(60);
        }
        return horariosDia;
    }

    //validações:

    private LocalDateTime validarFormatoDataHora(String dataHora) {
        try {
            return LocalDateTime.parse(dataHora, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Erro ao formatar a dataHora: " + e.getMessage());
            throw new BadRequestException("Formato inválido para a dataHora. Use o formato 'yyyy-MM-dd'T'HH:mm'.");
        }
    }
    private void validarData(LocalDate data) {
        if (data == null) {
            throw new BadRequestException("A data é obrigatória.");
        }
    }
    private void validarCamposObrigatorios(String idUsuario, Agendamentos.TipoServico servico, String dataHora) {
        if (idUsuario == null || idUsuario.trim().isEmpty() ||
                Objects.isNull(servico) ||
                dataHora == null) {
            throw new BadRequestException("Id do usuário, serviço e dataHora são campos obrigatórios.");
        }
    }
        private void validarServico(Agendamentos.TipoServico servico) {
        if (!Agendamentos.TipoServico.contains(servico.name())) {
            throw new HttpMessageNotReadableException("Serviço inválido. Escolha um serviço válido.");
        }
    }
    private void validarHorarioComercial(LocalDateTime dataHora) {
        LocalTime horaAgendamento = dataHora.toLocalTime();
        if (horaAgendamento.isBefore(LocalTime.of(9, 0)) || horaAgendamento.isAfter(LocalTime.of(17, 0))) {
            throw new BadRequestException("Os agendamentos só podem ocorrer em horário comercial, das 9h às 17h.");
        }
    }
    private void validarHoraCheia(LocalDateTime dataHora) {
        if (dataHora.getMinute() != 0 || dataHora.getSecond() != 0) {
            throw new BadRequestException("Cada atendimento tem duração de uma hora. Selecione um bloco de horário completo.");
        }
    }
        private void validarHorarioDisponivel(LocalDateTime dataHora) {
        List<Agendamentos> agendamentos = agendamentosRepository.getAgendamentos();

            for (int i = agendamentos.size() - 1; i >= 0; i--) {
                Agendamentos agendamento = agendamentos.get(i);

                LocalDateTime agendamentoDateTime = validarFormatoDataHora(agendamento.getDataHora());
                LocalDateTime agendamentoTruncado = agendamentoDateTime.truncatedTo(ChronoUnit.MINUTES);
                LocalDateTime dataHoraTruncado = dataHora.truncatedTo(ChronoUnit.MINUTES);

                if (agendamentoTruncado.equals(dataHoraTruncado) && (agendamento.getStatus() == Agendamentos.statusAgendamento.AGENDADO || agendamento.getStatus() == Agendamentos.statusAgendamento.CANCELADO)) {
                    if (agendamento.getStatus() == Agendamentos.statusAgendamento.AGENDADO) {
                        throw new BadRequestException("O horário está indisponível. Selecione outro horário.");
                    } else {
                        return;
                    }
                }
            }
        }
    private void validarHorarioFuturo(LocalDateTime dataHora) {
        LocalDateTime now = LocalDateTime.now();
        if (dataHora.isBefore(now)) {
            throw new BadRequestException("O agendamento só pode ser feito para horários futuros.");
        }
    }
    private void validarCancelarAgendamento(Agendamentos agendamento) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime agendamentoDateTime = validarFormatoDataHora(agendamento.getDataHora());

        if (agendamentoDateTime.isAfter(agora)) {
            if (agendamento.getStatus() == Agendamentos.statusAgendamento.AGENDADO) {
                agendamento.setStatus(Agendamentos.statusAgendamento.CANCELADO);
            } else {
                throw new BadRequestException("Só é possível cancelar um agendamento ativo.");
            }
        } else {
            throw new BadRequestException("Só é possível cancelar agendamentos futuros.");
        }
    }
    private List<Agendamentos> adicionarNovoAgendamento(Agendamentos novoAgendamento) {
        List<Agendamentos> agendamentos = agendamentosRepository.getAgendamentos();
        if (agendamentos != null) {
            novoAgendamento.setStatus(Agendamentos.statusAgendamento.AGENDADO);
            agendamentos.add(novoAgendamento);
            return agendamentos;
        } else {
            return List.of(novoAgendamento);
        }
    }
}
