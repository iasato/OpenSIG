package br.com.opensig.produto.shared.modelo;

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
 * Classe que representa um preço no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 16/07/2009
 */
@Entity
@Table(name = "prod_preco")
public class ProdPreco extends Dados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prod_preco_id")
	private int prodPrecoId;

	@Column(name = "prod_preco_valor")
	private double prodPrecoValor;

	@Column(name = "prod_preco_barra")
	private Long prodPrecoBarra;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prod_produto_id")
	private ProdProduto prodProduto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prod_embalagem_id")
	private ProdEmbalagem prodEmbalagem;

	public ProdPreco() {
		this(0);
	}

	public ProdPreco(int prodPrecoId) {
		super("pu_produto", "ProdPreco", "prodPrecoId", "prodPrecoValor");
		this.prodPrecoId = prodPrecoId;
	}

	public int getProdPrecoId() {
		return this.prodPrecoId;
	}

	public void setProdPrecoId(int prodPrecoId) {
		this.prodPrecoId = prodPrecoId;
	}

	public double getProdPrecoValor() {
		return this.prodPrecoValor;
	}

	public void setProdPrecoValor(double prodPrecoValor) {
		this.prodPrecoValor = prodPrecoValor;
	}

	public Long getProdPrecoBarra() {
		return prodPrecoBarra;
	}

	public void setProdPrecoBarra(Long prodPrecoBarra) {
		this.prodPrecoBarra = prodPrecoBarra;
	}

	public ProdProduto getProdProduto() {
		return this.prodProduto;
	}

	public void setProdProduto(ProdProduto prodProduto) {
		this.prodProduto = prodProduto;
	}

	public ProdEmbalagem getProdEmbalagem() {
		return this.prodEmbalagem;
	}

	public void setProdEmbalagem(ProdEmbalagem prodEmbalagem) {
		this.prodEmbalagem = prodEmbalagem;
	}

	public Number getId() {
		return prodPrecoId;
	}

	public void setId(Number id) {
		prodPrecoId = id.intValue();
	}

	public String[] toArray() {
		String barra = prodPrecoBarra == null || prodPrecoBarra == 0L ? null : prodPrecoBarra + "";
		return new String[] { prodPrecoId + "", prodEmbalagem.getProdEmbalagemId() + "", prodEmbalagem.getProdEmbalagemNome(), prodPrecoValor + "", barra };
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("prodProduto")) {
			return new ProdProduto();
		} else {
			return null;
		}
	}

	public void anularDependencia() {
		prodProduto = null;
	}
}