package br.com.opensig.core.client.controlador.comando.exportar;

import java.util.Map;

import br.com.opensig.core.client.controlador.comando.EModo;
import br.com.opensig.core.shared.modelo.EArquivo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Classe de exportacao do tipo HTML.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ComandoExportarHtml extends ComandoExportar {

	/**
	 * Construtor padrao que define o modo como LISTAGEM.
	 */
	public ComandoExportarHtml() {
		this(EModo.LISTAGEM);
	}

	/**
	 * Construtor que recebe como parametro o modo.
	 * 
	 * @param modo
	 *            especificar o modo da acao.
	 */
	public ComandoExportarHtml(EModo modo) {
		super(EArquivo.HTML, modo);
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