package ar.edu.utn.dds.k3003.dominio;

public class Insignia {

    private final String insigniaID;
    private final String nombre;
    private final String descripcion;

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