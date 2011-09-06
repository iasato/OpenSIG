package br.com.opensig.financeiro.client.controlador.comando.boleto;

import java.util.Map;

import br.com.opensig.core.shared.modelo.EArquivo;

public class ComandoGerarPdf extends ComandoGerar {

	public ComandoGerarPdf() {
		super(EArquivo.PDF, false);
	}

	public void execute(Map contexto) {
		super.execute(contexto);
		getGerar().execute(contexto);
	}
}
