// GastoRepository.java
package mivet.repository;

import mivet.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByMascotaId(Long mascotaId);
}
