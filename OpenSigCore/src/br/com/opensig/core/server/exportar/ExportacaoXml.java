package br.com.opensig.core.server.exportar;

import java.text.DateFormat;
import java.util.Collection;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;

/**
 * Classe que define a exportacao de arquivo no formato de XML.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoXml extends AExportacao {

	@Override
	public byte[] getArquivo(ExportacaoListagem lista, String[] empresa, String[][] enderecos, String[][] contatos) {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		// no raiz
		sb.append("<opensig>\n");
		// no de cabecalho
		sb.append(getCabecalhoEmpresa(empresa));
		// nos dos registros
		sb.append(getCorpoListagem(lista));
		// no do rodape da listagem
		sb.append(getRodapeListagem(lista));
		// no de rodape
		sb.append(getRodapeEmpresa(enderecos, contatos));
		// fecha no raiz
		sb.append("</opensig>");
		return sb.toString().getBytes();
	}

	@Override
	public byte[] getArquivo(ExportacaoRegistro registro, Collection<ExportacaoListagem> listas, String[] empresa, String[][] enderecos, String[][] contatos) {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		// no raiz
		sb.append("<opensig>\n");
		// no de cabecalho
		sb.append(getCabecalhoEmpresa(empresa));
		// nos dos registros
		sb.append(getCorpoRegistro(listas, registro));
		// no de rodape
		sb.append(getRodapeEmpresa(enderecos, contatos));
		// fecha no raiz
		sb.append("</opensig>");
		return sb.toString().getBytes();
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
		StringBuffer sb = new StringBuffer("\t<cabecalho>\n");
		sb.append("\t\t<empresa><![CDATA[" + empresa[2] + "]]></empresa>\n");
		sb.append("\t\t<cnpj><![CDATA[" + empresa[5] + "]]></cnpj>\n");
		sb.append("\t\t<ie><![CDATA[" + empresa[6] + "]]></ie>\n");
		sb.append("\t\t<usuario><![CDATA[" + UtilServer.CONF.get("usuario") + "]]></usuario>\n");
		sb.append("\t\t<data><![CDATA[" + UtilServer.formataData(UtilServer.getData(), DateFormat.MEDIUM) + "]]></data>\n");
		sb.append("\t\t<hora><![CDATA[" + UtilServer.formataHora(UtilServer.getData(), DateFormat.MEDIUM) + "]]></hora>\n");
		sb.append("\t</cabecalho>\n");
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
		int fim = lista.getDados().length - lista.getInicio();
		if (lista.getLimite() > 0 && lista.getLimite() < fim) {
			fim = lista.getLimite();
		}
		
		agrupados = new double[lista.getAgrupamentos().length];
		StringBuffer sb = new StringBuffer();

		for (int j = 0; j < fim; j++) {
			sb.append("\t<reg>\n");
			for (int i = 0; i < lista.getRotulos().length; i++) {
				if (lista.getRotulos()[i] != null) {
					String tag = UtilServer.normaliza(lista.getRotulos()[i]).toLowerCase().replaceAll("\\W", "");
					sb.append("\t\t<" + tag + "><![CDATA[" + getValor(lista.getDados()[j][i]) + "]]></" + tag + ">\n");

					if (lista.getAgrupamentos()[i] != null) {
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
					} else {
						agrupados[i] = -1;
					}
				}
			}
			sb.append("\t</reg>\n");
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
		StringBuffer sb = new StringBuffer("\t<reg>\n");
		for (int i = 0; i < reg.getRotulos().length; i++) {
			if (reg.getRotulos()[i] != null) {
				String tag = UtilServer.normaliza(reg.getRotulos()[i]).toLowerCase().replaceAll("\\W", "");
				sb.append("\t\t<" + tag + "><![CDATA[" + getValor(reg.getDados()[i]) + "]]></" + tag + ">\n");
			}
		}
		sb.append("\t</reg>\n");

		if (listas != null) {
			for (ExportacaoListagem lista : listas) {
				String tag = UtilServer.normaliza(lista.getNome()).toLowerCase();
				sb.append("\t<" + tag + ">\n");
				sb.append(getCorpoListagem(lista));
				sb.append(getRodapeListagem(lista));
				sb.append("\t</" + tag + ">\n");
			}
		}

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
		StringBuffer sb = new StringBuffer("\t<total>\n");
		for (int i = 0; i < agrupados.length; i++) {
			if (agrupados[i] > 0) {
				String tag = UtilServer.normaliza(lista.getRotulos()[i]).toLowerCase().replaceAll("\\W", "");
				sb.append("\t\t<" + tag + "><![CDATA[" + UtilServer.formataNumero(agrupados[i], 1, 2, true) + "]]></" + tag + ">\n");
			}
		}

		sb.append("\t</total>\n");
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
		StringBuffer sbEndereco = new StringBuffer();
		for (String[] endereco : enderecos) {
			sbEndereco.append("\t\t\t<endereco>");
			sbEndereco.append("<![CDATA[" + endereco[2] + ":: " + endereco[4] + ", " + endereco[5] + "  " + endereco[6] + " " + endereco[7] + " " + endereco[8] + "]]>");
			sbEndereco.append("</endereco>\n");
		}

		// dados do contato
		StringBuffer sbContato = new StringBuffer();
		for (String[] contato : contatos) {
			sbContato.append("\t\t\t<contato>");
			sbContato.append("<![CDATA[" + contato[2] + ":: " + contato[3] + "]]>");
			sbContato.append("</contato>\n");
		}

		// alinhamento
		StringBuffer sb = new StringBuffer("\t<rodape>\n");
		sb.append("\t\t<enderecos>\n" + sbEndereco.toString() + "\t\t</enderecos>\n");
		sb.append("\t\t<contatos>\n" + sbContato.toString() + "\t\t</contatos>\n");
		sb.append("\t</rodape>\n");

		return sb.toString();
	}

}
