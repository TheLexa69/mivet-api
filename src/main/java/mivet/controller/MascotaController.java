package mivet.controller;

import mivet.model.Mascota;
import mivet.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    @Autowired
    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /*
     * Endpoint para listar todas las mascotas.
     * @return Una lista de todas las mascotas.
     * Si la lista está vacía, se devuelve un código 404 Not Found.
     * Si hay mascotas, se devuelve un código 200 OK con la lista de mascotas.
*/
    @GetMapping
    public ResponseEntity<List<Mascota>> listarMascotas() {
        List<Mascota> mascotas = mascotaService.findAll();
        if (!mascotas.isEmpty()) {
            return ResponseEntity.ok(mascotas); // Devuelve 200 OK con la lista de mascotas
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 si no hay mascotas
        }
    }

    /*
     * Endpoint para obtener una lista de mascotas por el ID del propietario (Usuario).
     * @param idUsuario El ID del propietario.
     * @return Una lista de mascotas pertenecientes al propietario especificado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mascota> obtenerMascotaPorId(@PathVariable Long id) {
        Optional<Mascota> mascota = mascotaService.findById(id);
        if (mascota.isPresent()) {
            return ResponseEntity.ok(mascota.get()); // Devuelve 200 OK con la mascota encontrada
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 si no se encuentra la mascota
        }
    }
}
