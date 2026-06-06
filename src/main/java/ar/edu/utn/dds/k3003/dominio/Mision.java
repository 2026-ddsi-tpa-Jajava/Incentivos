package ar.edu.utn.dds.k3003.dominio;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "misiones")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_mision")
public abstract class Mision {

    @Id
    private String misionID;

    private String nombre;

    @Column(name = "insignia_id")
    private String insigniaID;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoriaInicio;

    @Enumerated(EnumType.STRING)
    private CategoriaDonadorEnum categoriaFin;

    protected Mision() {}

    public Mision(String misionID, String nombre, String insigniaID,
                  CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        this.misionID = misionID;
        this.nombre = nombre;
        this.insigniaID = insigniaID;
        this.categoriaInicio = categoriaInicio;
        this.categoriaFin = categoriaFin;
    }

    public abstract boolean estaCumplida(List<?> donaciones);
    public abstract TipoMisionEnum getTipo();

    public String getMisionID() { return misionID; }
    public String getNombre() { return nombre; }
    public String getInsigniaID() { return insigniaID; }
    public CategoriaDonadorEnum getCategoriaInicio() { return categoriaInicio; }
    public CategoriaDonadorEnum getCategoriaFin() { return categoriaFin; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setInsigniaID(String insigniaID) { this.insigniaID = insigniaID; }
}
