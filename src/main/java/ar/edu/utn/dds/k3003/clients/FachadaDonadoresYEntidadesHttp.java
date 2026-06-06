package ar.edu.utn.dds.k3003.clients;

import java.util.List;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EntidadBeneficaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.NecesidadMaterialDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;

public class FachadaDonadoresYEntidadesHttp implements FachadaDonadoresYEntidades {

    private final String urlBase;

    public FachadaDonadoresYEntidadesHttp(String urlBase) {
        this.urlBase = urlBase;
    }

    @Override
    public DonadorDTO buscarDonadorPorID(String donadorID) {
        String url = urlBase + "/donadores/" + donadorID;
        try {
            // ¡Acá usamos la clase de tu compañero!
            return HttpClientBuilder.get(url, DonadorDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el donador en el módulo externo: " + url, e);
        }
    }

    // --- El resto de los métodos van vacíos/null porque Incentivos no los usa ---
    @Override public DonadorDTO agregarDonador(DonadorDTO d) { return null; }
    @Override public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO e) { return null; }
    @Override public EntidadBeneficaDTO buscarEntidadPorID(String id) { return null; }
    @Override public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO n) { return null; }
    @Override public QuejaDTO agregarQueja(QuejaDTO q) { return null; }
    @Override public Boolean puedeDonar(String id) { return true; }
    @Override public List<QuejaDTO> obtenerQuejasDe(String id) { return List.of(); }
    @Override public DonadorDTO modificarEstado(String id, EstadoDonadorEnum e) { return null; }
    @Override public DonadorDTO modifcarCategoria(String id, String c) { return null; }
    @Override public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String p) { return List.of(); }
    @Override public NecesidadMaterialDTO satisfacerNecesidad(String id, Integer c) { return null; }
    @Override public DonadorStatsDTO estadisticasDonador(String id) { return null; }
    @Override public void setFachadaIncentivos(FachadaIncentivos f) {}
}