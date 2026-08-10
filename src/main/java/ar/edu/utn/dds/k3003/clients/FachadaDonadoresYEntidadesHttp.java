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

    private record EstadoRequest(String estado) {}
    private record CategoriaRequest(String categoria) {}

    public FachadaDonadoresYEntidadesHttp(String urlBase) {
        this.urlBase = urlBase;
    }

    @Override
    public DonadorDTO buscarDonadorPorID(String donadorID) {
        // Armamos la URL 
        String url = urlBase + "/donadores/" + donadorID;
        try {
            System.out.println("Intentando buscar donador ID: '" + donadorID + "' en " + url);
            return HttpClientBuilder.get(url, DonadorDTO.class);
        } catch (Exception e) {
            System.err.println("!!! EXPLOTÓ LA BÚSQUEDA DEL DONADOR !!!");
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
    @Override
    public DonadorDTO modificarEstado(String id, EstadoDonadorEnum estado) {
        String url = urlBase + "/donadores/" + id + "/estado";
        try {
            return HttpClientBuilder.patch(url, new EstadoRequest(estado.name()), DonadorDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar estado del donador en el modulo externo", e);
        }
    }

    @Override
    public DonadorDTO modifcarCategoria(String id, String categoria) {
        String url = urlBase + "/donadores/" + id + "/categoria";
        try {
            return HttpClientBuilder.patch(url, new CategoriaRequest(categoria), DonadorDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar categoria del donador en el modulo externo", e);
        }
    }
    @Override public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String p) { return List.of(); }
    @Override public NecesidadMaterialDTO satisfacerNecesidad(String id, Integer c) { return null; }
    @Override public DonadorStatsDTO estadisticasDonador(String id) { return null; }
    @Override public void setFachadaIncentivos(FachadaIncentivos f) {}
}