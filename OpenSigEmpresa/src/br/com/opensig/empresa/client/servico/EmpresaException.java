package br.com.opensig.empresa.client.servico;

import br.com.opensig.core.client.servico.CoreException;

/**
 * Classe que representa uma exceçao do modulo empresa no sistema.
 * @author Pedro H. Lira
 * @version 1.0
 * @since 15/10/2008
 *
 */
public class EmpresaException extends CoreException {
    private static final long serialVersionUID = -5203345892627186453L;

    /**
     * @see CoreException#CoreException
     */
    public EmpresaException() {
        super();
    }

    /**
     * @see CoreException#CoreException(String message)
     */
    public EmpresaException(String message) {
        super(message);
    }

    /**
     * @see CoreException#CoreException(String message, Throwable cause)
     */
    public EmpresaException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see CoreException#CoreException(Throwable cause)
     */
    public EmpresaException(Throwable cause) {
        super(cause);
    }
}
