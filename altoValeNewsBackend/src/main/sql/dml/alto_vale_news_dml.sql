--USUÁRIOS
INSERT INTO public.usuario (user_id, nome, sobrenome, email, cpf, endereco, cidade, estado, cep, senhahash, tipo)
VALUES (1, 'Admin', 'Admin', 'admin@mail.com', '57266056918', 'Rua dos adms', 'Ibirama', 'SC', '89140-000',
        '$2a$10$psAnY6PFVN.ECCbq0WYO8e0tx2qeCFZ.K6Me8LQpE0sPJ7K7YJVoi', 3);

INSERT INTO public.usuario (user_id, nome, sobrenome, email, cpf, endereco, cidade, estado, cep, senhahash, tipo)
VALUES (2, 'Andre', 'Ludwig', 'andre@mail.com', '06985933599', 'Rua Vargas', 'Ibirama', 'SC', '89140-000',
        '$2a$10$psAnY6PFVN.ECCbq0WYO8e0tx2qeCFZ.K6Me8LQpE0sPJ7K7YJVoi', 2);

INSERT INTO public.usuario (user_id, nome, sobrenome, email, cpf, endereco, cidade, estado, cep, senhahash, tipo)
VALUES (3, 'Mateus', 'Dido', 'mateus@mail.com', '04235874047', 'Rua 15', 'Ibirama', 'SC', '89140-000',
        '$2a$10$psAnY6PFVN.ECCbq0WYO8e0tx2qeCFZ.K6Me8LQpE0sPJ7K7YJVoi', 1);

INSERT INTO public.usuario (user_id, nome, sobrenome, email, cpf, endereco, cidade, estado, cep, senhahash, tipo)
VALUES (4, 'Geraldo', 'Varela', 'geraldo@mail.com', '83022855958', 'Rua 27', 'Ibirama', 'SC', '89140-000',
        '$2a$10$psAnY6PFVN.ECCbq0WYO8e0tx2qeCFZ.K6Me8LQpE0sPJ7K7YJVoi', 0);

--ASSINATURAS
INSERT INTO public.assinatura (assinatura_id, vencimento, ativo)
VALUES (1, '2025-09-30', true);

INSERT INTO public.assinatura (assinatura_id, vencimento, ativo)
VALUES (2, '2025-09-30', true);

INSERT INTO public.assinatura (assinatura_id, vencimento, ativo)
VALUES (3, '2025-05-18', true);

INSERT INTO public.assinatura (assinatura_id, vencimento, ativo)
VALUES (4, DEFAULT, false);

--PUBLICAÇÕES
INSERT INTO public.publicacao (publicacao_id, editor_id, titulo, data, texto, visibilidade_vip, imagem_id, video_id)
VALUES (DEFAULT, 2, 'Reforma BR-470', '2024-09-10 19:45:30.000000',
        'Nunca vai ficar pronta, sem duplicação, os moradores já estão carecas', false, null, null);

INSERT INTO public.publicacao (publicacao_id, editor_id, titulo, data, texto, visibilidade_vip, imagem_id, video_id)
VALUES (DEFAULT, 1,
        'Dólar fecha em R$ 5,65 e tem maior patamar em um mês, com foco em debate nos EUA e apesar de deflação no Brasil; Ibovespa cai',
        '2024-09-10 19:48:57.000000', e'O dólar fechou a sessão desta terça-feira (10) em alta, à medida que o mercado seguia de olho nas eleições norte-americanas. Investidores também repercutiram os novos dados de inflação por aqui e continuaram à espera das decisões de juros dos bancos centrais do Brasil e dos EUA.
Segundo o Instituto Brasileiro de Geografia e Estatística (IBGE), os preços medidos pelo Índice de Preços ao Consumidor Amplo (IPCA) caíram 0,02% em agosto, na primeira deflação do ano.
O resultado veio um pouco melhor que as expectativas, que esperavam uma alta de, em média, 0,01% nos preços.
Investidores do mundo todo também aguardam pelo primeiro e talvez o único debate entre Kamala Harris e Donald Trump, na corrida eleitoral para a Presidência dos Estados Unidos.
Já o Ibovespa, principal índice acionário da bolsa de valores brasileira, fechou em queda.', true, null, null);

--CATEGORIAS
INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Trânsito');

INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Variedades');

INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Economia');

INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Esportes');

INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Educação');

INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Saúde');

INSERT INTO public.categoria (categoria_id, nome)
VALUES (DEFAULT, 'Moda');

--CATEGORIAxPUBLICAÇÃO
INSERT INTO public.categoria_publicacao (categoria_id, publicacao_id)
VALUES (1, 1);

INSERT INTO public.categoria_publicacao (categoria_id, publicacao_id)
VALUES (3, 2);

--COMENTÁRIOS
INSERT INTO public.comentario (comentario_id, publicacao_id, user_id, data, texto)
VALUES (DEFAULT, 1, 4, '2024-09-10', 'Já furei um pneu nos buracos da BR-470!! Está horrível');

INSERT INTO public.comentario (comentario_id, publicacao_id, user_id, data, texto)
VALUES (DEFAULT, 2, 3, '2024-09-10', 'Só tenho Bitcoins, partiu Disney!');

--CURTIDAS
INSERT INTO public.curtida (curtida_id, comentario_id, publicacao_id, usuario_id)
VALUES (DEFAULT, null, 1, 4);

INSERT INTO public.curtida (curtida_id, comentario_id, publicacao_id, usuario_id)
VALUES (DEFAULT, 1, null, 3);

INSERT INTO public.curtida (curtida_id, comentario_id, publicacao_id, usuario_id)
VALUES (DEFAULT, null, 2, 3);