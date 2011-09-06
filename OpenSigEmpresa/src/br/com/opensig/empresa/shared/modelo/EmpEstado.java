package br.com.opensig.empresa.shared.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.com.opensig.core.shared.modelo.Dados;

/**
 * Classe que representa um estado no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 09/06/2009
 */
@Entity
@Table(name = "emp_estado")
public class EmpEstado extends Dados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_estado_id")
	private int empEstadoId;

	@Column(name = "emp_estado_ibge")
	private int empEstadoIbge;
	
	@Column(name = "emp_estado_descricao")
	private String empEstadoDescricao;

	@Column(name = "emp_estado_sigla")
	private String empEstadoSigla;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_pais_id")
	private EmpPais empPais;

	@OneToMany(mappedBy = "empEstado")
	private List<EmpMunicipio> empMunicipios;

	public EmpEstado() {
		this(0);
	}

	public EmpEstado(int empEstadoId) {
		super("pu_empresa", "EmpEstado", "empEstadoId", "empEstadoDescricao");
		this.empEstadoId = empEstadoId;
	}

	public int getEmpEstadoId() {
		return empEstadoId;
	}

	public void setEmpEstadoId(int empEstadoId) {
		this.empEstadoId = empEstadoId;
	}

	public int getEmpEstadoIbge() {
		return empEstadoIbge;
	}

	public void setEmpEstadoIbge(int empEstadoIbge) {
		this.empEstadoIbge = empEstadoIbge;
	}

	public String getEmpEstadoDescricao() {
		return empEstadoDescricao;
	}

	public void setEmpEstadoDescricao(String empEstadoDescricao) {
		this.empEstadoDescricao = empEstadoDescricao;
	}

	public String getEmpEstadoSigla() {
		return empEstadoSigla;
	}

	public void setEmpEstadoSigla(String empEstadoSigla) {
		this.empEstadoSigla = empEstadoSigla;
	}

	public EmpPais getEmpPais() {
		return empPais;
	}

	public void setEmpPais(EmpPais empPais) {
		this.empPais = empPais;
	}

	public List<EmpMunicipio> getEmpMunicipios() {
		return empMunicipios;
	}

	public void setEmpMunicipios(List<EmpMunicipio> empMunicipios) {
		this.empMunicipios = empMunicipios;
	}

	public Number getId() {
		return empEstadoId;
	}

	public void setId(Number id) {
		empEstadoId = id.intValue();
	}

	public void anularDependencia() {
		empPais = null;
		empMunicipios = null;
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("empPais")) {
			return new EmpPais();
		} else {
			return null;
		}
	}

	public String[] toArray() {
		return new String[] { empEstadoId + "", empEstadoIbge + "", empEstadoDescricao, empEstadoSigla, empPais.getEmpPaisId() + "", empPais.getEmpPaisDescricao() };
	}
}