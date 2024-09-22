package com.ajmv.altoValeNewsBackend.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "categoria")
@Entity(name = "categoria")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "categoriaId")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer categoriaId;

	private String nome;

	//TODO verificar LazyFetch
	@OneToMany(mappedBy = "categoriaId")
	private List<Publicacao> publicacoes;

	public Integer getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Integer categoriaId) {
		this.categoriaId = categoriaId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Publicacao> getPublicacoes() {
		return publicacoes;
	}

	public void setPublicacoes(List<Publicacao> publicacoes) {
		this.publicacoes = publicacoes;
	}

}
