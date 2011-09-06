package br.com.opensig.financeiro.client.controlador.comando.boleto;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.shared.modelo.EArquivo;

import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.MessageBox;

public class ComandoRecibo extends ComandoGerar {

	public ComandoRecibo() {
		this(EArquivo.HTML);
	}

	public ComandoRecibo(EArquivo tipo) {
		super(tipo, true);
	}

	public void execute(Map contexto) {
		super.execute(contexto);
		final Record rec = LISTA.getPanel().getSelectionModel().getSelected();

		if (rec.getAsBoolean("finRecebimentoQuitado")) {
			getGerar().execute(contexto);
		} else {
			MessageBox.alert(OpenSigCore.i18n.txtBoleto(), OpenSigCore.i18n.errRecibo());
		}
	}
}
