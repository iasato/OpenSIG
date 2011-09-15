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

	@Column(name = "com_ecf_venda_produto_cancelado")
	private int comEcfVendaProdutoCancelado;

	@Column(name = "com_ecf_venda_produto_ordem")
	private int comEcfVendaProdutoOrdem;

	@Column(name = "com_ecf_venda_produto_quantidade")
	private Double comEcfVendaProdutoQuantidade;

	@Column(name = "com_ecf_venda_produto_total")
	private Double comEcfVendaProdutoTotal;

	@Column(name = "com_ecf_venda_produto_valor")
	private Double comEcfVendaProdutoValor;

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

	public Double getComEcfVendaProdutoValor() {
		return this.comEcfVendaProdutoValor;
	}

	public void setComEcfVendaProdutoValor(Double comEcfVendaProdutoValor) {
		this.comEcfVendaProdutoValor = comEcfVendaProdutoValor;
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

	public Number getId() {
		return comEcfVendaProdutoId;
	}

	public void setId(Number id) {
		comEcfVendaProdutoId = id.intValue();
	}

	public String[] toArray() {
		String barra = prodProduto.getProdProdutoBarra() == null || prodProduto.getProdProdutoBarra() == 0L ? null : prodProduto.getProdProdutoBarra() + "";
		return new String[] { comEcfVendaProdutoId + "", comEcfVenda.getComEcfVendaId() + "", comEcfVenda.getComEcf().getComEcfId() + "", comEcfVenda.getComEcf().getComEcfSerie(),
				comEcfVenda.getComEcf().getEmpEmpresa().getEmpEmpresaId() + "", comEcfVenda.getComEcf().getEmpEmpresa().getEmpEntidade().getEmpEntidadeNome1(),
				comEcfVenda.getEmpCliente().getEmpEntidade().getEmpEntidadeNome1(), prodProduto.getEmpFornecedor().getEmpEntidade().getEmpEntidadeNome1(), prodProduto.getProdProdutoId() + "", barra,
				prodProduto.getProdProdutoDescricao(), prodProduto.getProdProdutoReferencia(), UtilClient.getDataGrid(comEcfVenda.getComEcfVendaData()), comEcfVendaProdutoQuantidade + "",
				prodEmbalagem.getProdEmbalagemId() + "", prodEmbalagem.getProdEmbalagemNome(), comEcfVendaProdutoValor.toString(), comEcfVendaProdutoTotal.toString(),
				getComEcfVendaProdutoCancelado() + "", comEcfVendaProdutoOrdem + "" };
	}

}