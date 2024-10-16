import React, { useState, useRef } from "react";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faSignOutAlt } from "@fortawesome/free-solid-svg-icons";
import { Navbar, Nav, Container, Overlay } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import LoginCard from "./LoginCard"; // Assumindo que LoginCard é um componente separado

function MyNavBar({ usuario, setUsuario }) {
  const [showLoginCard, setShowLoginCard] = useState(false);
  const target = useRef(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setUsuario(null);
  };

  return (
    <Navbar expand="lg" className="nav-custom">
      <Navbar.Brand as={Link} to="/">
        <img src="/img/logo.svg" alt="Alto Vale News" height="40" />
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="ms-auto">
          <Nav.Link as={Link} to="/">
            Home
          </Nav.Link>
          {usuario && usuario.tipo >= 2 && (
            <Nav.Link as={Link} to="/painel-editor">
              Painel do Editor
            </Nav.Link>
          )}
          {usuario && usuario.tipo === 3 && (
            <Nav.Link as={Link} to="/painel-admin">
              Painel do Admin
            </Nav.Link>
          )}
          <Nav.Link as={Link} to="/contato">
            Contato
          </Nav.Link>
          {usuario ? (
            <>
              <Nav.Link as={Link} to="/editar-perfil">
                <FontAwesomeIcon icon={faUser} /> Editar Perfil
              </Nav.Link>
              <Nav.Link onClick={handleLogout}>
                <FontAwesomeIcon icon={faSignOutAlt} /> Sair
              </Nav.Link>
            </>
          ) : (
            <Nav.Link
              ref={target}
              onClick={() => setShowLoginCard(!showLoginCard)}
            >
              Cadastro/Login
            </Nav.Link>
          )}
        </Nav>
      </Navbar.Collapse>
      <Overlay
        target={target.current}
        show={showLoginCard}
        placement="bottom-end"
      >
        {({ placement, arrowProps, show: _show, popper, ...props }) => (
          <div
            {...props}
            style={{
              position: "absolute",
              padding: "2px",
              borderRadius: 3,
              ...props.style,
            }}
          >
            <LoginCard
              onClose={() => setShowLoginCard(false)}
              onLogin={(user) => {
                setUsuario(user);
                setShowLoginCard(false);
              }}
            />
          </div>
        )}
      </Overlay>
    </Navbar>
  );
}

export default MyNavBar;
