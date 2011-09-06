package br.com.opensig.financeiro.client.controlador.comando.boleto;

import java.util.Map;

import br.com.opensig.core.shared.modelo.EArquivo;


public class ComandoGerarHtml extends ComandoGerar {

	public ComandoGerarHtml() {
		super(EArquivo.HTML, false);
	}

	public void execute(Map contexto) {
		super.execute(contexto);
		getGerar().execute(contexto);
	}

}
