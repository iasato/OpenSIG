package br.com.opensig.financeiro.server.boleto;

import br.com.opensig.core.shared.modelo.EArquivo;

/**
 * Classe para recuparar o boleto de acordo com o tipo.
 * 
 * @author Pedro H. Lira
 * @since 13/04/2009
 * @version 1.0
 */
public class FabricaRecibo {

	private static final FabricaRecibo fb = new FabricaRecibo();

	private FabricaRecibo() {
	}

	/**
	 * Metodo que retorna a instancia da fábrica.
	 * 
	 * @return uma fábrica de exportação.
	 */
	public static FabricaRecibo getInstancia() {
		return fb;
	}

	/**
	 * Metodo que retorna uma classe de boleto informado o tipo.
	 * 
	 * @param tipo
	 *            da classe que representa a exportacao.
	 * @return a exportacao propriemente dita.
	 */
	public IRecibo getRecibo(EArquivo tipo) {
		IRecibo boleto;

		switch (tipo) {
		case PDF:
			boleto = new ReciboPdf();
			break;
		default:
			boleto = new ReciboHtml();
			break;
		}

		return boleto;
	}
}
