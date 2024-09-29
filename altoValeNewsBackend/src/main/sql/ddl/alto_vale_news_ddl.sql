create table usuario
(
    user_id   serial primary key,
    nome      varchar  not null,
    sobrenome varchar  not null,
    email     varchar  not null
        constraint email_unique
            unique,
    cpf       varchar  not null
        constraint cpf_unique
            unique,
    endereco  varchar,
    cidade    varchar,
    estado    varchar,
    cep       varchar,
    senhahash varchar  not null,
    tipo      smallint not null
        constraint usuario_tipo_check
            check (tipo = ANY (ARRAY[0, 1, 2, 3]))
);

create table assinatura
(
    assinatura_id integer primary key references usuario,
    vencimento    date    not null default date(localtimestamp),
    ativo         boolean not null default false
);

create table categoria
(
    categoria_id serial primary key,
    nome         varchar not null
);

create table media_file
(
    id   serial primary key,
    path varchar      not null,
    name varchar(255) not null,
    type varchar(100) not null
);

create table publicacao
(
    publicacao_id    serial
        primary key,
    editor_id        integer   not null
        references usuario,
    titulo           varchar   not null,
    data             timestamp not null default localtimestamp,
    texto            text      not null,
    visibilidade_vip boolean            default false not null,
    imagem_id        integer
        references media_file,
    video_id         integer
        references media_file
);

create table categoria_publicacao
(
    categoria_id  integer not null,
    publicacao_id integer not null,
    primary key (categoria_id, publicacao_id),
    foreign key (categoria_id) references categoria (categoria_id),
    foreign key (publicacao_id) references publicacao (publicacao_id)
);


create table comentario
(
    comentario_id serial
        primary key,
    publicacao_id integer not null
        references publicacao,
    user_id       integer not null
        references usuario,
    data          date    not null,
    texto         text    not null
);

create table curtida
(
    curtida_id    serial primary key,
    comentario_id integer references comentario,
    publicacao_id integer references publicacao,
    usuario_id    integer not null references usuario,
    check (
        (comentario_id is not null and publicacao_id is null) or
        (comentario_id is null and publicacao_id is not null)
        )
);

create index idx_curtida_comentario_id on curtida (comentario_id);
create index idx_curtida_publicacao_id on curtida (publicacao_id);
create index idx_comentario_publicacao_id on comentario (publicacao_id)

CREATE TABLE log_verificacao_assinaturas
(
    id                      SERIAL PRIMARY KEY,
    data_execucao           TIMESTAMP NOT NULL,
    usuarios_atualizados    INTEGER   NOT NULL,
    assinaturas_desativadas INTEGER   NOT NULL
);