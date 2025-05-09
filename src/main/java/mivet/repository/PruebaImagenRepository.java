// PruebaImagenRepository.java
package mivet.repository;

import mivet.model.PruebaImagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PruebaImagenRepository extends JpaRepository<PruebaImagen, Long> {
    List<PruebaImagen> findByMascotaId(Long mascotaId);
}
