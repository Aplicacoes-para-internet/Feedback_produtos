package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.UsuarioDAO;
import br.com.feedbackprodutos.exception.RegistroNaoEncontradoException;
import br.com.feedbackprodutos.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO dao = new UsuarioDAO();

    public List<Usuario> listar() throws SQLException {
        return dao.listar();
    }

    public Usuario buscarPorId(int id) throws SQLException {
        return dao.buscarPorId(id);
    }

    public Usuario salvar(Usuario usuario) throws SQLException {
        validar(usuario, null);
        dao.salvar(usuario);
        return usuario;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        if (usuario == null || usuario.getId() <= 0) {
            throw new IllegalArgumentException("Usuário inválido para atualização.");
        }
        if (dao.buscarPorId(usuario.getId()) == null) {
            throw new RegistroNaoEncontradoException("Usuário não encontrado.");
        }
        validar(usuario, usuario.getId());
        dao.atualizar(usuario);
    }

    public void excluir(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("ID do usuário inválido.");
        }
        if (dao.buscarPorId(id) == null) {
            throw new RegistroNaoEncontradoException("Usuário não encontrado.");
        }
        dao.excluir(id);
    }

    private void validar(Usuario usuario, Integer idIgnorado) throws SQLException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário inválido.");
        }
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório.");
        }
        if (usuario.getNome().length() > 120) {
            throw new IllegalArgumentException("O nome do usuário deve ter no máximo 120 caracteres.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("O e-mail do usuário é obrigatório.");
        }

        String email = usuario.getEmail().trim();
        if (email.length() > 180) {
            throw new IllegalArgumentException("O e-mail deve ter no máximo 180 caracteres.");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        usuario.setEmail(email);

        if (dao.existeEmail(email, idIgnorado)) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
        }
    }
}
