package ar.edu.utn.frba.dds.models.dtos;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RegistroClimaDto {
    private Long id;
    private String ubicacion;
    private Double temperatura;
    private Double humedad;
    private LocalDateTime fechaHora;
}
