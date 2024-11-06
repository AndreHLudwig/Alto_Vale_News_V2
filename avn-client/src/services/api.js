import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

// Interceptor para adicionar o token de autenticação a todas as requisições
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = "Bearer " + token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Funções para interagir com a API

// Autenticação
export const login = (email, senha) =>
  api.post("/usuario/login", { email, senha });
export const registro = (dadosUsuario) => api.post("/usuario", dadosUsuario);
export const logout = () => {
  localStorage.removeItem("token");
  // Adicione aqui qualquer lógica adicional de logout
};

// Usuários
export const obterPerfilUsuario = (idUsuario) =>
  api.get(`/usuario/${idUsuario}`);
export const atualizarPerfilUsuario = (idUsuario, dados) =>
  api.patch(`/usuario/${idUsuario}`, dados);

// Publicações
export const listarPublicacoes = () => api.get("/publicacao");
export const obterPublicacao = (id) => api.get(`/publicacao/${id}`);
export const criarPublicacao = (dados) => api.post("/publicacao", dados);
export const atualizarPublicacao = (id, dados) =>
  api.put(`/publicacao/${id}`, dados);
export const deletarPublicacao = (id) => api.delete(`/publicacao/${id}`);

// Comentários
export const listarComentarios = (publicacaoId) =>
  api.get(`/comentario?publicacaoId=${publicacaoId}`);
export const criarComentario = (dados) => api.post("/comentario", dados);
export const deletarComentario = (id) => api.delete(`/comentario/${id}`);

// Curtidas
export const curtirPublicacao = (id) => api.post(`/publicacao/${id}/like`);
export const descurtirPublicacao = (id) => api.delete(`/publicacao/${id}/like`);
export const curtirComentario = (id) => api.post(`/comentario/${id}/like`);
export const descurtirComentario = (id) => api.delete(`/comentario/${id}/like`);

// Categorias
export const listarCategorias = () => api.get("/categoria");

// Assinatura
export const assinar = (assinatura, dias) =>
  api.put(`/assinatura/assinar?dias=${dias}`, assinatura);

//TODO - Cancelar Assinatura

// MediaFile
export const obterMediaFile = (id) =>
  api.get(`/api/media/files/${id}`, { responseType: "blob" });

//Contato
export const enviarContato = (dadosContato) =>
  api.post("/contato", dadosContato);

export default api;
