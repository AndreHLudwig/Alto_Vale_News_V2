import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import MyNavbar from "./Navbar";
import api from "../services/api";

function Header() {
  const [usuario, setUsuario] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      // Fazer uma chamada à API para obter os dados do usuário
      api
        .get("/usuario", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
          setUsuario(response.data);
        })
        .catch((error) => {
          console.error("Erro ao obter dados do usuário", error);
          // Lidar com erros, como token expirado
        });
    }
  }, []);

  return (
    <header>
      <nav className="navbar navbar-expand-lg navbar-light bg-custom">
        <div className="container">
          <MyNavbar usuario={usuario} />
        </div>
      </nav>
    </header>
  );
}

export default Header;
