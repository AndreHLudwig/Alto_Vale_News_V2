import React from "react";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import {AuthProvider, PrivateRoute} from "./auth";
import {LoginProvider} from "./auth/LoginContext";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Post from "./pages/Post";
import Cadastro from "./pages/Cadastro";
import EditarPerfil from "./pages/EditarPerfil";
import Contato from "./pages/Contato";
import PainelAdmin from "./pages/PainelAdmin";
import PainelEditor from "./pages/PainelEditor";

import 'bootstrap/dist/css/bootstrap.min.css';
import './styles/Index.css';

function App() {
    return (
        <Router>
            <AuthProvider>
                <LoginProvider>
                    <div className="App d-flex flex-column min-vh-100">
                        <Header/>
                        <main className="flex-grow-1">
                            <Routes>
                                <Route path="/" element={<Home/>}/>
                                <Route path="/post/:id" element={<Post/>}/>
                                <Route path="/cadastro" element={<Cadastro/>}/>
                                <Route
                                    path="/editar-perfil"
                                    element={
                                        <PrivateRoute>
                                            <EditarPerfil/>
                                        </PrivateRoute>
                                    }
                                />
                                <Route path="/contato" element={<Contato/>}/>
                                <Route
                                    path="/painel-admin"
                                    element={
                                        <PrivateRoute requiredRole="admin">
                                            <PainelAdmin/>
                                        </PrivateRoute>
                                    }
                                />
                                <Route
                                    path="/painel-editor"
                                    element={
                                        <PrivateRoute requiredRole="editor">
                                            <PainelEditor/>
                                        </PrivateRoute>
                                    }
                                />
                            </Routes>
                        </main>
                        <Footer/>
                    </div>
                </LoginProvider>
            </AuthProvider>
        </Router>
    );
}

export default App;