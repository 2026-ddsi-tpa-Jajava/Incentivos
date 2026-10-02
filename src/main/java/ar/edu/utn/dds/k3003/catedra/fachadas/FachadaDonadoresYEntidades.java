package ar.edu.utn.dds.k3003.catedra.fachadas;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;

public interface FachadaDonadoresYEntidades {

  DonadorDTO buscarDonadorPorID(String donadorID);

  DonadorDTO modifcarCategoria(String donadorID, String categoria);
}
