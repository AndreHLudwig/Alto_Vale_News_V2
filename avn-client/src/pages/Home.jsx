import React, { useState, useEffect } from "react";
import { Container } from "react-bootstrap";
import { publicacoesAPI, mediaAPI } from '../services/api';
import ListaPublicacoes from '../components/ListaPublicacoes';

function Home() {
    const [publicacoes, setPublicacoes] = useState([]);
    const [erro, setErro] = useState(null);

    useEffect(() => {
        carregarPublicacoes();
    }, []);

    const carregarPublicacoes = async () => {
        try {
            const response = await publicacoesAPI.listar();

            const publicacoesComImagensAnexadas = await Promise.all(
                response.data.map(async (publicacao) => {
                    if (publicacao.imagem?.id) {
                        try {
                            const imagemResponse = await mediaAPI.obter(publicacao.imagem.id);
                            publicacao.imagemUrl = URL.createObjectURL(imagemResponse.data);
                        } catch (error) {
                            console.error(`Erro ao carregar imagem para publicação ${publicacao.publicacaoId}:`, error);
                            publicacao.imagemUrl = null;
                        }
                    }
                    return publicacao;
                })
            );

            setPublicacoes(publicacoesComImagensAnexadas);
            setErro(null);
        } catch (error) {
            console.error("Erro ao carregar publicações:", error);
            setErro("Falha ao carregar publicações. Por favor, tente novamente mais tarde.");
        }
    };

    useEffect(() => {
        return () => {
            publicacoes.forEach(publicacao => {
                if (publicacao.imagemUrl) {
                    URL.revokeObjectURL(publicacao.imagemUrl);
                }
            });
        };
    }, [publicacoes]);

    if (erro) {
        return <Container><p className="text-danger">{erro}</p></Container>;
    }

    return (
        <Container className="my-4">
            <h1 className="text-center mb-4">Alto Vale News</h1>
            <p className="lead text-center mb-5">
                Seu portal de Notícias do Alto Vale do Itajaí!
            </p>
            <ListaPublicacoes publicacoes={publicacoes} />
        </Container>
    );
}

export default Home;