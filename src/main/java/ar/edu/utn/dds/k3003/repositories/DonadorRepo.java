package ar.edu.utn.dds.k3003.repositories;

import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;

public class DonadorRepo {

    private final List<Donador> donadores = new ArrayList<>();

    public void guardar(Donador donador) {
        donadores.add(donador);
    }

    public Donador buscar(String donadorID) {
        return donadores.stream()
                .filter(d -> d.getDonadorID().equals(donadorID))
                .findFirst()
                .orElseThrow(() -> new DonadorNoEncontradoException(donadorID));
    }

    public boolean existe(String donadorID) {
        return donadores.stream().anyMatch(d -> d.getDonadorID().equals(donadorID));
    }

    public List<Donador> todos() {
        return donadores;
    }
}
