package ar.edu.utn.frba.dds.models.dtos;

import lombok.Data;

@Data
public class WeatherResponseDto {
    private LocationDto location;
    private CurrentDto current;
}
