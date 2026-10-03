package ar.edu.utn.dds.k3003.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "incentivos.reglas")
public class ReglasMisionProperties {

    private int categoriasCompletitud;
    private int donacionesExitosas;
    private int donacionesAscendentes;
    private int donacionesRevolucion;
    private int cantidadMinimaRevolucion;

    public int getCategoriasCompletitud() {
        return categoriasCompletitud;
    }

    public void setCategoriasCompletitud(int categoriasCompletitud) {
        this.categoriasCompletitud = categoriasCompletitud;
    }

    public int getDonacionesExitosas() {
        return donacionesExitosas;
    }

    public void setDonacionesExitosas(int donacionesExitosas) {
        this.donacionesExitosas = donacionesExitosas;
    }

    public int getDonacionesAscendentes() {
        return donacionesAscendentes;
    }

    public void setDonacionesAscendentes(int donacionesAscendentes) {
        this.donacionesAscendentes = donacionesAscendentes;
    }

    public int getDonacionesRevolucion() {
        return donacionesRevolucion;
    }

    public void setDonacionesRevolucion(int donacionesRevolucion) {
        this.donacionesRevolucion = donacionesRevolucion;
    }

    public int getCantidadMinimaRevolucion() {
        return cantidadMinimaRevolucion;
    }

    public void setCantidadMinimaRevolucion(int cantidadMinimaRevolucion) {
        this.cantidadMinimaRevolucion = cantidadMinimaRevolucion;
    }
}
