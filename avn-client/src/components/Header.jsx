import React, { useState, useEffect } from "react";
import MyNavbar from "./Navbar";
import api from "../services/api";

function Header() {
  const [usuario, setUsuario] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      api.get("/usuario", {
        headers: { Authorization: `Bearer ${token}` },
      })
        .then((response) => {
          setUsuario(response.data);
        })
        .catch((error) => {
          console.error("Erro ao obter dados do usuário", error);
          localStorage.removeItem("token");
        });
    }
  }, []);

  return (
    <header>
      <nav className="navbar navbar-expand-lg navbar-light bg-custom">
        <div className="container">
          <MyNavbar usuario={usuario} setUsuario={setUsuario} />
        </div>
      </nav>
    </header>
  );
}

export default Header;