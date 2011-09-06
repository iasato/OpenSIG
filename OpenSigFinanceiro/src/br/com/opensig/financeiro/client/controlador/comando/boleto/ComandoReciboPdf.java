package br.com.opensig.financeiro.client.controlador.comando.boleto;

import br.com.opensig.core.shared.modelo.EArquivo;

public class ComandoReciboPdf extends ComandoRecibo {

	public ComandoReciboPdf() {
		super(EArquivo.PDF);
	}
}
