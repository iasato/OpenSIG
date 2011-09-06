package br.com.opensig.core.shared.modelo;

import java.io.Serializable;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.filtro.IFiltro;

/**
 * Classe que representa a exportacao da listagem.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoListagem<E extends Dados> implements Serializable {

	private static final long serialVersionUID = 1L;
	private String[][] dados;
	private E unidade;
	private String campoOrdem;
	private EDirecao direcao;
	private IFiltro filtro;
	private String[] rotulos;
	private Integer[] tamanhos;
	private EBusca[] agrupamentos;
	private String nome;
	private int inicio;
	private int limite;

	// Gets e Seteres
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String[][] getDados() {
		return dados;
	}

	public void setDados(String[][] dados) {
		this.dados = dados;
	}

	public E getUnidade() {
		return unidade;
	}

	public void setUnidade(E unidade) {
		this.unidade = unidade;
	}

	public String getCampoOrdem() {
		campoOrdem = campoOrdem == null ? unidade.getCampoOrdem() : campoOrdem;
		return UtilClient.getCampoPrefixado(campoOrdem);
	}

	public void setCampoOrdem(String campoOrdem) {
		this.campoOrdem = campoOrdem;
	}

	public EDirecao getDirecao() {
		return direcao;
	}

	public void setDirecao(EDirecao direcao) {
		this.direcao = direcao;
	}

	public IFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(IFiltro filtro) {
		this.filtro = filtro;
	}

	public int getInicio() {
		return inicio;
	}

	public void setInicio(int inicio) {
		this.inicio = inicio;
	}

	public int getLimite() {
		return limite;
	}

	public void setLimite(int limite) {
		this.limite = limite;
	}

	public String[] getRotulos() {
		return rotulos;
	}

	public void setRotulos(String[] rotulos) {
		this.rotulos = rotulos;
	}

	public Integer[] getTamanhos() {
		return tamanhos;
	}

	public void setTamanhos(Integer[] tamanhos) {
		this.tamanhos = tamanhos;
	}

	public EBusca[] getAgrupamentos() {
		return agrupamentos;
	}

	public void setAgrupamentos(EBusca[] agrupamentos) {
		this.agrupamentos = agrupamentos;
	}

}
