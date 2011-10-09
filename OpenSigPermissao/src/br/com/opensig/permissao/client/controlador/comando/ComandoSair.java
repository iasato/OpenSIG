package br.com.opensig.permissao.client.controlador.comando;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.comando.AComando;

import com.google.gwt.user.client.Window;
import com.gwtext.client.widgets.MessageBox;

public class ComandoSair extends AComando {

	public void execute(Map contexto) {
		MessageBox.confirm(OpenSigCore.i18n.txtSair(), OpenSigCore.i18n.msgSair(), new MessageBox.ConfirmCallback() {
			public void execute(String btnID) {
				if (btnID.equalsIgnoreCase("yes")) {
					Window.Location.reload();
				}
			}
		});
	}
}
