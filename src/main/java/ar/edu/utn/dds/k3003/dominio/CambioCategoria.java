package ar.edu.utn.dds.k3003.dominio;

import java.time.LocalDateTime;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cambios_categoria")
public class CambioCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoriaAnterior;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoriaNueva;

    private LocalDateTime fechaHora;
    private String motivo;

    protected CambioCategoria() {
    }

    public CambioCategoria(CategoriaDonadorEnum categoriaAnterior,
                           CategoriaDonadorEnum categoriaNueva,
                           LocalDateTime fechaHora,
                           String motivo) {
        this.categoriaAnterior = categoriaAnterior;
        this.categoriaNueva = categoriaNueva;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public CategoriaDonadorEnum getCategoriaAnterior() { return categoriaAnterior; }
    public CategoriaDonadorEnum getCategoriaNueva() { return categoriaNueva; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getMotivo() { return motivo; }
}
