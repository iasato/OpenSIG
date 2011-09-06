package br.com.opensig.fiscal.client.controlador.comando.acao;

import java.util.Map;

import br.com.opensig.core.client.controlador.comando.ComandoAcao;
import br.com.opensig.fiscal.client.visao.form.FormularioInutilizar;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ComandoInutilizar extends ComandoAcao {

	@Override
	public void execute(Map contexto) {
		super.execute(contexto, new AsyncCallback() {
			public void onSuccess(Object result) {
				new FormularioInutilizar(LISTA);
			}

			public void onFailure(Throwable caught) {
			}
		});
	}
}
