package ar.edu.utn.dds.k3003;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.dominio.Mision;
import ar.edu.utn.dds.k3003.dominio.MisionCompletitud;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesExitosas;
import ar.edu.utn.dds.k3003.exceptions.DonadorNoEncontradoException;
import ar.edu.utn.dds.k3003.exceptions.EntidadNoEncontradaException;
import ar.edu.utn.dds.k3003.repositories.DonadorRepo;
import ar.edu.utn.dds.k3003.repositories.InsigniaRepo;
import ar.edu.utn.dds.k3003.repositories.MisionRepo;

public class Fachada implements FachadaIncentivos {

    private final DonadorRepo donadorRepo;
    private final MisionRepo misionRepo;
    private final InsigniaRepo insigniaRepo;
    private final AtomicLong insigniaSeq = new AtomicLong(1);
    private final AtomicLong misionSeq = new AtomicLong(1);

    private FachadaDonaciones fachadaDonaciones;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

    public Fachada() {
        this.donadorRepo = new DonadorRepo();
        this.misionRepo = new MisionRepo();
        this.insigniaRepo = new InsigniaRepo();
    }

    @Override
    public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {
        this.fachadaDonaciones = fachadaDonaciones;
    }

    @Override
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
        insigniaRepo.guardar(insignia);
        return toInsigniaDTO(insignia);
    }

    @Override
    public MisionDTO agregarMision(MisionDTO misionDTO) {
        if (misionDTO == null || misionDTO.id() != null) {
            throw new IllegalArgumentException("La mision es invalida");
        }

        String id = "mis-" + misionSeq.getAndIncrement();
        Mision mision;

        if (CategoriaDonadorEnum.COLABORADOR.equals(misionDTO.categoriaFin())) {
            mision = new MisionCompletitud(id, misionDTO.insigniaID());
        } else {
            mision = new MisionDonacionesExitosas(id, misionDTO.insigniaID());
        }

        if (misionDTO.nombre() != null) {
            mision.setNombre(misionDTO.nombre());
        }

        misionRepo.guardar(mision);
        return toMisionDTO(mision);
    }

    @Override
    public void asignarInsigniaADonador(String donadorID, InsigniaDTO insigniaDTO) {
        verificarExistenciaExterna(donadorID);

        if (insigniaDTO == null || insigniaDTO.id() == null) {
            throw new IllegalArgumentException("La insignia es invalida");
        }

        Donador donador = obtenerOCrearDonador(donadorID);
        Insignia insignia = insigniaRepo.buscar(insigniaDTO.id());
        donador.agregarInsignia(insignia);
    }

    @Override
    public void asignarMisionADonador(String donadorID, MisionDTO misionDTO) {
        verificarExistenciaExterna(donadorID);

        if (misionDTO == null || misionDTO.id() == null) {
            throw new IllegalArgumentException("La mision es invalida");
        }

        Donador donador = obtenerOCrearDonador(donadorID);
        Mision mision = misionRepo.buscar(misionDTO.id());
        donador.setMisionActual(mision);
    }

    @Override
    public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) {
        if (!donadorRepo.existe(donadorID)) {
            verificarExistenciaExterna(donadorID);
        }
        Donador donador = donadorRepo.buscar(donadorID);
        return donador.getInsignias().stream()
            .map(this::toInsigniaDTO)
            .collect(Collectors.toList());
    }

    @Override
    public MisionDTO getMisionEnCursoDeDonador(String donadorID) {
        if (!donadorRepo.existe(donadorID)) {
            verificarExistenciaExterna(donadorID);
        }
        Donador donador = donadorRepo.buscar(donadorID);
        if (donador.getMisionActual() == null) {
            return null;
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
                Insignia insignia = insigniaRepo.buscar(misionActual.getInsigniaID());
                donador.agregarInsignia(insignia);
            } catch (EntidadNoEncontradaException e) {
                // Ignora insignia faltante en repo local.
            }

            CategoriaDonadorEnum nuevaCategoria = misionActual.getCategoriaFin();
            Mision siguienteMision = buscarMisionParaCategoria(nuevaCategoria);
            donador.avanzarCategoria(nuevaCategoria, siguienteMision);
        }
    }

    private void verificarExistenciaExterna(String donadorID) {
        try {
            fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        } catch (RuntimeException e) {
            throw new DonadorNoEncontradoException(donadorID);
        }
    }

    private Donador obtenerOCrearDonador(String donadorID) {
        if (!donadorRepo.existe(donadorID)) {
            donadorRepo.guardar(new Donador(donadorID));
        }
        return donadorRepo.buscar(donadorID);
    }

    private List<String> extraerDatosParaMision(Mision mision, List<DonacionDTO> donaciones) {
        if (mision instanceof MisionCompletitud) {
            return donaciones.stream().map(DonacionDTO::productoID).collect(Collectors.toList());
        }

        return donaciones.stream().map(d -> d.estado().name()).collect(Collectors.toList());
    }

    private Mision buscarMisionParaCategoria(CategoriaDonadorEnum categoria) {
        return misionRepo.todas().stream()
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
            CategoriaDonadorEnum.valueOf(mision.getCategoriaFin().name())
        );
    }
}
