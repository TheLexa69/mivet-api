package mivet.repository;

import mivet.model.Analitica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnaliticaRepository extends JpaRepository<Analitica, Long> {
    List<Analitica> findByMascotaId(Long mascotaId);
}
