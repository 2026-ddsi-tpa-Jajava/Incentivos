package ar.edu.utn.dds.k3003.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;

@RestController
public class IncentivosController {

    private final Fachada fachada;

    public IncentivosController(Fachada fachada) {
        this.fachada = fachada;
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Servicio de Incentivos activo. Usa /insignias o /misiones.");
    }

    @PostMapping("/insignias")
    public ResponseEntity<InsigniaDTO> crearInsignia(@RequestBody InsigniaDTO insigniaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fachada.agregarInsignia(insigniaDTO));
    }

    @GetMapping("/insignias")
    public ResponseEntity<?> listarInsignias() {
        return ResponseEntity.ok(fachada.getInsignias());
    }

    @GetMapping("/insignias/{id}")
    public ResponseEntity<InsigniaDTO> buscarInsignia(@PathVariable("id") String id) {
        return ResponseEntity.ok(fachada.getInsigniaPorID(id));
    }

    @PostMapping("/misiones")
    public ResponseEntity<MisionDTO> crearMision(@RequestBody MisionDTO misionDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fachada.agregarMision(misionDTO));
    }

    @GetMapping("/misiones")
    public ResponseEntity<?> listarMisiones() {
        return ResponseEntity.ok(fachada.getMisiones());
    }

    @GetMapping("/misiones/{id}")
    public ResponseEntity<MisionDTO> buscarMision(@PathVariable("id") String id) {
        return ResponseEntity.ok(fachada.getMisionPorID(id));
    }
}
