package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.model.TipoUsuario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuarioService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(BCryptPasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public ResponseEntity<Usuario> getUsuarioById(Integer id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<?> createUsuario(Usuario novoUsuario) {
        try {
            String senhaPlana = novoUsuario.getSenha();
            novoUsuario.setSenhahash(passwordEncoder.encode(senhaPlana));
            novoUsuario.setSenha(null);

            novoUsuario.criarAssinatura();
            novoUsuario.setTipo(TipoUsuario.USUARIO);

            Usuario usuarioCriado = usuarioRepository.save(novoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);

        } catch (DataIntegrityViolationException e) {
            String mensagemErro = e.getMostSpecificCause().getMessage();

            if (mensagemErro.contains("email_unique")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("E-mail já cadastrado.");
            } else if (mensagemErro.contains("cpf_unique")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("CPF já cadastrado.");
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro de integridade de dados ao criar usuário.");
        }
    }


    public ResponseEntity<?> deleteUsuario(Integer id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Usuario> updateUsuario(Integer id, Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioExistente = usuarioOptional.get();
            usuarioExistente.setEndereco(usuarioAtualizado.getEndereco());
            usuarioExistente.setCidade(usuarioAtualizado.getCidade());
            usuarioExistente.setEstado(usuarioAtualizado.getEstado());
            usuarioExistente.setCep(usuarioAtualizado.getCep());

            Usuario usuarioAtualizadoBanco = usuarioRepository.save(usuarioExistente);
            return ResponseEntity.ok(usuarioAtualizadoBanco);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Usuario> partialUpdateUsuario(Integer id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            if (usuarioAtualizado.getSenha() != null) {
                String senhaPlana = usuarioAtualizado.getSenha();
                usuarioExistente.setSenhahash(passwordEncoder.encode(senhaPlana));
            }
            if (usuarioAtualizado.getEndereco() != null) {
                usuarioExistente.setEndereco(usuarioAtualizado.getEndereco());
            }
            if (usuarioAtualizado.getCidade() != null) {
                usuarioExistente.setCidade(usuarioAtualizado.getCidade());
            }
            if (usuarioAtualizado.getEstado() != null) {
                usuarioExistente.setEstado(usuarioAtualizado.getEstado());
            }
            if (usuarioAtualizado.getCep() != null) {
                usuarioExistente.setCep(usuarioAtualizado.getCep());
            }
            Usuario salvo = usuarioRepository.save(usuarioExistente);
            return ResponseEntity.ok(salvo);
        }).orElse(ResponseEntity.notFound().build());
    }


    public ResponseEntity<Usuario> setTipoUsuario(Integer id, Integer tipoUsuario, Integer adm) {
        try {
            Usuario admEncontrado = usuarioRepository.findById(adm)
                    .orElseThrow(() -> new NoSuchElementException("Administrador não encontrado."));
            
            Usuario usuarioEncontrado = usuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));

            if (admEncontrado.getTipo() == TipoUsuario.ADMINISTRADOR) {
                TipoUsuario novoTipo = TipoUsuario.getTipoUsuario(tipoUsuario);
                usuarioEncontrado.setTipo(novoTipo);
                Usuario usuarioAtualizadoBanco = usuarioRepository.save(usuarioEncontrado);
                return ResponseEntity.ok(usuarioAtualizadoBanco);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}