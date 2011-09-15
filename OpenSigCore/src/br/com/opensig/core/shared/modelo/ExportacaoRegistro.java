package br.com.opensig.core.shared.modelo;

import java.io.Serializable;
import java.util.Collection;

/**
 * Classe que representa a exportacao do registro.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoRegistro implements Serializable {

	private String nome;
	private String[] dados;
	private String[] rotulos;
	private Collection<ExportacaoListagem> expLista;

	// Gets e Seteres
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String[] getDados() {
		return dados;
	}

	public void setDados(String[] dados) {
		this.dados = dados;
	}

	public String[] getRotulos() {
		return rotulos;
	}

	public void setRotulos(String[] rotulos) {
		this.rotulos = rotulos;
	}

	public Collection<ExportacaoListagem> getExpLista() {
		return expLista;
	}

	public void setExpLista(Collection<ExportacaoListagem> expLista) {
		this.expLista = expLista;
	}

}
