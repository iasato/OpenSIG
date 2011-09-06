package br.com.opensig.financeiro.client.controlador.comando.boleto;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Ext;
import com.gwtext.client.widgets.MessageBox;

public class ComandoGerarImprimir extends ComandoGerarHtml {

	public void execute(final Map contexto) {
		setAsyncCallback(new AsyncCallback<String>() {

			public void onSuccess(String arg0) {
				MessageBox.hide();
				if (Ext.isOpera()) {
					UtilClient.abrirUrl(GWT.getModuleBaseURL() + "/FinanceiroService?modo=text/html&id=" + arg0);
				} else {
					UtilClient.exportar("FinanceiroService?modo=text/html&id=" + arg0);
				}

				if (comando != null) {
					comando.execute(contexto);
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(OpenSigCore.i18n.txtImprimir(), OpenSigCore.i18n.errImprimir());
			}
		});

		super.execute(contexto);
		getGerar().execute(contexto);
	}
}
