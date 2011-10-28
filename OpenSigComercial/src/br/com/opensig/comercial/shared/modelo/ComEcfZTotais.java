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

/**
 * Classe que representa um total do Z.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
@Entity
@Table(name = "com_ecf_z_totais")
public class ComEcfZTotais extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_z_totais_id")
	private int comEcfZTotaisId;

	@Column(name = "com_ecf_z_totais_codigo")
	private String comEcfZTotaisCodigo;

	@Column(name = "com_ecf_z_totais_valor")
	private Double comEcfZTotaisValor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "com_ecf_z_id")
	private ComEcfZ comEcfZ;

	public ComEcfZTotais() {
		this(0);
	}

	public ComEcfZTotais(int comEcfZTotaisId) {
		super("pu_comercial", "ComEcfZTotais", "comEcfZTotaisId", "comEcfZTotaisId");
		this.comEcfZTotaisId = comEcfZTotaisId;
	}

	public int getComEcfZTotaisId() {
		return this.comEcfZTotaisId;
	}

	public void setComEcfZTotaisId(int comEcfZTotaisId) {
		this.comEcfZTotaisId = comEcfZTotaisId;
	}

	public String getComEcfZTotaisCodigo() {
		return this.comEcfZTotaisCodigo;
	}

	public void setComEcfZTotaisCodigo(String comEcfZTotaisCodigo) {
		this.comEcfZTotaisCodigo = comEcfZTotaisCodigo;
	}

	public Double getComEcfZTotaisValor() {
		return this.comEcfZTotaisValor;
	}

	public void setComEcfZTotaisValor(Double comEcfZTotaisValor) {
		this.comEcfZTotaisValor = comEcfZTotaisValor;
	}

	public ComEcfZ getComEcfZ() {
		return comEcfZ;
	}

	public void setComEcfZ(ComEcfZ comEcfZ) {
		this.comEcfZ = comEcfZ;
	}

	public Number getId() {
		return comEcfZTotaisId;
	}

	public void setId(Number id) {
		comEcfZTotaisId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { comEcfZ.getComEcfZId() + "", comEcfZTotaisId + "", comEcfZTotaisCodigo, comEcfZTotaisValor.toString() };
	}

	public void anularDependencia() {
		comEcfZ = null;
	}
}