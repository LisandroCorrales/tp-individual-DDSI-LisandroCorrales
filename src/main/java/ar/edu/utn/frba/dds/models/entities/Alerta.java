package ar.edu.utn.frba.dds.models.entities;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Alerta {

    private Long id;
    private RegistroClima registroDisparador;
    private LocalDateTime fechaHora;
    private boolean notificada;

    public Alerta(RegistroClima registroDisparador) {
        this.registroDisparador = registroDisparador;
        this.fechaHora = LocalDateTime.now();
        this.notificada = false;
    }

    public void marcarComoNotificada() {
        this.notificada = true;
    }

}
