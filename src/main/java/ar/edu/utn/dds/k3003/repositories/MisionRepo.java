package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.dominio.Mision;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MisionRepo extends JpaRepository<Mision, String> {
    Optional<Mision> findByNombreIgnoreCase(String nombre);
    List<Mision> findByCategoriaInicio(CategoriaDonadorEnum categoriaInicio);
}
