package ar.edu.utn.dds.k3003.dominio;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.config.ReglasMisionProperties;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DONACIONES_EXITOSAS")
public class MisionDonacionesExitosas extends Mision {

    private static final String ESTADO_EXITOSA = "ACEPTADA";

    protected MisionDonacionesExitosas() {}

    public MisionDonacionesExitosas(String misionID, String insigniaID) {
        super(misionID, "Donaciones Exitosas", insigniaID,
                CategoriaDonadorEnum.COLABORADOR, CategoriaDonadorEnum.TRANSFORMADOR);
    }

    public MisionDonacionesExitosas(String misionID, String insigniaID,
            CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        super(misionID, "Donaciones Exitosas", insigniaID, categoriaInicio, categoriaFin);
    }

    @Override
    public boolean estaCumplida(List<?> estadosDonaciones, ReglasMisionProperties reglas) {
        long exitosas = estadosDonaciones.stream()
                .map(Object::toString)
                .filter(ESTADO_EXITOSA::equals)
                .count();
        return exitosas >= reglas.getDonacionesExitosas();
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.DONACIONES_EXITOSAS; }
}
