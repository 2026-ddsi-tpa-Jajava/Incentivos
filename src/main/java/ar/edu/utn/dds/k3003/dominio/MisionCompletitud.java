package ar.edu.utn.dds.k3003.dominio;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Misión "Completitud": se cumple al realizar donaciones en 3 categorías distintas.
 * Avanza al donador de OCASIONAL a COLABORADOR.
 *
 * Cada elemento de la lista se castea a un objeto con getCategoriaID() o similar.
 * Como los DTOs son de otro módulo, usamos Object y accedemos por reflexión
 * o pedimos que el caller pase los datos ya procesados.
 * Para esta entrega usamos DonacionDTO de la cátedra vía el campo categoriaProducto.
 */
public class MisionCompletitud extends Mision {

    private static final int CATEGORIAS_REQUERIDAS = 3;

    public MisionCompletitud(String misionID, String insigniaID) {
        super(misionID, "Completitud", insigniaID,
                CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR);
    }

    /**
     * La lista recibida son objetos DonacionDTO de la cátedra.
     * Usamos el productoID como proxy de categoría: extraemos el categoriaID
     * del producto. Como DonacionDTO tiene productoID (String), le pedimos
     * al caller que pase una lista de Strings con las categorías de cada donación.
     *
     * En la práctica, el caller (Fachada) ya extrae las categorías antes de llamar aquí.
     */
    @Override
    public boolean estaCumplida(List<?> categoriasDonadas) {
        long categoriasdistintas = categoriasDonadas.stream()
                .map(Object::toString)
                .collect(Collectors.toSet())
                .size();
        return categoriasdistintas >= CATEGORIAS_REQUERIDAS;
    }
}
