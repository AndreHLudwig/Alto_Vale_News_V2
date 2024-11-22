import React, { useRef } from "react";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faSignOutAlt } from "@fortawesome/free-solid-svg-icons";
import { Navbar, Nav, Overlay } from "react-bootstrap";
import { useAuth, authUtils } from "../auth";
import { useLogin } from "../auth/LoginContext";
import LoginCard from "./LoginCard";

function MyNavBar() {
    const { usuario, logout } = useAuth();
    const { showLoginCard, openLoginCard, closeLoginCard } = useLogin();
    const loginButtonRef = useRef(null);

    const handleLogout = async () => {
        try {
            await logout();
        } catch (error) {
            console.error("Erro ao fazer logout:", error);
        }
    };

    const isAuthenticated = authUtils.isAuthenticated();
    const canAccessAdminPanel = authUtils.isAdmin();
    const canAccessEditorPanel = authUtils.canEditContent();

    return (
        <Navbar expand="lg" className="nav-custom">
            <div className="container">
                <Navbar.Brand as={Link} to="/">
                    <img src="/img/logo.svg" alt="Alto Vale News" height="40" />
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="basic-navbar-nav" />

                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ms-auto">
                        <Nav.Link as={Link} to="/">
                            Home
                        </Nav.Link>

                        {canAccessEditorPanel && (
                            <Nav.Link as={Link} to="/painel-editor">
                                Painel do Editor
                            </Nav.Link>
                        )}

                        {canAccessAdminPanel && (
                            <Nav.Link as={Link} to="/painel-admin">
                                Painel do Admin
                            </Nav.Link>
                        )}

                        <Nav.Link as={Link} to="/contato">
                            Contato
                        </Nav.Link>

                        {isAuthenticated ? (
                            <>
                                <Nav.Link as={Link} to="/editar-perfil">
                                    <FontAwesomeIcon icon={faUser} /> Perfil
                                </Nav.Link>
                                <Nav.Link onClick={handleLogout}>
                                    <FontAwesomeIcon icon={faSignOutAlt} /> Sair
                                </Nav.Link>
                            </>
                        ) : (
                            <Nav.Link
                                ref={loginButtonRef}
                                onClick={() => openLoginCard()}
                            >
                                Cadastro/Login
                            </Nav.Link>
                        )}
                    </Nav>
                </Navbar.Collapse>

                <Overlay
                    target={loginButtonRef.current}
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
                                onClose={closeLoginCard}
                                onLoginSuccess={closeLoginCard}
                            />
                        </div>
                    )}
                </Overlay>
            </div>
        </Navbar>
    );
}

export default MyNavBar;