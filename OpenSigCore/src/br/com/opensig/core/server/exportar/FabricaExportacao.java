package br.com.opensig.core.server.exportar;

import br.com.opensig.core.shared.modelo.EArquivo;

/**
 * Classe para recuparar a exportação de acordo com o tipo.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class FabricaExportacao {

	private static final FabricaExportacao fb = new FabricaExportacao();

	private FabricaExportacao() {
	}

	/**
	 * Metodo que retorna a instancia da fábrica.
	 * 
	 * @return uma fábrica de exportação.
	 */
	public static FabricaExportacao getInstancia() {
		return fb;
	}

	/**
	 * Metodo que retorna uma classe de exportacao informado o tipo.
	 * 
	 * @param tipo
	 *            da classe que representa a exportacao.
	 * @return a exportacao propriemente dita.
	 */
	public IExportacao getExpotacao(EArquivo tipo) {
		IExportacao exportacao;

		switch (tipo) {
		case PDF:
			exportacao = new ExportacaoPdf();
			break;
		case XLS:
			exportacao = new ExportacaoXls();
			break;
		case CSV:
			exportacao = new ExportacaoCsv();
			break;
		case XML:
			exportacao = new ExportacaoXml();
			break;
		default:
			exportacao = new ExportacaoHtml();
			break;
		}

		return exportacao;
	}
}
