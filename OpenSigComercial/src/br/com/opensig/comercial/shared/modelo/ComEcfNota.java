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

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.empresa.shared.modelo.EmpCliente;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;

/**
 * Classe que representa uma venda tipo D1 do ECF.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 10/09/2012
 */
@Entity
@Table(name = "com_ecf_nota")
public class ComEcfNota extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_nota_id")
	private int comEcfNotaId;

	@Column(name = "com_ecf_nota_serie")
	private String comEcfNotaSerie;

	@Column(name = "com_ecf_nota_subserie")
	private String comEcfNotaSubserie;

	@Column(name = "com_ecf_nota_numero")
	private int comEcfNotaNumero;

	@Temporal(TemporalType.DATE)
	@Column(name = "com_ecf_nota_data")
	private Date comEcfNotaData;

	@Column(name = "com_ecf_nota_bruto")
	private Double comEcfNotaBruto;

	@Column(name = "com_ecf_nota_desconto")
	private Double comEcfNotaDesconto;

	@Column(name = "com_ecf_nota_liquido")
	private Double comEcfNotaLiquido;

	@Column(name = "com_ecf_nota_pis")
	private Double comEcfNotaPis;

	@Column(name = "com_ecf_nota_cofins")
	private Double comEcfNotaCofins;

	@Column(name = "com_ecf_nota_cancelada")
	private int comEcfNotaCancelada;

	@JoinColumn(name = "emp_cliente_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EmpCliente empCliente;

	@JoinColumn(name = "emp_empresa_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EmpEmpresa empEmpresa;

	@OneToMany(mappedBy = "comEcfNota", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<ComEcfNotaProduto> comEcfNotaProdutos;

	public ComEcfNota() {
		this(0);
	}

	public ComEcfNota(int comEcfNotaId) {
		super("pu_comercial", "ComEcfNota", "comEcfNotaId", "comEcfNotaData", EDirecao.DESC);
		this.comEcfNotaId = comEcfNotaId;
	}

	public int getComEcfNotaId() {
		return comEcfNotaId;
	}

	public void setComEcfNotaId(int comEcfNotaId) {
		this.comEcfNotaId = comEcfNotaId;
	}

	public String getComEcfNotaSerie() {
		return comEcfNotaSerie;
	}

	public void setComEcfNotaSerie(String comEcfNotaSerie) {
		this.comEcfNotaSerie = comEcfNotaSerie;
	}

	public String getComEcfNotaSubserie() {
		return comEcfNotaSubserie;
	}

	public void setComEcfNotaSubserie(String comEcfNotaSubserie) {
		this.comEcfNotaSubserie = comEcfNotaSubserie;
	}

	public int getComEcfNotaNumero() {
		return comEcfNotaNumero;
	}

	public void setComEcfNotaNumero(int comEcfNotaNumero) {
		this.comEcfNotaNumero = comEcfNotaNumero;
	}

	public Date getComEcfNotaData() {
		return comEcfNotaData;
	}

	public void setComEcfNotaData(Date comEcfNotaData) {
		this.comEcfNotaData = comEcfNotaData;
	}

	public Double getComEcfNotaBruto() {
		return comEcfNotaBruto;
	}

	public void setComEcfNotaBruto(Double comEcfNotaBruto) {
		this.comEcfNotaBruto = comEcfNotaBruto;
	}

	public Double getComEcfNotaDesconto() {
		return comEcfNotaDesconto;
	}

	public void setComEcfNotaDesconto(Double comEcfNotaDesconto) {
		this.comEcfNotaDesconto = comEcfNotaDesconto;
	}

	public Double getComEcfNotaLiquido() {
		return comEcfNotaLiquido;
	}

	public void setComEcfNotaLiquido(Double comEcfNotaLiquido) {
		this.comEcfNotaLiquido = comEcfNotaLiquido;
	}

	public Double getComEcfNotaPis() {
		return comEcfNotaPis;
	}

	public void setComEcfNotaPis(Double comEcfNotaPis) {
		this.comEcfNotaPis = comEcfNotaPis;
	}

	public Double getComEcfNotaCofins() {
		return comEcfNotaCofins;
	}

	public void setComEcfNotaCofins(Double comEcfNotaCofins) {
		this.comEcfNotaCofins = comEcfNotaCofins;
	}

	public boolean getComEcfNotaCancelada() {
		return comEcfNotaCancelada == 0 ? false : true;
	}

	public void setComEcfNotaCancelada(boolean comEcfNotaCancelada) {
		this.comEcfNotaCancelada = comEcfNotaCancelada == false ? 0 : 1;
	}

	public EmpCliente getEmpCliente() {
		return empCliente;
	}

	public void setEmpCliente(EmpCliente empCliente) {
		this.empCliente = empCliente;
	}

	public EmpEmpresa getEmpEmpresa() {
		return empEmpresa;
	}

	public void setEmpEmpresa(EmpEmpresa empEmpresa) {
		this.empEmpresa = empEmpresa;
	}

	public List<ComEcfNotaProduto> getComEcfNotaProdutos() {
		return comEcfNotaProdutos;
	}

	public void setComEcfNotaProdutos(List<ComEcfNotaProduto> comEcfNotaProdutos) {
		this.comEcfNotaProdutos = comEcfNotaProdutos;
	}

	public Number getId() {
		return comEcfNotaId;
	}

	public void setId(Number id) {
		comEcfNotaId = id.intValue();
	}

	public String[] toArray() {
		int clienteId = empCliente != null ? empCliente.getEmpClienteId() : 0;
		String clienteNome = empCliente != null ? empCliente.getEmpEntidade().getEmpEntidadeNome1() : "";

		return new String[] { comEcfNotaId + "", clienteId + "", clienteNome, empEmpresa.getEmpEmpresaId() + "", empEmpresa.getEmpEntidade().getEmpEntidadeNome1(), comEcfNotaSerie,
				comEcfNotaSubserie, comEcfNotaNumero + "", UtilClient.getDataGrid(comEcfNotaData), comEcfNotaBruto.toString(), comEcfNotaDesconto.toString(), comEcfNotaLiquido.toString(),
				comEcfNotaPis.toString(), comEcfNotaCofins.toString(), getComEcfNotaCancelada() + "" };
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("empCliente")) {
			return new EmpCliente();
		} else if (campo.startsWith("empEmpresa")) {
			return new EmpEmpresa();
		} else {
			return null;
		}
	}

	public void anularDependencia() {
		empCliente = null;
		empEmpresa = null;
		comEcfNotaProdutos = null;
	}
}
