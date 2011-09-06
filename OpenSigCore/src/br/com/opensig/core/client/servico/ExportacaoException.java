package br.com.opensig.core.client.servico;

/**
 * Classe que representa uma exceçao de exportacao do sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * 
 */
public class ExportacaoException extends CoreException {
	private static final long serialVersionUID = -5203345892627186453L;

	/**
	 * @see CoreException#CoreException
	 */
	public ExportacaoException() {
		super();
	}

	/**
	 * @see CoreException#CoreException(String message)
	 */
	public ExportacaoException(String message) {
		super(message);
	}

	/**
	 * @see CoreException#CoreException(String message, Throwable cause)
	 */
	public ExportacaoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see CoreException#CoreException(Throwable cause)
	 */
	public ExportacaoException(Throwable cause) {
		super(cause);
	}
}
