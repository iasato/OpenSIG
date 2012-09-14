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
@Table(name = "com_ecf_documento")
public class ComEcfDocumento extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_documento_id")
	private int comEcfDocumentoId;

	@Column(name = "com_ecf_documento_usuario")
	private int comEcfDocumentoUsuario;

	@Column(name = "com_ecf_documento_coo")
	private int comEcfDocumentoCoo;

	@Column(name = "com_ecf_documento_gnf")
	private int comEcfDocumentoGnf;

	@Column(name = "com_ecf_documento_grg")
	private int comEcfDocumentoGrg;

	@Column(name = "com_ecf_documento_cdc")
	private int comEcfDocumentoCdc;

	@Column(name = "com_ecf_documento_tipo")
	private String comEcfDocumentoTipo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "com_ecf_documento_data")
	private Date comEcfDocumentoData;

	@JoinColumn(name = "com_ecf_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ComEcf comEcf;

	public ComEcfDocumento() {
		this(0);
	}

	public ComEcfDocumento(int comEcfDocumentoId) {
		super("pu_comercial", "ComEcfDocumento", "comEcfDocumentoId", "comEcfDocumentoData", EDirecao.DESC);
		this.comEcfDocumentoId = comEcfDocumentoId;
	}

	public Number getId() {
		return comEcfDocumentoId;
	}

	public void setId(Number id) {
		comEcfDocumentoId = id.intValue();
	}

	public int getComEcfDocumentoId() {
		return comEcfDocumentoId;
	}

	public void setComEcfDocumentoId(int comEcfDocumentoId) {
		this.comEcfDocumentoId = comEcfDocumentoId;
	}

	public int getComEcfDocumentoUsuario() {
		return comEcfDocumentoUsuario;
	}

	public void setComEcfDocumentoUsuario(int comEcfDocumentoUsuario) {
		this.comEcfDocumentoUsuario = comEcfDocumentoUsuario;
	}

	public int getComEcfDocumentoCoo() {
		return comEcfDocumentoCoo;
	}

	public void setComEcfDocumentoCoo(int comEcfDocumentoCoo) {
		this.comEcfDocumentoCoo = comEcfDocumentoCoo;
	}

	public int getComEcfDocumentoGnf() {
		return comEcfDocumentoGnf;
	}

	public void setComEcfDocumentoGnf(int comEcfDocumentoGnf) {
		this.comEcfDocumentoGnf = comEcfDocumentoGnf;
	}

	public int getComEcfDocumentoGrg() {
		return comEcfDocumentoGrg;
	}

	public void setComEcfDocumentoGrg(int comEcfDocumentoGrg) {
		this.comEcfDocumentoGrg = comEcfDocumentoGrg;
	}

	public int getComEcfDocumentoCdc() {
		return comEcfDocumentoCdc;
	}

	public void setComEcfDocumentoCdc(int comEcfDocumentoCdc) {
		this.comEcfDocumentoCdc = comEcfDocumentoCdc;
	}

	public String getComEcfDocumentoTipo() {
		return comEcfDocumentoTipo;
	}

	public void setComEcfDocumentoTipo(String comEcfDocumentoTipo) {
		this.comEcfDocumentoTipo = comEcfDocumentoTipo;
	}

	public Date getComEcfDocumentoData() {
		return comEcfDocumentoData;
	}

	public void setComEcfDocumentoData(Date comEcfDocumentoData) {
		this.comEcfDocumentoData = comEcfDocumentoData;
	}

	public ComEcf getComEcf() {
		return comEcf;
	}

	public void setComEcf(ComEcf comEcf) {
		this.comEcf = comEcf;
	}

	public String[] toArray() {
		return new String[] { comEcfDocumentoId + "", comEcf.getEmpEmpresa().getEmpEmpresaId() + "", comEcf.getEmpEmpresa().getEmpEntidade().getEmpEntidadeNome1(), comEcf.getComEcfId() + "",
				comEcf.getComEcfSerie(), comEcfDocumentoCoo + "", comEcfDocumentoGnf + "", comEcfDocumentoGrg + "", comEcfDocumentoCdc + "", comEcfDocumentoTipo,
				UtilClient.getDataHoraGrid(comEcfDocumentoData) };
	}

	public void anularDependencia() {
		comEcf = null;
	}
}