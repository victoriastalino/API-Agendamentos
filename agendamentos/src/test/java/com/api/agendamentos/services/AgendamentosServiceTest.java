package com.api.agendamentos.services;

import com.api.agendamentos.exceptions.BadRequestException;
import com.api.agendamentos.exceptions.CustomNotFoundException;
import com.api.agendamentos.models.Agendamentos;
import com.api.agendamentos.repository.AgendamentosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;;

public class AgendamentosServiceTest {
    @Mock
    private AgendamentosRepository agendamentosRepository;
    @Mock
    private UsuariosService usuariosService;
    private AgendamentosService agendamentosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        agendamentosService = new AgendamentosService(agendamentosRepository,usuariosService);
    }
    @Test
    void GetAgendamentosComSucesso() {
        List<Agendamentos> mockAgendamentos = Arrays.asList(
                new Agendamentos("1", Agendamentos.TipoServico.SERVICO1, "2022-01-01T10:00"),
                new Agendamentos("2", Agendamentos.TipoServico.SERVICO2, "2022-01-02T14:00")
        );
        when(agendamentosRepository.getAgendamentos()).thenReturn(mockAgendamentos);

        List<Agendamentos> result = agendamentosService.getAgendamentos();

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getIdUsuario());
        assertEquals("2", result.get(1).getIdUsuario());
    }
    @Test
    void GetAgendamentosPorUsuarioComSucesso() {
        when(usuariosService.usuarioExiste(Mockito.anyString())).thenReturn(true);

        List<Agendamentos> agendamentos = Arrays.asList(
                new Agendamentos("1", Agendamentos.TipoServico.SERVICO1, "2022-01-01T10:00"),
                new Agendamentos("1", Agendamentos.TipoServico.SERVICO2, "2022-01-01T14:00")
        );

        when(agendamentosRepository.getAgendamentos()).thenReturn(agendamentos);

        List<Agendamentos> result = agendamentosService.getAgendamentosPorUsuario("1");

        assertEquals(agendamentos, result);
    }
    @Test
    void GetAgendamentosPorUsuarioComUsuarioInexistente() {
        List<Agendamentos> mockAgendamentos = Arrays.asList(
                new Agendamentos("1", Agendamentos.TipoServico.SERVICO1, "2022-01-01T10:00"),
                new Agendamentos("2", Agendamentos.TipoServico.SERVICO2, "2022-01-02T14:00")
        );
        when(agendamentosRepository.getAgendamentos()).thenReturn(mockAgendamentos);

        assertThrows(CustomNotFoundException.class, () -> agendamentosService.getAgendamentosPorUsuario("3"));
    }

    @Test
    void GetAgendamentosPorUsuarioPossuindoNenhumAgendamento() {
        when(usuariosService.usuarioExiste(Mockito.anyString())).thenReturn(true);
        when(agendamentosRepository.getAgendamentos()).thenReturn(new ArrayList<>());

        assertThrows(CustomNotFoundException.class, () -> agendamentosService.getAgendamentosPorUsuario("usuarioSemAgendamentos"));
    }

    @Test
    void CriarAgendamentoComSucesso() {
        when(usuariosService.usuarioExiste(Mockito.anyString())).thenReturn(true);

        LocalDateTime dataHoraValida = LocalDateTime.of(2024,1,20,9,0);

        when(agendamentosRepository.getAgendamentos()).thenReturn(new ArrayList<>());

        ArgumentCaptor<List<Agendamentos>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.doNothing().when(agendamentosRepository).salvarAgendamentos(argumentCaptor.capture());

        Agendamentos agendamentoValido = agendamentosService.createAgendamento("1", Agendamentos.TipoServico.SERVICO1, dataHoraValida.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));

        Mockito.verify(agendamentosRepository, Mockito.times(1)).salvarAgendamentos(Mockito.anyList());

        assertEquals(Agendamentos.statusAgendamento.AGENDADO, agendamentoValido.getStatus());
    }
    @Test
    void CriarAgendamentoComHorarioPassadoLancaException() {
        when(usuariosService.usuarioExiste(Mockito.anyString())).thenReturn(true);

        LocalDateTime dataHoraPassada = LocalDateTime.of(2023,1,20,9,0);

        when(agendamentosRepository.getAgendamentos()).thenReturn(new ArrayList<>());
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                agendamentosService.createAgendamento("1", Agendamentos.TipoServico.SERVICO1, dataHoraPassada.toString()));
        assertEquals("O agendamento só pode ser feito para horários futuros.", exception.getMessage());
    }
    @Test
    void CriarAgendamentoForaDoHorarioComercial() {
        when(usuariosService.usuarioExiste(Mockito.anyString())).thenReturn(true);

        LocalDateTime dataHoraForaExpediente = LocalDateTime.of(2024,1,20,6,0);

        when(agendamentosRepository.getAgendamentos()).thenReturn(new ArrayList<>());
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                agendamentosService.createAgendamento("1", Agendamentos.TipoServico.SERVICO1, dataHoraForaExpediente.toString()));
        assertEquals("Os agendamentos só podem ocorrer em horário comercial, das 9h às 17h.", exception.getMessage());
    }
    @Test
    void CancelarAgendamentoComSucesso() {
        List<Agendamentos> agendamentos = new ArrayList<>();
        LocalDateTime dataHoraValida = LocalDateTime.now().plusDays(1);
        Agendamentos agendamentoAgendado = new Agendamentos("1", Agendamentos.TipoServico.SERVICO1, dataHoraValida.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        agendamentos.add(agendamentoAgendado);
        when(agendamentosRepository.getAgendamentos()).thenReturn(agendamentos);

        agendamentosService.cancelarAgendamento(agendamentoAgendado.getIdAgendamento());

        assertEquals(Agendamentos.statusAgendamento.CANCELADO, agendamentoAgendado.getStatus());
    }
    @Test
    void CancelarAgendamentoInexistente() {
        when(agendamentosRepository.getAgendamentos()).thenReturn(new ArrayList<>());

        assertThrows(CustomNotFoundException.class, () -> agendamentosService.cancelarAgendamento("idAgendamentoInexistente"));
    }
    @Test
    void CancelarAgendamentoJaCancelado() {
        List<Agendamentos> agendamentosCancelados = new ArrayList<>();
        Agendamentos agendamentoCancelado = new Agendamentos("1", Agendamentos.TipoServico.SERVICO1, LocalDateTime.now().plusDays(1).toString());
        agendamentoCancelado.setStatus(Agendamentos.statusAgendamento.CANCELADO);
        agendamentosCancelados.add(agendamentoCancelado);
        when(agendamentosRepository.getAgendamentos()).thenReturn(agendamentosCancelados);

        assertThrows(BadRequestException.class, () -> agendamentosService.cancelarAgendamento(agendamentoCancelado.getIdAgendamento()));
    }
    @Test
    void GetHorariosDisponiveisComSucesso() {
        when(agendamentosRepository.getAgendamentos()).thenReturn(new ArrayList<>());
        LocalDate data = LocalDate.now().plusDays(1);

        List<LocalDateTime> horariosDisponiveis = agendamentosService.getHorariosDisponiveis(data);

        assertFalse(horariosDisponiveis.isEmpty());
    }

}
