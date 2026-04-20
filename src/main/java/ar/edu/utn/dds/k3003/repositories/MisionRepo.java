package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.dominio.Mision;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

public class MisionRepo {

    private final List<Mision> misiones = new ArrayList<>();

    public void guardar(Mision mision) {
        this.misiones.add(mision);
    }

    public Mision buscar(String misionID) {
        return misiones.stream()
                .filter(m -> m.getMisionID().equals(misionID))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Misión no encontrada con ID: " + misionID));
    }

    public Mision buscarPorNombre(String nombre) {
        return misiones.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Misión no encontrada con nombre: " + nombre));
    }

    public List<Mision> todas() {
        return new ArrayList<>(misiones);
    }
}