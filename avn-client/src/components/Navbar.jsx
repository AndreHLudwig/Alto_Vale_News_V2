import React from "react";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faSignOutAlt } from "@fortawesome/free-solid-svg-icons";
import { Navbar, Nav, Container } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";

function MyNavBar({ usuario }) {
  const autenticado = !!usuario;

  return (
    <Navbar expand="lg" className="bg-custom">
      <Container>
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
            {autenticado ? (
              <>
                <Nav.Link as={Link} to="/editar-perfil">
                  <FontAwesomeIcon icon={faUser} /> Editar Perfil
                </Nav.Link>
                <Nav.Link as={Link} to="/logout">
                  <FontAwesomeIcon icon={faSignOutAlt} /> Sair
                </Nav.Link>
              </>
            ) : (
              <Nav.Link as={Link} to="/auth">
                Cadastro/Login
              </Nav.Link>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default MyNavBar;
