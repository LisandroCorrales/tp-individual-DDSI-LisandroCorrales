package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.frba.dds.models.entities.Alerta;
import ar.edu.utn.frba.dds.models.entities.Notificador;
import ar.edu.utn.frba.dds.models.entities.RegistroClima;
import ar.edu.utn.frba.dds.repository.AlertaRepository;
import ar.edu.utn.frba.dds.repository.RegistroClimaRepository;
import ar.edu.utn.frba.dds.service.implementaciones.ProcesamientoAlertasService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ClimalertApplicationTest {

    @Autowired
    private ProcesamientoAlertasService procesamientoAlertasService;

    @MockBean
    private RegistroClimaRepository registroClimaRepository;

    @MockBean
    private AlertaRepository alertaRepository;

    @MockBean
    private Notificador notificador;

    @Test
    public void contextLoads() {
        // Verifica que levante el contexto de Spring correctamente
    }

    @Test
    public void testRegistroClima_superaUmbrales() {
        // Caso de éxito: supera umbrales (Temp > 35 y Hum > 60)
        RegistroClima critico = new RegistroClima("CABA", 36.0, 65.0, LocalDateTime.now());
        assertTrue(critico.esClimaCritico(35.0, 60.0), "Debería superar los umbrales críticos");

        // Caso alternativo: temperatura menor al umbral
        RegistroClima templado = new RegistroClima("CABA", 25.0, 70.0, LocalDateTime.now());
        assertFalse(templado.esClimaCritico(35.0, 60.0), "No debería superar si la temperatura es menor");

        // Caso alternativo: humedad menor al umbral
        RegistroClima seco = new RegistroClima("CABA", 38.0, 45.0, LocalDateTime.now());
        assertFalse(seco.esClimaCritico(35.0, 60.0), "No debería superar si la humedad es menor");
    }

    @Test
    public void testProcesamientoAlertas_generaAlertaSiEsCritico() {
        // Configurar Mock para que retorne un clima crítico
        RegistroClima climaCritico = new RegistroClima("CABA", 37.0, 75.0, LocalDateTime.now());
        climaCritico.setId(100L);
        when(registroClimaRepository.findFirstByOrderByFechaHoraDesc()).thenReturn(climaCritico);
        
        // Simular que no existe una alerta previa para este registro
        when(alertaRepository.existsByRegistroDisparadorId(100L)).thenReturn(false);
        
        // Simular el guardado de la alerta
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> {
            Alerta a = invocation.getArgument(0);
            a.setId(500L);
            return a;
        });

        // Ejecutar el procesamiento
        Alerta resultado = procesamientoAlertasService.procesarAlertas();

        // Verificar resultados
        assertNotNull(resultado, "Debería haberse generado una alerta");
        assertTrue(resultado.isNotificada(), "La alerta debería marcarse como notificada");
        verify(notificador).enviarAlerta(any(Alerta.class));
        verify(alertaRepository).save(any(Alerta.class));
    }
}
