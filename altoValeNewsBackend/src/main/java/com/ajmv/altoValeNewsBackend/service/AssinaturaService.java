package com.ajmv.altoValeNewsBackend.service;
import com.ajmv.altoValeNewsBackend.model.Assinatura;
import com.ajmv.altoValeNewsBackend.model.TipoUsuario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.AssinaturaRepository;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AssinaturaService {

    private final AssinaturaRepository assinaturaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AssinaturaService(AssinaturaRepository assinaturaRepository, UsuarioRepository usuarioRepository) {
        this.assinaturaRepository = assinaturaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Busca todas as assinaturas
    public List<Assinatura> getAllAssinaturas() {
        return assinaturaRepository.findAll();
    }

    // Buscar assiantura por ID
    public ResponseEntity<Assinatura> getAssinaturaById(Integer id) {
        Optional<Assinatura> assinatura = assinaturaRepository.findById(id);
        return assinatura.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    //Modificar Tipo usuário ao assinar
    private ResponseEntity<Usuario> setTipoUsuario(Integer id) {
        try {
            Usuario usuarioOptional = usuarioRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));
            usuarioOptional.setTipo(TipoUsuario.USUARIO_VIP);
            Usuario usuarioAtualizadoBanco = usuarioRepository.save(usuarioOptional);
            return ResponseEntity.ok(usuarioAtualizadoBanco);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Assinatura> assinar(Assinatura novaAssinatura, Integer dias) {
        try {
            Assinatura assinaturaExistente = assinaturaRepository.findById(novaAssinatura.getAssinaturaId()).orElseThrow(() -> new NoSuchElementException("Assinatura não encontrada"));

            LocalDateTime novaData;
            if (!assinaturaExistente.isAtivo()) {
                // Se a assinatura estiver inativa, define a data como agora + dias
                novaData = LocalDateTime.now().plusDays(dias);
                setTipoUsuario(novaAssinatura.getAssinaturaId());
            } else {
                // Se a assinatura estiver ativa, soma os dias à data de vencimento atual
                novaData = assinaturaExistente.getVencimento().plusDays(dias);
            }

            assinaturaExistente.setVencimento(novaData);
            assinaturaExistente.setAtivo(true);

            Assinatura assinaturaAtualizadaBanco = assinaturaRepository.save(assinaturaExistente);
            return ResponseEntity.ok(assinaturaAtualizadaBanco);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
