package br.com.opensig.core.server.exportar;

import java.util.Collection;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;

/**
 * Classe que define a exportacao de arquivo no formato de CSV.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoCsv extends AExportacao {

	@Override
	public byte[] getArquivo(ExportacaoListagem lista, String[] empresa, String[][] enderecos, String[][] contatos) {
		// inicio do arquivo
		StringBuffer sb = new StringBuffer(getCabecalho(lista.getRotulos()));
		// registros
		sb.append(getCorpoListagem(lista));
		return sb.toString().getBytes();
	}

	@Override
	public byte[] getArquivo(ExportacaoRegistro registro, Collection<ExportacaoListagem> listas, String[] empresa, String[][] enderecos, String[][] contatos) {
		// inicio do arquivo
		StringBuffer sb = new StringBuffer(getCabecalho(registro.getRotulos()));
		// registros
		sb.append(getCorpoRegistro(listas, registro));
		return sb.toString().getBytes();
	}

	/**
	 * Metodo que gera o cabecalho
	 * 
	 * @param rotulos
	 *            um array de nomes.
	 * @return o cabecalho.
	 */
	public String getCabecalho(String[] rotulos) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rotulos.length; i++) {
			if (rotulos[i] != null) {
				String tag = UtilServer.normaliza(rotulos[i]).toLowerCase().replaceAll("[\\W]", "");
				sb.append("\"" + tag + "\",");
			}
		}
		return sb.substring(0, sb.length() - 1) + "\n";
	}

	/**
	 * Metodo que gera o corpo da listagem.
	 * 
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 * @return o corpo da listagem.
	 */
	public String getCorpoListagem(ExportacaoListagem lista) {
		StringBuffer sb = new StringBuffer();
		int fim = lista.getDados().length - lista.getInicio();
		if (lista.getLimite() > 0 && lista.getLimite() < fim) {
			fim = lista.getLimite();
		}

		for (int j = 0; j < fim; j++) {
			for (int i = 0; i < lista.getRotulos().length; i++) {
				if (lista.getRotulos()[i] != null) {
					sb.append("\"" + getValor(lista.getDados()[j][i]) + "\",");
				}
			}
			sb = sb.replace(sb.length() - 1, sb.length(), "\n");
		}
		return sb.toString();
	}

	/**
	 * Metodo que gera o corpo do registro.
	 * 
	 * @param listas
	 *            os objetos das sub-listas do registro.
	 * @param reg
	 *            o objeto de exportacao do registro.
	 * @return o corpo do registro.
	 */
	public String getCorpoRegistro(Collection<ExportacaoListagem> listas, ExportacaoRegistro reg) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < reg.getRotulos().length; i++) {
			if (reg.getRotulos()[i] != null) {
				sb.append("\"" + getValor(reg.getDados()[i]) + "\",");
			}
		}
		sb = sb.replace(sb.length() - 1, sb.length(), "\n");

		if (listas != null) {
			for (ExportacaoListagem lista : listas) {
				sb.append("::" + UtilServer.normaliza(lista.getNome()) + "::\n");
				sb.append(getCabecalho(lista.getRotulos()));
				sb.append(getCorpoListagem(lista));
			}
		}

		return sb.toString();
	}
}
