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
import br.com.opensig.empresa.shared.modelo.EmpCliente;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.permissao.shared.modelo.SisUsuario;

@Entity
@Table(name = "com_ecf_venda")
public class ComEcfVenda extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_venda_id")
	private int comEcfVendaId;

	@Column(name = "com_ecf_venda_bruto")
	private Double comEcfVendaBruto;

	@Column(name = "com_ecf_venda_fechada")
	private int comEcfVendaFechada;

	@Column(name = "com_ecf_venda_cancelada")
	private int comEcfVendaCancelada;

	@Column(name = "com_ecf_venda_ccf")
	private int comEcfVendaCcf;

	@Column(name = "com_ecf_venda_coo")
	private int comEcfVendaCoo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "com_ecf_venda_data")
	private Date comEcfVendaData;

	@Column(name = "com_ecf_venda_desconto")
	private Double comEcfVendaDesconto;

	@Column(name = "com_ecf_venda_acrescimo")
	private Double comEcfVendaAcrescimo;

	@Column(name = "com_ecf_venda_liquido")
	private Double comEcfVendaLiquido;

	@Transient
	private String cancelada;

	@Transient
	private String descIndicador;

	@Transient
	private String acresIndicador;

	@JoinColumn(name = "sis_usuario_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private SisUsuario sisUsuario;

	@JoinColumn(name = "emp_cliente_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EmpCliente empCliente;

	@JoinColumn(name = "fin_receber_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private FinReceber finReceber;

	@JoinColumn(name = "com_ecf_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ComEcf comEcf;

	@JoinColumn(name = "com_ecf_z_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ComEcfZ comEcfZ;

	@OneToMany(mappedBy = "comEcfVenda", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ComEcfVendaProduto> comEcfVendaProdutos;

	public ComEcfVenda() {
		this(0);
	}

	public ComEcfVenda(int comEcfVendaId) {
		super("pu_comercial", "ComEcfVenda", "comEcfVendaId", "comEcfVendaData", EDirecao.DESC);
		this.comEcfVendaId = comEcfVendaId;
	}

	public int getComEcfVendaId() {
		return this.comEcfVendaId;
	}

	public void setComEcfVendaId(int comEcfVendaId) {
		this.comEcfVendaId = comEcfVendaId;
	}

	public Double getComEcfVendaBruto() {
		return this.comEcfVendaBruto;
	}

	public void setComEcfVendaBruto(Double comEcfVendaBruto) {
		this.comEcfVendaBruto = comEcfVendaBruto;
	}

	public boolean getComEcfVendaFechada() {
		return comEcfVendaFechada == 0 ? false : true;
	}

	public void setComEcfVendaFechada(boolean comEcfVendaFechada) {
		this.comEcfVendaFechada = comEcfVendaFechada == false ? 0 : 1;
	}

	public boolean getComEcfVendaCancelada() {
		return comEcfVendaCancelada == 0 ? false : true;
	}

	public void setComEcfVendaCancelada(boolean comEcfVendaCancelada) {
		this.comEcfVendaCancelada = comEcfVendaCancelada == false ? 0 : 1;
	}

	public int getComEcfVendaCcf() {
		return comEcfVendaCcf;
	}

	public void setComEcfVendaCcf(int comEcfVendaCcf) {
		this.comEcfVendaCcf = comEcfVendaCcf;
	}

	public int getComEcfVendaCoo() {
		return this.comEcfVendaCoo;
	}

	public void setComEcfVendaCoo(int comEcfVendaCoo) {
		this.comEcfVendaCoo = comEcfVendaCoo;
	}

	public Date getComEcfVendaData() {
		return this.comEcfVendaData;
	}

	public void setComEcfVendaData(Date comEcfVendaData) {
		this.comEcfVendaData = comEcfVendaData;
	}

	public Double getComEcfVendaDesconto() {
		return this.comEcfVendaDesconto;
	}

	public void setComEcfVendaDesconto(Double comEcfVendaDesconto) {
		this.comEcfVendaDesconto = comEcfVendaDesconto;
	}

	public Double getComEcfVendaAcrescimo() {
		return comEcfVendaAcrescimo;
	}

	public void setComEcfVendaAcrescimo(Double comEcfVendaAcrescimo) {
		this.comEcfVendaAcrescimo = comEcfVendaAcrescimo;
	}

	public Double getComEcfVendaLiquido() {
		return this.comEcfVendaLiquido;
	}

	public void setComEcfVendaLiquido(Double comEcfVendaLiquido) {
		this.comEcfVendaLiquido = comEcfVendaLiquido;
	}

	public EmpCliente getEmpCliente() {
		return empCliente;
	}

	public void setEmpCliente(EmpCliente empCliente) {
		this.empCliente = empCliente;
	}

	public FinReceber getFinReceber() {
		return finReceber;
	}

	public void setFinReceber(FinReceber finReceber) {
		this.finReceber = finReceber;
	}

	public SisUsuario getSisUsuario() {
		return sisUsuario;
	}

	public void setSisUsuario(SisUsuario sisUsuario) {
		this.sisUsuario = sisUsuario;
	}

	public ComEcf getComEcf() {
		return this.comEcf;
	}

	public void setComEcf(ComEcf comEcf) {
		this.comEcf = comEcf;
	}

	public ComEcfZ getComEcfZ() {
		return comEcfZ;
	}

	public void setComEcfZ(ComEcfZ comEcfZ) {
		this.comEcfZ = comEcfZ;
	}

	public String getCancelada() {
		return cancelada;
	}

	public void setCancelada(String cancelada) {
		this.cancelada = cancelada;
	}

	public String getDescIndicador() {
		return descIndicador;
	}

	public void setDescIndicador(String descIndicador) {
		this.descIndicador = descIndicador;
	}

	public String getAcresIndicador() {
		return acresIndicador;
	}

	public void setAcresIndicador(String acresIndicador) {
		this.acresIndicador = acresIndicador;
	}

	public List<ComEcfVendaProduto> getComEcfVendaProdutos() {
		return this.comEcfVendaProdutos;
	}

	public void setComEcfVendaProdutos(List<ComEcfVendaProduto> comEcfVendaProdutos) {
		this.comEcfVendaProdutos = comEcfVendaProdutos;
	}

	public Number getId() {
		return comEcfVendaId;
	}

	public void setId(Number id) {
		comEcfVendaId = id.intValue();
	}

	public String[] toArray() {
		int receberId = finReceber == null ? 0 : finReceber.getFinReceberId();
		int clienteId = empCliente == null ? 0 : empCliente.getEmpClienteId();

		return new String[] { comEcfVendaId + "", comEcfZ.getComEcfZId() + "", comEcf.getEmpEmpresa().getEmpEmpresaId() + "", comEcf.getEmpEmpresa().getEmpEntidade().getEmpEntidadeNome1(),
				sisUsuario.getSisUsuarioId() + "", sisUsuario.getSisUsuarioLogin(), comEcf.getComEcfId() + "", comEcf.getComEcfSerie(), comEcfVendaCcf + "", comEcfVendaCoo + "",
				UtilClient.getDataGrid(comEcfVendaData), comEcfVendaBruto.toString(), comEcfVendaDesconto.toString(), comEcfVendaAcrescimo.toString(), comEcfVendaLiquido.toString(),
				getComEcfVendaFechada() + "", clienteId + "", receberId + "", getComEcfVendaCancelada() + "" };
	}

	public void anularDependencia() {
		sisUsuario = null;
		empCliente = null;
		finReceber = null;
		comEcf = null;
		comEcfZ = null;
		comEcfVendaProdutos = null;
	}
}