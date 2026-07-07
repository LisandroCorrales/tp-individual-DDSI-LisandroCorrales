package ar.edu.utn.frba.dds.repository;

import ar.edu.utn.frba.dds.models.entities.RegistroClima;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroClimaRepository {
    RegistroClima findFirstByOrderByFechaHoraDesc();
    RegistroClima save(RegistroClima registroClima);
    List<RegistroClima> findAll();
}
