package br.com.opensig.core.shared.modelo;

import java.io.Serializable;

/**
 * Enumerador que define os meios de busca aritmética no banco de dados.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public enum EBusca implements Serializable {

	/**
	 * Campo que define como SUM busca.
	 */
	SOMA,
	/**
	 * Campo que define como AVG busca.
	 */
	MEDIA,
	/**
	 * Campo que define como MAX busca.
	 */
	MAXIMO,
	/**
	 * Campo que define como MIN busca.
	 */
	MINIMO,
	/**
	 * Campo que define como COUNT busca.
	 */
	CONTAGEM;

	/**
	 * Metodo que retorna no formato JQL a busca do enumerador.
	 * 
	 * @return uma string no padrao de JQL.
	 */
	public String toString() {
		switch (this) {
		case SOMA:
			return "SUM";
		case MEDIA:
			return "AVG";
		case MAXIMO:
			return "MAX";
		case MINIMO:
			return "MIN";
		default:
			return "COUNT";
		}
	}

	/**
	 * Metodo que retorna o tipo de Busca pela string.
	 * 
	 * @param tipo
	 *            a string que representa a busca.
	 * @return o EBusca correspondente a string.
	 */
	public static final EBusca getBusca(String tipo) {
		if (tipo.equalsIgnoreCase("soma") || tipo.equalsIgnoreCase("sum")) {
			return EBusca.SOMA;
		} else if (tipo.equalsIgnoreCase("media") || tipo.equalsIgnoreCase("avg")) {
			return EBusca.MEDIA;
		} else if (tipo.equalsIgnoreCase("maximo") || tipo.equalsIgnoreCase("max")) {
			return EBusca.MAXIMO;
		} else if (tipo.equalsIgnoreCase("minimo") || tipo.equalsIgnoreCase("min")) {
			return EBusca.MINIMO;
		} else if (tipo.equalsIgnoreCase("contagem") || tipo.equalsIgnoreCase("count")) {
			return EBusca.CONTAGEM;
		} else {
			return null;
		}
	}
}