package br.com.opensig.core.shared.modelo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import br.com.opensig.core.shared.modelo.permissao.SisModulo;

/**
 * Classe que representa a autenticacao realizada no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class Autenticacao implements Serializable {

	private static final long serialVersionUID = -5010002520668925052L;
	private String[] usuario;
	private String[] empresa;
	private List<SisModulo> modulos;
	private boolean bloqueado;
	private Map<String, String> conf;

	/**
	 * Construtor padrao.
	 */
	public Autenticacao() {
	}

	// Gets e Seteres

	public String[] getUsuario() {
		return usuario;
	}

	public void setUsuario(String[] usuario) {
		this.usuario = usuario;
	}

	public String[] getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String[] empresa) {
		this.empresa = empresa;
	}

	public List<SisModulo> getModulos() {
		return modulos;
	}

	public void setModulos(List<SisModulo> modulos) {
		this.modulos = modulos;
	}

	public boolean isBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}

	public Map<String, String> getConf() {
		return conf;
	}

	public void setConf(Map<String, String> conf) {
		this.conf = conf;
	}

}
