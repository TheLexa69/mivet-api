// MensajeRepository.java
package mivet.repository;

import mivet.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByUsuarioId(Long usuarioId);
}
