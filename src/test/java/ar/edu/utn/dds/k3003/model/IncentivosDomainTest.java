package ar.edu.utn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.config.ReglasMisionProperties;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.dominio.MisionCompletitud;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesAscendentes;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesExitosas;
import java.util.List;
import org.junit.jupiter.api.Test;

class IncentivosDomainTest {

    private final ReglasMisionProperties reglas = reglasPredeterminadas();

    private ReglasMisionProperties reglasPredeterminadas() {
        ReglasMisionProperties propiedades = new ReglasMisionProperties();
        propiedades.setCategoriasCompletitud(3);
        propiedades.setDonacionesExitosas(20);
        propiedades.setDonacionesAscendentes(5);
        propiedades.setDonacionesRevolucion(10);
        propiedades.setCantidadMinimaRevolucion(50);
        return propiedades;
    }

    @Test
    void completitudRequiereTresCategoriasDistintas() {
        MisionCompletitud mision = new MisionCompletitud(
                "mis-1", "ins-1", CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR);

        assertTrue(mision.estaCumplida(List.of("cat-a", "cat-b", "cat-c"), reglas));
        assertFalse(mision.estaCumplida(List.of("cat-a", "cat-a", "cat-b"), reglas));
    }

    @Test
    void donacionesExitosasRequiereVeinteAceptadas() {
        MisionDonacionesExitosas mision = new MisionDonacionesExitosas(
                "mis-1", "ins-1", CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR);

        assertFalse(mision.estaCumplida(List.of("ACEPTADA", "INGRESADA"), reglas));
        assertTrue(mision.estaCumplida(List.of(
                "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA",
                "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA",
                "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA",
                "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA", "ACEPTADA"), reglas));
    }

    @Test
    void donacionesAscendentesEvaluaLasUltimasCinco() {
        MisionDonacionesAscendentes mision = new MisionDonacionesAscendentes(
                "mis-1", "ins-1", CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR);

        assertTrue(mision.estaCumplida(List.of("1", "2", "3", "4", "5"), reglas));
        assertFalse(mision.estaCumplida(List.of("1", "2", "3", "5", "4"), reglas));
    }

    @Test
    void donadorRegistraInsigniaYAvanceDeCategoria() {
        Donador donador = new Donador("don-1");
        Insignia insignia = new Insignia("ins-1", "Primera", "Primera insignia");

        donador.agregarInsignia(insignia);
        donador.avanzarCategoria(CategoriaDonadorEnum.COLABORADOR, null);

        assertTrue(donador.tieneInsignia("ins-1"));
        assertEquals(CategoriaDonadorEnum.COLABORADOR, donador.getCategoria());
        assertEquals(2, donador.getHistorialCategorias().size());
    }
}
