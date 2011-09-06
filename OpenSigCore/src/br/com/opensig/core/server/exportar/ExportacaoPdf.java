package br.com.opensig.core.server.exportar;

import java.util.Collection;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;

/**
 * Classe que define a exportacao de arquivo no formato de PDF.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoPdf extends ExportacaoHtml {

	@Override
	public byte[] getArquivo(ExportacaoListagem lista, String[] empresa, String[][] enderecos, String[][] contatos) {
		byte[] obj = super.getArquivo(lista, empresa, enderecos, contatos);
		return UtilServer.getPDF(obj);
	}

	@Override
	public byte[] getArquivo(ExportacaoRegistro registro, Collection<ExportacaoListagem> listas, String[] empresa, String[][] enderecos, String[][] contatos) {
		byte[] obj = super.getArquivo(registro, listas, empresa, enderecos, contatos);
		return UtilServer.getPDF(obj);
	}
}
