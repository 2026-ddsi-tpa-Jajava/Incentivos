package ar.edu.utn.dds.k3003.dominio;

import java.util.List;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("COMPLETITUD")
public class MisionCompletitud extends Mision {

    private static final int CATEGORIAS_REQUERIDAS = 3;

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
    public boolean estaCumplida(List<?> categoriasDonadas) {
        long categoriasdistintas = categoriasDonadas.stream()
                .map(Object::toString)
                .collect(Collectors.toSet())
                .size();
        return categoriasdistintas >= CATEGORIAS_REQUERIDAS;
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.COMPLETITUD; }
}
