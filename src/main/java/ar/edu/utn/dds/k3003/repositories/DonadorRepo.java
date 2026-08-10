package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.dominio.Donador;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonadorRepo extends JpaRepository<Donador, String> {
    List<Donador> findByMisionActualIsNotNull();
}
