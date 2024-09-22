package com.ajmv.altoValeNewsBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ajmv.altoValeNewsBackend.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
	// TODO
	Categoria findByCategoriaId(Integer categoriaId);

	Categoria findByNome(String nome);
}
