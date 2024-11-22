import { AuthProvider, useAuth, PrivateRoute } from './AuthContext';
import {
    getToken,
    setToken,
    clearToken,
    getUserDataFromToken,
    isAuthenticated,
    isAdmin,
    isEditor,
    isVip,
    checkUserRole
} from './authUtils';

// Exportações diretas dos componentes e hooks
export {
    AuthProvider,
    useAuth,
    PrivateRoute
};

// Exportação das funções de utils individuais
export {
    isAuthenticated,
    isAdmin,
    isEditor,
    isVip,
    checkUserRole,
    getUserDataFromToken
};

// Objeto consolidado com utilitários de autenticação
export const authUtils = {
    // Token management
    getToken,
    setToken,
    clearToken,
    getUserData: getUserDataFromToken,

    // Verificações básicas
    isAuthenticated,
    checkUserRole,

    // Roles específicas
    isAdmin,
    isEditor,
    isVip,

    // Permissões compostas
    canDeleteContent: () => isEditor() || isAdmin(),
    canEditContent: () => isEditor() || isAdmin(),
    canAccessVip: () => isVip(),
    canManageUsers: () => isAdmin(),

    // Helpers
    isContentOwner: (userId) => {
        const userData = getUserDataFromToken();
        return userData?.userId === userId;
    },

    // Verificações combinadas
    hasContentPermission: (content) => {
        const isOwner = authUtils.isContentOwner(content.editor?.userId);
        return isOwner || authUtils.canEditContent();
    },

    canAccessContent: (content) => {
        if (!content.visibilidadeVip) return true;
        return authUtils.canAccessVip();
    }
};