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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;

/**
 * The persistent class for the fis_sped_fiscal database table.
 * 
 */
@Entity
@Table(name = "fis_sped_fiscal")
public class FisSpedFiscal extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fis_sped_fiscal_id")
	private int fisSpedFiscalId;

	@Column(name = "fis_sped_fiscal_ano")
	private int fisSpedFiscalAno;

	@Column(name = "fis_sped_fiscal_compras")
	private int fisSpedFiscalCompras;

	@Column(name = "fis_sped_fiscal_frete")
	private int fisSpedFiscalFrete;

	@Column(name = "fis_sped_fiscal_vendas")
	private int fisSpedFiscalVendas;

	@Column(name = "fis_sped_fiscal_ecf")
	private int fisSpedFiscalEcf;

	@Column(name = "fis_sped_fiscal_protocolo")
	private String fisSpedFiscalProtocolo;

	@Temporal(TemporalType.DATE)
	@Column(name = "fis_sped_fiscal_data")
	private Date fisSpedFiscalData;

	@Column(name = "fis_sped_fiscal_mes")
	private int fisSpedFiscalMes;

	@Column(name = "fis_sped_fiscal_tipo")
	private String fisSpedFiscalTipo;

	@Column(name = "fis_sped_fiscal_ativo")
	private int fisSpedFiscalAtivo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_empresa_id")
	private EmpEmpresa empEmpresa;

	@Transient
	private int finalidade;

	@Transient
	private Integer[] registros;

	@Transient
	private Integer[] compras;

	@Transient
	private Integer[] fretes;

	@Transient
	private Integer[] vendas;

	@Transient
	private Integer[] ecfs;

	public FisSpedFiscal() {
		this(0);
	}

	public FisSpedFiscal(int fisSpedFiscalId) {
		super("pu_fiscal", "FisSpedFiscal", "fisSpedFiscalId", "fisSpedFiscalData", EDirecao.DESC);
		this.fisSpedFiscalId = fisSpedFiscalId;
	}

	public int getFisSpedFiscalId() {
		return this.fisSpedFiscalId;
	}

	public void setFisSpedFiscalId(int fisSpedFiscalId) {
		this.fisSpedFiscalId = fisSpedFiscalId;
	}

	public int getFisSpedFiscalAno() {
		return this.fisSpedFiscalAno;
	}

	public void setFisSpedFiscalAno(int fisSpedFiscalAno) {
		this.fisSpedFiscalAno = fisSpedFiscalAno;
	}

	public Date getFisSpedFiscalData() {
		return this.fisSpedFiscalData;
	}

	public void setFisSpedFiscalData(Date fisSpedFiscalData) {
		this.fisSpedFiscalData = fisSpedFiscalData;
	}

	public int getFisSpedFiscalMes() {
		return this.fisSpedFiscalMes;
	}

	public void setFisSpedFiscalMes(int fisSpedFiscalMes) {
		this.fisSpedFiscalMes = fisSpedFiscalMes;
	}

	public String getFisSpedFiscalTipo() {
		return this.fisSpedFiscalTipo;
	}

	public void setFisSpedFiscalTipo(String fisSpedFiscalTipo) {
		this.fisSpedFiscalTipo = fisSpedFiscalTipo;
	}

	public int getFisSpedFiscalVendas() {
		return fisSpedFiscalVendas;
	}

	public void setFisSpedFiscalVendas(int fisSpedFiscalVendas) {
		this.fisSpedFiscalVendas = fisSpedFiscalVendas;
	}

	public int getFisSpedFiscalCompras() {
		return fisSpedFiscalCompras;
	}

	public void setFisSpedFiscalCompras(int fisSpedFiscalCompras) {
		this.fisSpedFiscalCompras = fisSpedFiscalCompras;
	}

	public int getFisSpedFiscalEcf() {
		return fisSpedFiscalEcf;
	}

	public void setFisSpedFiscalEcf(int fisSpedFiscalEcf) {
		this.fisSpedFiscalEcf = fisSpedFiscalEcf;
	}

	public int getFisSpedFiscalFrete() {
		return fisSpedFiscalFrete;
	}

	public void setFisSpedFiscalFrete(int fisSpedFiscalFrete) {
		this.fisSpedFiscalFrete = fisSpedFiscalFrete;
	}

	public boolean getFisSpedAtivo() {
		return this.fisSpedFiscalAtivo == 0 ? false : true;
	}

	public void setFisSpedAtivo(boolean fisSpedFiscalAtivo) {
		this.fisSpedFiscalAtivo = fisSpedFiscalAtivo == false ? 0 : 1;
	}

	public String getFisSpedFiscalProtocolo() {
		return fisSpedFiscalProtocolo;
	}

	public void setFisSpedFiscalProtocolo(String fisSpedFiscalProtocolo) {
		this.fisSpedFiscalProtocolo = fisSpedFiscalProtocolo;
	}

	public EmpEmpresa getEmpEmpresa() {
		return empEmpresa;
	}

	public void setEmpEmpresa(EmpEmpresa empEmpresa) {
		this.empEmpresa = empEmpresa;
	}

	public Integer[] getRegistros() {
		return registros;
	}

	public void setRegistros(Integer[] registros) {
		this.registros = registros;
	}

	public Integer[] getCompras() {
		return compras;
	}

	public void setCompras(Integer[] compras) {
		this.compras = compras;
	}

	public Integer[] getFretes() {
		return fretes;
	}

	public void setFretes(Integer[] fretes) {
		this.fretes = fretes;
	}

	public Integer[] getVendas() {
		return vendas;
	}

	public void setVendas(Integer[] vendas) {
		this.vendas = vendas;
	}

	public Integer[] getEcfs() {
		return ecfs;
	}

	public void setEcfs(Integer[] ecfs) {
		this.ecfs = ecfs;
	}

	public int getFinalidade() {
		return finalidade;
	}

	public void setFinalidade(int finalidade) {
		this.finalidade = finalidade;
	}

	public Number getId() {
		return fisSpedFiscalId;
	}

	public void setId(Number id) {
		fisSpedFiscalId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { fisSpedFiscalId + "", empEmpresa.getEmpEmpresaId() + "", empEmpresa.getEmpEntidade().getEmpEntidadeNome1(), fisSpedFiscalAno + "", fisSpedFiscalMes + "",
				fisSpedFiscalTipo, UtilClient.getDataGrid(fisSpedFiscalData), fisSpedFiscalCompras + "", fisSpedFiscalFrete + "", fisSpedFiscalVendas + "", fisSpedFiscalEcf + "",
				getFisSpedAtivo() + "", fisSpedFiscalProtocolo };
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