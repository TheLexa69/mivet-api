package mivet.controller;

import mivet.dto.MascotaDTO;
import mivet.dto.ProtectoraDTO;
import mivet.model.Mascota;
import mivet.model.Protectora;
import mivet.model.Usuario;
import mivet.service.MascotaService;
import mivet.service.ProtectoraService;
import mivet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mivet.util.JwtUtil;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/protectora")
public class ProtectoraController {

    private final MascotaService mascotaService;
    private final ProtectoraService protectoraService;
    private final UsuarioService usuarioService;

    @Autowired
    public ProtectoraController(MascotaService mascotaService, ProtectoraService protectoraService, UsuarioService usuarioService) {
        this.mascotaService = mascotaService;
        this.protectoraService = protectoraService;
        this.usuarioService = usuarioService;
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

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfilProtectora(@RequestHeader("Authorization") String token,
                                                        @RequestBody ProtectoraDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token expirado o inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        Optional<Protectora> optionalProtectora = protectoraService.findByUsuarioId(idUsuario.intValue());

        // Recuperamos el usuario de la BDD para evitar "detached entity"
        Usuario usuario = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Protectora protectora;
        if (optionalProtectora.isPresent()) {
            protectora = optionalProtectora.get();
        } else {
            protectora = new Protectora();
            protectora.setUsuario(usuario);
        }

        // Actualizamos los campos
        if (dto.getNombre() != null) {
            usuario.setNombre(dto.getNombre());
        }

        if (dto.getCif() != null) protectora.setCif(dto.getCif());
        if (dto.getTelefono() != null) protectora.setTelefono(dto.getTelefono());
        if (dto.getWeb() != null) protectora.setWeb(dto.getWeb());
        if (dto.getCodigoONG() != null) protectora.setCodigoONG(dto.getCodigoONG());
        if (dto.getDireccion() != null) protectora.setDireccion(dto.getDireccion());
        if (dto.getLogo() != null) protectora.setLogo(dto.getLogo());
        if (dto.getFacebook() != null) protectora.setFacebook(dto.getFacebook());
        if (dto.getInstagram() != null) protectora.setInstagram(dto.getInstagram());
        if (dto.getTiktok() != null) protectora.setTiktok(dto.getTiktok());
        if (dto.getLinkedin() != null) protectora.setLinkedin(dto.getLinkedin());

        // Guardamos los cambios
        usuarioService.save(usuario);
        protectoraService.save(protectora);

        return ResponseEntity.ok("Perfil actualizado correctamente");
    }

    @GetMapping("/perfil")
    public ResponseEntity<ProtectoraDTO> obtenerPerfil(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).build();
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        Optional<Protectora> optionalProtectora = protectoraService.findByUsuarioId(idUsuario.intValue());

        if (optionalProtectora.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        ProtectoraDTO dto = convertToDTO(optionalProtectora.get());
        return ResponseEntity.ok(dto);
    }

    private ProtectoraDTO convertToDTO(Protectora protectora) {
        ProtectoraDTO dto = new ProtectoraDTO();
        dto.setId(protectora.getId());
        dto.setCif(protectora.getCif());
        dto.setTelefono(protectora.getTelefono());
        dto.setWeb(protectora.getWeb());
        dto.setCodigoONG(protectora.getCodigoONG());
        dto.setDireccion(protectora.getDireccion());
        dto.setLogo(protectora.getLogo());
        dto.setFacebook(protectora.getFacebook());
        dto.setInstagram(protectora.getInstagram());
        dto.setTiktok(protectora.getTiktok());
        dto.setLinkedin(protectora.getLinkedin());

        if (protectora.getUsuario() != null) {
            dto.setIdUsuario(protectora.getUsuario().getId());
            dto.setNombre(protectora.getUsuario().getNombre());
        }

        return dto;
    }

}
