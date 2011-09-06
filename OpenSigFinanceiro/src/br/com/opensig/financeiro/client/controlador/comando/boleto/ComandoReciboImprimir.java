package br.com.opensig.financeiro.client.controlador.comando.boleto;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Ext;
import com.gwtext.client.widgets.MessageBox;

public class ComandoReciboImprimir extends ComandoReciboHtml {

	public ComandoReciboImprimir() {
		super();
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
	}
}
