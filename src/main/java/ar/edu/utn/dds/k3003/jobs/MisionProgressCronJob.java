package ar.edu.utn.dds.k3003.jobs;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.dominio.Donador;
import ar.edu.utn.dds.k3003.repositories.DonadorRepo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MisionProgressCronJob {

    private static final Logger log = LoggerFactory.getLogger(MisionProgressCronJob.class);
    private final DonadorRepo donadorRepo;
    private final Fachada fachada;

    public MisionProgressCronJob(DonadorRepo donadorRepo, Fachada fachada) {
        this.donadorRepo = donadorRepo;
        this.fachada = fachada;
    }

    @Scheduled(fixedDelayString = "${incentivos.procesamiento.intervalo-ms:60000}")
    public void procesarDonadoresConMisionAsignada() {
        List<Donador> donadores = donadorRepo.findByMisionActualIsNotNull();
        log.info("[CRON_INCENTIVOS] Inicio de ciclo. Donadores con misión asignada: {}", donadores.size());

        for (Donador donador : donadores) {
            String donadorID = donador.getDonadorID();
            try {
                log.info("[CRON_INCENTIVOS] Procesando donador={}", donadorID);
                fachada.procesarDonador(donadorID);
                log.info("[CRON_INCENTIVOS] Donador procesado correctamente donador={}", donadorID);
            } catch (RuntimeException exception) {
                log.error("[CRON_INCENTIVOS] Error procesando donador={}", donadorID, exception);
            }
        }

        log.info("[CRON_INCENTIVOS] Fin de ciclo.");
    }
}
