package ar.edu.utn.dds.k3003.jobs;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.repositories.DonadorRepo;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class MisionProgressCronJob {

    private static final Logger log = LoggerFactory.getLogger(MisionProgressCronJob.class);
    private final DonadorRepo donadorRepo;
    private final Fachada fachada;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger donadoresPendientes = new AtomicInteger();

    public MisionProgressCronJob(DonadorRepo donadorRepo, Fachada fachada, MeterRegistry meterRegistry) {
        this.donadorRepo = donadorRepo;
        this.fachada = fachada;
        this.meterRegistry = meterRegistry;
        meterRegistry.gauge(
                "incentivos.cron.donadores_pendientes",
                donadoresPendientes);
    }

    @Scheduled(fixedDelayString = "${incentivos.procesamiento.intervalo-ms:60000}")
    public void procesarDonadoresConMisionAsignada() {
        meterRegistry.counter(
                "incentivos.cron.ejecuciones",
                "componente", "incentivos",
                "origen", "cron").increment();

        List<Donador> donadores = donadorRepo.findByMisionActualIsNotNull();
        donadoresPendientes.set(donadores.size());
        log.info("[CRON_INCENTIVOS] Inicio de ciclo. Donadores con misión asignada: {}", donadores.size());

        for (Donador donador : donadores) {
            String donadorID = donador.getDonadorID();
            try {
                log.info("[CRON_INCENTIVOS] Procesando donador={}", donadorID);
                fachada.procesarDonador(donadorID);
                meterRegistry.counter(
                        "incentivos.cron.donadores_procesados",
                        "componente", "incentivos",
                        "origen", "cron").increment();
                log.info("[CRON_INCENTIVOS] Donador procesado correctamente donador={}", donadorID);
            } catch (RuntimeException exception) {
                meterRegistry.counter(
                        "incentivos.procesamiento.errores",
                        "componente", "incentivos",
                        "origen", "cron",
                        "operacion", "procesar_donador").increment();
                log.error("[CRON_INCENTIVOS] Error procesando donador={}", donadorID, exception);
            }
        }

        log.info("[CRON_INCENTIVOS] Fin de ciclo.");
    }
}
