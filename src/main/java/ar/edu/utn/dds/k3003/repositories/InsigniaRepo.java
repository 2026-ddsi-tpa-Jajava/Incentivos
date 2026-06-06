package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.dominio.Insignia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InsigniaRepo extends JpaRepository<Insignia, String> {
    Optional<Insignia> findByNombreIgnoreCase(String nombre);
}
