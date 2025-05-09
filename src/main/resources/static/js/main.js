window.onload = function () {
    console.log("JS cargado...");
    var mensajeDiv = document.getElementById('mensaje');
    var ticketUsuario = document.getElementById('ticketUsuario');
    var btnNewTicket = document.getElementById('btnNewTicket');
    var btnEnviarTicket = document.getElementById('btnEnviarTicket');
    var btnCloseTicket = document.getElementById('btnCloseTicket');

    if (mensajeDiv) { // Solo ejecutar si el div existe
        setTimeout(function () {
            mensajeDiv.style.top = '20%';
            setTimeout(function () {
                mensajeDiv.style.top = '-150%';
            }, 5000);
        }, 100);
    }

    if (btnNewTicket) {
        btnNewTicket.addEventListener('click', function () {
            ticketUsuario.style.top = '15%';
        });
    }

    if (btnEnviarTicket) {
        btnEnviarTicket.addEventListener('click', function () {
            ticketUsuario.style.transition = 'top 0.5s';
            ticketUsuario.style.top = '-150%';
        });
    }

    if (btnCloseTicket) {
        btnCloseTicket.addEventListener('click', function () {
            ticketUsuario.style.transition = 'top 0.5s';
            ticketUsuario.style.top = '-150%';
        });
    }
    if (document.getElementById("ticketForm")) {
        document.getElementById("ticketForm").addEventListener("submit", function (event) {
            event.preventDefault();

            const horaContacto = document.getElementById("horaContacto").value;
            const tema = document.getElementById("tema").value;
            const asunto = document.getElementById("asunto").value;
            const descripcion = document.getElementById("descripcion").value;

            console.log("Hora de contacto:", horaContacto);
            console.log("Tema:", tema);
            console.log("Asunto:", asunto);
            console.log("Descripción:", descripcion);


            if (!horaContacto || !tema || !asunto || !descripcion) {
                alert("Por favor, rellena todos los campos del formulario.");
                ticketUsuario.style.top = '15%';
                return;
            }

            const formData = new FormData();
            formData.append("horaContacto", horaContacto);
            formData.append("tema", tema);
            formData.append("asunto", asunto);
            formData.append("descripcion", descripcion);

            const userIdElement = document.getElementById("userId");
            if (!userIdElement) {
                alert("Error: No se pudo obtener el ID del usuario.");
                return;
            }
            const userId = userIdElement.innerText.trim();

            fetch(`/tickets/${userId}`, {
                method: "POST",
                body: formData
            }).then(response => {
                if (response.ok) {
                    alert("Ticket enviado con éxito.");
                    window.location.reload();
                } else {
                    alert("Error al enviar el ticket.");
                }
            }).catch(error => {
                console.error("Error:", error);
                alert("Hubo un problema con el envío del ticket.");
            });
        });
    }

    // FUNCION PARA EL FOOTER
    // document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM cargado...");
    const footer = document.querySelector("footer");
    const navbarNavOptions = document.getElementById("navbarNavOptions");

    console.log(window.location.pathname);

    if (!window.location.pathname.includes("/login")) {
        console.log("entra")
        footer.style.position = "relative";
        footer.style.setProperty("margin-top", "50px", "important");
    } else {
        //Estamos en el login

        footer.style.position = "absolute";
        if (navbarNavOptions) {
            navbarNavOptions.classList.add("hidden");
        }
    }

    console.log(window.location.pathname)
    if (window.location.pathname === ("/")) {
         if (navbarNavOptions) {
            navbarNavOptions.classList.add("hidden");
        }
    }


    // Codigo para manejar la clase 'active' en los elementos del menú
    const menuItems = document.querySelectorAll(".list-group-item");

    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            // Remover la clase 'active' de todos los elementos
            menuItems.forEach(i => i.classList.remove("active"));
            // Agregar la clase 'active' al elemento clicado
            this.classList.add("active");
        });
    });
};

//[ADMINISTRADOR] FUNCION PARA ENSEÑAR EL MODAL DE ACTUALIZAR USUARIO
document.addEventListener("DOMContentLoaded", function () {
    //ABRIR MODAL DE EDICION
    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", function () {
            let fila = this.closest("tr"); // <tr> más cercano
            let primerTD = fila.querySelector("td"); // Primer <td>
            let userId = primerTD.textContent.trim(); // Recogemos el usuario del primer <td>
            let modalId = 'updateUserModal-' + userId;

            document.getElementById("backgroundOverlay").classList.toggle('d-none');
            let modal = document.getElementById(modalId);
            modal.classList.toggle('d-none');
            modal.style.display = 'flex';
            modal.style.position = 'absolute';
            modal.style.zIndex = '1050';
            modal.style.top = '0';
            modal.style.justifyContent = 'center';
            modal.style.alignItems = 'center';
            modal.style.left = '0';
            modal.style.right = '0';

            console.log("Primer TD:", userId);
        });
    });
    //CERRAR MODAL DE ACTUALIZAR USUARIO
    document.querySelectorAll(".close-btn").forEach(button => {
        button.addEventListener("click", function () {
            let modal = this.closest("div[id^='updateUserModal-']"); // Encontramos el div con id 'updateUserModal-'
            if (modal) {
                modal.classList.toggle('d-none');
                document.getElementById("backgroundOverlay").classList.toggle('d-none');
                // console.log("ID del modal:", modal.id);
            }
        });
    });
    //ABRIR MODAL BORRADO
    document.querySelectorAll(".del-btn").forEach(button => {
        button.addEventListener("click", function () {
            console.log("Botón de eliminar usuario");

            let fila = this.closest("tr"); // Encuentra la fila <tr> más cercana
            let primerTD = fila.querySelector("td"); // Primer <td>
            let userId = primerTD.textContent.trim(); // Obtiene el ID del usuario
            let modalId = 'deleteUserModal-' + userId; // Construimos el ID del modal

            let modal = document.getElementById(modalId); // Buscamos el modal
            if (!modal) {
                console.error("No se encontró el modal con ID:", modalId);
                return;
            }

            console.log("Abriendo modal:", modalId);
            modal.classList.toggle('d-none');
            modal.style.display = 'flex';
            modal.style.position = 'absolute';
            modal.style.zIndex = '1050';
            modal.style.top = '0';
            modal.style.justifyContent = 'center';
            modal.style.alignItems = 'center';
            modal.style.left = '0';
            modal.style.right = '0';
            document.getElementById("backgroundOverlay").classList.toggle('d-none');
        });
    });
    //CERRAR MODAL DE BORRADO USUARIO
    document.querySelectorAll(".btn-close-del").forEach(button => {
        button.addEventListener("click", function () {
            console.log("Cerrando modal de eliminación");

            let modal = this.closest("div[id^='deleteUserModal-']"); // Modal más cercano con id 'deleteUserModal-'
            if (!modal) {
                console.error("No se encontró el modal a cerrar.");
                return;
            }

            // console.log("Cerrando modal:", modal.id);
            modal.classList.add('d-none');
            document.getElementById("backgroundOverlay").classList.add('d-none');
        });
    });
    if (document.getElementById('habilitarCampos')) {
        document.getElementById('habilitarCampos').addEventListener('click', function () {
            // Seleccionar todos los inputs en el contenedor
            const inputs = document.querySelectorAll('.col-md-6 input');

            // Habilitar todos los inputs
            inputs.forEach(function (input) {
                input.disabled = false;
            });

            // Obtener el elemento por su ID
            const elemento = document.getElementById("confirmarEdit");

            // Cambiar el estilo para hacerlo visible
            elemento.style.display = "block";

        });
    }

});

