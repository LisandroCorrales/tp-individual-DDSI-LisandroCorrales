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
    private Double humidityCritica;

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

    @Scheduled(fixedRate = 60000, initialDelay = 5000)
    public Alerta procesarAlertas() {
        log.info("Iniciando tarea programada: Analizando última información disponible del clima...");

        RegistroClima ultimoClima = registroClimaRepository.findFirstByOrderByFechaHoraDesc();

        if (ultimoClima == null) {
            log.info("No hay registros climáticos disponibles para analizar todavía.");
            return null;
        }

        log.info("Último clima registrado -> Ubicación: {}, Temp: {}°C, Hum: {}% (ID: {})",
            ultimoClima.getUbicacion(), ultimoClima.getTemperatura(), ultimoClima.getHumedad(), ultimoClima.getId());

        // Usamos los nuevos nombres de variables leídos de las properties
        if (ultimoClima.esClimaCritico(temperaturaCritica, humidityCritica)) {
            log.warn("⚠️ ¡Condiciones críticas detectadas! Temp > {}°C y Humedad > {}%", temperaturaCritica, humidityCritica);

            boolean yaExisteAlerta = alertaRepository.existsByRegistroDisparadorId(ultimoClima.getId());

            if (!yaExisteAlerta) {
                log.info("Generando nueva alerta de dominio...");
                Alerta alerta = new Alerta(ultimoClima);

                try {
                    notificador.enviarAlerta(alerta);
                    alerta.marcarComoNotificada();
                    log.info("Notificación enviada con éxito.");
                } catch (Exception e) {
                    log.error("Error al enviar la notificación: {}", e.getMessage());
                }

                Alerta guardada = alertaRepository.save(alerta);
                log.info("Alerta persistida en el repositorio en memoria. ID asignado: {}", guardada.getId());
                return guardada;
            } else {
                log.info("Ya se generó y notificó una alerta previamente para el registro de clima ID: {}", ultimoClima.getId());
            }
        } else {
            log.info("Las condiciones climáticas actuales son normales. No se requieren alertas.");
        }

        return null;
    }
}
