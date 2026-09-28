package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncentivosTools {

    private final Fachada fachada;

    public IncentivosTools(Fachada fachada) {
        this.fachada = fachada;
    }

    @McpTool(name = "procesar_donador", description = "Fuerza el procesamiento de un donador para evaluar si cumplió su misión, asignar insignias o avanzar de categoría.")
    public String procesarDonador(
            @McpToolParam(description = "ID del donador a procesar (ejemplo: d-1)", required = true) String donadorID) {
        fachada.procesarDonador(donadorID);
        return "Donador " + donadorID + " procesado correctamente. Revisar logs o consultar estado para ver los cambios.";
    }

    @McpTool(name = "consultar_mision_curso", description = "Consulta cuál es la misión que tiene actualmente asignada y en curso un donador.")
    public MisionDTO consultarMisionEnCurso(
            @McpToolParam(description = "ID del donador", required = true) String donadorID) {
        return fachada.getMisionEnCursoDeDonador(donadorID);
    }

    @McpTool(name = "consultar_insignias_donador", description = "Devuelve la lista de insignias que el donador ya ganó y tiene asignadas.")
    public List<InsigniaDTO> consultarInsignias(
            @McpToolParam(description = "ID del donador", required = true) String donadorID) {
        return fachada.getInsigniasDeDonador(donadorID);
    }
}