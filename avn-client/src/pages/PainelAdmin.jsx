import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { isAdmin, getUserIdFromToken } from "../utils/authUtils";
import {
    listarUsuarios,
    alterarTipoUsuario,
} from "../services/api";

function PainelAdmin() {
    const [usuarios, setUsuarios] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        if (!isAdmin()) {
            navigate("/acesso-negado");
        } else {
            fetchUsuarios();
        }
    }, [navigate]);

    const fetchUsuarios = async () => {
        try {
            const response = await listarUsuarios();
            const usuariosOrdenados = response.data.sort((a, b) => a.userId - b.userId);
            setUsuarios(usuariosOrdenados);
        } catch (error) {
            console.error("Erro ao buscar usuários:", error);
            alert("Erro ao carregar lista de usuários.");
        }
    };

    const updateTipoUsuario = async (id, tipoUsuario) => {
        try {
            const adminId = getUserIdFromToken();
            await alterarTipoUsuario(id, tipoUsuario, adminId);
            alert("Tipo de usuário atualizado com sucesso");
            fetchUsuarios(); // Atualiza a lista
        } catch (error) {
            console.error("Erro ao atualizar tipo de usuário:", error);
            alert("Erro ao atualizar tipo de usuário");
        }
    };

    return (
        <div className="container">
            <h1>Painel de Administração</h1>
            <table className="table table-striped" id="usuariosTable">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Tipo de Usuário</th>
                    <th>Ação</th>
                </tr>
                </thead>
                <tbody>
                {usuarios.map((usuario) => (
                    <tr key={usuario.userId}>
                        <td>{usuario.userId}</td>
                        <td>{usuario.nome}</td>
                        <td>{usuario.email}</td>
                        <td>
                            <input
                                type="radio"
                                name={`tipo-${usuario.userId}`}
                                value="0"
                                defaultChecked={usuario.tipo === 0}
                            />{" "}
                            Usuário
                            <input
                                type="radio"
                                name={`tipo-${usuario.userId}`}
                                value="1"
                                defaultChecked={usuario.tipo === 1}
                            />{" "}
                            Usuário VIP
                            <input
                                type="radio"
                                name={`tipo-${usuario.userId}`}
                                value="2"
                                defaultChecked={usuario.tipo === 2}
                            />{" "}
                            Editor
                            <input
                                type="radio"
                                name={`tipo-${usuario.userId}`}
                                value="3"
                                defaultChecked={usuario.tipo === 3}
                            />{" "}
                            Administrador
                        </td>
                        <td>
                            <button
                                className="btn btn-primary"
                                onClick={() => {
                                    const tipo = document.querySelector(
                                        `input[name="tipo-${usuario.userId}"]:checked`
                                    ).value;
                                    updateTipoUsuario(usuario.userId, tipo);
                                }}
                            >
                                Salvar
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default PainelAdmin;
