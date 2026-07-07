package ar.edu.utn.frba.dds.repository.implementaciones;

import ar.edu.utn.frba.dds.models.entities.RegistroClima;
import ar.edu.utn.frba.dds.repository.RegistroClimaRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistroClimaRepositoryInMemory implements RegistroClimaRepository {
  private final List<RegistroClima> registrosClima = new ArrayList<>();

  @Override
  public RegistroClima findFirstByOrderByFechaHoraDesc() {
    if (registrosClima.isEmpty()) {
      return null;
    }
    return registrosClima.get(registrosClima.size() - 1);
  }

  @Override
  public RegistroClima save(RegistroClima registroClima) {
    registrosClima.add(registroClima);
    return registroClima;
  }

  @Override
  public List<RegistroClima> findAll() {
    return registrosClima;
  }

}
