import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { criarPublicacao } from "../services/api"; 
import { getUserIdFromToken, isEditor, isAdmin } from "../utils/authUtils";

//TODO painel para editar e excluir publicações
const PainelEditor = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    titulo: "",
    texto: "",
    categorias: [""],  
    visibilidadeVip: false,
    imagem: null,
    video: null,
  });

  
  useEffect(() => {
    if (!isEditor() && !isAdmin()) {
      navigate("/acesso-negado"); 
    }
  }, [navigate]);

  
  const handleInputChange = (event) => {
    const { name, value, type, checked, files, dataset } = event.target;
    const index = dataset.index;
    setForm((prevForm) => {
      if (name === "categorias") {
        const newCategories = [...prevForm.categorias];
        newCategories[index] = value; 
        return { ...prevForm, categorias: newCategories };
      } else {
        return {
          ...prevForm,
          [name]: type === "checkbox" ? checked : files ? files[0] : value,
        };
      }
    });
  };

 
  const addCategoria = () => {
    setForm((prevForm) => ({
      ...prevForm,
      categorias: [...prevForm.categorias, ""],
    }));
  };

 
  const removeCategoria = (index) => {
    setForm((prevForm) => {
      const newCategories = [...prevForm.categorias];
      newCategories.splice(index, 1);
      return { ...prevForm, categorias: newCategories };
    });
  };

 
  const handleSubmit = async (event) => {
    event.preventDefault();
    const editorId = getUserIdFromToken();
    const dataPubli = new Date().toISOString();

    const formData = new FormData();
    formData.append("editorId", editorId);
    formData.append("titulo", form.titulo);
    formData.append("texto", form.texto);
    formData.append("visibilidadeVip", form.visibilidadeVip);
    formData.append("data", dataPubli);
    formData.append("imagem", form.imagem);
    formData.append("video", form.video);
    
   
    form.categorias.forEach((categoria) => {
      formData.append("categorias", categoria);
    });

    try {
      const response = await criarPublicacao(formData);
      if (response && response.data.publicacaoId) {
        alert("Publicação criada com sucesso!");
        setForm({
          titulo: "",
          texto: "",
          categorias: [""],
          visibilidadeVip: false,
          imagem: null,
          video: null,
        });
      } else {
        throw new Error("Erro ao criar a publicação.");
      }
    } catch (error) {
      console.error("Erro ao criar a publicação:", error);
      alert("Erro ao criar a publicação. Verifique os campos e tente novamente.");
    }
  };

  return (
    <div className="container">
      <h2>Criar Nova Publicação</h2>
      <form onSubmit={handleSubmit} encType="multipart/form-data">
        <div className="form-group">
          <label htmlFor="titulo">Título:</label>
          <input
            type="text"
            className="form-control"
            id="titulo"
            name="titulo"
            value={form.titulo}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="texto">Texto:</label>
          <textarea
            className="form-control"
            id="texto"
            name="texto"
            value={form.texto}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="imagem">Imagem:</label>
          <input
            type="file"
            className="form-control"
            id="imagem"
            name="imagem"
            accept="image/*"
            onChange={handleInputChange}
          />
        </div>
        <div className="form-group">
          <label htmlFor="video">Vídeo:</label>
          <input
            type="file"
            className="form-control"
            id="video"
            name="video"
            accept="video/*"
            onChange={handleInputChange}
          />
        </div>
        {/*TODO diminuir tamanho dos campos*/}
        <div className="form-group">
          <label>Categorias:</label>
          {form.categorias.map((categoria, index) => (
            <div key={index} className="d-flex align-items-center">
              <input
                type="text"
                className="form-control"
                name="categorias"
                value={categoria}
                data-index={index}
                onChange={handleInputChange}
                required
              />
              <button
                type="button"
                className="btn btn-danger ml-2"
                onClick={() => removeCategoria(index)}
                disabled={form.categorias.length <= 1}
              >
                Remover
              </button>
            </div>
          ))}
          <button
            type="button"
            className="btn btn-secondary mt-2"
            onClick={addCategoria}
          >
            Adicionar Categoria
          </button>
        </div>
        <div className="form-group">
          <label htmlFor="visibilidadeVip">Visibilidade VIP:</label>
          <input
            type="checkbox"
            id="visibilidadeVip"
            name="visibilidadeVip"
            checked={form.visibilidadeVip}
            onChange={handleInputChange}
          />
        </div>
        <button type="submit" className="btn btn-primary">
          Enviar
        </button>
      </form>
    </div>
  );
};

export default PainelEditor;
