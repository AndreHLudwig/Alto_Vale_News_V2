package com.ajmv.altoValeNewsBackend.controller;
import com.ajmv.altoValeNewsBackend.model.Assinatura;
import com.ajmv.altoValeNewsBackend.service.AssinaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("assinatura")
public class AssinaturaController {

    private final AssinaturaService assinaturaService;

    @Autowired
    public AssinaturaController(AssinaturaService assinaturaService) {
        this.assinaturaService = assinaturaService;
    }

    @GetMapping
    public List<Assinatura> getAll() {
        return assinaturaService.getAllAssinaturas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assinatura> getById(@PathVariable Integer id) {
        return assinaturaService.getAssinaturaById(id);
    }

    @PutMapping("/assinar")
    public ResponseEntity<Assinatura> assinar(@RequestBody Assinatura assinaturaUsuario, @RequestParam Integer dias) {
        return assinaturaService.assinar(assinaturaUsuario, dias);
    }

}
