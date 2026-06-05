package ar.edu.utn.dds.k3003.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "insignias")
public class Insignia {

    @Id
    private String insigniaID;
    private String nombre;
    private String descripcion;

    protected Insignia() {
    }

    public Insignia(String insigniaID, String nombre, String descripcion) {
        this.insigniaID = insigniaID;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getInsigniaID() {
        return insigniaID;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}