import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { getUserDataFromToken, isAuthenticated, checkUserRole } from './authUtils.js';
import { authAPI, usuariosAPI } from '../services/api.js';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        checkAuth();
    }, []);

    const checkAuth = async () => {
        try {
            if (isAuthenticated()) {
                const userInfo = getUserDataFromToken();
                if (userInfo?.userId) {
                    const response = await usuariosAPI.obterPerfil(userInfo.userId);
                    setUserData(response.data);
                }
            } else {
                setUserData(null);
                authAPI.logout();
            }
        } catch (error) {
            console.error("Erro ao verificar autenticação:", error);
            setError(error.message);
            setUserData(null);
            authAPI.logout();
        } finally {
            setLoading(false);
        }
    };

    const login = async (credentials) => {
        try {
            setLoading(true);
            setError(null);

            const { userData } = await authAPI.login(credentials.email, credentials.senha);
            await checkAuth();
            navigate('/');
        } catch (error) {
            setError(error.message);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const registro = async (dadosUsuario) => {
        try {
            setLoading(true);
            setError(null);

            const { userData } = await authAPI.registro(dadosUsuario);
            await checkAuth();
            navigate('/');
        } catch (error) {
            setError(error.message);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        authAPI.logout();
        setUserData(null);
        navigate('/');
    };

    const updateUserData = async (dadosAtualizados) => {
        try {
            setLoading(true);
            setError(null);

            if (!userData?.userId) {
                throw new Error("Usuário não autenticado");
            }

            const response = await usuariosAPI.atualizarPerfil(
                userData.userId,
                dadosAtualizados
            );

            setUserData(response.data);
            return response.data;
        } catch (error) {
            setError(error.message);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const value = {
        usuario: userData,
        usuarioId: userData?.userId,
        loading,
        error,
        login,
        registro,
        logout,
        updateUserData,
        isAuthenticated: () => isAuthenticated() && userData !== null,
        isAdmin: () => userData?.tipo === 3,
        isEditor: () => userData?.tipo === 2 || userData?.tipo === 3
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth deve ser usado dentro de um AuthProvider');
    }
    return context;
}

export function PrivateRoute({ children, requiredRole = null }) {
    const { isAuthenticated, isAdmin, isEditor, loading } = useAuth();

    if (loading) {
        return <div>Carregando...</div>;
    }

    if (!isAuthenticated()) {
        return <Navigate to="/cadastro" />;
    }

    if (requiredRole === 'admin' && !isAdmin()) {
        return <Navigate to="/" />;
    }

    if (requiredRole === 'editor' && !isEditor()) {
        return <Navigate to="/" />;
    }

    return children;
}