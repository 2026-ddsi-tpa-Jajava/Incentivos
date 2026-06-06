package ar.edu.utn.dds.k3003.dominio;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("REVOLUCION_DONADORA")
public class MisionRevolucionDonadora extends Mision {

    private static final int CANTIDAD_MINIMA_DONACION = 50;
    private static final int DONACIONES_REQUERIDAS = 10;

    protected MisionRevolucionDonadora() {}

    public MisionRevolucionDonadora(String misionID, String insigniaID,
            CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        super(misionID, "Revolucion donadora", insigniaID, categoriaInicio, categoriaFin);
    }

    @Override
    public boolean estaCumplida(List<?> cantidadesDonaciones) {
        long validas = cantidadesDonaciones.stream()
            .map(Object::toString).map(Integer::parseInt)
            .filter(c -> c >= CANTIDAD_MINIMA_DONACION).count();
        return validas > DONACIONES_REQUERIDAS;
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.REVOLUCION_DONADORA; }
}
