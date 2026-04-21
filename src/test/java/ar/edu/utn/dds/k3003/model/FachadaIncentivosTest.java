package ar.edu.utn.dds.k3003.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class FachadaIncentivosTest {

    private Fachada fachada;

    @Mock
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

    @Mock
    private FachadaDonaciones fachadaDonaciones;

    @BeforeEach
    void setUp() {
        fachada = new Fachada();
        fachada.setFachadaDonadoresYEntidades(fachadaDonadoresYEntidades);
        fachada.setFachadaDonaciones(fachadaDonaciones);
    }

    private DonadorDTO donadorValido(String donadorId) {
        return new DonadorDTO(
            donadorId,
            "Nombre",
            "Apellido",
            20,
            "mail@x.com",
            "123",
            "Dir",
            EstadoDonadorEnum.VERIFICADO,
            "OCASIONAL"
        );
    }

    @Test
    void flujoBasicoInsigniaYMision() {
        String donadorId = "d-1";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(donadorValido(donadorId));

        InsigniaDTO insignia = fachada.agregarInsignia(new InsigniaDTO(null, "Ins", "Desc"));
        MisionDTO mision = fachada.agregarMision(new MisionDTO(
                null,
                "Completitud",
                insignia.id(),
                CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR,
            TipoMisionEnum.COMPLETITUD));

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
            null,
            "Completitud",
            insignia.id(),
            CategoriaDonadorEnum.OCASIONAL,
            CategoriaDonadorEnum.COLABORADOR,
            TipoMisionEnum.COMPLETITUD
        ));

        fachada.asignarMisionADonador(donadorId, mision);

        List<DonacionDTO> donaciones = List.of(
            new DonacionDTO("d1", donadorId, "dep", "x", "p1", 1, EstadoDonacionEnum.ACEPTADA),
            new DonacionDTO("d2", donadorId, "dep", "x", "p2", 2, EstadoDonacionEnum.ACEPTADA),
            new DonacionDTO("d3", donadorId, "dep", "x", "p3", 3, EstadoDonacionEnum.ACEPTADA)
        );

        when(fachadaDonaciones.buscarPorDonadorYFechaInicio(anyString(), any())).thenReturn(donaciones);
        when(fachadaDonaciones.buscarProductoPorID("p1"))
            .thenReturn(new ProductoDTO("p1", "Prod1", "Desc", "cat-a", "id-a"));
        when(fachadaDonaciones.buscarProductoPorID("p2"))
            .thenReturn(new ProductoDTO("p2", "Prod2", "Desc", "cat-b", "id-b"));
        when(fachadaDonaciones.buscarProductoPorID("p3"))
            .thenReturn(new ProductoDTO("p3", "Prod3", "Desc", "cat-c", "id-c"));

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
            null,
            "Donaciones Exitosas",
            insignia.id(),
            CategoriaDonadorEnum.COLABORADOR,
            CategoriaDonadorEnum.TRANSFORMADOR,
            TipoMisionEnum.DONACIONES_EXITOSAS
        ));
        fachada.asignarMisionADonador(donadorId, mision);

        assertThrows(EntidadNoEncontradaException.class, () -> fachada.getInsigniasDeDonador(donadorId));
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
}
