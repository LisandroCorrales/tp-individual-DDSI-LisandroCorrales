package ar.edu.utn.frba.dds.repository;

import ar.edu.utn.frba.dds.models.entities.Alerta;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaRepository {
    
    // Método para saber si ya se generó una alerta para un registro de clima específico
    boolean existsByRegistroDisparadorId(Long registroClimaId);
    Alerta save(Alerta alerta);
    List<Alerta> findAll();
}
