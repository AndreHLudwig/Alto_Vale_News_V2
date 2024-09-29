package com.ajmv.altoValeNewsBackend.service;

import com.ajmv.altoValeNewsBackend.exception.CPFInvalidoException;
import com.ajmv.altoValeNewsBackend.exception.EmailJaCadastradoException;
import com.ajmv.altoValeNewsBackend.model.TipoUsuario;
import com.ajmv.altoValeNewsBackend.model.Usuario;
import com.ajmv.altoValeNewsBackend.repository.UsuarioRepository;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
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

    private final UsuarioRepository repository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(BCryptPasswordEncoder passwordEncoder, UsuarioRepository repository, UsuarioRepository usuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarioList = repository.findAll();
        return usuarioList;
    }

    public ResponseEntity<Usuario> getUsuarioById(Integer id) {
        Optional<Usuario> usuarioOptional = repository.findById(id);
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Usuario> createUsuario(Usuario novoUsuario) {
        try {
            String senhaPlana = novoUsuario.getSenha();
            novoUsuario.setSenhahash(passwordEncoder.encode(senhaPlana));
            novoUsuario.setSenha(null);

            novoUsuario.criarAssinatura();
            novoUsuario.setTipo(TipoUsuario.USUARIO);

            Usuario usuarioCriado = repository.save(novoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
        } catch (DataIntegrityViolationException e) {
            Throwable cause = e.getRootCause();
            if (cause instanceof PSQLException) {
                PSQLException sqlException = (PSQLException) cause;
                if (PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    throw new EmailJaCadastradoException("E-mail já cadastrado.");
                } else if (PSQLState.INVALID_PARAMETER_VALUE.getState().equals(sqlException.getSQLState())) {
                    throw new CPFInvalidoException("CPF inválido.");
                }
            }
            throw new RuntimeException("Erro ao criar usuário.", e);
        }
    }

    public ResponseEntity<?> deleteUsuario(Integer id) {
        try {
            Optional<Usuario> usuarioOptional = repository.findById(id);
            if (usuarioOptional.isPresent()) {
                repository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Usuario> updateUsuario(Integer id, Usuario usuarioAtualizado) {
        try {
            Optional<Usuario> usuarioOptional = repository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuarioExistente = usuarioOptional.get();
                usuarioExistente.setEndereco(usuarioAtualizado.getEndereco());
                usuarioExistente.setCidade(usuarioAtualizado.getCidade());
                usuarioExistente.setEstado(usuarioAtualizado.getEstado());
                usuarioExistente.setCep(usuarioAtualizado.getCep());

                Usuario usuarioAtualizadoBanco = repository.save(usuarioExistente);
                return ResponseEntity.ok(usuarioAtualizadoBanco);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Usuario> partialUpdateUsuario(Integer id, Usuario usuarioAtualizado) {
        try {
            Optional<Usuario> usuarioOptional = repository.findById(id);
            if (usuarioOptional.isPresent()) {
                Usuario usuarioExistente = usuarioOptional.get();
                if (usuarioAtualizado.getSenha() != null) {
                    String senhaPlana = usuarioAtualizado.getSenha();
                    usuarioExistente.setSenhahash(passwordEncoder.encode(senhaPlana));
                    usuarioAtualizado.setSenha(null);
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

                /*
                TODO - Verificar com o Farah para onde mover esse código
                TODO - Criar um endpoint específico para gerenciar 1- alteração de tipos para administrador;
                */

//                if (usuarioAtualizado.getTipo() != null) {
//                    usuarioExistente.setTipo(usuarioAtualizado.getTipo());
//                }

                Usuario usuarioAtualizadoSalvo = repository.save(usuarioExistente);
                return ResponseEntity.ok(usuarioAtualizadoSalvo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    public ResponseEntity<Usuario> setTipoUsuario(Integer id, Integer tipoUsuario, Integer adm) {
        try {
            Usuario admOptional = usuarioRepository.findById(adm).orElseThrow(() -> new NoSuchElementException("Administrador não encontrado."));
            Usuario usuarioOptional = usuarioRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuário não encontrado."));

            if (admOptional.getTipo() == TipoUsuario.ADMINISTRADOR) {
                usuarioOptional.setTipo(tipoUsuario);
                Usuario usuarioAtualizadoBanco = repository.save(usuarioOptional);
                return ResponseEntity.ok(usuarioAtualizadoBanco);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}

