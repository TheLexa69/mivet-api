package mivet.repository;

import mivet.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaClinicaRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByMascotaId(Long mascotaId);
}
