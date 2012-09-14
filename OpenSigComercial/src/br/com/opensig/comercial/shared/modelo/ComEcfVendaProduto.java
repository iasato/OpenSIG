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
import javax.persistence.Transient;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdProduto;

@Entity
@Table(name = "com_ecf_venda_produto")
public class ComEcfVendaProduto extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "com_ecf_venda_produto_id")
	private int comEcfVendaProdutoId;

	@Column(name = "com_ecf_venda_produto_codigo")
	private String comEcfVendaProdutoCodigo;

	@Column(name = "com_ecf_venda_produto_descricao")
	private String comEcfVendaProdutoDescricao;

	@Column(name = "com_ecf_venda_produto_quantidade")
	private Double comEcfVendaProdutoQuantidade;

	@Column(name = "com_ecf_venda_produto_bruto")
	private Double comEcfVendaProdutoBruto;

	@Column(name = "com_ecf_venda_produto_desconto")
	private Double comEcfVendaProdutoDesconto;

	@Column(name = "com_ecf_venda_produto_acrescimo")
	private Double comEcfVendaProdutoAcrescimo;

	@Column(name = "com_ecf_venda_produto_liquido")
	private Double comEcfVendaProdutoLiquido;

	@Column(name = "com_ecf_venda_produto_total")
	private Double comEcfVendaProdutoTotal;

	@Column(name = "com_ecf_venda_produto_cancelado")
	private int comEcfVendaProdutoCancelado;

	@Column(name = "com_ecf_venda_produto_ordem")
	private int comEcfVendaProdutoOrdem;

	@Transient
	private String cancelado;

	@Transient
	private int comEcfVendaProdutoCoo;

	@Transient
	private String comEcfVendaProdutoUnd;

	@JoinColumn(name = "prod_produto_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ProdProduto prodProduto;

	@JoinColumn(name = "prod_embalagem_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ProdEmbalagem prodEmbalagem;

	@JoinColumn(name = "com_ecf_venda_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ComEcfVenda comEcfVenda;

	public ComEcfVendaProduto() {
		this(0);
	}

	public ComEcfVendaProduto(int comEcfVendaProdutoId) {
		super("pu_comercial", "ComEcfVendaProduto", "comEcfVendaProdutoId", "comEcfVendaProdutoOrdem");
		this.comEcfVendaProdutoId = comEcfVendaProdutoId;
	}

	public int getComEcfVendaProdutoId() {
		return this.comEcfVendaProdutoId;
	}

	public void setComEcfVendaProdutoId(int comEcfVendaProdutoId) {
		this.comEcfVendaProdutoId = comEcfVendaProdutoId;
	}

	public boolean getComEcfVendaProdutoCancelado() {
		return this.comEcfVendaProdutoCancelado == 0 ? false : true;
	}

	public void setComEcfVendaProdutoCancelado(boolean comEcfVendaProdutoCancelado) {
		this.comEcfVendaProdutoCancelado = comEcfVendaProdutoCancelado == false ? 0 : 1;
	}

	public int getComEcfVendaProdutoOrdem() {
		return this.comEcfVendaProdutoOrdem;
	}

	public void setComEcfVendaProdutoOrdem(int comEcfVendaProdutoOrdem) {
		this.comEcfVendaProdutoOrdem = comEcfVendaProdutoOrdem;
	}

	public Double getComEcfVendaProdutoQuantidade() {
		return this.comEcfVendaProdutoQuantidade;
	}

	public void setComEcfVendaProdutoQuantidade(Double comEcfVendaProdutoQuantidade) {
		this.comEcfVendaProdutoQuantidade = comEcfVendaProdutoQuantidade;
	}

	public Double getComEcfVendaProdutoTotal() {
		return this.comEcfVendaProdutoTotal;
	}

	public void setComEcfVendaProdutoTotal(Double comEcfVendaProdutoTotal) {
		this.comEcfVendaProdutoTotal = comEcfVendaProdutoTotal;
	}

	public Double getComEcfVendaProdutoBruto() {
		return this.comEcfVendaProdutoBruto;
	}

	public String getComEcfVendaProdutoCodigo() {
		return comEcfVendaProdutoCodigo;
	}

	public void setComEcfVendaProdutoCodigo(String comEcfVendaProdutoCodigo) {
		this.comEcfVendaProdutoCodigo = comEcfVendaProdutoCodigo;
	}

	public String getComEcfVendaProdutoDescricao() {
		return comEcfVendaProdutoDescricao;
	}

	public void setComEcfVendaProdutoDescricao(String comEcfVendaProdutoDescricao) {
		this.comEcfVendaProdutoDescricao = comEcfVendaProdutoDescricao;
	}

	public Double getComEcfVendaProdutoDesconto() {
		return comEcfVendaProdutoDesconto;
	}

	public void setComEcfVendaProdutoDesconto(Double comEcfVendaProdutoDesconto) {
		this.comEcfVendaProdutoDesconto = comEcfVendaProdutoDesconto;
	}

	public Double getComEcfVendaProdutoLiquido() {
		return comEcfVendaProdutoLiquido;
	}

	public void setComEcfVendaProdutoLiquido(Double comEcfVendaProdutoLiquido) {
		this.comEcfVendaProdutoLiquido = comEcfVendaProdutoLiquido;
	}

	public void setComEcfVendaProdutoBruto(Double comEcfVendaProdutoBruto) {
		this.comEcfVendaProdutoBruto = comEcfVendaProdutoBruto;
	}

	public Double getComEcfVendaProdutoAcrescimo() {
		return comEcfVendaProdutoAcrescimo;
	}

	public void setComEcfVendaProdutoAcrescimo(Double comEcfVendaProdutoAcrescimo) {
		this.comEcfVendaProdutoAcrescimo = comEcfVendaProdutoAcrescimo;
	}

	public ProdProduto getProdProduto() {
		return prodProduto;
	}

	public void setProdProduto(ProdProduto prodProduto) {
		this.prodProduto = prodProduto;
	}

	public ProdEmbalagem getProdEmbalagem() {
		return prodEmbalagem;
	}

	public void setProdEmbalagem(ProdEmbalagem prodEmbalagem) {
		this.prodEmbalagem = prodEmbalagem;
	}

	public ComEcfVenda getComEcfVenda() {
		return this.comEcfVenda;
	}

	public void setComEcfVenda(ComEcfVenda comEcfVenda) {
		this.comEcfVenda = comEcfVenda;
	}

	public String getCancelado() {
		return cancelado;
	}

	public void setCancelado(String cancelado) {
		this.cancelado = cancelado;
	}

	public int getComEcfVendaProdutoCoo() {
		return comEcfVendaProdutoCoo;
	}

	public void setComEcfVendaProdutoCoo(int comEcfVendaProdutoCoo) {
		this.comEcfVendaProdutoCoo = comEcfVendaProdutoCoo;
	}

	public String getComEcfVendaProdutoUnd() {
		return comEcfVendaProdutoUnd;
	}

	public void setComEcfVendaProdutoUnd(String comEcfVendaProdutoUnd) {
		this.comEcfVendaProdutoUnd = comEcfVendaProdutoUnd;
	}

	public Number getId() {
		return comEcfVendaProdutoId;
	}

	public void setId(Number id) {
		comEcfVendaProdutoId = id.intValue();
	}

	public String[] toArray() {
		String fornecedor = "";
		String prodId = "0";
		String barra = comEcfVendaProdutoCodigo;
		String desc = comEcfVendaProdutoDescricao;
		String ref = "";

		if (prodProduto != null) {
			fornecedor = prodProduto.getEmpFornecedor().getEmpEntidade().getEmpEntidadeNome1();
			prodId = prodProduto.getProdProdutoId() + "";
			barra = prodProduto.getProdProdutoBarra();
			desc = prodProduto.getProdProdutoDescricao();
			ref = prodProduto.getProdProdutoReferencia();
		}

		return new String[] { comEcfVendaProdutoId + "", comEcfVenda.getComEcfVendaId() + "", comEcfVenda.getComEcf().getComEcfId() + "", comEcfVenda.getComEcf().getComEcfSerie(),
				comEcfVenda.getComEcf().getEmpEmpresa().getEmpEmpresaId() + "", comEcfVenda.getComEcf().getEmpEmpresa().getEmpEntidade().getEmpEntidadeNome1(), fornecedor, prodId, barra, desc, ref,
				UtilClient.getDataGrid(comEcfVenda.getComEcfVendaData()), comEcfVendaProdutoQuantidade.toString(), prodEmbalagem.getProdEmbalagemId() + "", prodEmbalagem.getProdEmbalagemNome(),
				comEcfVendaProdutoBruto.toString(), comEcfVendaProdutoDesconto.toString(), comEcfVendaProdutoAcrescimo.toString(), comEcfVendaProdutoLiquido.toString(),
				comEcfVendaProdutoTotal.toString(), getComEcfVendaProdutoCancelado() + "", comEcfVendaProdutoOrdem + "" };
	}

}