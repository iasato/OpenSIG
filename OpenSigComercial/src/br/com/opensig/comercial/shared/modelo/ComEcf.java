package br.com.opensig.comercial.shared.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;

@Entity
@Table(name = "com_ecf")
public class ComEcf extends Dados implements Serializable {

	private static final long serialVersionUID = 2653171555341138705L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_id")
	private int comEcfId;

	@Column(name = "com_ecf_caixa")
	private int comEcfCaixa;

	@Column(name = "com_ecf_codigo")
	private String comEcfCodigo;

	@Column(name = "com_ecf_modelo")
	private String comEcfModelo;

	@Column(name = "com_ecf_serie")
	private String comEcfSerie;

	@JoinColumn(name = "emp_empresa_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EmpEmpresa empEmpresa;

	public ComEcf() {
		this(0);
	}

	public ComEcf(int comEcfId) {
		super("pu_comercial", "ComEcf", "comEcfId", "comEcfModelo");
		this.comEcfId = comEcfId;
	}

	public int getComEcfId() {
		return this.comEcfId;
	}

	public void setComEcfId(int comEcfId) {
		this.comEcfId = comEcfId;
	}

	public int getComEcfCaixa() {
		return this.comEcfCaixa;
	}

	public void setComEcfCaixa(int comEcfCaixa) {
		this.comEcfCaixa = comEcfCaixa;
	}

	public String getComEcfCodigo() {
		return this.comEcfCodigo;
	}

	public void setComEcfCodigo(String comEcfCodigo) {
		this.comEcfCodigo = comEcfCodigo;
	}

	public String getComEcfModelo() {
		return this.comEcfModelo;
	}

	public void setComEcfModelo(String comEcfModelo) {
		this.comEcfModelo = comEcfModelo;
	}

	public String getComEcfSerie() {
		return this.comEcfSerie;
	}

	public void setComEcfSerie(String comEcfSerie) {
		this.comEcfSerie = comEcfSerie;
	}

	public EmpEmpresa getEmpEmpresa() {
		return empEmpresa;
	}

	public void setEmpEmpresa(EmpEmpresa empEmpresa) {
		this.empEmpresa = empEmpresa;
	}

	public Number getId() {
		return comEcfId;
	}

	public void setId(Number id) {
		comEcfId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { comEcfId + "", empEmpresa.getEmpEmpresaId() + "", empEmpresa.getEmpEntidade().getEmpEntidadeNome1(), comEcfCodigo, comEcfModelo, comEcfSerie, comEcfCaixa + "" };
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("empEmpresa")) {
			return new EmpEmpresa();
		} else {
			return null;
		}
	}

	public void anularDependencia() {
		empEmpresa = null;
	}
}