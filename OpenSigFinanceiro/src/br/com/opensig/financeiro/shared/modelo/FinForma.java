package br.com.opensig.financeiro.shared.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.opensig.core.shared.modelo.Dados;

/**
 * Classe que representa uma forma de pagamento no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 18/11/2009
 */
@Entity
@Table(name = "fin_forma")
public class FinForma extends Dados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fin_forma_id")
	private int finFormaId;

	@Column(name = "fin_forma_descricao")
	private String finFormaDescricao;

	public FinForma() {
		this(0);
	}

	public FinForma(int finFormaId) {
		super("pu_financeiro", "FinForma", "finFormaId", "finFormaDescricao");
		this.finFormaId = finFormaId;
	}

	public int getFinFormaId() {
		return finFormaId;
	}

	public void setFinFormaId(int finFormaId) {
		this.finFormaId = finFormaId;
	}

	public String getFinFormaDescricao() {
		return finFormaDescricao;
	}

	public void setFinFormaDescricao(String finFormaDescricao) {
		this.finFormaDescricao = finFormaDescricao;
	}

	public Number getId() {
		return finFormaId;
	}

	public void setId(Number id) {
		finFormaId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { finFormaId + "", finFormaDescricao };
	}
}
