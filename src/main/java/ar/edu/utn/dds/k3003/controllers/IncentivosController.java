package ar.edu.utn.dds.k3003.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.dominio.CambioCategoria;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
public class IncentivosController {

    public record InsigniaAsignacionRequest(String insigniaID) {}

    public record MisionAsignacionRequest(String misionID) {}

    public record CambioCategoriaResponse(String anterior, String nueva, String fechaHora, String motivo) {}

    public record EstadoDonadorResponse(
        String donadorID,
        String categoria,
        String misionActualID,
        List<String> insignias,
        List<CambioCategoriaResponse> historialCategorias) {}

    private final Fachada fachada;
    private final Counter insigniasCreadas;
    private final Counter misionesCreadas;
    private final Counter procesarDonadorLlamadas;
    private final Counter errores;

    public IncentivosController(Fachada fachada, MeterRegistry registry) {
        this.fachada = fachada;
        this.insigniasCreadas = Counter.builder("incentivos.insignias.creadas")
            .description("Insignias creadas").register(registry);
        this.misionesCreadas = Counter.builder("incentivos.misiones.creadas")
            .description("Misiones creadas").register(registry);
        this.procesarDonadorLlamadas = Counter.builder("incentivos.procesar_donador.llamadas")
            .description("Llamadas a procesarDonador").register(registry);
        this.errores = Counter.builder("incentivos.errores")
            .description("Errores en endpoints").register(registry);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "mensaje", "Servicio de Incentivos activo. Usa /insignias o /misiones."
        ));
    }

    @PostMapping("/insignias")
    public ResponseEntity<InsigniaDTO> crearInsignia(@RequestBody InsigniaDTO insigniaDTO) {
        insigniasCreadas.increment();
        return ResponseEntity.status(HttpStatus.CREATED).body(fachada.agregarInsignia(insigniaDTO));
    }

    @GetMapping("/insignias")
    public ResponseEntity<?> listarInsignias() {
        return ResponseEntity.ok(fachada.getInsignias());
    }

    @GetMapping("/insignias/{id:ins-[^/]+}")
    public ResponseEntity<InsigniaDTO> buscarInsignia(@PathVariable("id") String id) {
        return ResponseEntity.ok(fachada.getInsigniaPorID(id));
    }

    @PostMapping("/insignias/{donadorID}")
    public ResponseEntity<Void> asignarInsigniaADonador(
        @PathVariable("donadorID") String donadorID,
        @RequestBody InsigniaAsignacionRequest request) {
        fachada.asignarInsigniaADonador(donadorID, new InsigniaDTO(request.insigniaID(), null, null));
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/misiones/{parametro}")
public ResponseEntity<MisionDTO> buscarMisionODonador(@PathVariable("parametro") String parametro) {
    if (parametro.startsWith("mis-")) {
        return ResponseEntity.ok(fachada.getMisionPorID(parametro));
    }
    return ResponseEntity.ok(fachada.getMisionEnCursoDeDonador(parametro));
}

@GetMapping("/insignias/{parametro}")
public ResponseEntity<?> buscarInsigniaODonador(@PathVariable("parametro") String parametro) {
    if (parametro.startsWith("ins-")) {
        return ResponseEntity.ok(fachada.getInsigniaPorID(parametro));
    }
    return ResponseEntity.ok(fachada.getInsigniasDeDonador(parametro));
}

    @PostMapping("/misiones")
    public ResponseEntity<MisionDTO> crearMision(@RequestBody MisionDTO misionDTO) {
        misionesCreadas.increment();
        return ResponseEntity.status(HttpStatus.CREATED).body(fachada.agregarMision(misionDTO));
    }
    
    @GetMapping("/misiones")
    public ResponseEntity<?> listarMisiones() {
        // Asumiendo que tu interfaz Fachada tiene el método getMisiones()
        // (igual que tenés fachada.getInsignias() en la línea 61)
        return ResponseEntity.ok(fachada.getMisiones());
    }
    @PostMapping("/misiones/{donadorID}")
    public ResponseEntity<Void> asignarMisionADonador(
        @PathVariable("donadorID") String donadorID,
        @RequestBody MisionAsignacionRequest request) {
        fachada.asignarMisionADonador(donadorID, new MisionDTO(request.misionID(), null, null, null, null, null));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/procesamiento/{donadorID}")
    public ResponseEntity<Void> procesarDonador(@PathVariable("donadorID") String donadorID) {
        procesarDonadorLlamadas.increment();
        fachada.procesarDonador(donadorID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/donadores/{donadorID}/estado")
    public ResponseEntity<EstadoDonadorResponse> obtenerEstadoDonador(@PathVariable("donadorID") String donadorID) {
        Donador donador = fachada.getEstadoDonadorLocal(donadorID);
        List<String> insignias = donador.getInsignias().stream().map(i -> i.getInsigniaID()).toList();
        List<CambioCategoriaResponse> historial = donador.getHistorialCategorias().stream()
            .map(this::toCambioCategoriaResponse)
            .toList();
        String misionActualID = donador.getMisionActual() != null ? donador.getMisionActual().getMisionID() : null;

        return ResponseEntity.ok(new EstadoDonadorResponse(
            donador.getDonadorID(),
            donador.getCategoria().name(),
            misionActualID,
            insignias,
            historial
        ));
    }

    @DeleteMapping("/reset")
    public ResponseEntity<Void> limpiarTodo() {
        fachada.limpiarTodo();
        return ResponseEntity.noContent().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
        DonadorNoEncontradoException.class,
        EntidadNoEncontradaException.class
    })
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException exception) {
        errores.increment();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage() != null ? exception.getMessage() : "Recurso no encontrado"));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        errores.increment();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage() != null ? exception.getMessage() : "Petición incorrecta"));
    }

    private CambioCategoriaResponse toCambioCategoriaResponse(CambioCategoria cambio) {
        String anterior = cambio.getCategoriaAnterior() != null ? cambio.getCategoriaAnterior().name() : null;
        return new CambioCategoriaResponse(
            anterior,
            cambio.getCategoriaNueva().name(),
            cambio.getFechaHora().toString(),
            cambio.getMotivo());
    }
}