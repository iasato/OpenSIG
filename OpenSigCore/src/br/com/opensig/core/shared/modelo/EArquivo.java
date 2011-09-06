package br.com.opensig.core.shared.modelo;

import java.io.Serializable;

/**
 * Enumerador que define os tipos de arquivos usados para download/upload.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public enum EArquivo implements Serializable {
	/**
	 * Arquivo de impressao
	 */
	PDF,
	/**
	 * Arquivo de planilha
	 */
	XLS, 
	/**
	 * Arquivo em texto separado
	 */
	CSV, 
	/**
	 * Arquivo em tag
	 */
	XML, 
	/**
	 * Arquivo em web
	 */
	HTML,
	/**
	 * Arquivo compactado
	 */
	ZIP;

}
