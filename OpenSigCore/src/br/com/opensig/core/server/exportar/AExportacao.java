package br.com.opensig.core.server.exportar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.opensig.core.server.UtilServer;

/**
 * Classe abstrata que generaliza a forma como o sistema trata as exportacoes.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public abstract class AExportacao implements IExportacao {

	/**
	 * valors agrupados para usar como sumarios.
	 */
	protected double[] agrupados;

	/**
	 * Metodo que identifica o tipo de valor e formata de acordo com a
	 * localizacao.
	 * 
	 * @param valor
	 *            o texto cuja informacao sera avaliada.
	 * @return o texto formato de acordo com o tipo.
	 */
	public String getValor(String valor) {
		String retorno = valor;

		if (valor != null) {
			try {
				Date data = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(valor);
				retorno = UtilServer.formataData(data, DateFormat.MEDIUM);
				if (valor.indexOf(":") > 0) {
					data = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.US).parse(valor);
					retorno += " " + UtilServer.formataHora(data, DateFormat.MEDIUM);
				}
			} catch (Exception e1) {
				try {
					if (valor.contains(".")) {
						retorno = UtilServer.formataNumero(valor, 1, 2, true);
					} else {
						retorno = Long.parseLong(valor) + "";
					}
				} catch (Exception e4) {
					if (valor.equalsIgnoreCase("true")) {
						retorno = "Sim";
					} else if (valor.equalsIgnoreCase("false")) {
						retorno = "Nao";
					}
				}
			}
		}

		return retorno == null || retorno.equals("") || retorno.equals("null") ? " " : retorno;
	}
}
