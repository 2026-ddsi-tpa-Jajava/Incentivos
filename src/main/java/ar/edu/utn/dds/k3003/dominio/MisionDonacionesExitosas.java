package ar.edu.utn.dds.k3003.dominio;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import java.util.List;

/**
 * Misión "Donaciones Exitosas": se cumple al alcanzar 20 donaciones
 * que hayan sido recibidas correctamente (sin quejas).
 * Avanza al donador de COLABORADOR a TRANSFORMADOR.
 *
 * El caller pasa una lista de Strings con el estado de cada donación.
 * Solo se cuentan las que tienen estado "ACEPTADA".
 */
public class MisionDonacionesExitosas extends Mision {

    private static final int DONACIONES_REQUERIDAS = 20;
    private static final String ESTADO_EXITOSA = "ACEPTADA";

    public MisionDonacionesExitosas(String misionID, String insigniaID) {
        super(misionID, "Donaciones Exitosas", insigniaID,
                CategoriaDonadorEnum.COLABORADOR, CategoriaDonadorEnum.TRANSFORMADOR);
    }

    /**
     * La lista recibida son Strings con el estado de cada donación del donador.
     * Se cuentan únicamente las ACEPTADA (sin quejas).
     */
    @Override
    public boolean estaCumplida(List<?> estadosDonaciones) {
        long exitosas = estadosDonaciones.stream()
                .map(Object::toString)
                .filter(ESTADO_EXITOSA::equals)
                .count();
        return exitosas >= DONACIONES_REQUERIDAS;
    }

    @Override
    public TipoMisionEnum getTipo() {
        return TipoMisionEnum.DONACIONES_EXITOSAS;
    }
}
