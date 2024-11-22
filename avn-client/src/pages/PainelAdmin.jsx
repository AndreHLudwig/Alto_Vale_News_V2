import React, { useState, useEffect } from "react";
import { usuariosAPI } from "../services/api";
import { useAuth } from "../auth";

function PainelAdmin() {
    const [usuarios, setUsuarios] = useState([]);
    const { usuario } = useAuth();
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUsuarios();
    }, []);

    const fetchUsuarios = async () => {
        try {
            setLoading(true);
            const response = await usuariosAPI.listar();
            const usuariosOrdenados = response.data.sort((a, b) => a.userId - b.userId);
            setUsuarios(usuariosOrdenados);
        } catch (error) {
            console.error("Erro ao buscar usuários:", error);
            setError("Erro ao carregar lista de usuários.");
        } finally {
            setLoading(false);
        }
    };

    const updateTipoUsuario = async (id, tipoUsuario) => {
        try {
            await usuariosAPI.alterarTipo(id, tipoUsuario, usuario.userId);
            alert("Tipo de usuário atualizado com sucesso");
            fetchUsuarios();
        } catch (error) {
            console.error("Erro ao atualizar tipo de usuário:", error);
            alert("Erro ao atualizar tipo de usuário");
        }
    };

    if (loading) {
        return <div className="container py-4">Carregando...</div>;
    }

    if (error) {
        return <div className="container py-4 alert alert-danger">{error}</div>;
    }

    return (
        <div className="container py-4">
            <h1 className="mb-4">Painel de Administração</h1>
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
                {usuarios.map((user) => (
                    <tr key={user.userId}>
                        <td>{user.userId}</td>
                        <td>{user.nome}</td>
                        <td>{user.email}</td>
                        <td>
                            {[
                                { value: 0, label: "Usuário" },
                                { value: 1, label: "Usuário VIP" },
                                { value: 2, label: "Editor" },
                                { value: 3, label: "Administrador" }
                            ].map(tipo => (
                                <div key={tipo.value} className="form-check form-check-inline">
                                    <input
                                        className="form-check-input"
                                        type="radio"
                                        name={`tipo-${user.userId}`}
                                        value={tipo.value}
                                        defaultChecked={user.tipo === tipo.value}
                                        id={`tipo-${user.userId}-${tipo.value}`}
                                    />
                                    <label
                                        className="form-check-label"
                                        htmlFor={`tipo-${user.userId}-${tipo.value}`}
                                    >
                                        {tipo.label}
                                    </label>
                                </div>
                            ))}
                        </td>
                        <td>
                            <button
                                className="btn btn-primary"
                                onClick={() => {
                                    const tipo = document.querySelector(
                                        `input[name="tipo-${user.userId}"]:checked`
                                    ).value;
                                    updateTipoUsuario(user.userId, tipo);
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