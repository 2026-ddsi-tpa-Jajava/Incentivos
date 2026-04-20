package ar.edu.utn.dds.k3003.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;

class DonadorRepoTest {

  @Test
  void guardarYBuscarPorId() {
    DonadorRepo repo = new DonadorRepo();
    Donador donador = new Donador("d-100");

    repo.guardar(donador);

    Donador encontrado = repo.buscar("d-100");
    Assertions.assertNotNull(encontrado);
    Assertions.assertEquals("d-100", encontrado.getDonadorID());
  }

  @Test
  void buscarInexistenteLanzaExcepcion() {
    DonadorRepo repo = new DonadorRepo();

    Assertions.assertThrows(DonadorNoEncontradoException.class, () -> repo.buscar("missing"));
  }

  @Test
  void existeYTodosReflejanEstadoActual() {
    DonadorRepo repo = new DonadorRepo();
    Donador primero = new Donador("d-1");
    Donador segundo = new Donador("d-2");

    repo.guardar(primero);
    repo.guardar(segundo);

    Assertions.assertTrue(repo.existe("d-1"));
    Assertions.assertTrue(repo.existe("d-2"));
    Assertions.assertFalse(repo.existe("d-3"));

    List<Donador> todos = repo.todos();
    Assertions.assertEquals(2, todos.size());
  }
}
