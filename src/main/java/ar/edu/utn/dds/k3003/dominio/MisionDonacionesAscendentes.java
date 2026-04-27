package ar.edu.utn.dds.k3003.dominio;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import java.util.ArrayList;
import java.util.List;

public class MisionDonacionesAscendentes extends Mision {

    private static final int DONACIONES_REQUERIDAS = 5;

    public MisionDonacionesAscendentes(
            String misionID,
            String insigniaID,
            CategoriaDonadorEnum categoriaInicio,
            CategoriaDonadorEnum categoriaFin) {
        super(misionID, "Donaciones ascendentes", insigniaID, categoriaInicio, categoriaFin);
    }

    @Override
    public boolean estaCumplida(List<?> cantidadesDonaciones) {
        if (cantidadesDonaciones.size() < DONACIONES_REQUERIDAS) {
            return false;
        }

        List<Integer> cantidades = cantidadesDonaciones.stream()
            .map(Object::toString)
            .map(Integer::parseInt)
            .toList();

        List<Integer> ultimas = new ArrayList<>(
            cantidades.subList(cantidades.size() - DONACIONES_REQUERIDAS, cantidades.size())
        );

        for (int i = 1; i < ultimas.size(); i++) {
            if (ultimas.get(i) <= ultimas.get(i - 1)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TipoMisionEnum getTipo() {
        return TipoMisionEnum.DONACIONES_ASCENDENTES;
    }
}
