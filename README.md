
# MiVet API
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/TheLexa69/mivet-api)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/TheLexa69/mivet-api/actions)
[![Coverage](https://img.shields.io/badge/coverage-85%25-yellowgreen)](https://github.com/TheLexa69/mivet-api)
[![Java](https://img.shields.io/badge/java-17-blue)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring--boot-3.1.0-brightgreen)](https://spring.io/projects/spring-boot)
![Docker](https://img.shields.io/badge/docker-ready-blue)

# Indice 
- [Supuesto](#Supuesto)
- [Arquitectura General](#Arquitectura-General)
- [Manual Técnico para Desarrolladores](#Manual-técnico-para-desarrolladores)
- [Manual de Usuario](#Manual-de-Usuario)
- [GitHub](#GitHub)
- [Conclusiones](#Conclusiones)
- [Dedicación temporal](#Dedicación-temporal)


# Supuesto

Este backend forma parte de un ecosistema de aplicaciones llamado MiVet, centrado en la gestión de adopciones de mascotas. La idea surge de facilitar la comunicación entre usuarios y protectoras, permitiendo:

- Registrar protectoras de animales.
- Subir mascotas en adopción.
- Gestionar solicitudes de adopción.
- Consultar estados y datos de usuarios.

Este microservicio en Java Spring Boot es la API principal para el almacenamiento, consulta y operación de los datos.

# Arquitectura General
El proyecto sigue una arquitectura REST con los siguientes componentes:

- Spring Boot + Maven
- Controladores REST (@RestController)
- Repositorios JPA (no incluidos aquí pero inferidos)
- OpenAPI/Swagger para documentación automática
- Docker para contenedorización

> [Cliente Frontend/Flask] → [API REST (este proyecto)] → [Base de Datos MySQL]

# Manual Técnico para Desarrolladores

Para el desarrollo de la aplicación son necesarias las siguientes herramientas: 

Explora la API en el [Swagger](http://13.48.85.87:8080/swagger-ui/index.html).

## Requisitos
- Java 17
- Maven 3.8+
- Docker (opcional)
- MySQL 8 (remoto o local)
- Postman o Swagger para pruebas de endpoints

## Instalación y Ejecución Local
> #### Clonar el repositorio
> >git clone https://github.com/TheLexa69/mivet-api
>
> >cd mivet-api

> #### Construir el proyecto
>> ./mvn clean install

> #### Ejecutar el proyecto
>> ./mvnw spring-boot:run
>
>> La API queda accesible en: http://localhost:8080

## Ejecución con Docker
> docker build -t mivet-api .
> docker run -p 8080:8080 mivet-api
>> La API queda accesible en: http://localhost:8080

## Generación del JAR + Docker + Despliegue Local
> URL: http://localhost:8080
>> mvn clean install
> 
>> mvn package
> 
> >java -jar .\target\mivet-api-1.0.0.jar
> 
>> docker build -t mivet-api .
> 
>> docker run -p 8080:8080 mivet-api


## Estructura 

Para la realización del proyecto hemos utilizado un patrón por Capas.

Este modelo se suele usar en Spring Boot para aplicaciones web tradicionales con vistas renderizadas en el servidor. Se organizarían en los siguientes apartados que veremos a continuación.

A continuación mostaremos algunas de las partes más interesantes del código de las capas de nuestro proyecto.

## Controladores y Funcionalidades
### UsuarioController
- Registro y login de usuarios.
- Obtención de datos personales.

### 1. UsuarioController
#### Ver las ascotas.
```java
    @GetMapping("/mascotas")
    public ResponseEntity<?> listarMascotasDelUsuario(@RequestHeader("Authorization") String token) {
        try {
            validarPrivado(token);

            Long idUsuario = JwtUtil.extractUserId(token);
            List<Mascota> mascotas = mascotaService.findByUsuarioId(idUsuario);

           if (mascotas.isEmpty()) {
                return ResponseEntity.status(404).body("No se encontraron mascotas para este usuario");
            }

            return ResponseEntity.ok(mascotas);
        } catch (RuntimeException e) {
           return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
       }
    }
```
#### Crear una cita.
 ```java
     @PostMapping("/citas")
    public ResponseEntity<?> crearCita(@RequestHeader("Authorization") String token,
                                       @RequestBody CitaDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Usuario usuario = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mascota mascota = mascotaService.findById(dto.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (!mascota.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para asignar esta mascota");
        }

        Cita cita = new Cita();
        cita.setTipo(dto.getTipo().name());
        cita.setFecha(dto.getFecha());
        cita.setEmpresa(dto.getEmpresa());
        cita.setUsuario(usuario);
        cita.setMascota(mascota);

        citaService.save(cita);

        return ResponseEntity.ok("Cita creada correctamente");
    }
```
#### Filtrar gastos.
 ```java
    @GetMapping("/gastos")
    public ResponseEntity<?> filtrarGastos(@RequestHeader("Authorization") String token,
                                           @RequestParam(required = false) String tipo,
                                           @RequestParam(required = false) String dia,
                                           @RequestParam(required = false) Integer mes,
                                           @RequestParam(required = false) Integer anio,
                                           @RequestParam(required = false) String desde,
                                           @RequestParam(required = false) String hasta) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        List<Mascota> mascotas = mascotaService.findByUsuarioId(idUsuario);

        List<Gasto> gastos = mascotas.stream()
                .flatMap(mascota -> gastoService.findByMascotaId(mascota.getId().longValue()).stream())
                .toList();

        // Filtro por tipo
        if (tipo != null) {
            try {
                TipoGasto tipoEnum = TipoGasto.valueOf(tipo);
                gastos = gastos.stream()
                        .filter(g -> g.getTipo() == tipoEnum)
                        .toList();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Tipo de gasto no válido");
            }
        }

        // Filtro por día exacto
        if (dia != null) {
            LocalDate fecha = LocalDate.parse(dia);
            gastos = gastos.stream()
                    .filter(g -> g.getFecha().equals(fecha))
                    .toList();
        }

        // Filtro por mes y/o año
        if (mes != null || anio != null) {
            gastos = gastos.stream()
                    .filter(g -> {
                        LocalDate f = g.getFecha();
                        boolean coincideMes = mes == null || f.getMonthValue() == mes;
                        boolean coincideAnio = anio == null || f.getYear() == anio;
                        return coincideMes && coincideAnio;
                    })
                    .toList();
        }

        // Filtro por rango de fechas
        if (desde != null || hasta != null) {
            LocalDate desdeFecha = (desde != null) ? LocalDate.parse(desde) : LocalDate.MIN;
            LocalDate hastaFecha = (hasta != null) ? LocalDate.parse(hasta) : LocalDate.MAX;

            gastos = gastos.stream()
                    .filter(g -> !g.getFecha().isBefore(desdeFecha) && !g.getFecha().isAfter(hastaFecha))
                    .toList();
        }

        return ResponseEntity.ok(gastos);
    }
```

### 2. MascotaController
- Gestión de mascotas (crear, listar, eliminar).
- Asociación con protectoras.
#### Info de una mascota por ID.
 ```java
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMascotaPorId(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long id) {
        try {
            validarTipoUsuario(token);
            Optional<Mascota> mascota = mascotaService.findById(id);
            return mascota.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
```


### 3. ProtectoraController
- Alta de protectoras.
- Asignación de mascotas.

#### Actualizar perfil de protectora.
 ```java
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfilProtectora(@RequestHeader("Authorization") String token,
                                                        @RequestBody ProtectoraDTO dto) {
        try {
            validarProtectora(token); // Validate token and user type

            Long idUsuario = JwtUtil.extractUserId(token);
            Optional<Protectora> optionalProtectora = protectoraService.findByUsuarioId(idUsuario.intValue());

            // Retrieve the user from the database to avoid "detached entity"
            Usuario usuario = usuarioService.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Protectora protectora;
            if (optionalProtectora.isPresent()) {
                protectora = optionalProtectora.get();
            } else {
                protectora = new Protectora();
                protectora.setUsuario(usuario);
            }

            // Update fields
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

            // Save changes
            usuarioService.save(usuario);
            protectoraService.save(protectora);

            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }
```


### 4. AdopcionController
- Crear y consultar solicitudes de adopción.
- Aprobación o rechazo.
#### Actualizar la solicitud de adopción.
 ```java
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoAdopcion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam("estado") String estado
    ) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String rol = JwtUtil.extractRol(token);
        if (!"admin".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo protectoras pueden modificar el estado");
        }

        if (!estado.equalsIgnoreCase("aceptada") && !estado.equalsIgnoreCase("rechazada")) {
            return ResponseEntity.badRequest().body("Estado no válido. Usa 'aceptada' o 'rechazada'.");
        }

        Optional<Adopcion> adopcionOpt = adopcionRepository.findById(id);
        if (adopcionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Adopcion adopcion = adopcionOpt.get();
        Long idProtectora = JwtUtil.extractUserId(token);
        Integer protectoraId = idProtectora.intValue();

        if (!adopcion.getMascota().getUsuario().getId().equals(protectoraId)) {
            return ResponseEntity.status(403).body("No tienes permiso para modificar esta solicitud");
        }

        adopcion.setEstado(EstadoAdopcion.valueOf(estado.toLowerCase()));
        adopcionRepository.save(adopcion);

        return ResponseEntity.ok(mapToDTO(adopcion));
    }
```

### 5. StatusController
- Devuelve estados del sistema o para pruebas de salud.
- 
#### Devuelve el estado de la API.
 ```java
    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        return response;
    }
```

## Modelo de Datos

El sistema está compuesto por varias entidades JPA (`@Entity`) mapeadas a tablas en la base de datos:

### Usuario
- **Campos**: `id`, `nombre`, `correo`, `contrasena`, `tipoUsuario`, `rol`.
- **Descripción**: Contiene la información básica de los usuarios del sistema.
- **Relaciones**:
  - `@OneToMany` con `Mascota`, `Mensaje`, `Cita`, `HistorialDueno`.
  - `@OneToOne` con `Auth`.

### Mascota
- **Descripción**: Representa una mascota registrada por un usuario o protectora.
- **Campos**: `id`, `nombre`, `tipo`, `raza`, `fechaNac`, `descripcion`.
- **Relaciones**:
  - `@ManyToOne` con `Usuario` (propietario actual).
  - `@OneToMany` con `HistorialClinico`, `Analitica`, `PruebaImagen`, `Tratamiento`, `Citopatologia`, `Cita`, `HistorialDueno`, `Gasto`.

### Protectora
- **Descripción**: Entidad que representa una organización registrada.
- **Campos**: `id`, `cif`, `codigoONG`, `telefono`, `web`, `direccion`, `logo`, redes sociales.
- **Relaciones**:
  - `@OneToOne` con `Usuario` (el usuario que administra la protectora).

### Adopcion
- **Descripción**: Solicitudes creadas por usuarios para adoptar mascotas.
- **Campos**: `id`, `mensaje`, `estado`, `fechaSolicitud`.
- **Relaciones**:
  - `@ManyToOne` con `Usuario` (solicitante).
  - `@ManyToOne` con `Mascota` (animal solicitado).

## Relaciones:
\- Un usuario puede tener muchas mascotas y muchas citas, mensajes o solicitudes de adopción.

\- Una mascota puede tener muchos historiales clínicos, tratamientos, citas y propietarios anteriores.

\- Una protectora está ligada a un usuario con rol especial.

\- Cada adopción une a una mascota con el usuario que la solicita.

## Notas Técnicas
\- Todas las entidades están anotadas con \@Entity y mapeadas con JPA.

\- Se utilizan \@Enumerated, \@JsonManagedReference, \@JsonBackReference para control de serialización.

\- Relaciones cascade = ALL para persistencia automática en relaciones hijas.

\- Conversores personalizados como TipoUsuarioConverter permiten mapear enums a strings.

# Manual de Usuario

El backend se comunica mediante endpoints REST. Algunos ejemplos:

- GET /usuarios — lista todos los usuarios.
- POST /mascotas — crea una nueva mascota.
- POST /adopciones — inicia una solicitud de adopción.
- GET /adopciones/user/{id} — adopciones del usuario.
- GET /swagger-ui/index.html — documentación interactiva.

# GitHub
El control de versiones del proyecto se ha gestionado mediante GitHub, permitiendo un seguimiento eficiente de los cambios y despliegue continuo. Puedes acceder al repositorio en: [MiVet API GitHub](https://github.com/TheLexa69/mivet-api).

# Conclusiones
El proyecto mivet-api proporciona una base sólida como backend REST para una aplicación de adopciones. Es modular, extensible, y bien estructurado para su integración con una base de datos y otros servicios como el frontend o dashboard en Flask.

He aprendido a construir una arquitectura RESTful robusta con Spring Boot, aplicar control de acceso JWT, y organizar la lógica por capas. El proyecto queda listo para escalar, añadir nuevas funcionalidades y conectar con otros módulos como el frontend Flask o app Android.

# Dedicación temporal
Se estiman alrededor de 40-50 horas de trabajo para su desarrollo, incluyendo planificación, desarrollo, pruebas y contenedorización.
