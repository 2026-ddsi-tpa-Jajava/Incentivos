package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import ar.edu.utn.dds.k3003.dominio.Insignia;
import ar.edu.utn.dds.k3003.dominio.Mision;
import ar.edu.utn.dds.k3003.dominio.MisionCompletitud;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesAscendentes;
import ar.edu.utn.dds.k3003.dominio.MisionDonacionesExitosas;
import ar.edu.utn.dds.k3003.dominio.MisionRevolucionDonadora;
import ar.edu.utn.dds.k3003.repositories.InsigniaRepo;
import ar.edu.utn.dds.k3003.repositories.MisionRepo;
import java.util.function.Supplier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IncentivosDataInitializer implements CommandLineRunner {

    private final InsigniaRepo insigniaRepo;
    private final MisionRepo misionRepo;

    public IncentivosDataInitializer(InsigniaRepo insigniaRepo, MisionRepo misionRepo) {
        this.insigniaRepo = insigniaRepo;
        this.misionRepo = misionRepo;
    }

    @Override
    public void run(String... args) {
        inicializarDatosBase();
    }

    public void inicializarDatosBase() {
        crearInsignia("ins-completitud", "Completitud",
                "Donaciones de tres categorias distintas");
        crearInsignia("ins-donaciones-exitosas", "Donaciones exitosas",
                "Veinte donaciones aceptadas");
        crearInsignia("ins-donaciones-ascendentes", "Donaciones ascendentes",
                "Ultimas cinco donaciones con cantidades crecientes");
        crearInsignia("ins-revolucion-donadora", "Revolucion donadora",
                "Mas de diez donaciones de mas de cincuenta unidades");

        crearMision("mis-completitud", TipoMisionEnum.COMPLETITUD,
                () -> new MisionCompletitud("mis-completitud", "ins-completitud",
                        CategoriaDonadorEnum.OCASIONAL, CategoriaDonadorEnum.COLABORADOR));
        crearMision("mis-donaciones-exitosas", TipoMisionEnum.DONACIONES_EXITOSAS,
                () -> new MisionDonacionesExitosas("mis-donaciones-exitosas",
                        "ins-donaciones-exitosas", CategoriaDonadorEnum.COLABORADOR,
                        CategoriaDonadorEnum.TRANSFORMADOR));
        crearMision("mis-donaciones-ascendentes", TipoMisionEnum.DONACIONES_ASCENDENTES,
                () -> new MisionDonacionesAscendentes("mis-donaciones-ascendentes",
                        "ins-donaciones-ascendentes", CategoriaDonadorEnum.COLABORADOR,
                        CategoriaDonadorEnum.SALVADOR));
        crearMision("mis-revolucion-donadora", TipoMisionEnum.REVOLUCION_DONADORA,
                () -> new MisionRevolucionDonadora("mis-revolucion-donadora",
                        "ins-revolucion-donadora", CategoriaDonadorEnum.SALVADOR,
                        CategoriaDonadorEnum.REVOLUCIONARIO));
    }

    private void crearInsignia(String id, String nombre, String descripcion) {
        if (insigniaRepo.existsById(id)) {
            return;
        }
        insigniaRepo.save(new Insignia(id, nombre, descripcion));
    }

    private void crearMision(String id, TipoMisionEnum tipo, Supplier<Mision> factory) {
        if (misionRepo.existsById(id)
                || misionRepo.findByNombreIgnoreCase(nombreDe(tipo)).isPresent()) {
            return;
        }
        misionRepo.save(factory.get());
    }

    private String nombreDe(TipoMisionEnum tipo) {
        return switch (tipo) {
            case COMPLETITUD -> "Completitud";
            case DONACIONES_EXITOSAS -> "Donaciones Exitosas";
            case DONACIONES_ASCENDENTES -> "Donaciones ascendentes";
            case REVOLUCION_DONADORA -> "Revolucion donadora";
        };
    }
}
