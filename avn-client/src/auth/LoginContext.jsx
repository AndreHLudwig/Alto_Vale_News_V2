import React, { createContext, useContext, useState } from 'react';

const LoginContext = createContext();

export function LoginProvider({ children }) {
    const [showLoginCard, setShowLoginCard] = useState(false);

    const openLoginCard = () => setShowLoginCard(true);
    const closeLoginCard = () => setShowLoginCard(false);

    return (
        <LoginContext.Provider value={{ showLoginCard, openLoginCard, closeLoginCard }}>
            {children}
        </LoginContext.Provider>
    );
}

export function useLogin() {
    const context = useContext(LoginContext);
    if (!context) {
        throw new Error('useLogin deve ser usado dentro de um LoginProvider');
    }
    return context;
}