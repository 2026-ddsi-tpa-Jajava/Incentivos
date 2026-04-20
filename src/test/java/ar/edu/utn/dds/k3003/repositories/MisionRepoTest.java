package ar.edu.utn.dds.k3003.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ar.edu.utn.dds.k3003.dominio.Mision;
import ar.edu.utn.dds.k3003.dominio.MisionCompletitud;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;

class MisionRepoTest {

  @Test
  void guardarYBuscarPorId() {
    MisionRepo repo = new MisionRepo();
    Mision mision = new MisionCompletitud("mis-1", "ins-1");

    repo.guardar(mision);

    Mision encontrada = repo.buscar("mis-1");
    Assertions.assertEquals("mis-1", encontrada.getMisionID());
    Assertions.assertEquals("Completitud", encontrada.getNombre());
  }

  @Test
  void buscarPorNombreEsCaseInsensitive() {
    MisionRepo repo = new MisionRepo();
    repo.guardar(new MisionCompletitud("mis-2", "ins-2"));

    Mision encontrada = repo.buscarPorNombre("completitud");

    Assertions.assertEquals("mis-2", encontrada.getMisionID());
  }

  @Test
  void buscarInexistenteLanzaExcepcion() {
    MisionRepo repo = new MisionRepo();

    Assertions.assertThrows(EntidadNoEncontradaException.class, () -> repo.buscar("mis-x"));
    Assertions.assertThrows(EntidadNoEncontradaException.class, () -> repo.buscarPorNombre("nada"));
  }

  @Test
  void todasDevuelveCopiaDefensiva() {
    MisionRepo repo = new MisionRepo();
    repo.guardar(new MisionCompletitud("mis-3", "ins-3"));

    List<Mision> copia = repo.todas();
    copia.clear();

    Assertions.assertEquals(1, repo.todas().size());
  }
}
