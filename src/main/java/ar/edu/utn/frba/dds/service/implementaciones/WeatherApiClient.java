package ar.edu.utn.frba.dds.service.implementaciones;

import ar.edu.utn.frba.dds.models.dtos.WeatherResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherApiClient {
    private static final Logger log = LoggerFactory.getLogger(WeatherApiClient.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${climalert.weather.api.key}")
    private String apiKey;

    @Value("${climalert.weather.api.location}")
    private String location;

    @Value("${climalert.weather.api.url}")
    private String apiUrl;

    public WeatherResponseDto obtenerClimaActual() {
        try {
            // construye la URL real: http://api.weatherapi.com/v1/current.json?key=TU_KEY&q=CABA
            String url = String.format("%s?key=%s&q=%s", apiUrl, apiKey, location);
            log.info("Llamando a WeatherAPI (Real): {}", url.replace(apiKey, "******"));

            // consumo la API y Jackson se encarga de transformarlo en  DTO
            return restTemplate.getForObject(url, WeatherResponseDto.class);

        } catch (Exception e) {
            log.error("Error al conectarse con WeatherAPI real: {}", e.getMessage());
            throw new RuntimeException("No se pudo obtener el clima de la API externa", e);
        }
    }
}