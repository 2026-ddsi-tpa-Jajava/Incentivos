package ar.edu.utn.dds.k3003.tools;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
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

    @Tool(name = "procesar_donador", description = "Fuerza el procesamiento de un donador para evaluar si cumplió su misión, asignar insignias o avanzar de categoría.")
    public String procesarDonador(
            @ToolParam(description = "ID del donador a procesar (ejemplo: d-1)", required = true) String donadorID) {
        fachada.procesarDonador(donadorID);
        return "Donador " + donadorID + " procesado correctamente. Revisar logs o consultar estado para ver los cambios.";
    }

    @Tool(name = "consultar_mision_curso", description = "Consulta cuál es la misión que tiene actualmente asignada y en curso un donador.")
    public MisionDTO consultarMisionEnCurso(
            @ToolParam(description = "ID del donador", required = true) String donadorID) {
        return fachada.getMisionEnCursoDeDonador(donadorID);
    }

    @Tool(name = "consultar_insignias_donador", description = "Devuelve la lista de insignias que el donador ya ganó y tiene asignadas.")
    public List<InsigniaDTO> consultarInsignias(
            @ToolParam(description = "ID del donador", required = true) String donadorID) {
        return fachada.getInsigniasDeDonador(donadorID);
    }
}