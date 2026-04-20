package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

public class InsigniaRepo {

    private final List<Insignia> insignias = new ArrayList<>();

    public void guardar(Insignia insignia) {
        this.insignias.add(insignia);
    }

    public Insignia buscar(String insigniaID) {
        return insignias.stream()
                .filter(i -> i.getInsigniaID().equals(insigniaID))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Insignia no encontrada con ID: " + insigniaID));
    }

    public Insignia buscarPorNombre(String nombre) {
        return insignias.stream()
                .filter(i -> i.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Insignia no encontrada con nombre: " + nombre));
    }

    public List<Insignia> todas() {
        return new ArrayList<>(insignias);
    }
}