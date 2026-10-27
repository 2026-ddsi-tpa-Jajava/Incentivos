package ar.edu.utn.dds.k3003.events;

import static ar.edu.utn.dds.k3003.config.RabbitConfig.CQRS_EVENTS_QUEUE;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.dominio.CambioCategoria;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.dominio.Mision;

@Service
public class IntegrationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(IntegrationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final boolean enabled;

    public IntegrationEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${incentivos.eventos.enabled:false}") boolean enabled) {
        this.rabbitTemplate = rabbitTemplate;
        this.enabled = enabled;
    }

    public void publishDonadorActualizado(Donador donador) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", donador.getDonadorID());
        payload.put("donadorID", donador.getDonadorID());
        payload.put("categoria", donador.getCategoria().name());
        payload.put("insignias", donador.getInsignias().stream().map(this::toInsignia).toList());
        payload.put("historialCategorias",
                donador.getHistorialCategorias().stream().map(this::toCambioCategoria).toList());
        if (donador.getMisionActual() != null) {
            payload.put("misionActual", toMision(donador.getMisionActual()));
        }
        publish("DONADOR_ACTUALIZADO", payload);
    }

    public void publishInsigniaAsignada(Donador donador, Insignia insignia) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", donador.getDonadorID() + ":" + insignia.getInsigniaID());
        payload.put("donadorID", donador.getDonadorID());
        payload.put("insigniaID", insignia.getInsigniaID());
        payload.put("nombre", insignia.getNombre());
        payload.put("descripcion", insignia.getDescripcion());
        publish("INSIGNIA_ASIGNADA", payload);
    }

    public void publishMisionAsignada(Donador donador, Mision mision) {
        Map<String, Object> payload = new HashMap<>(toMision(mision));
        payload.put("id", donador.getDonadorID() + ":" + mision.getMisionID());
        payload.put("donadorID", donador.getDonadorID());
        publish("MISION_ASIGNADA", payload);
    }

    private void publish(String type, Map<String, Object> payload) {
        if (!enabled) {
            log.debug("[INCENTIVOS] Publicación de eventos deshabilitada tipo={}", type);
            return;
        }
        Map<String, Object> event = Map.of(
                "id", UUID.randomUUID().toString(),
                "tipo", type,
                "version", 0L,
                "ocurridoEn", Instant.now().toString(),
                "payload", payload);
        try {
            rabbitTemplate.convertAndSend(CQRS_EVENTS_QUEUE, event);
            log.info("[INCENTIVOS] Evento publicado tipo={}", type);
        } catch (RuntimeException exception) {
            log.error("[INCENTIVOS] No se pudo publicar evento tipo={}", type, exception);
        }
    }

    private Map<String, Object> toInsignia(Insignia insignia) {
        return Map.of(
                "id", insignia.getInsigniaID(),
                "nombre", insignia.getNombre(),
                "descripcion", insignia.getDescripcion());
    }

    private Map<String, Object> toCambioCategoria(CambioCategoria cambio) {
        return Map.of(
                "categoriaAnterior", cambio.getCategoriaAnterior() == null
                        ? "" : cambio.getCategoriaAnterior().name(),
                "categoriaNueva", cambio.getCategoriaNueva().name(),
                "fechaHora", cambio.getFechaHora().toString(),
                "motivo", cambio.getMotivo());
    }

    private Map<String, Object> toMision(Mision mision) {
        return Map.of(
                "id", mision.getMisionID(),
                "nombre", mision.getNombre(),
                "insigniaID", mision.getInsigniaID() == null ? "" : mision.getInsigniaID(),
                "categoriaInicio", mision.getCategoriaInicio().name(),
                "categoriaFin", mision.getCategoriaFin().name(),
                "tipo", mision.getTipo().name());
    }
}
