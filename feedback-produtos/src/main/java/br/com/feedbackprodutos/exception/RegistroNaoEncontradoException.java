package br.com.feedbackprodutos.exception;

public class RegistroNaoEncontradoException extends IllegalArgumentException {
    public RegistroNaoEncontradoException(String message) {
        super(message);
    }
}
