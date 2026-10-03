package ar.edu.utn.dds.k3003;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.config.ReglasMisionProperties;
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

    private static final Logger log = LoggerFactory.getLogger(Fachada.class);
    private static final LocalDate FECHA_INICIO_HISTORICA = LocalDate.of(2000, 1, 1);

    private final DonadorRepo donadorRepo;
    private final MisionRepo misionRepo;
    private final InsigniaRepo insigniaRepo;
    private final MeterRegistry meterRegistry;
    private final Counter avancesCategoria;
    private final Counter rollbacks;
    private final Timer tiempoProcesamiento;
    private final ReglasMisionProperties reglasMision;
    private final AtomicLong insigniaSeq = new AtomicLong(1);
    private final AtomicLong misionSeq = new AtomicLong(1);

    private FachadaDonaciones fachadaDonaciones;
    private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

  
    @Autowired
    public Fachada(
            DonadorRepo donadorRepo,
            MisionRepo misionRepo,
            InsigniaRepo insigniaRepo,
            MeterRegistry meterRegistry,
            ReglasMisionProperties reglasMision) {
        this.donadorRepo = donadorRepo;
        this.misionRepo = misionRepo;
        this.insigniaRepo = insigniaRepo;
        this.meterRegistry = meterRegistry;
        this.reglasMision = reglasMision;
        this.avancesCategoria = Counter.builder("incentivos.categorias.avances")
                .description("Avances de categoria producidos por misiones")
                .tag("componente", "incentivos")
                .register(meterRegistry);
        this.rollbacks = Counter.builder("incentivos.procesamiento.rollback")
                .description("Retrocesos por perdida de progreso")
                .tag("componente", "incentivos")
                .register(meterRegistry);
        this.tiempoProcesamiento = Timer.builder("incentivos.procesamiento.duracion")
                .description("Duracion del procesamiento de un donador")
                .tag("componente", "incentivos")
                .register(meterRegistry);
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
        // --- AGREGAR ESTA VALIDACIÓN ---
        if (insigniaDTO.nombre() == null || insigniaDTO.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la insignia no puede estar vacio");
        }
        if (insigniaDTO.descripcion() == null || insigniaDTO.descripcion().isBlank()) {
            throw new IllegalArgumentException("La descripcion de la insignia no puede estar vacia");
        }
        // -------------------------------
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
        donadorRepo.save(donador); 
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
        donadorRepo.save(donador); 
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

    public Donador getEstadoDonadorLocal(String donadorID) {
        return donadorRepo.findById(donadorID)
                .orElseThrow(() -> new DonadorNoEncontradoException("Donador no existe en la base local"));
    }

    @Override
    public void procesarDonador(String donadorID) {
        Timer.Sample procesamiento = Timer.start(meterRegistry);
        log.info("[INCENTIVOS] Inicio procesamiento donador={}", donadorID);
        try {
            verificarExistenciaExterna(donadorID);

            Donador donador = obtenerOCrearDonador(donadorID);
            asignarMisionInicialSiCorresponde(donador);
            Mision misionActual = donador.getMisionActual();

            if (fachadaDonaciones == null) {
                registrarErrorConfiguracion("donaciones");
                log.warn("[INCENTIVOS] No hay fachadaDonaciones configurada. Se omite procesamiento donador={}", donadorID);
                return;
            }

            List<DonacionDTO> donaciones;
            try {
                donaciones = fachadaDonaciones
                        .buscarPorDonadorYFechaInicio(donadorID, FECHA_INICIO_HISTORICA);
            } catch (RuntimeException exception) {
                registrarErrorIntegracion("donaciones", "buscar_donaciones");
                throw exception;
            }
            log.info("[INCENTIVOS] Donaciones recuperadas donador={} cantidad={}", donadorID, donaciones.size());

            if (misionActual != null) {
                evaluarMisionEnCurso(donador, misionActual, donaciones);
            } else {
                log.info("[INCENTIVOS] El donador={} no tiene misión en curso", donadorID);
            }

            evaluarPerdidaDeProgresoEnDonacionesExitosas(donador, donaciones);
            log.info("[INCENTIVOS] Fin procesamiento donador={}", donadorID);
        } finally {
            procesamiento.stop(tiempoProcesamiento);
        }
    }

    private void evaluarMisionEnCurso(Donador donador, Mision misionActual, List<DonacionDTO> donaciones) {
        String donadorID = donador.getDonadorID();
        List<String> datosEvaluacion = extraerDatosParaMision(misionActual, donaciones);
        boolean misionCumplida = misionActual.estaCumplida(datosEvaluacion, reglasMision);
        log.info("[INCENTIVOS] Evaluación misión donador={} mision={} tipo={} cumplida={}",
                donadorID, misionActual.getMisionID(), misionActual.getTipo(), misionCumplida);

        if (!misionCumplida) {
            return;
        }
        meterRegistry.counter(
                "incentivos.misiones.completadas",
                "componente", "incentivos",
                "tipo_mision", misionActual.getTipo().name()).increment();

        if (misionActual.getInsigniaID() != null && !donador.tieneInsignia(misionActual.getInsigniaID())) {
            try {
                Insignia insignia = insigniaRepo.findById(misionActual.getInsigniaID())
                        .orElseThrow(() -> new EntidadNoEncontradaException(""));
                donador.agregarInsignia(insignia);
                log.info("[INCENTIVOS] Insignia asignada por misión cumplida donador={} insignia={}",
                        donadorID, insignia.getInsigniaID());
            } catch (EntidadNoEncontradaException e) {
                log.warn("[INCENTIVOS] No se encontró insignia={} de misión={} para donador={}",
                        misionActual.getInsigniaID(), misionActual.getMisionID(), donadorID);
            }
        }

        CategoriaDonadorEnum categoriaAnterior = donador.getCategoria();
        CategoriaDonadorEnum nuevaCategoria = misionActual.getCategoriaFin();
        donador.avanzarCategoria(nuevaCategoria, null);
        if (categoriaAnterior != nuevaCategoria) {
            avancesCategoria.increment();
        }
        sincronizarCategoriaExterna(donadorID, nuevaCategoria);
        donadorRepo.save(donador);
        log.info("[INCENTIVOS] Donador avanzado de categoría donador={} nuevaCategoria={} misionActual={}",
                donadorID, nuevaCategoria, null);
    }

    private void evaluarPerdidaDeProgresoEnDonacionesExitosas(Donador donador, List<DonacionDTO> donaciones) {
        String donadorID = donador.getDonadorID();
        List<String> estadosDonaciones = donaciones.stream().map(donacion -> donacion.estado().name()).toList();
        List<Mision> misionesDonacionesExitosas = misionRepo.findAll().stream()
                .filter(mision -> TipoMisionEnum.DONACIONES_EXITOSAS.equals(mision.getTipo()))
                .toList();

        for (Mision mision : misionesDonacionesExitosas) {
            if (!donador.tieneInsignia(mision.getInsigniaID())) {
                continue;
            }
            if (mision.estaCumplida(estadosDonaciones, reglasMision)) {
                continue;
            }

            log.warn("[INCENTIVOS] Se detectó pérdida de progreso donador={} misión={} categoriaActual={}",
                    donadorID, mision.getMisionID(), donador.getCategoria());
            donador.removerInsigniaPorID(mision.getInsigniaID());
            donador.retrocederCategoria(
                    mision.getCategoriaInicio(),
                    mision,
                    "Retroceso por pérdida de progreso en misión " + mision.getNombre());
            sincronizarCategoriaExterna(donadorID, mision.getCategoriaInicio());
            donadorRepo.save(donador);
            rollbacks.increment();
            log.warn("[INCENTIVOS] Rollback aplicado donador={} categoriaNueva={} misiónReasignada={}",
                    donadorID, mision.getCategoriaInicio(), mision.getMisionID());
            return;
        }
    }

    private void sincronizarCategoriaExterna(String donadorID, CategoriaDonadorEnum categoria) {
        try {
            fachadaDonadoresYEntidades.modifcarCategoria(donadorID, categoria.name());
            log.info("[INCENTIVOS] Categoría sincronizada con Donadores y Entidades donador={} categoria={}",
                    donadorID, categoria);
        } catch (RuntimeException e) {
            registrarErrorIntegracion("donadores_entidades", "modificar_categoria");
            log.warn("[INCENTIVOS] Falló sincronización externa donador={} categoria={}. Se conserva cambio local.",
                    donadorID, categoria, e);
        }
    }

    private void verificarExistenciaExterna(String donadorID) {
        try {
            fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
        } catch (RuntimeException e) {
            registrarErrorIntegracion("donadores_entidades", "buscar_donador");
            throw new DonadorNoEncontradoException("El donador con ID " + donadorID + " no existe en el sistema de Entidades.");
        }
    }

    // Adaptado para usar JPA (findById y save)
    private Donador obtenerOCrearDonador(String donadorID) {
        return donadorRepo.findById(donadorID).orElseGet(() -> {
            return donadorRepo.save(new Donador(donadorID));
        });
    }

    private void asignarMisionInicialSiCorresponde(Donador donador) {
        if (!CategoriaDonadorEnum.OCASIONAL.equals(donador.getCategoria())
                || donador.getMisionActual() != null) {
            return;
        }

        Mision misionInicial = misionRepo.findByCategoriaInicio(CategoriaDonadorEnum.OCASIONAL)
                .stream()
                .filter(mision -> TipoMisionEnum.COMPLETITUD.equals(mision.getTipo()))
                .findFirst()
                .orElse(null);

        if (misionInicial == null) {
            registrarErrorConfiguracion("mision_inicial_completitud");
            log.warn("[INCENTIVOS] No existe la misión inicial de completitud para el donador={}",
                    donador.getDonadorID());
            return;
        }

        donador.setMisionActual(misionInicial);
        donadorRepo.save(donador);
        log.info("[INCENTIVOS] Misión inicial asignada donador={} mision={}",
                donador.getDonadorID(), misionInicial.getMisionID());
    }

    private List<String> extraerDatosParaMision(Mision mision, List<DonacionDTO> donaciones) {
        return switch (mision.getTipo()) {
            case COMPLETITUD -> donaciones.stream()
                    .map(d -> obtenerCategoriaProducto(d.productoID()))
                    .collect(Collectors.toList());
            case DONACIONES_EXITOSAS -> donaciones.stream().map(d -> d.estado().name()).collect(Collectors.toList());
            case DONACIONES_ASCENDENTES, REVOLUCION_DONADORA ->
                    donaciones.stream().map(d -> String.valueOf(d.cantidad())).collect(Collectors.toList());
        };
    }

    private String obtenerCategoriaProducto(String productoID) {
        try {
            ProductoDTO producto = fachadaDonaciones.buscarProductoPorID(productoID);
            if (producto == null || producto.subcategoriaID() == null
                    || producto.subcategoriaID().isBlank()) {
                throw new IllegalStateException("El producto " + productoID
                        + " no contiene una subcategoría válida");
            }
            SubcategoriaDTO subcategoria =
                    fachadaDonaciones.buscarSubcategoriaPorID(producto.subcategoriaID());
            if (subcategoria == null || subcategoria.categoriaID() == null
                    || subcategoria.categoriaID().isBlank()) {
                throw new IllegalStateException("La subcategoría " + producto.subcategoriaID()
                        + " no contiene una categoría válida");
            }
            return subcategoria.categoriaID();
        } catch (RuntimeException exception) {
            registrarErrorIntegracion("donaciones", "buscar_producto");
            throw exception;
        }
    }

    private void registrarErrorIntegracion(String servicio, String operacion) {
        meterRegistry.counter(
                "incentivos.integraciones.errores",
                "componente", servicio,
                "servicio", servicio,
                "operacion", operacion).increment();
    }

    private void registrarErrorConfiguracion(String dependencia) {
        meterRegistry.counter(
                "incentivos.configuracion.errores",
                "componente", "incentivos",
                "dependencia", dependencia).increment();
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
            case COMPLETITUD -> new MisionCompletitud(id, misionDTO.insigniaID(),
                misionDTO.categoriaInicio(), misionDTO.categoriaFin());
            case DONACIONES_EXITOSAS -> new MisionDonacionesExitosas(id, misionDTO.insigniaID(),
                misionDTO.categoriaInicio(), misionDTO.categoriaFin());
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
        insigniaSeq.set(1);
        misionSeq.set(1);
    }
}