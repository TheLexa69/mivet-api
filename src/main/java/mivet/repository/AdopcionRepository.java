package mivet.repository;

import mivet.enums.EstadoAdopcion;
import mivet.model.Adopcion;
import mivet.model.Mascota;
import mivet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdopcionRepository extends JpaRepository<Adopcion, Long> {
    List<Adopcion> findByEstado(EstadoAdopcion estado);
    boolean existsByMascotaAndUsuario(Mascota mascota, Usuario usuario);
    boolean existsByMascotaAndUsuarioAndEstado(Mascota mascota, Usuario usuario, EstadoAdopcion estado);

}
