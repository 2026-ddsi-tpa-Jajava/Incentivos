package ar.edu.utn.dds.k3003.model;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;

@SpringBootTest(classes = ar.edu.utn.dds.k3003.app.Application.class)
class FachadaIncentivosTest {

    @Autowired
    private Fachada fachada;

    @SuppressWarnings("removal")
    @MockBean
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

    @SuppressWarnings("removal")
    @MockBean
    private FachadaDonaciones fachadaDonaciones;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        // Limpiamos la base de datos de test antes de cada prueba para que no choquen entre sí
        fachada.limpiarTodo(); 
        fachada.setFachadaDonadoresYEntidades(fachadaDonadoresYEntidades);
        fachada.setFachadaDonaciones(fachadaDonaciones);
    }

    private DonadorDTO donadorValido(String donadorId) {
        return new DonadorDTO(
            donadorId, "Nombre", "Apellido", 20, "mail@x.com", "123", "Dir",
            EstadoDonadorEnum.VERIFICADO, "OCASIONAL"
        );
    }

    @Test
    void flujoBasicoInsigniaYMision() {
        String donadorId = "d-1";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins", "Desc"));
        MisionDTO mision = fachada.agregarMision(new MisionDTO(
                null, "Completitud", insignia.id(), CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR, TipoMisionEnum.COMPLETITUD));

        fachada.asignarInsigniaADonador(donadorId, insignia);
        fachada.asignarMisionADonador(donadorId, mision);

        List<InsigniaDTO> insignias = fachada.getInsigniasDeDonador(donadorId);
        MisionDTO enCurso = fachada.getMisionEnCursoDeDonador(donadorId);

        assertEquals(1, insignias.size());
        assertNotNull(enCurso);
    }

    @Test
    void asignarInsigniaADonadorInexistenteLanzaExcepcion() {
        when(fachadaDonadoresYEntidades.buscarDonadorPorID("no-existe"))
                .thenThrow(new RuntimeException("no existe"));

        Throwable error = assertThrows(DonadorNoEncontradoException.class,
                () -> fachada.asignarInsigniaADonador("no-existe", new InsigniaDTO("i", "n", "d")));
        assertNotNull(error);
    }

    @Test
    void procesarDonadorConCompletitudCumplidaAsignaInsignia() {
        String donadorId = "d-proc";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins C", "Desc C"));
        MisionDTO mision = fachada.agregarMision(new MisionDTO(
            null, "Completitud", insignia.id(), CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR, TipoMisionEnum.COMPLETITUD
        ));

        fachada.asignarMisionADonador(donadorId, mision);

        List<DonacionDTO> donaciones = List.of(
            new DonacionDTO("d1", donadorId, "dep", "x", "p1", 1, EstadoDonacionEnum.ACEPTADA),
            new DonacionDTO("d2", donadorId, "dep", "x", "p2", 2, EstadoDonacionEnum.ACEPTADA),
            new DonacionDTO("d3", donadorId, "dep", "x", "p3", 3, EstadoDonacionEnum.ACEPTADA)
        );

        when(fachadaDonaciones.buscarPorDonadorYFechaInicio(anyString(), any())).thenReturn(donaciones);
        when(fachadaDonaciones.buscarProductoPorID("p1")).thenReturn(new ProductoDTO("p1", "Prod1", "Desc", "cat-a", "id-a"));
        when(fachadaDonaciones.buscarProductoPorID("p2")).thenReturn(new ProductoDTO("p2", "Prod2", "Desc", "cat-b", "id-b"));
        when(fachadaDonaciones.buscarProductoPorID("p3")).thenReturn(new ProductoDTO("p3", "Prod3", "Desc", "cat-c", "id-c"));

        fachada.procesarDonador(donadorId);

        List<InsigniaDTO> insignias = fachada.getInsigniasDeDonador(donadorId);
        assertEquals(1, insignias.size());
        assertEquals(insignia.id(), insignias.get(0).id());
        verify(fachadaDonaciones, times(1)).buscarPorDonadorYFechaInicio(anyString(), any());
        verify(fachadaDonaciones, times(3)).buscarProductoPorID(anyString());
    }

    @Test
    void getInsigniasDeDonadorSinInsigniasLanzaError() {
        String donadorId = "d-sin-ins";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins X", "Desc X"));
        MisionDTO mision = fachada.agregarMision(new MisionDTO(
            null, "Donaciones Exitosas", insignia.id(), CategoriaDonadorEnum.COLABORADOR,
            CategoriaDonadorEnum.TRANSFORMADOR, TipoMisionEnum.DONACIONES_EXITOSAS
        ));
        fachada.asignarMisionADonador(donadorId, mision);

        Throwable insigniasError = assertThrows(EntidadNoEncontradaException.class, () -> fachada.getInsigniasDeDonador(donadorId));
        assertNotNull(insigniasError);
    }

    @Test
    void getMisionEnCursoSinMisionLanzaError() {
        String donadorId = "d-sin-mis";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins Y", "Desc Y"));
        fachada.asignarInsigniaADonador(donadorId, insignia);

        Throwable error = assertThrows(EntidadNoEncontradaException.class,
            () -> fachada.getMisionEnCursoDeDonador(donadorId));
        assertTrue(error.getMessage().contains("mision"));
    }

    @Test
    void procesarDonadorConVeinteDonacionesExitosasAvanzaCategoriaYSincroniza() {
        String donadorId = "d-20-exitosas";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins 20", "Desc 20"));
        MisionDTO mision = fachada.agregarMision(new MisionDTO(
            null, "20 exitosas", insignia.id(), CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR, TipoMisionEnum.DONACIONES_EXITOSAS
        ));

        fachada.asignarMisionADonador(donadorId, mision);

        List<DonacionDTO> donaciones = IntStream.rangeClosed(1, 20)
            .mapToObj(i -> new DonacionDTO("d" + i, donadorId, "dep", "x", "p" + i, i, EstadoDonacionEnum.ACEPTADA))
            .toList();
        when(fachadaDonaciones.buscarPorDonadorYFechaInicio(anyString(), any())).thenReturn(donaciones);

        fachada.procesarDonador(donadorId);

        assertEquals(1, fachada.getInsigniasDeDonador(donadorId).size());
        Throwable misionError = assertThrows(EntidadNoEncontradaException.class, () -> fachada.getMisionEnCursoDeDonador(donadorId));
        assertNotNull(misionError);
        verify(fachadaDonadoresYEntidades, times(1)).modifcarCategoria(donadorId, CategoriaDonadorEnum.COLABORADOR.name());
    }

    @Test
    void procesarDonadorConOnceDonacionesMayoresA50AvanzaARevolucionario() {
        String donadorId = "d-revolucion";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins Rev", "Desc Rev"));
        MisionDTO mision = fachada.agregarMision(new MisionDTO(
            null, "Revolucion", insignia.id(), CategoriaDonadorEnum.TRANSFORMADOR,
            CategoriaDonadorEnum.REVOLUCIONARIO, TipoMisionEnum.REVOLUCION_DONADORA
        ));

        fachada.asignarMisionADonador(donadorId, mision);

        List<DonacionDTO> donaciones = IntStream.rangeClosed(1, 11)
            .mapToObj(i -> new DonacionDTO("r" + i, donadorId, "dep", "x", "p" + i, 51, EstadoDonacionEnum.ACEPTADA))
            .toList();
        when(fachadaDonaciones.buscarPorDonadorYFechaInicio(anyString(), any())).thenReturn(donaciones);

        fachada.procesarDonador(donadorId);

        verify(fachadaDonadoresYEntidades, times(1)).modifcarCategoria(donadorId, CategoriaDonadorEnum.REVOLUCIONARIO.name());
        assertEquals(1, fachada.getInsigniasDeDonador(donadorId).size());
    }
}