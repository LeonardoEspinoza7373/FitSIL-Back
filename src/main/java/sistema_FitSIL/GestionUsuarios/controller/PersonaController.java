package sistema_FitSIL.GestionUsuarios.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sistema_FitSIL.GestionUsuarios.model.Persona;
import sistema_FitSIL.GestionUsuarios.service.PersonaService;

@RestController
@RequestMapping("/persona")
public class PersonaController {

    private final PersonaService personaService;

    // inyeccion del servicio (lo conecta)
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    // Endpoint para crear o actualizar la persona
    @PostMapping("/guardar")
    public Persona guardarPersona(@RequestBody Persona persona) {
        return personaService.guardarPersona(persona);
    }

    // Endpoint para obtener todos los ejercicios
    @GetMapping("/obtener")
    public List<Persona> obtenerTodosLasPersonas() {
        return personaService.obtenerTodosLasPersonas();
    }

}
