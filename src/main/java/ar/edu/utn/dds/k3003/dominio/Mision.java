package ar.edu.utn.dds.k3003.dominio;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;

public abstract class Mision {

    private final String misionID;
    private String nombre;
    private String insigniaID;
    private final CategoriaDonadorEnum categoriaInicio;
    private final CategoriaDonadorEnum categoriaFin;

    public Mision(String misionID, String nombre, String insigniaID,
                  CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        this.misionID = misionID;
        this.nombre = nombre;
        this.insigniaID = insigniaID;
        this.categoriaInicio = categoriaInicio;
        this.categoriaFin = categoriaFin;
    }

    /**
     * Evalúa si el donador cumplió la misión en base a sus donaciones.
     * Cada subclase implementa su propia lógica de evaluación.
     *
     * @param donaciones lista de DonacionDTO del donador (obtenida de FachadaDonaciones)
     * @return true si la misión fue cumplida
     */
    public abstract boolean estaCumplida(List<?> donaciones);

    public String getMisionID() { return misionID; }
    public String getNombre() { return nombre; }
    public String getInsigniaID() { return insigniaID; }
    public CategoriaDonadorEnum getCategoriaInicio() { return categoriaInicio; }
    public CategoriaDonadorEnum getCategoriaFin() { return categoriaFin; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setInsigniaID(String insigniaID) { this.insigniaID = insigniaID; }
}
