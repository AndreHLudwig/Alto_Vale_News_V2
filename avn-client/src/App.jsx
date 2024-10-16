import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Post from "./pages/Post";
import Cadastro from "./pages/Cadastro.jsx";
import EditarPerfil from "./pages/EditarPerfil";
import Contato from "./pages/Contato";
import PainelAdmin from "./pages/PainelAdmin";
import PainelEditor from "./pages/PainelEditor";

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/post/:id" element={<Post />} />
            <Route path="/cadastro" element={<Cadastro />} />
            <Route path="/editar-perfil" element={<EditarPerfil />} />
            <Route path="/contato" element={<Contato />} />
            <Route path="/painel-admin" element={<PainelAdmin />} />
            <Route path="/painel-editor" element={<PainelEditor />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
