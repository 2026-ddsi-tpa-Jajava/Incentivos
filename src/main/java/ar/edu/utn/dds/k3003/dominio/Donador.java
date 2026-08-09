package ar.edu.utn.dds.k3003.dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "donadores")
public class Donador {

    @Id
    private String donadorID;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoria;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "donador_insignias",
        joinColumns = @JoinColumn(name = "donador_id"),
        inverseJoinColumns = @JoinColumn(name = "insignia_id"))
    private List<Insignia> insignias = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "donador_id")
    private List<CambioCategoria> historialCategorias = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mision_actual_id")
    private Mision misionActual;

    protected Donador() {}

    public Donador(String donadorID) {
        this.donadorID = donadorID;
        this.categoria = CategoriaDonadorEnum.OCASIONAL;
        this.historialCategorias.add(new CambioCategoria(null, this.categoria, LocalDateTime.now(), "Alta donador"));
    }

    public void agregarInsignia(Insignia insignia) { this.insignias.add(insignia); }

    public boolean tieneInsignia(String insigniaID) {
        return insignias.stream().anyMatch(insignia -> insignia.getInsigniaID().equals(insigniaID));
    }

    public void removerInsigniaPorID(String insigniaID) {
        insignias.removeIf(insignia -> insignia.getInsigniaID().equals(insigniaID));
    }

    public void avanzarCategoria(CategoriaDonadorEnum nuevaCategoria, Mision nuevaMision) {
        CategoriaDonadorEnum anterior = this.categoria;
        this.categoria = nuevaCategoria;
        this.misionActual = nuevaMision;
        registrarCambioCategoria(anterior, nuevaCategoria, "Avance por mision");
    }

    public void retrocederCategoria(CategoriaDonadorEnum nuevaCategoria, Mision nuevaMision, String motivo) {
        CategoriaDonadorEnum anterior = this.categoria;
        this.categoria = nuevaCategoria;
        this.misionActual = nuevaMision;
        registrarCambioCategoria(anterior, nuevaCategoria, motivo);
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

    private void registrarCambioCategoria(CategoriaDonadorEnum categoriaAnterior,
            CategoriaDonadorEnum categoriaNueva, String motivo) {
        if (categoriaAnterior == categoriaNueva) return;
        historialCategorias.add(new CambioCategoria(categoriaAnterior, categoriaNueva, LocalDateTime.now(), motivo));
    }
}
