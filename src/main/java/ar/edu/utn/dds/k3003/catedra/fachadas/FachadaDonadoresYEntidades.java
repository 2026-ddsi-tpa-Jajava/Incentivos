package ar.edu.utn.dds.k3003.catedra.fachadas;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import java.util.List;

public interface FachadaDonadoresYEntidades {

  DonadorDTO buscarDonadorPorID(String donadorID);

  List<DonadorDTO> listarDonadores();

  DonadorDTO modifcarCategoria(String donadorID, String categoria);
}
