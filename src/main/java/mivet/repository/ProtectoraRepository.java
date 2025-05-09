package mivet.repository;

import mivet.model.Protectora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad Protectora.
 * Proporciona acceso a operaciones CRUD y consultas personalizadas si se requieren.
 */
public interface ProtectoraRepository extends JpaRepository<Protectora, Long> {
    Optional<Protectora> findByUsuarioId(Integer idUsuario);
}
