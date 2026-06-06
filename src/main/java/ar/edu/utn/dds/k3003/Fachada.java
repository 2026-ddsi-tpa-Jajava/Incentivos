package ar.edu.utn.dds.k3003;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.dominio.Mision;
import ar.edu.utn.dds.k3003.dominio.MisionCompletitud;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesAscendentes;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesExitosas;
import ar.edu.utn.dds.k3003.dominio.MisionRevolucionDonadora;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;
import ar.edu.utn.dds.k3003.repositories.DonadorRepo;
import ar.edu.utn.dds.k3003.repositories.InsigniaRepo;
import ar.edu.utn.dds.k3003.repositories.MisionRepo;

@Service
public class Fachada implements FachadaIncentivos {

    private final DonadorRepo donadorRepo;
    private final MisionRepo misionRepo;
    private final InsigniaRepo insigniaRepo;
    private final AtomicLong insigniaSeq = new AtomicLong(1);
    private final AtomicLong misionSeq = new AtomicLong(1);

    private FachadaDonaciones fachadaDonaciones;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

    // ¡Acá inyectamos los repositorios de Spring Data JPA!
    @Autowired
    public Fachada(DonadorRepo donadorRepo, MisionRepo misionRepo, InsigniaRepo insigniaRepo) {
        this.donadorRepo = donadorRepo;
        this.misionRepo = misionRepo;
        this.insigniaRepo = insigniaRepo;
    }

    @Override
    @Autowired
    public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {
        this.fachadaDonaciones = fachadaDonaciones;
    }

    @Override
    @Autowired
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
        this.fachadaDonadoresYEntidades = fachadaDonadoresYEntidades;
    }

    @Override
    public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO) {
        if (insigniaDTO == null || insigniaDTO.id() != null) {
            throw new IllegalArgumentException("La insignia es invalida");
        }

        String id = "ins-" + insigniaSeq.getAndIncrement();
        Insignia insignia = new Insignia(id, insigniaDTO.nombre(), insigniaDTO.descripcion());
        insigniaRepo.save(insignia); // Usamos save() de JPA
        return toInsigniaDTO(insignia);
    }

    @Override
    public MisionDTO agregarMision(MisionDTO misionDTO) {
        if (misionDTO == null || misionDTO.id() != null) {
            throw new IllegalArgumentException("La mision es invalida");
        }

        String id = "mis-" + misionSeq.getAndIncrement();
        TipoMisionEnum tipo = resolverTipoMision(misionDTO);
        Mision mision = construirMision(id, misionDTO, tipo);

        if (misionDTO.nombre() != null) {
            mision.setNombre(misionDTO.nombre());
        }

        misionRepo.save(mision); // Usamos save() de JPA
        return toMisionDTO(mision);
    }

    public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
        if (donadorDTO == null) {
            throw new IllegalArgumentException("El donador es invalido");
        }
        return fachadaDonadoresYEntidades.agregarDonador(donadorDTO);
    }

    public DonadorDTO buscarDonadorPorID(String donadorID) {
        if (donadorID == null || donadorID.isBlank()) {
            throw new IllegalArgumentException("El donadorID es invalido");
        }
        return fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    }

    public List<InsigniaDTO> getInsignias() {
        return insigniaRepo.findAll().stream().map(this::toInsigniaDTO).toList(); // Usamos findAll()
    }

    public InsigniaDTO getInsigniaPorID(String insigniaID) {
        Insignia insignia = insigniaRepo.findById(insigniaID)
                .orElseThrow(() -> new EntidadNoEncontradaException("Insignia no encontrada")); // Usamos findById()
        return toInsigniaDTO(insignia);
    }

    public List<MisionDTO> getMisiones() {
        return misionRepo.findAll().stream().map(this::toMisionDTO).toList(); // Usamos findAll()
    }

    public MisionDTO getMisionPorID(String misionID) {
        Mision mision = misionRepo.findById(misionID)
                .orElseThrow(() -> new EntidadNoEncontradaException("Mision no encontrada")); // Usamos findById()
        return toMisionDTO(mision);
    }

    @Override
    public void asignarInsigniaADonador(String donadorID, InsigniaDTO insigniaDTO) {
        verificarExistenciaExterna(donadorID);

        if (insigniaDTO == null || insigniaDTO.id() == null) {
            throw new IllegalArgumentException("La insignia es invalida");
        }

        Donador donador = obtenerOCrearDonador(donadorID);
        Insignia insignia = insigniaRepo.findById(insigniaDTO.id())
                .orElseThrow(() -> new EntidadNoEncontradaException("Insignia no encontrada en base de datos"));

        donador.agregarInsignia(insignia);
        donadorRepo.save(donador); // ¡Guardamos el donador actualizado en la DB!
    }

    @Override
    public void asignarMisionADonador(String donadorID, MisionDTO misionDTO) {
        verificarExistenciaExterna(donadorID);

        if (misionDTO == null || misionDTO.id() == null) {
            throw new IllegalArgumentException("La mision es invalida");
        }

        Donador donador = obtenerOCrearDonador(donadorID);
        Mision mision = misionRepo.findById(misionDTO.id())
                .orElseThrow(() -> new EntidadNoEncontradaException("Mision no encontrada en base de datos"));

        donador.setMisionActual(mision);
        donadorRepo.save(donador); // ¡Guardamos el donador actualizado!
    }

    @Override
    public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) {
        Donador donador = donadorRepo.findById(donadorID)
                .orElseThrow(() -> new DonadorNoEncontradoException("Donador no existe en la base local"));

        if (donador.getInsignias().isEmpty()) {
            throw new EntidadNoEncontradaException("El donador no tiene insignias asignadas");
        }
        return donador.getInsignias().stream()
                .map(this::toInsigniaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MisionDTO getMisionEnCursoDeDonador(String donadorID) {
        Donador donador = donadorRepo.findById(donadorID)
                .orElseThrow(() -> new DonadorNoEncontradoException("Donador no existe en la base local"));

        if (donador.getMisionActual() == null) {
            throw new EntidadNoEncontradaException("El donador no tiene mision en curso");
        }
        return toMisionDTO(donador.getMisionActual());
    }

    @Override
    public void procesarDonador(String donadorID) {
        verificarExistenciaExterna(donadorID);

        Donador donador = obtenerOCrearDonador(donadorID);
        Mision misionActual = donador.getMisionActual();

        if (misionActual == null || fachadaDonaciones == null) {
            return;
        }

        List<DonacionDTO> donaciones = fachadaDonaciones
                .buscarPorDonadorYFechaInicio(donadorID, LocalDate.of(2000, 1, 1));

        List<String> datosEvaluacion = extraerDatosParaMision(misionActual, donaciones);

        if (misionActual.estaCumplida(datosEvaluacion)) {
            try {
                Insignia insignia = insigniaRepo.findById(misionActual.getInsigniaID())
                        .orElseThrow(() -> new EntidadNoEncontradaException(""));
                donador.agregarInsignia(insignia);
            } catch (EntidadNoEncontradaException e) {
                // Ignora insignia faltante en repo local.
            }

            CategoriaDonadorEnum nuevaCategoria = misionActual.getCategoriaFin();
            Mision siguienteMision = buscarMisionParaCategoria(nuevaCategoria);
            donador.avanzarCategoria(nuevaCategoria, siguienteMision);

            donadorRepo.save(donador); // ¡Guardamos en DB que subió de nivel!
        }
    }

    private void verificarExistenciaExterna(String donadorID) {
        try {
            fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        } catch (RuntimeException e) {
            throw new DonadorNoEncontradoException(donadorID);
        }
    }

    // Adaptado para usar JPA (findById y save)
    private Donador obtenerOCrearDonador(String donadorID) {
        return donadorRepo.findById(donadorID).orElseGet(() -> {
            return donadorRepo.save(new Donador(donadorID));
        });
    }

    private List<String> extraerDatosParaMision(Mision mision, List<DonacionDTO> donaciones) {
        return switch (mision.getTipo()) {
            case COMPLETITUD -> donaciones.stream()
                    .map(d -> fachadaDonaciones.buscarProductoPorID(d.productoID()).categoriaID())
                    .collect(Collectors.toList());
            case DONACIONES_EXITOSAS -> donaciones.stream().map(d -> d.estado().name()).collect(Collectors.toList());
            case DONACIONES_ASCENDENTES, REVOLUCION_DONADORA ->
                    donaciones.stream().map(d -> String.valueOf(d.cantidad())).collect(Collectors.toList());
        };
    }

    private Mision buscarMisionParaCategoria(CategoriaDonadorEnum categoria) {
        return misionRepo.findAll().stream()
                .filter(m -> m.getCategoriaInicio().equals(categoria))
                .findFirst()
                .orElse(null);
    }

    private InsigniaDTO toInsigniaDTO(Insignia insignia) {
        return new InsigniaDTO(insignia.getInsigniaID(), insignia.getNombre(), insignia.getDescripcion());
    }

    private MisionDTO toMisionDTO(Mision mision) {
        return new MisionDTO(
                mision.getMisionID(),
                mision.getNombre(),
                mision.getInsigniaID(),
                CategoriaDonadorEnum.valueOf(mision.getCategoriaInicio().name()),
                CategoriaDonadorEnum.valueOf(mision.getCategoriaFin().name()),
                mision.getTipo()
        );
    }

    private TipoMisionEnum resolverTipoMision(MisionDTO misionDTO) {
        if (misionDTO.tipo() != null) {
            return misionDTO.tipo();
        }

        if (CategoriaDonadorEnum.COLABORADOR.equals(misionDTO.categoriaFin())) {
            return TipoMisionEnum.COMPLETITUD;
        }
        return TipoMisionEnum.DONACIONES_EXITOSAS;
    }

    private Mision construirMision(String id, MisionDTO misionDTO, TipoMisionEnum tipo) {
        return switch (tipo) {
            case COMPLETITUD -> new MisionCompletitud(id, misionDTO.insigniaID());
            case DONACIONES_EXITOSAS -> new MisionDonacionesExitosas(id, misionDTO.insigniaID());
            case DONACIONES_ASCENDENTES ->
                    new MisionDonacionesAscendentes(id, misionDTO.insigniaID(),
                            misionDTO.categoriaInicio(), misionDTO.categoriaFin());
            case REVOLUCION_DONADORA ->
                    new MisionRevolucionDonadora(id, misionDTO.insigniaID(),
                            misionDTO.categoriaInicio(), misionDTO.categoriaFin());
        };
    }

    // Método agregado para limpiar la DB (Necesario para tu Controller)
    public void limpiarTodo() {
        donadorRepo.deleteAll();
        misionRepo.deleteAll();
        insigniaRepo.deleteAll();
    }
}