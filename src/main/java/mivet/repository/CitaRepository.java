package mivet.repository;

import mivet.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByUsuarioId(Long usuarioId);
    List<Cita> findByMascotaId(Long mascotaId);
}
