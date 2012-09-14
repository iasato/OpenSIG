package br.com.opensig.comercial.shared.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EDirecao;

@Entity
@Table(name = "com_ecf_z")
public class ComEcfZ extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_z_id")
	private int comEcfZId;

	@Column(name = "com_ecf_z_usuario")
	private int comEcfZUsuario;

	@Column(name = "com_ecf_z_coo_ini")
	private int comEcfZCooIni;

	@Column(name = "com_ecf_z_coo_fin")
	private int comEcfZCooFin;

	@Column(name = "com_ecf_z_cro")
	private int comEcfZCro;

	@Column(name = "com_ecf_z_crz")
	private int comEcfZCrz;

	@Temporal(TemporalType.DATE)
	@Column(name = "com_ecf_z_movimento")
	private Date comEcfZMovimento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "com_ecf_z_emissao")
	private Date comEcfZEmissao;

	@Column(name = "com_ecf_z_bruto")
	private Double comEcfZBruto;

	@Column(name = "com_ecf_z_gt")
	private Double comEcfZGt;

	@Column(name = "com_ecf_z_issqn")
	private int comEcfZIssqn;

	@JoinColumn(name = "com_ecf_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ComEcf comEcf;

	@OneToMany(mappedBy = "comEcfZ", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ComEcfZTotais> comEcfZTotais;

	@OneToMany(mappedBy = "comEcfZ", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ComEcfVenda> comEcfVendas;

	@Transient
	private String issqn;

	public ComEcfZ() {
		this(0);
	}

	public ComEcfZ(int comEcfZId) {
		super("pu_comercial", "ComEcfZ", "comEcfZId", "comEcfZMovimento", EDirecao.DESC);
		this.comEcfZId = comEcfZId;
	}

	public int getComEcfZId() {
		return this.comEcfZId;
	}

	public void setComEcfZId(int comEcfZId) {
		this.comEcfZId = comEcfZId;
	}

	public int getComEcfZUsuario() {
		return comEcfZUsuario;
	}

	public void setComEcfZUsuario(int comEcfZUsuario) {
		this.comEcfZUsuario = comEcfZUsuario;
	}

	public int getComEcfZCooIni() {
		return comEcfZCooIni;
	}

	public void setComEcfZCooIni(int comEcfZCooIni) {
		this.comEcfZCooIni = comEcfZCooIni;
	}

	public int getComEcfZCooFin() {
		return this.comEcfZCooFin;
	}

	public void setComEcfZCooFin(int comEcfZCooFin) {
		this.comEcfZCooFin = comEcfZCooFin;
	}

	public int getComEcfZCro() {
		return this.comEcfZCro;
	}

	public void setComEcfZCro(int comEcfZCro) {
		this.comEcfZCro = comEcfZCro;
	}

	public int getComEcfZCrz() {
		return this.comEcfZCrz;
	}

	public void setComEcfZCrz(int comEcfZCrz) {
		this.comEcfZCrz = comEcfZCrz;
	}

	public Date getComEcfZMovimento() {
		return this.comEcfZMovimento;
	}

	public void setComEcfZMovimento(Date comEcfZMovimento) {
		this.comEcfZMovimento = comEcfZMovimento;
	}

	public Date getComEcfZEmissao() {
		return comEcfZEmissao;
	}

	public void setComEcfZEmissao(Date comEcfZEmissao) {
		this.comEcfZEmissao = comEcfZEmissao;
	}

	public Double getComEcfZBruto() {
		return this.comEcfZBruto;
	}

	public void setComEcfZBruto(Double comEcfZBruto) {
		this.comEcfZBruto = comEcfZBruto;
	}

	public Double getComEcfZGt() {
		return this.comEcfZGt;
	}

	public void setComEcfZGt(Double comEcfZGt) {
		this.comEcfZGt = comEcfZGt;
	}

	public ComEcf getComEcf() {
		return this.comEcf;
	}

	public void setComEcf(ComEcf comEcf) {
		this.comEcf = comEcf;
	}

	public List<ComEcfZTotais> getComEcfZTotais() {
		return comEcfZTotais;
	}

	public void setComEcfZTotais(List<ComEcfZTotais> comEcfZTotais) {
		this.comEcfZTotais = comEcfZTotais;
	}

	public List<ComEcfVenda> getComEcfVendas() {
		return comEcfVendas;
	}

	public void setComEcfVendas(List<ComEcfVenda> comEcfVendas) {
		this.comEcfVendas = comEcfVendas;
	}

	public boolean getComEcfZIssqn() {
		return comEcfZIssqn == 0 ? false : true;
	}

	public void setComEcfZIssqn(boolean comEcfZIssqn) {
		this.comEcfZIssqn = comEcfZIssqn == false ? 0 : 1;
	}

	public String getIssqn() {
		return issqn;
	}

	public void setIssqn(String issqn) {
		this.issqn = issqn;
	}

	public Number getId() {
		return comEcfZId;
	}

	public void setId(Number id) {
		comEcfZId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { comEcfZId + "", comEcf.getComEcfId() + "", comEcf.getComEcfSerie(), comEcf.getEmpEmpresa().getEmpEmpresaId() + "",
				comEcf.getEmpEmpresa().getEmpEntidade().getEmpEntidadeNome1(), comEcfZUsuario + "", comEcfZCooIni + "", comEcfZCooFin + "", comEcfZCro + "", comEcfZCrz + "",
				UtilClient.getDataGrid(comEcfZMovimento), UtilClient.getDataHoraGrid(comEcfZEmissao), comEcfZBruto.toString(), comEcfZGt.toString(), getComEcfZIssqn() + "" };
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("comEcf")) {
			return new ComEcf();
		} else {
			return null;
		}
	}

	public void anularDependencia() {
		comEcf = null;
		comEcfZTotais = null;
		comEcfVendas = null;
	}
}