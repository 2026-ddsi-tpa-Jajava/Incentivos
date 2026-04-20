package ar.edu.utn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FachadaIncentivosTest {

    private Fachada fachada;

    @Mock
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

    @BeforeEach
    void setUp() {
        fachada = new Fachada();
        fachada.setFachadaDonadoresYEntidades(fachadaDonadoresYEntidades);
    }

    @Test
    void flujoBasicoInsigniaYMision() {
        String donadorId = "d-1";
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorId)).thenReturn(new DonadorDTO(
            donadorId, "Nombre", "Apellido", 1, "mail@x.com", "123", "Dir", null, "CAT"));

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
}
