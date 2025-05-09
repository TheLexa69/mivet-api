package mivet.repository;

import mivet.model.Citopatologia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitopatologiaRepository extends JpaRepository<Citopatologia, Long> {
    List<Citopatologia> findByMascotaId(Long mascotaId);
}
