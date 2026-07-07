package ar.edu.utn.frba.dds.models.entities;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RegistroClima {

    private Long id;
    private String ubicacion;
    private Double temperatura;
    private Double humedad;
    private LocalDateTime fechaHora;

    public RegistroClima(String ubicacion, Double temperatura, Double humedad, LocalDateTime fechaHora) {
        this.ubicacion = ubicacion;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.fechaHora = LocalDateTime.now();
    }

    public boolean esClimaCritico(Double limiteTemperatura, Double limiteHumedad) {
        if (this.temperatura == null || this.humedad == null) {
            return false;
        }
        return this.temperatura > limiteTemperatura && this.humedad > limiteHumedad;
    }

}
