package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.models.entities.Alerta;
import ar.edu.utn.frba.dds.models.entities.RegistroClima;
import ar.edu.utn.frba.dds.repository.AlertaRepository;
import ar.edu.utn.frba.dds.repository.RegistroClimaRepository;
import ar.edu.utn.frba.dds.service.IProcesamientoAlertasService;
import ar.edu.utn.frba.dds.service.IRegistroClimaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/climalert/api")
public class ClimalertController {

    private final RegistroClimaRepository registroClimaRepository;
    private final AlertaRepository alertaRepository;
    private final IRegistroClimaService registroClimaService;
    private final IProcesamientoAlertasService procesamientoAlertaService;

    public ClimalertController( RegistroClimaRepository registroClimaRepository,
                                AlertaRepository alertaRepository,
                                IRegistroClimaService registroClimaService,
                                IProcesamientoAlertasService procesamientoAlertaService ) {
        this.registroClimaRepository = registroClimaRepository;
        this.alertaRepository = alertaRepository;
        this.registroClimaService = registroClimaService;
        this.procesamientoAlertaService = procesamientoAlertaService;
    }

    @GetMapping("/clima")
    public ResponseEntity<List<RegistroClima>> obtenerHistorialClima() {
        return ResponseEntity.ok(registroClimaRepository.findAll());
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<Alerta>> obtenerAlertas() {
        return ResponseEntity.ok(alertaRepository.findAll());
    }

    // para forzar la consulta del clima a WeatherAPI en este instante
    @PostMapping("/clima/forzar-monitoreo")
    public ResponseEntity<RegistroClima> forzarMonitoreo() {
        RegistroClima clima = registroClimaService.obtenerYGuardarClima();
        if (clima != null) {
            return ResponseEntity.ok(clima);
        }
        return ResponseEntity.internalServerError().build();
    }

    // para forzar el analisis y procesamiento de alertas en este instante
    @PostMapping("/alertas/forzar-procesamiento")
    public ResponseEntity<Alerta> forzarProcesamiento() {
        Alerta alerta = procesamientoAlertaService.procesarAlertas();
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }

        return ResponseEntity.noContent().build();
    }
}
