import axios from "axios";
import { authUtils } from "../auth";

// API Base Configuration
const api = axios.create({
    baseURL: "http://localhost:8080",
});

// Request Interceptor
api.interceptors.request.use(
    (config) => {
        const token = authUtils.getToken();
        if (token) {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response Interceptor
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            authUtils.clearToken();
            window.location.href = '/cadastro';
        }
        return Promise.reject(error);
    }
);

// Auth API
export const authAPI = {
    login: async (email, senha) => {
        try {
            const response = await api.post("/usuario/login", { email, senha });
            const { jwt: token, ...userData } = response.data;
            authUtils.setToken(token);
            return { userData };
        } catch (error) {
            throw handleApiError(error);
        }
    },

    registro: async (dadosUsuario) => {
        try {
            const response = await api.post("/usuario", dadosUsuario);
            const { jwt: token, ...userData } = response.data;
            authUtils.setToken(token);
            return { userData };
        } catch (error) {
            throw handleApiError(error);
        }
    },

    logout: () => {
        authUtils.clearToken();
    }
};

// Users API
export const usuariosAPI = {
    obterPerfil: (idUsuario) =>
        api.get(`/usuario/${idUsuario}`),

    atualizarPerfil: (idUsuario, dados) =>
        api.patch(`/usuario/${idUsuario}`, dados),

    listar: () =>
        api.get("/usuario"),

    alterarTipo: (idUsuario, tipoUsuario, adminId) =>
        api.put(`/usuario/${idUsuario}/tipo`, null, {
            headers: { "Admin-Id": adminId },
            params: { tipoUsuario: Number(tipoUsuario) }
        })
};

// Publications API
export const publicacoesAPI = {
    listar: () =>
        api.get("/publicacao"),

    obter: (id, usuarioId) =>
        api.get(`/publicacao/${id}`, { params: { usuarioId } }),

    criar: (dados) =>
        api.post("/publicacao", dados, {
            headers: { "Content-Type": "multipart/form-data" }
        }),

    atualizar: (id, dados) =>
        api.put(`/publicacao/${id}`, dados),

    deletar: (id) =>
        api.delete(`/publicacao/${id}`),

    curtir: async (publicacaoId, usuarioId) => {
        try {
            return await api.post(`/publicacao/${publicacaoId}/like`, null, {
                params: { usuarioId }
            });
        } catch (error) {
            if (error.response?.status === 409) return error.response;
            throw error;
        }
    },

    descurtir: async (publicacaoId, usuarioId) => {
        try {
            return await api.delete(`/publicacao/${publicacaoId}/like`, {
                params: { usuarioId }
            });
        } catch (error) {
            if (error.response?.status === 409) return error.response;
            throw error;
        }
    }
};

// Comments API
export const comentariosAPI = {
    listar: (publicacaoId, usuarioId) =>
        api.get("/comentario", { params: { publicacaoId, usuarioId } }),

    criar: (dados) =>
        api.post("/comentario", dados),

    deletar: (id) =>
        api.delete(`/comentario/${id}`),

    curtir: async (comentarioId, usuarioId) => {
        try {
            return await api.post(`/comentario/${comentarioId}/like`, null, {
                params: { usuarioId }
            });
        } catch (error) {
            if (error.response?.status === 409) return error.response;
            throw error;
        }
    },

    descurtir: async (comentarioId, usuarioId) => {
        try {
            return await api.delete(`/comentario/${comentarioId}/like`, {
                params: { usuarioId }
            });
        } catch (error) {
            if (error.response?.status === 409) return error.response;
            throw error;
        }
    }
};

// Categories API
export const categoriasAPI = {
    listar: () => api.get("/categoria")
};

// Subscription API
export const assinaturaAPI = {
    assinar: (assinatura, dias) =>
        api.put(`/assinatura/assinar?dias=${dias}`, assinatura),

    cancelar: (assinaturaId) =>
        api.delete(`/assinatura/${assinaturaId}`)
};

// Media API
export const mediaAPI = {
    obter: (id) =>
        api.get(`/api/media/files/${id}`, { responseType: "blob" })
};

// Contact API
export const contatoAPI = {
    enviar: (dadosContato) =>
        api.post("/contato", dadosContato)
};

// Error Handler
const handleApiError = (error) => {
    let errorMessage = "Erro inesperado no servidor. Tente novamente mais tarde.";

    if (error.response) {
        const { status, data } = error.response;

        switch (status) {
            case 400:
                errorMessage = data; // CPF inválido
                break;
            case 401:
                errorMessage = "Não autorizado. Faça login novamente.";
                break;
            case 409:
                errorMessage = data; // Email já cadastrado
                break;
            default:
                if (typeof data === 'string') errorMessage = data;
        }
    } else if (error.request) {
        errorMessage = "Erro de conexão. Verifique sua rede.";
    }

    return new Error(errorMessage);
};

export default api;