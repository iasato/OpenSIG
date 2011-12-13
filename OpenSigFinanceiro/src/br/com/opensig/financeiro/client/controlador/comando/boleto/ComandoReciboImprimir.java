package br.com.opensig.financeiro.client.controlador.comando.boleto;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.MessageBox;

public class ComandoReciboImprimir extends ComandoReciboHtml {

	public ComandoReciboImprimir() {
		super();
		setAsyncCallback(new AsyncCallback<String>() {

			public void onSuccess(String arg0) {
				MessageBox.hide();
				UtilClient.exportar("ExportacaoService?imp=true&id=" + arg0);

				if (comando != null) {
					comando.execute(contexto);
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(OpenSigCore.i18n.txtImprimir(), OpenSigCore.i18n.errImprimir());
			}
		});
	}
}
