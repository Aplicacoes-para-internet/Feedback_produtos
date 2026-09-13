CREATE DATABASE IF NOT EXISTS feedback_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE feedback_db;

CREATE TABLE IF NOT EXISTS Produtos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(500),
    preco DECIMAL(10, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    produto_id INT NOT NULL,
    usuario_id INT NOT NULL,
    nota TINYINT NOT NULL,
    comentario VARCHAR(1000) NOT NULL,
    data_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_feedback_produto
        FOREIGN KEY (produto_id) REFERENCES Produtos(id),

    CONSTRAINT fk_feedback_usuario
        FOREIGN KEY (usuario_id) REFERENCES Usuarios(id),

    CONSTRAINT ck_feedback_nota
        CHECK (nota BETWEEN 1 AND 5)
);