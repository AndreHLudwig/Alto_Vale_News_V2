import { jwtDecode } from "jwt-decode";

export const TOKEN_KEY = "token";

export const getToken = () => localStorage.getItem(TOKEN_KEY);

export const setToken = (token) => {
    if (token) {
        localStorage.setItem(TOKEN_KEY, token);
    } else {
        localStorage.removeItem(TOKEN_KEY);
    }
};

export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

export const decodeToken = (token) => {
    if (!token) return null;

    try {
        return jwtDecode(token);
    } catch (error) {
        console.error("Erro ao decodificar o token:", error);
        return null;
    }
};

export const getUserDataFromToken = () => {
    const token = getToken();
    if (!token) return null;

    try {
        const decoded = decodeToken(token);
        return {
            userId: decoded.userId,
            tipo: decoded.tipo,
            nome: decoded.nome,
            email: decoded.email
        };
    } catch (error) {
        console.error("Erro ao obter dados do usuário do token:", error);
        return null;
    }
};

export const getUserIdFromToken = () => {
    const userData = getUserDataFromToken();
    return userData?.userId || null;
};

export const checkUserRole = (requiredRole) => {
    const userData = getUserDataFromToken();
    if (!userData) return false;

    switch (requiredRole) {
        case 'admin':
            return userData.tipo === 3;
        case 'editor':
            return userData.tipo === 2 || userData.tipo === 3;
        case 'vip':
            return userData.tipo >= 1; // Qualquer tipo maior que usuário comum (1+)
        default:
            return true;
    }
};

export const isAuthenticated = () => {
    const token = getToken();
    if (!token) return false;

    try {
        const decoded = decodeToken(token);
        const currentTime = Date.now() / 1000;
        return decoded.exp > currentTime;
    } catch (error) {
        return false;
    }
};

export const isAdmin = () => checkUserRole('admin');
export const isEditor = () => checkUserRole('editor');
export const isVip = () => checkUserRole('vip');