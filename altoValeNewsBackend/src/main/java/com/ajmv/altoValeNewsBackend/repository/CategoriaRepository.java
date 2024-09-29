package com.ajmv.altoValeNewsBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ajmv.altoValeNewsBackend.model.Categoria;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
	Categoria findByCategoriaId(Integer categoriaId);

	Optional<Categoria> findByNome(String nome);
}
