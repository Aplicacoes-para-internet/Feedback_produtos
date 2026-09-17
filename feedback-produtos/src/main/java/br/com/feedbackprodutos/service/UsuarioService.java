package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.UsuarioDAO;
import br.com.feedbackprodutos.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
	private final UsuarioDAO dao = new UsuarioDAO();

	public List<Usuario> listar() throws SQLException { return dao.listar(); }
	public Usuario buscarPorId(int id) throws SQLException {
		validarId(id);
		return dao.buscarPorId(id);
	}

	public void salvar(Usuario usuario) throws SQLException {
		validarUsuario(usuario);
		if (dao.existeEmail(usuario.getEmail(), null)) {
			throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
		}
		dao.salvar(usuario);
	}

	public void atualizar(Usuario usuario) throws SQLException {
		validarUsuario(usuario);
		if (dao.existeEmail(usuario.getEmail(), usuario.getId())) {
			throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
		}
		if (usuario.getId() <= 0 || !dao.atualizar(usuario)) {
			throw new IllegalArgumentException("Usuário não encontrado.");
		}
	}

	public void excluir(int id) throws SQLException {
		validarId(id);
		if (dao.possuiFeedbackVinculado(id)) {
			throw new IllegalArgumentException("Não é possível excluir este usuário porque existem feedbacks vinculados a ele.");
		}
		if (!dao.excluir(id)) {
			throw new IllegalArgumentException("Usuário não encontrado.");
		}
	}

	private void validarUsuario(Usuario usuario) {
		if (usuario == null) {
			throw new IllegalArgumentException("Usuário inválido.");
		}
		if (usuario.getNome() == null || usuario.getNome().isBlank()) {
			throw new IllegalArgumentException("Informe o nome do usuário.");
		}
		if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
			throw new IllegalArgumentException("Informe o e-mail do usuário.");
		}
		if (!usuario.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw new IllegalArgumentException("Informe um e-mail válido.");
		}
	}

	private void validarId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("Informe um ID de usuário válido.");
		}
	}
}
