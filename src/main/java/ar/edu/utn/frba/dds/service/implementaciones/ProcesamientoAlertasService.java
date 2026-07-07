package ar.edu.utn.frba.dds.service.implementaciones;

import ar.edu.utn.frba.dds.models.entities.Alerta;
import ar.edu.utn.frba.dds.models.entities.Notificador;
import ar.edu.utn.frba.dds.models.entities.RegistroClima;
import ar.edu.utn.frba.dds.repository.AlertaRepository;
import ar.edu.utn.frba.dds.repository.RegistroClimaRepository;
import ar.edu.utn.frba.dds.service.IProcesamientoAlertasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ProcesamientoAlertasService implements IProcesamientoAlertasService {

    private static final Logger log = LoggerFactory.getLogger(ProcesamientoAlertasService.class);

    @Value("${climalert.alertas.temperatura-critica}")
    private Double temperaturaCritica;

    @Value("${climalert.alertas.humedad-critica}")
    private Double humedadCritica;

    private final RegistroClimaRepository registroClimaRepository;
    private final AlertaRepository alertaRepository;
    private final Notificador notificador;

    public ProcesamientoAlertasService(RegistroClimaRepository registroClimaRepository,
                                       AlertaRepository alertaRepository,
                                       Notificador notificador) {
        this.registroClimaRepository = registroClimaRepository;
        this.alertaRepository = alertaRepository;
        this.notificador = notificador;
    }

    @Override
    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    public Alerta procesarAlertas() {
        RegistroClima ultimoClima = registroClimaRepository.findFirstByOrderByFechaHoraDesc();

        if (ultimoClima == null || !ultimoClima.esClimaCritico(temperaturaCritica, humedadCritica)) {
            return null; //si no es critico termina
        }

        if (alertaRepository.existsByRegistroDisparadorId(ultimoClima.getId())) {
            return null;
        }

        log.warn("Condiciones críticas detectadas (ID: {})", ultimoClima.getId());
        Alerta alerta = new Alerta(ultimoClima);

        try {
            notificador.enviarAlerta(alerta);
            alerta.marcarComoNotificada();
        } catch (Exception e) {
            log.error("Error al enviar la notificación: {}", e.getMessage());
        }

        return alertaRepository.save(alerta);
    }
}
