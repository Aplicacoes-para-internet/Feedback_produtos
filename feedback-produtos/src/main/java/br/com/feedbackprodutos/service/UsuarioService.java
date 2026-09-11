package br.com.feedbackprodutos.service;

import br.com.feedbackprodutos.dao.UsuarioDAO;
import br.com.feedbackprodutos.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
	private final UsuarioDAO dao = new UsuarioDAO();

	public List<Usuario> listar() throws SQLException { return dao.listar(); }
	public Usuario buscarPorId(int id) throws SQLException { return dao.buscarPorId(id); }

	public void salvar(Usuario usuario) throws SQLException {
		if (usuario == null || usuario.getNome() == null || usuario.getNome().isBlank()
				|| usuario.getEmail() == null || usuario.getEmail().isBlank()) {
			throw new IllegalArgumentException("Informe nome e e-mail.");
		}
		dao.salvar(usuario);
	}
}
