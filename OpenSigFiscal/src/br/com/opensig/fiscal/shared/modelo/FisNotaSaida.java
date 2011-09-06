package br.com.opensig.fiscal.shared.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.core.shared.modelo.ELetra;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;

/**
 * Classe que representa uma nfe de saida no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 20/07/2010
 */
@Entity
@Table(name = "fis_nota_saida")
public class FisNotaSaida extends Dados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fis_nota_saida_id")
	private int fisNotaSaidaId;

	@Column(name = "fis_nota_saida_chave")
	private String fisNotaSaidaChave;

	@Temporal(TemporalType.DATE)
	@Column(name = "fis_nota_saida_cadastro")
	private Date fisNotaSaidaCadastro;

	@Temporal(TemporalType.DATE)
	@Column(name = "fis_nota_saida_data")
	private Date fisNotaSaidaData;

	@Column(name = "fis_nota_saida_numero")
	private int fisNotaSaidaNumero;

	@Column(name = "fis_nota_saida_protocolo")
	private String fisNotaSaidaProtocolo;

	@Column(name = "fis_nota_saida_recibo")
	private String fisNotaSaidaRecibo;

	@Column(name = "fis_nota_saida_protocolo_cancelado")
	private String fisNotaSaidaProtocoloCancelado;

	@Column(name = "fis_nota_saida_valor")
	private Double fisNotaSaidaValor;

	@Column(name = "fis_nota_saida_icms")
	private Double fisNotaSaidaIcms;

	@Column(name = "fis_nota_saida_ipi")
	private Double fisNotaSaidaIpi;

	@Column(name = "fis_nota_saida_pis")
	private Double fisNotaSaidaPis;

	@Column(name = "fis_nota_saida_cofins")
	private Double fisNotaSaidaCofins;

	@Lob()
	@Column(name = "fis_nota_saida_xml")
	private String fisNotaSaidaXml;

	@Lob()
	@Column(name = "fis_nota_saida_xml_cancelado")
	private String fisNotaSaidaXmlCancelado;

	@Lob()
	@Column(name = "fis_nota_saida_erro")
	private String fisNotaSaidaErro;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fis_nota_status_id")
	private FisNotaStatus fisNotaStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_empresa_id")
	private EmpEmpresa empEmpresa;

	public FisNotaSaida() {
		this(0);
	}

	public FisNotaSaida(int fisNotaSaidaId) {
		super("pu_fiscal", "FisNotaSaida", "fisNotaSaidaId", "fisNotaSaidaData", EDirecao.DESC);
		this.fisNotaSaidaId = fisNotaSaidaId;
		this.tipoLetra = ELetra.NORMAL;
	}

	public int getFisNotaSaidaId() {
		return this.fisNotaSaidaId;
	}

	public void setFisNotaSaidaId(int fisNotaSaidaId) {
		this.fisNotaSaidaId = fisNotaSaidaId;
	}

	public String getFisNotaSaidaChave() {
		return this.fisNotaSaidaChave;
	}

	public void setFisNotaSaidaChave(String fisNotaSaidaChave) {
		this.fisNotaSaidaChave = fisNotaSaidaChave;
	}

	public Date getFisNotaSaidaCadastro() {
		return fisNotaSaidaCadastro;
	}

	public void setFisNotaSaidaCadastro(Date fisNotaSaidaCadastro) {
		this.fisNotaSaidaCadastro = fisNotaSaidaCadastro;
	}

	public Date getFisNotaSaidaData() {
		return this.fisNotaSaidaData;
	}

	public void setFisNotaSaidaData(Date fisNotaSaidaData) {
		this.fisNotaSaidaData = fisNotaSaidaData;
	}

	public int getFisNotaSaidaNumero() {
		return this.fisNotaSaidaNumero;
	}

	public void setFisNotaSaidaNumero(int fisNotaSaidaNumero) {
		this.fisNotaSaidaNumero = fisNotaSaidaNumero;
	}

	public String getFisNotaSaidaProtocolo() {
		return this.fisNotaSaidaProtocolo;
	}

	public void setFisNotaSaidaProtocolo(String fisNotaSaidaProtocolo) {
		this.fisNotaSaidaProtocolo = fisNotaSaidaProtocolo;
	}

	public String getFisNotaSaidaRecibo() {
		return fisNotaSaidaRecibo;
	}

	public void setFisNotaSaidaRecibo(String fisNotaSaidaRecibo) {
		this.fisNotaSaidaRecibo = fisNotaSaidaRecibo;
	}

	public Double getFisNotaSaidaValor() {
		return this.fisNotaSaidaValor;
	}

	public void setFisNotaSaidaValor(Double fisNotaSaidaValor) {
		this.fisNotaSaidaValor = fisNotaSaidaValor;
	}

	public String getFisNotaSaidaXml() {
		return this.fisNotaSaidaXml;
	}

	public void setFisNotaSaidaXml(String fisNotaSaidaXml) {
		this.fisNotaSaidaXml = fisNotaSaidaXml;
	}

	public FisNotaStatus getFisNotaStatus() {
		return this.fisNotaStatus;
	}

	public void setFisNotaStatus(FisNotaStatus fisNotaStatus) {
		this.fisNotaStatus = fisNotaStatus;
	}

	public EmpEmpresa getEmpEmpresa() {
		return empEmpresa;
	}

	public void setEmpEmpresa(EmpEmpresa empEmpresa) {
		this.empEmpresa = empEmpresa;
	}

	public Number getId() {
		return fisNotaSaidaId;
	}

	public void setId(Number id) {
		fisNotaSaidaId = id.intValue();
	}

	public Double getFisNotaSaidaIcms() {
		return fisNotaSaidaIcms;
	}

	public void setFisNotaSaidaIcms(Double fisNotaSaidaIcms) {
		this.fisNotaSaidaIcms = fisNotaSaidaIcms;
	}

	public Double getFisNotaSaidaIpi() {
		return fisNotaSaidaIpi;
	}

	public void setFisNotaSaidaIpi(Double fisNotaSaidaIpi) {
		this.fisNotaSaidaIpi = fisNotaSaidaIpi;
	}

	public String getFisNotaSaidaErro() {
		return fisNotaSaidaErro;
	}

	public void setFisNotaSaidaErro(String fisNotaSaidaErro) {
		this.fisNotaSaidaErro = fisNotaSaidaErro;
	}

	public Double getFisNotaSaidaPis() {
		return fisNotaSaidaPis;
	}

	public void setFisNotaSaidaPis(Double fisNotaSaidaPis) {
		this.fisNotaSaidaPis = fisNotaSaidaPis;
	}

	public Double getFisNotaSaidaCofins() {
		return fisNotaSaidaCofins;
	}

	public void setFisNotaSaidaCofins(Double fisNotaSaidaCofins) {
		this.fisNotaSaidaCofins = fisNotaSaidaCofins;
	}

	public String getFisNotaSaidaProtocoloCancelado() {
		return fisNotaSaidaProtocoloCancelado;
	}

	public void setFisNotaSaidaProtocoloCancelado(String fisNotaSaidaProtocoloCancelado) {
		this.fisNotaSaidaProtocoloCancelado = fisNotaSaidaProtocoloCancelado;
	}

	public String getFisNotaSaidaXmlCancelado() {
		return fisNotaSaidaXmlCancelado;
	}

	public void setFisNotaSaidaXmlCancelado(String fisNotaSaidaXmlCancelado) {
		this.fisNotaSaidaXmlCancelado = fisNotaSaidaXmlCancelado;
	}

	public String[] toArray() {
		return new String[] { fisNotaSaidaId + "", empEmpresa.getEmpEmpresaId() + "", empEmpresa.getEmpEntidade().getEmpEntidadeNome1(), fisNotaStatus.getFisNotaStatusId() + " ",
				fisNotaStatus.getFisNotaStatusDescricao(), UtilClient.getDataGrid(fisNotaSaidaCadastro), fisNotaSaidaNumero + "", UtilClient.getDataGrid(fisNotaSaidaData),
				fisNotaSaidaValor.toString(), fisNotaSaidaChave, fisNotaSaidaIcms.toString(), fisNotaSaidaIpi.toString(), fisNotaSaidaPis.toString(), fisNotaSaidaCofins.toString(),
				fisNotaSaidaProtocolo, "xml", "danfe", fisNotaSaidaProtocoloCancelado, "cancelada", fisNotaSaidaRecibo, "*" };
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("fisNotaStatus")) {
			return new FisNotaStatus();
		} else if (campo.startsWith("empEmpresa")) {
			return new EmpEmpresa();
		} else {
			return null;
		}
	}

	public void anularDependencia() {
		fisNotaStatus = null;
		empEmpresa = null;
	}
}