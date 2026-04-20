package ar.edu.utn.dds.k3003.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;

class InsigniaRepoTest {

  @Test
  void guardarYBuscarPorId() {
    InsigniaRepo repo = new InsigniaRepo();
    Insignia insignia = new Insignia("ins-1", "Solidaria", "Descripcion");

    repo.guardar(insignia);

    Insignia encontrada = repo.buscar("ins-1");
    Assertions.assertEquals("ins-1", encontrada.getInsigniaID());
    Assertions.assertEquals("Solidaria", encontrada.getNombre());
  }

  @Test
  void buscarPorNombreEsCaseInsensitive() {
    InsigniaRepo repo = new InsigniaRepo();
    repo.guardar(new Insignia("ins-2", "Completitud", "Desc"));

    Insignia encontrada = repo.buscarPorNombre("completitud");

    Assertions.assertEquals("ins-2", encontrada.getInsigniaID());
  }

  @Test
  void buscarInexistenteLanzaExcepcion() {
    InsigniaRepo repo = new InsigniaRepo();

    Assertions.assertThrows(EntidadNoEncontradaException.class, () -> repo.buscar("ins-x"));
    Assertions.assertThrows(EntidadNoEncontradaException.class, () -> repo.buscarPorNombre("nada"));
  }

  @Test
  void todasDevuelveCopiaDefensiva() {
    InsigniaRepo repo = new InsigniaRepo();
    repo.guardar(new Insignia("ins-3", "A", "D"));

    List<Insignia> copia = repo.todas();
    copia.clear();

    Assertions.assertEquals(1, repo.todas().size());
  }
}
