package ar.edu.utn.frba.dds.models.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificador implements Notificador {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificador.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${climalert.email.enabled:false}")
    private boolean emailEnabled;

    private final String[] destinatarios = {
        "admin@clima.com",
        "emergencias@clima.com",
        "meteorologia@clima.com"
    };

    @Override
    public void enviarAlerta(Alerta alerta) {
        RegistroClima clima = alerta.getRegistroDisparador();
        
        String asunto = "⚠️ ALERTA METEOROLÓGICA - CONDICIONES CRÍTICAS DETECTADAS";
        String cuerpo = String.format(
            "Se han detectado condiciones climáticas críticas en la ubicación: %s\n\n" +
            "Detalle del Clima:\n" +
            "- Temperatura: %.2f °C (Umbral > 35 °C)\n" +
            "- Humedad: %.2f %% (Umbral > 60 %%)\n" +
            "- Fecha/Hora de la Alerta: %s\n\n" +
            "Este es un correo automático generado por Climalert.",
            clima.getUbicacion(),
            clima.getTemperatura(),
            clima.getHumedad(),
            alerta.getFechaHora().toString()
        );

        if (!emailEnabled) {
            log.info("[MOCK EMAIL] Simulación de correo enviado con éxito:");
            log.info("Destinatarios: {}", String.join(", ", destinatarios));
            log.info("Asunto: {}", asunto);
            log.info("Cuerpo:\n{}", cuerpo);
            return;
        }

        try {
            if (mailSender == null) {
                log.warn("JavaMailSender no está configurado, no se puede enviar el email real.");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatarios);
            message.setSubject(asunto);
            message.setText(cuerpo);
            
            mailSender.send(message);
            log.info("Correo electrónico enviado con éxito a los destinatarios.");
        } catch (Exception e) {
            log.error("Error al enviar el correo electrónico real: {}. Se omitirá el envío.", e.getMessage());
        }
    }
}
