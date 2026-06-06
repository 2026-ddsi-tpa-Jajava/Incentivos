package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
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
        // Armamos la URL pasándole el filtro por Query Params que espera el módulo de Donaciones
        String url = urlBase + "/donaciones?donadorID=" + donadorID;
        try {
            // ¡Acá usamos el TypeReference del builder de tu compañero para el mapeo de la Lista!
            return HttpClientBuilder.get(url, new TypeReference<List<DonacionDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar las donaciones por red del donador: " + donadorID, e);
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

    // --- El resto de métodos devuelven null/vacío porque Incentivos no los ejecuta ---
    @Override public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) { return null; }
    @Override public DonacionDTO buscarDonacionPorID(String donacionID) { return null; }
    @Override public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado) { return null; }
    @Override public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) { return null; }
    @Override public ProductoDTO agregarProducto(ProductoDTO productoDTO) { return null; }
    @Override public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) { return null; }
    @Override public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) { return null; }
    @Override public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades f) {}
    @Override public void setFachadaLogistica(FachadaLogistica f) {}
}