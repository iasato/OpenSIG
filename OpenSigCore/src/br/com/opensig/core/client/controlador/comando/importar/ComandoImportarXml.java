package br.com.opensig.core.client.controlador.comando.importar;

import java.util.Map;

import br.com.opensig.core.shared.modelo.EArquivo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Classe de importacao do tipo XML.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ComandoImportarXml extends ComandoImportar {
	
	/**
	 * Construtor padrao.
	 */
	public ComandoImportarXml() {
		super(EArquivo.XML);
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
