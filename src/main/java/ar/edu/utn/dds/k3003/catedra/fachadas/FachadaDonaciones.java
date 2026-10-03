package ar.edu.utn.dds.k3003.catedra.fachadas;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import java.time.LocalDate;
import java.util.List;

public interface FachadaDonaciones {

  List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha);

  ProductoDTO buscarProductoPorID(String productoID);

  SubcategoriaDTO buscarSubcategoriaPorID(String subcategoriaID);
}
