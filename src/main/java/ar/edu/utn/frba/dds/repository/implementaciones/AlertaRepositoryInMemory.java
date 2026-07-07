package ar.edu.utn.frba.dds.repository.implementaciones;

import ar.edu.utn.frba.dds.models.entities.Alerta;
import ar.edu.utn.frba.dds.repository.AlertaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class AlertaRepositoryInMemory implements AlertaRepository {
  private final List<Alerta> alertasGeneradas = new ArrayList<>();

  @Override
  public boolean existsByRegistroDisparadorId(Long registroClimaId) {
    return alertasGeneradas.stream()
        .anyMatch(alerta -> alerta.getRegistroDisparador() != null && 
                           registroClimaId.equals(alerta.getRegistroDisparador().getId()));
  }

  @Override
  public Alerta save(Alerta alerta) {
    // Asignar ID autoincremental de mentira
    alerta.setId((long) (alertasGeneradas.size() + 1));
    alertasGeneradas.add(alerta);
    return alerta;
  }

  @Override
  public List<Alerta> findAll() {
    return alertasGeneradas;
  }
}
