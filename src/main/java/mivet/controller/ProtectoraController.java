package mivet.controller;

import mivet.dto.MascotaDTO;
import mivet.model.Mascota;
import mivet.model.Usuario;
import mivet.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mivet.util.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/protectora")
public class ProtectoraController {

    private final MascotaService mascotaService;

    @Autowired
    public ProtectoraController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping("/mascotas")
    public ResponseEntity<List<Mascota>> listarMascotasDeProtectora(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.isTokenValid(token)) {
            System.out.println(JwtUtil.isTokenValid(token));
            return ResponseEntity.status(401).build();
        }
        Long idProtectora = JwtUtil.extractUserId(token);

        List<Mascota> mascotas = mascotaService.findByUsuarioId(idProtectora);
        return ResponseEntity.ok(mascotas);
    }


    @PostMapping("/mascotas")
    public ResponseEntity<Mascota> darDeAltaMascota(@RequestHeader("Authorization") String token,
                                                    @RequestBody MascotaDTO dto) {
        Long idProtectora = JwtUtil.extractUserId(token);

        Usuario usuario = new Usuario();
        usuario.setId(Math.toIntExact(idProtectora));

        Mascota nuevaMascota = new Mascota();
        nuevaMascota.setNombre(dto.getNombre());
        nuevaMascota.setRaza(dto.getRaza());
        nuevaMascota.setTipo(dto.getTipo());
        nuevaMascota.setFechaNac(dto.getFechaNac());
        nuevaMascota.setDescripcion(dto.getDescripcion());
        nuevaMascota.setUsuario(usuario);

        Mascota guardada = mascotaService.guardar(nuevaMascota);
        return ResponseEntity.ok(guardada);
    }

}
