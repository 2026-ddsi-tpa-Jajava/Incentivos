package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.dominio.Donador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonadorRepo extends JpaRepository<Donador, String> {
}
