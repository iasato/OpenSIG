package br.com.opensig.core.client.controlador.comando.lista;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportarHtml;
import br.com.opensig.core.client.servico.OpenSigException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Ext;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.AlertCallback;

/**
 * Classe de exportacao do tipo impressao em tela.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ComandoImprimir extends ComandoExportarHtml {

	@Override
	public void execute(final Map contexto) {
		setAsyncCallback(new AsyncCallback<String>() {

			public void onSuccess(String arg0) {
				LISTA.getPanel().getEl().unmask();
				if (Ext.isOpera()) {
					UtilClient.abrirUrl(GWT.getHostPageBaseURL() + "CoreService?modo=text/html&id=" + arg0);
				} else {
					UtilClient.exportar(GWT.getHostPageBaseURL() + "CoreService?modo=text/html&id=" + arg0);
				}

				if (comando != null) {
					comando.execute(contexto);
				}
			}

			public void onFailure(Throwable arg0) {
				LISTA.getPanel().getEl().unmask();
				if (arg0 instanceof OpenSigException) {
					MessageBox.alert(OpenSigCore.i18n.txtAtencao(), OpenSigCore.i18n.errSessao(), new AlertCallback() {

						public void execute() {
							UtilClient.atualizar();
						}
					});
				} else {
					MessageBox.alert(OpenSigCore.i18n.txtImprimir(), OpenSigCore.i18n.errImprimir());
				}
			}
		});

		super.execute(contexto);
	}
}
