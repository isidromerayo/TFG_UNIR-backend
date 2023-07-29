package eu.estilolibre.tfgunir.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:3000","http://localhost:5173"})
@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
}
