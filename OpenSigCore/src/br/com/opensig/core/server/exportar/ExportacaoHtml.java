package br.com.opensig.core.server.exportar;

import java.text.DateFormat;
import java.util.Collection;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;

/**
 * Classe que define a exportacao de arquivo no formato de HTML.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoHtml extends AExportacao {

	@Override
	public byte[] getArquivo(ExportacaoListagem lista, String[] empresa, String[][] enderecos, String[][] contatos) {
		// inicio do arquivo
		StringBuffer sb = new StringBuffer("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html xmlns='http://www.w3.org/1999/xhtml'>");
		// estilo do arquivo
		sb.append(getEstilo("landscape", lista.getNome()));
		// cabecalho da empresa
		sb.append(getCabecalhoEmpresa(empresa));
		// inicio da listagem
		sb.append("<table>");
		// cabeçalho da listagem
		sb.append(getCabecalhoListagem(lista));
		// corpo da listagem
		sb.append(getCorpoListagem(lista));
		// rodape da listagem
		sb.append(getRodapeListagem(lista));
		// fim da listagem
		sb.append("</table>");
		// rodape da empresa
		sb.append(getRodapeEmpresa(enderecos, contatos));
		// fim do arquivo
		sb.append("</body></html>");
		// normaliza
		return UtilServer.normaliza(sb.toString()).getBytes();
	}

	@Override
	public byte[] getArquivo(ExportacaoRegistro registro, Collection<ExportacaoListagem> listas, String[] empresa, String[][] enderecos, String[][] contatos) {
		// inicio do arquivo
		StringBuffer sb = new StringBuffer("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">");
		// estilo do arquivo
		sb.append(getEstilo(listas == null ? "portrait" : "landscape", registro.getNome()));
		// cabecalho da empresa
		sb.append(getCabecalhoEmpresa(empresa));
		// inicio do registro
		sb.append("<table>");
		// cabeçalho do registro
		sb.append(getCabecalhoRegistro(registro));
		// corpo do registro
		sb.append(getCorpoRegistro(registro));
		// fim do registro
		sb.append("</table>");
		// listas do registro
		if (listas != null) {
			for (ExportacaoListagem lista : listas) {
				// inicio listagem
				sb.append("<hr /><table>");
				// cabecalho da listagem
				sb.append(getCabecalhoListagem(lista));
				// corpo da listagem
				sb.append(getCorpoListagem(lista));
				// rodape da listagem
				sb.append(getRodapeListagem(lista));
				// fim da listagem
				sb.append("</table>");
			}
		}
		// rodape da empresa
		sb.append(getRodapeEmpresa(enderecos, contatos));
		// fim do arquivo
		sb.append("</body></html>");
		// normaliza
		return UtilServer.normaliza(sb.toString()).getBytes();
	}

	/**
	 * Metodo que gera o cabecalho do registro.
	 * 
	 * @param reg
	 *            o objeto de exportacao de registro.
	 * @return o cabecalho do registro.
	 */
	public String getCabecalhoRegistro(ExportacaoRegistro reg) {
		String cabecalho = "<caption>:: " + reg.getNome() + " ::</caption>";
		return cabecalho;
	}

	/**
	 * Metodo que gera o corpo do registro.
	 * 
	 * @param reg
	 *            o objeto de exportacao do registro.
	 * @return o corpo do registro.
	 */
	public String getCorpoRegistro(ExportacaoRegistro reg) {
		int col = 4;
		int rest = col - (reg.getRotulos().length % col);
		StringBuffer sb = new StringBuffer("<tbody><tr>");

		for (int i = 0; i < reg.getRotulos().length; i++) {
			if (i != 0 && i % col == 0) {
				sb.append("</tr><tr>");
			}
			sb.append("<td><b>" + reg.getRotulos()[i] + "</b>: " + getValor(reg.getDados()[i]) + "</td>");
		}
		if (rest != col) {
			sb.append("<td colspan='" + rest + "'>&nbsp;</td>");
		}
		sb.append("</tr></tbody>");
		return sb.toString();
	}

	/**
	 * Metodo que gera o cabecalho da exportacao com os dados da empresa.
	 * 
	 * @param empresa
	 *            o array de dados da empresa.
	 * @return o cabecalho da exportacao.
	 */
	public String getCabecalhoEmpresa(String[] empresa) {
		// dados da empresa
		StringBuffer sb = new StringBuffer("<table><tbody><tr style='height: 10px;'>");
		sb.append("<td>" + empresa[2] + "</td>");
		sb.append("<td align='right'>" + UtilServer.CONF.get("txtData") + " :: " + UtilServer.formataData(UtilServer.getData(), DateFormat.MEDIUM) + " "
				+ UtilServer.formataHora(UtilServer.getData(), DateFormat.MEDIUM) + "</td></tr>");
		sb.append("<tr style='height: 10px;'><td>" + UtilServer.CONF.get("txtEntidadeDoc1") + ": " + empresa[5] + " " + UtilServer.CONF.get("txtEntidadeDoc2") + ": " + empresa[6] + "</td>");
		sb.append("<td align='right'>" + UtilServer.CONF.get("txtUsuario") + " :: " + UtilServer.CONF.get("usuario") + "</td></tr>");
		// finalizando
		sb.append("</tbody></table><hr />");
		return sb.toString();
	}

	/**
	 * Metodo que gera o rodape da exportacao com os dados da empresa.
	 * 
	 * @param enderecos
	 *            os dados dos enderecos.
	 * @param contatos
	 *            os dados dos contatos.
	 * @return o rodape da exportacao.
	 */
	public String getRodapeEmpresa(String[][] enderecos, String[][] contatos) {
		// dados do endereco
		StringBuffer sbEndereco = new StringBuffer("<table><tbody>");
		for (String[] endereco : enderecos) {
			sbEndereco.append("<tr style='height: 10px;'>");
			sbEndereco.append("<td style='width:50px'>" + endereco[2] + "::</td>");
			sbEndereco.append("<td>" + endereco[7] + ", " + endereco[8] + "  " + endereco[9] + " " + endereco[10] + " " + endereco[11] + " - " + endereco[3] + " " + endereco[4] + " " + endereco[6]
					+ "</td>");
			sbEndereco.append("</tr>");
		}
		sbEndereco.append("</tbody></table>");

		// dados do contato
		StringBuffer sbContato = new StringBuffer("<table><tbody>");
		for (String[] contato : contatos) {
			sbContato.append("<tr style='height: 10px;'>");
			sbContato.append("<td align='right'>" + contato[2] + "::</td>");
			sbContato.append("<td>" + contato[3] + "</td>");
			sbContato.append("</tr>");
		}
		sbContato.append("</tbody></table>");

		// alinhamento
		StringBuffer sb = new StringBuffer("<hr /><table><tbody><tr style='height: 10px;'>");
		sb.append("<td style='width:70%'>" + sbEndereco.toString() + "</td>");
		sb.append("<td style='width:30%'>" + sbContato.toString() + "</td>");
		sb.append("</tr></tbody></table>");

		return sb.toString();
	}

	/**
	 * Metodo que gera os estilos usados pela exportacao.
	 * 
	 * @param size
	 *            o tamanho da pagina.
	 * @param titulo
	 *            o titulo da pagina.
	 * @return o estilo usado.
	 */
	public String getEstilo(String size, String titulo) {
		StringBuffer sb = new StringBuffer("<head>");
		sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
		sb.append("<style type=\"text/css\" media=\"all\">");
		sb.append("@page {size: " + size + "; margin: 10px;}");
		sb.append("table {width: 100%;border-spacing: 0px;border-bottom: none; font-family: serif; font-size: 12px;}");
		sb.append("caption {height: 30px;font-size: 14px;font-weight: bold;}");
		sb.append("thead tr {height: 30px;vertical-align: top; text-align: left; text-transform: uppercase;font-weight: bold;}");
		sb.append("tfoot tr {height: 30px;vertical-align: bottom; text-transform: uppercase;font-weight: bold;}");
		sb.append("tbody tr {height: 20px;vertical-align: middle;}");
		sb.append("</style>");
		sb.append("<title>" + titulo + "</title></head>");
		sb.append("<body>");
		return sb.toString();
	}

	/**
	 * Metodo que gera o cabecalho da listagem.
	 * 
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 * @return o cabecalho da listagem.
	 */
	public String getCabecalhoListagem(ExportacaoListagem lista) {
		StringBuffer sb = new StringBuffer("<caption>:: " + lista.getNome() + " ::</caption><thead><tr>");
		for (int i = 0; i < lista.getRotulos().length; i++) {
			if (lista.getRotulos()[i] != null) {
				sb.append("<th style='width:" + (lista.getTamanhos()[i] + 5) + "px'>" + lista.getRotulos()[i] + "</th>");
			}
		}
		sb.append("</thead>");
		return sb.toString();
	}

	/**
	 * Metodo que gera o corpo da listagem.
	 * 
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 * @return o corpo da listagem.
	 */
	public String getCorpoListagem(ExportacaoListagem lista) {
		agrupados = new double[lista.getRotulos().length];
		StringBuffer sb = new StringBuffer("<tbody>");
		int fim = lista.getDados().length - lista.getInicio();
		if (lista.getLimite() > 0 && lista.getLimite() < fim) {
			fim = lista.getLimite();
		}

		for (int j = 0; j < fim; j++) {
			sb.append("<tr>");
			for (int i = 0; i < lista.getRotulos().length; i++) {
				if (lista.getRotulos()[i] != null) {
					sb.append("<td>" + getValor(lista.getDados()[j][i]) + "</td>");

					if (lista.getAgrupamentos() != null && lista.getAgrupamentos()[i] != null) {
						double valor = Double.valueOf(lista.getDados()[j][i]);
						switch (lista.getAgrupamentos()[i]) {
						case CONTAGEM:
							agrupados[i]++;
							break;
						case MAXIMO:
							agrupados[i] = agrupados[i] > valor ? agrupados[i] : valor;
							break;
						case MINIMO:
							agrupados[i] = agrupados[i] < valor ? agrupados[i] : valor;
							break;
						case SOMA:
							agrupados[i] += valor;
							break;
						case MEDIA:
							agrupados[i] += valor / lista.getAgrupamentos().length;
							break;
						}
					}
				} else {
					agrupados[i] = -1;
				}
			}
			sb.append("</tr>");
		}
		sb.append("</tbody>");
		return sb.toString();
	}

	/**
	 * Metodo que gera o rodape da listagem.
	 * 
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 * @return o rodape da listagem.
	 */
	public String getRodapeListagem(ExportacaoListagem lista) {
		boolean semGrupo = true;
		String rodape = "";
		int reg = lista.getLimite() > 0 ? lista.getLimite() : lista.getDados().length;

		for (int i = 0; i < agrupados.length; i++) {
			if (agrupados[i] == 0) {
				rodape += "<td>&nbsp;</td>";
			} else if (agrupados[i] > 0) {
				semGrupo = false;
				rodape += "<td>" + UtilServer.formataNumero(agrupados[i], 1, 2, true) + "</td>";
			}
		}

		StringBuffer sb = new StringBuffer("<tfoot>");
		if (!semGrupo) {
			sb.append("<tr><td colspan='" + agrupados.length + "'>" + UtilServer.CONF.get("txtTotal") + "<hr /></td></tr>");
			sb.append("<tr>" + rodape + "<tr>");
		}
		sb.append("<tr><td colspan='" + agrupados.length + "'>" + UtilServer.CONF.get("txtRegistro") + " :: " + reg + "</td></tr></tfoot>");

		return sb.toString();
	}
}
