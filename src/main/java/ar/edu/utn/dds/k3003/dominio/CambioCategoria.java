package ar.edu.utn.dds.k3003.dominio;

import java.time.LocalDateTime;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;

public record CambioCategoria(
    CategoriaDonadorEnum categoriaAnterior,
    CategoriaDonadorEnum categoriaNueva,
    LocalDateTime fechaHora,
    String motivo
) {
}
