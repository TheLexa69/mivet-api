package mivet.repository;

import mivet.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByUsuarioId(Long usuarioId);

    @Query("SELECT m FROM Mascota m WHERE m.usuario.id = :userId")
    List<Mascota> findAllByUsuarioId(@Param("userId") Long userId);

}
