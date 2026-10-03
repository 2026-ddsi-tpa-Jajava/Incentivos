package ar.edu.utn.dds.k3003.dominio;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.config.ReglasMisionProperties;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMPLETITUD")
public class MisionCompletitud extends Mision {

    protected MisionCompletitud() {}

    public MisionCompletitud(String misionID, String insigniaID) {
        super(misionID, "Completitud", insigniaID,
                CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR);
    }

    public MisionCompletitud(String misionID, String insigniaID,
            CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        super(misionID, "Completitud", insigniaID, categoriaInicio, categoriaFin);
    }

    @Override
    public boolean estaCumplida(List<?> categoriasDonadas, ReglasMisionProperties reglas) {
        long categoriasdistintas = categoriasDonadas.stream()
                .map(Object::toString)
                .collect(Collectors.toSet())
                .size();
        return categoriasdistintas >= reglas.getCategoriasCompletitud();
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.COMPLETITUD; }
}
