package br.com.opensig.fiscal.shared.modelo;

import java.io.Serializable;

public enum ENotaStatus implements Serializable {

	/**
	 * Enquanto autoriza a nfe.
	 */
	AUTORIZANDO,
	/**
	 * Após autorizado a nfe.
	 */
	AUTORIZADO,
	/**
	 * Enquando cancela a nfe.
	 */
	CANCELANDO,
	/**
	 * Após cancelado a nfe.
	 */
	CANCELADO,
	/**
	 * Enquando inutiliza a nfe.
	 */
	INUTILIZANDO,
	/**
	 * Após inutilizado a nfe.
	 */
	INUTILIZADO,
	/**
	 * Enquando tem erro na nfe.
	 */
	ERRO,
	/**
	 * NFe em contigencia de FS-DA.
	 */
	FS_DA;
}
