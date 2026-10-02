package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;

public class FachadaDonacionesHttp implements FachadaDonaciones {

    private final String urlBase;

    public FachadaDonacionesHttp(String urlBase) {
        this.urlBase = urlBase;
    }

@Override
public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) {
    // Cambiamos la URL para que coincida con el /donaciones/search?donadorID=...&fechaInicio=... de Ale
    String url = urlBase + "/donaciones/search?donadorID=" + donadorID + "&fechaInicio=" + fecha.toString();
    try {
        return HttpClientBuilder.get(url, new TypeReference<List<DonacionDTO>>() {});
    } catch (Exception e) {
        throw new RuntimeException("Error al buscar las donaciones por red", e);
    }
}

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) {
        String url = urlBase + "/productos/" + productoID;
        try {
            return HttpClientBuilder.get(url, ProductoDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el producto por red por ID: " + productoID, e);
        }
    }

}