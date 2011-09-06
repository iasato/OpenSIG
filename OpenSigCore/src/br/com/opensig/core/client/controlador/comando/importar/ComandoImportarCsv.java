package br.com.opensig.core.client.controlador.comando.importar;

import java.util.Map;

import br.com.opensig.core.shared.modelo.EArquivo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Classe de importacao do tipo CSV.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ComandoImportarCsv extends ComandoImportar {
	
	/**
	 * Construtor padrao.
	 */
	public ComandoImportarCsv() {
		super(EArquivo.CSV);
	}
	
	@Override
	public void execute(final Map contexto) {
		super.execute(contexto, new AsyncCallback() {
			public void onSuccess(Object result) {
				execute();
			}

			public void onFailure(Throwable caught) {
			}
		});
	}

	@Override
	protected void execute() {
		super.execute(contexto);
	}
}
