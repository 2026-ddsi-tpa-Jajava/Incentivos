package ar.edu.utn.dds.k3003.dominio;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("DONACIONES_EXITOSAS")
public class MisionDonacionesExitosas extends Mision {

    private static final int DONACIONES_REQUERIDAS = 20;
    private static final String ESTADO_EXITOSA = "ACEPTADA";

    protected MisionDonacionesExitosas() {}

    public MisionDonacionesExitosas(String misionID, String insigniaID) {
        super(misionID, "Donaciones Exitosas", insigniaID,
                CategoriaDonadorEnum.COLABORADOR, CategoriaDonadorEnum.TRANSFORMADOR);
    }

    @Override
    public boolean estaCumplida(List<?> estadosDonaciones) {
        long exitosas = estadosDonaciones.stream()
                .map(Object::toString)
                .filter(ESTADO_EXITOSA::equals)
                .count();
        return exitosas >= DONACIONES_REQUERIDAS;
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.DONACIONES_EXITOSAS; }
}
