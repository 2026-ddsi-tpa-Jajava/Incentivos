package ar.edu.utn.dds.k3003.dominio;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.config.ReglasMisionProperties;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DONACIONES_ASCENDENTES")
public class MisionDonacionesAscendentes extends Mision {

    protected MisionDonacionesAscendentes() {}

    public MisionDonacionesAscendentes(String misionID, String insigniaID,
            CategoriaDonadorEnum categoriaInicio, CategoriaDonadorEnum categoriaFin) {
        super(misionID, "Donaciones ascendentes", insigniaID, categoriaInicio, categoriaFin);
    }

    @Override
    public boolean estaCumplida(List<?> cantidadesDonaciones, ReglasMisionProperties reglas) {
        int requeridas = reglas.getDonacionesAscendentes();
        if (cantidadesDonaciones.size() < requeridas) return false;
        List<Integer> cantidades = cantidadesDonaciones.stream()
            .map(Object::toString).map(Integer::parseInt).toList();
        List<Integer> ultimas = new ArrayList<>(
            cantidades.subList(cantidades.size() - requeridas, cantidades.size()));
        for (int i = 1; i < ultimas.size(); i++) {
            if (ultimas.get(i) <= ultimas.get(i - 1)) return false;
        }
        return true;
    }

    @Override
    public TipoMisionEnum getTipo() { return TipoMisionEnum.DONACIONES_ASCENDENTES; }
}
