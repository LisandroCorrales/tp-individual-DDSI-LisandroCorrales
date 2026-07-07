package ar.edu.utn.frba.dds.service.implementaciones;

import ar.edu.utn.frba.dds.models.entities.RegistroClima;
import ar.edu.utn.frba.dds.models.dtos.WeatherResponseDto;
import ar.edu.utn.frba.dds.repository.RegistroClimaRepository;
import ar.edu.utn.frba.dds.service.IRegistroClimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class RegistroClimaService implements IRegistroClimaService {
    private static final Logger log = LoggerFactory.getLogger(RegistroClimaService.class);

    private final WeatherApiClient weatherApiClient;
    private final RegistroClimaRepository registroClimaRepository;

    public RegistroClimaService(WeatherApiClient weatherApiClient,
                                RegistroClimaRepository registroClimaRepository) {
        this.weatherApiClient = weatherApiClient;
        this.registroClimaRepository = registroClimaRepository;
    }

    @Scheduled(fixedRate = 300000, initialDelay = 1000) // Cada 5 minutos
    public RegistroClima obtenerYGuardarClima() {
        log.info("Iniciando tarea programada: Obtener y guardar clima actual de la API...");
        try {
            WeatherResponseDto dto = weatherApiClient.obtenerClimaActual();

            if (dto == null) return null;

            RegistroClima clima = convertirAEntidad(dto);

            RegistroClima guardado = registroClimaRepository.save(clima);
            log.info("Registro de clima real guardado con éxito. ID: {}", guardado.getId());
            return guardado;

        } catch (Exception e) {
            log.error("Error en el servicio de registro de clima: {}", e.getMessage());
            return null;
        }
    }

    private RegistroClima convertirAEntidad(WeatherResponseDto dto) {
        String ubicacion = dto.getLocation().getName();
        Double temperatura = dto.getCurrent().getTemp_c();
        Double humedad = dto.getCurrent().getHumidity();

        return new RegistroClima(ubicacion, temperatura, humedad, LocalDateTime.now());
    }
}