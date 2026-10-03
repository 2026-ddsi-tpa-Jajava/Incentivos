package ar.edu.utn.dds.k3003.dominio;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.config.ReglasMisionProperties;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REVOLUCION_DONADORA")
public class MisionRevolucionDonadora extends Mision {

    protected MisionRevolucionDonadora() {}

    public MisionRevolucionDonadora(String misionID, String insigniaID,
            CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        super(misionID, "Revolucion donadora", insigniaID, categoriaInicio, categoriaFin);
    }

    @Override
    public boolean estaCumplida(List<?> cantidadesDonaciones) {
        return estaCumplida(cantidadesDonaciones, null);
    }

    @Override
    public boolean estaCumplida(List<?> cantidadesDonaciones, ReglasMisionProperties reglas) {
        int cantidadMinima = reglas != null ? reglas.getCantidadMinimaRevolucion() : 50;
        int donacionesRequeridas = reglas != null ? reglas.getDonacionesRevolucion() : 10;
        long validas = cantidadesDonaciones.stream()
            .map(Object::toString).map(Integer::parseInt)
            .filter(c -> c > cantidadMinima).count();
        return validas > donacionesRequeridas;
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.REVOLUCION_DONADORA; }
}
