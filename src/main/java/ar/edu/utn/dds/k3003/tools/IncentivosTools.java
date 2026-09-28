package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncentivosTools {

    private final Fachada fachada;

    public IncentivosTools(Fachada fachada) {
        this.fachada = fachada;
    }

    // ==========================================
    // CONSULTAS
    // ==========================================

    @Tool(name = "listar_insignias", description = "Obtiene el catálogo completo de todas las insignias disponibles en el sistema.")
    public List<InsigniaDTO> listarInsignias() {
        return fachada.getInsignias();
    }

    @Tool(name = "listar_misiones", description = "Obtiene el catálogo completo de todas las misiones disponibles en el sistema.")
    public List<MisionDTO> listarMisiones() {
        return fachada.getMisiones();
    }

    @Tool(name = "consultar_insignia", description = "Consulta los detalles de una insignia específica por su ID.")
    public InsigniaDTO consultarInsignia(
            @ToolParam(description = "ID único de la insignia, ej: 'ins-1'") String idInsignia) {
        return fachada.getInsigniaPorID(idInsignia);
    }

    @Tool(name = "consultar_mision", description = "Consulta los detalles de una misión específica por su ID.")
    public MisionDTO consultarMision(
            @ToolParam(description = "ID único de la misión, ej: 'mis-1'") String idMision) {
        return fachada.getMisionPorID(idMision);
    }

    @Tool(name = "consultar_mision_curso", description = "Consulta cuál es la misión que tiene actualmente asignada y en curso un donador.")
    public MisionDTO consultarMisionEnCurso(
            @ToolParam(description = "ID del donador, ej: 'd-1'", required = true) String donadorID) {
        return fachada.getMisionEnCursoDeDonador(donadorID);
    }

    @Tool(name = "consultar_insignias_donador", description = "Devuelve la lista de insignias que el donador ya ganó y tiene asignadas.")
    public List<InsigniaDTO> consultarInsignias(
            @ToolParam(description = "ID del donador, ej: 'd-1'", required = true) String donadorID) {
        return fachada.getInsigniasDeDonador(donadorID);
    }

    // ==========================================
    // OPERACIONES
    // ==========================================

    @Tool(name = "crear_insignia", description = "Crea una nueva insignia en el sistema.")
    public InsigniaDTO crearInsignia(
            @ToolParam(description = "Nombre visible de la insignia") String nombre,
            @ToolParam(description = "Descripción de la insignia") String descripcion) {
        return fachada.agregarInsignia(new InsigniaDTO(null, nombre, descripcion));
    }

    @Tool(name = "crear_mision", description = "Crea una nueva misión en el sistema de incentivos.")
    public MisionDTO crearMision(
            @ToolParam(description = "Nombre de la misión") String nombre,
            @ToolParam(description = "ID de la insignia asociada, ej: 'ins-1'") String insigniaID,
            @ToolParam(description = "Categoría inicial: OCASIONAL, COLABORADOR, TRANSFORMADOR, SALVADOR, REVOLUCIONARIO") String categoriaInicio,
            @ToolParam(description = "Categoría final: OCASIONAL, COLABORADOR, TRANSFORMADOR, SALVADOR, REVOLUCIONARIO") String categoriaFin,
            @ToolParam(description = "Tipo de misión: COMPLETITUD, DONACIONES_EXITOSAS, DONACIONES_ASCENDENTES, REVOLUCION_DONADORA") String tipo) {
        
        return fachada.agregarMision(new MisionDTO(
                null, 
                nombre, 
                insigniaID, 
                CategoriaDonadorEnum.valueOf(categoriaInicio.toUpperCase()), 
                CategoriaDonadorEnum.valueOf(categoriaFin.toUpperCase()), 
                TipoMisionEnum.valueOf(tipo.toUpperCase())
        ));
    }

    @Tool(name = "asignar_insignia_a_donador", description = "Asigna manualmente una insignia a un donador específico.")
    public String asignarInsigniaADonador(
            @ToolParam(description = "ID del donador, ej: 'd-1'") String donadorID,
            @ToolParam(description = "ID de la insignia a asignar, ej: 'ins-1'") String insigniaID) {
        fachada.asignarInsigniaADonador(donadorID, new InsigniaDTO(insigniaID, null, null));
        return "Insignia " + insigniaID + " asignada al donador " + donadorID + " correctamente.";
    }

    @Tool(name = "asignar_mision_a_donador", description = "Asigna una misión a un donador para que pase a estar 'en curso'.")
    public String asignarMisionADonador(
            @ToolParam(description = "ID del donador, ej: 'd-1'") String donadorID,
            @ToolParam(description = "ID de la misión a asignar, ej: 'mis-1'") String misionID) {
        fachada.asignarMisionADonador(donadorID, new MisionDTO(misionID, null, null, null, null, null));
        return "Misión " + misionID + " asignada al donador " + donadorID + " correctamente.";
    }

    @Tool(name = "procesar_donador", description = "Fuerza el procesamiento de un donador para evaluar si cumplió su misión, asignar insignias o avanzar de categoría.")
    public String procesarDonador(
            @ToolParam(description = "ID del donador a procesar, ej: 'd-1'", required = true) String donadorID) {
        fachada.procesarDonador(donadorID);
        return "Donador " + donadorID + " procesado correctamente. Revisar logs o consultar estado para ver los cambios.";
    }
}