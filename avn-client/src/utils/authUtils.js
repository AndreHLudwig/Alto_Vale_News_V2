import { jwtDecode } from "jwt-decode";

// Função para obter o ID do usuário do token
export const getUserIdFromToken = () => {
  const token = localStorage.getItem("token");
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return decoded.userId;
  } catch (error) {
    console.error("Erro ao decodificar o token:", error);
    return null;
  }
};

// Função para verificar se o usuário é admin
export const isAdmin = () => {
  const token = localStorage.getItem("token");
  if (!token) return false;

  try {
    const decoded = jwtDecode(token);
    return decoded.role === "admin"; // ou outra condição de verificação
  } catch (error) {
    console.error("Erro ao verificar se o usuário é admin:", error);
    return false;
  }
};
