package ar.edu.utn.dds.k3003.dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;

public class Donador {

    private final String donadorID;
    private CategoriaDonadorEnum categoria;
    private final List<Insignia> insignias;
    private final List<CambioCategoria> historialCategorias;
    private Mision misionActual;

    public Donador(String donadorID) {
        this.donadorID = donadorID;
        this.categoria = CategoriaDonadorEnum.OCASIONAL;
        this.insignias = new ArrayList<>();
        this.historialCategorias = new ArrayList<>();
        this.misionActual = null;
        this.historialCategorias.add(new CambioCategoria(
            null,
            this.categoria,
            LocalDateTime.now(),
            "Alta donador"
        ));
    }

    public void agregarInsignia(Insignia insignia) {
        this.insignias.add(insignia);
    }

    public void avanzarCategoria(CategoriaDonadorEnum nuevaCategoria, Mision nuevaMision) {
        CategoriaDonadorEnum anterior = this.categoria;
        this.categoria = nuevaCategoria;
        this.misionActual = nuevaMision;
        registrarCambioCategoria(anterior, nuevaCategoria, "Avance por mision");
    }

    public String getDonadorID() { return donadorID; }
    public CategoriaDonadorEnum getCategoria() { return categoria; }
    public List<Insignia> getInsignias() { return Collections.unmodifiableList(insignias); }
    public Mision getMisionActual() { return misionActual; }
    public List<CambioCategoria> getHistorialCategorias() { return Collections.unmodifiableList(historialCategorias); }

    public void setMisionActual(Mision mision) { this.misionActual = mision; }

    public void setCategoria(CategoriaDonadorEnum categoria) {
        CategoriaDonadorEnum anterior = this.categoria;
        this.categoria = categoria;
        registrarCambioCategoria(anterior, categoria, "Cambio manual");
    }

    private void registrarCambioCategoria(
        CategoriaDonadorEnum categoriaAnterior,
        CategoriaDonadorEnum categoriaNueva,
        String motivo
    ) {
        if (categoriaAnterior == categoriaNueva) {
            return;
        }

        historialCategorias.add(new CambioCategoria(
            categoriaAnterior,
            categoriaNueva,
            LocalDateTime.now(),
            motivo
        ));
    }
}
