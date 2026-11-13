package com.sistema.chamada.infrastructure.adapters.out.messaging;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.OutboxEventoEntity;
import com.sistema.chamada.infrastructure.adapters.out.persistence.repository.SpringDataOutboxRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutboxRelayTest {

    private OutboxEventoEntity pendente() {
        OutboxEventoEntity e = new OutboxEventoEntity();
        e.setId(1L);
        e.setTipo("CHAMADA_FINALIZADA");
        e.setChave("CHAMADA_FINALIZADA-1");
        e.setPayload("{\"chamadaId\":1,\"turmaId\":10}");
        e.setCriadoEm(LocalDateTime.now());
        return e;
    }

    @SuppressWarnings("unchecked")
    private ObjectProvider<ServiceBusSenderClient> provider(ServiceBusSenderClient sender) {
        ObjectProvider<ServiceBusSenderClient> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(sender);
        return provider;
    }

    @Test
    void enviaPendenteEMarcaComoEnviado() {
        SpringDataOutboxRepository repo = mock(SpringDataOutboxRepository.class);
        ServiceBusSenderClient sender = mock(ServiceBusSenderClient.class);
        OutboxEventoEntity evento = pendente();
        when(repo.findTop50ByEnviadoEmIsNullOrderByIdAsc()).thenReturn(List.of(evento));

        new OutboxRelay(repo, provider(sender)).enviarPendentes();

        ArgumentCaptor<ServiceBusMessage> captor = ArgumentCaptor.forClass(ServiceBusMessage.class);
        verify(sender).sendMessage(captor.capture());
        assertThat(captor.getValue().getMessageId()).isEqualTo("CHAMADA_FINALIZADA-1");
        assertThat(captor.getValue().getSubject()).isEqualTo("ChamadaFinalizada");
        assertThat(captor.getValue().getApplicationProperties().get("eventType")).isEqualTo("CHAMADA_FINALIZADA");
        assertThat(captor.getValue().getBody().toString()).contains("\"chamadaId\":1");
        assertThat(evento.getEnviadoEm()).isNotNull();
    }

    @Test
    void falhaNoEnvioMantemPendente() {
        SpringDataOutboxRepository repo = mock(SpringDataOutboxRepository.class);
        ServiceBusSenderClient sender = mock(ServiceBusSenderClient.class);
        OutboxEventoEntity evento = pendente();
        when(repo.findTop50ByEnviadoEmIsNullOrderByIdAsc()).thenReturn(List.of(evento));
        doThrow(new RuntimeException("Service Bus fora do ar")).when(sender).sendMessage(any(ServiceBusMessage.class));

        new OutboxRelay(repo, provider(sender)).enviarPendentes();

        assertThat(evento.getEnviadoEm()).isNull();
        assertThat(evento.getTentativas()).isEqualTo(1);
        assertThat(evento.getUltimoErro()).contains("fora do ar");
    }
}
