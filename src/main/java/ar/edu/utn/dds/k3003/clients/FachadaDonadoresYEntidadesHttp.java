package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;

public class FachadaDonadoresYEntidadesHttp implements FachadaDonadoresYEntidades {

    private final String urlBase;

    private record CategoriaRequest(String categoria) {}

    public FachadaDonadoresYEntidadesHttp(String urlBase) {
        this.urlBase = urlBase;
    }

    @Override
    public DonadorDTO buscarDonadorPorID(String donadorID) {
        String url = urlBase + "/donadores/" + donadorID;
        try {
            return HttpClientBuilder.get(url, DonadorDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el donador en el módulo externo: " + url, e);
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
}