package br.com.opensig.comercial.shared.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EDirecao;

@Entity
@Table(name = "com_ecf_z")
public class ComEcfZ extends Dados implements Serializable {

	private static final long serialVersionUID = 4966302699401243686L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_z_id")
	private int comEcfZId;

	@Column(name = "com_ecf_z_bruto")
	private Double comEcfZBruto;

	@Column(name = "com_ecf_z_coo")
	private int comEcfZCoo;

	@Column(name = "com_ecf_z_cro")
	private int comEcfZCro;

	@Column(name = "com_ecf_z_crz")
	private int comEcfZCrz;

	@Temporal(TemporalType.DATE)
	@Column(name = "com_ecf_z_data")
	private Date comEcfZData;

	@Column(name = "com_ecf_z_total")
	private Double comEcfZTotal;

	@JoinColumn(name = "com_ecf_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ComEcf comEcf;

	public ComEcfZ() {
		this(0);
	}

	public ComEcfZ(int comEcfZId) {
		super("pu_comercial", "ComEcfZ", "comEcfZId", "comEcfZData", EDirecao.DESC);
		this.comEcfZId = comEcfZId;
	}

	public int getComEcfZId() {
		return this.comEcfZId;
	}

	public void setComEcfZId(int comEcfZId) {
		this.comEcfZId = comEcfZId;
	}

	public Double getComEcfZBruto() {
		return this.comEcfZBruto;
	}

	public void setComEcfZBruto(Double comEcfZBruto) {
		this.comEcfZBruto = comEcfZBruto;
	}

	public int getComEcfZCoo() {
		return this.comEcfZCoo;
	}

	public void setComEcfZCoo(int comEcfZCoo) {
		this.comEcfZCoo = comEcfZCoo;
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

	public Date getComEcfZData() {
		return this.comEcfZData;
	}

	public void setComEcfZData(Date comEcfZData) {
		this.comEcfZData = comEcfZData;
	}

	public Double getComEcfZTotal() {
		return this.comEcfZTotal;
	}

	public void setComEcfZTotal(Double comEcfZTotal) {
		this.comEcfZTotal = comEcfZTotal;
	}

	public ComEcf getComEcf() {
		return this.comEcf;
	}

	public void setComEcf(ComEcf comEcf) {
		this.comEcf = comEcf;
	}

	public Number getId() {
		return comEcfZId;
	}

	public void setId(Number id) {
		comEcfZId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { comEcfZId + "", comEcf.getComEcfId() + "", comEcf.getComEcfSerie(), comEcf.getEmpEmpresa().getEmpEmpresaId() + "",
				comEcf.getEmpEmpresa().getEmpEntidade().getEmpEntidadeNome1(), comEcfZCoo + "", comEcfZCro + "", comEcfZCrz + "", UtilClient.getDataGrid(comEcfZData), comEcfZBruto.toString(),
				comEcfZTotal.toString() };
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
	}
}