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
 * Classe que representa uma badeira de cartao no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 18/11/2009
 */
@Entity
@Table(name = "fin_bandeira")
public class FinBandeira extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fin_bandeira_id")
	private int finBandeiraId;

	@Column(name = "fin_bandeira_descricao")
	private String finBandeiraDescricao;

	@Column(name = "fin_bandeira_debito")
	private int finBandeiraDebito;
	
	public FinBandeira() {
		this(0);
	}

	public FinBandeira(int finBandeiraId) {
		super("pu_financeiro", "FinBandeira", "finBandeiraId", "finBandeiraDescricao");
		this.finBandeiraId = finBandeiraId;
	}

	public int getFinBandeiraId() {
		return finBandeiraId;
	}

	public void setFinBandeiraId(int finBandeiraId) {
		this.finBandeiraId = finBandeiraId;
	}

	public String getFinBandeiraDescricao() {
		return finBandeiraDescricao;
	}

	public void setFinBandeiraDescricao(String finBandeiraDescricao) {
		this.finBandeiraDescricao = finBandeiraDescricao;
	}

	public boolean getFinBandeiraDebito() {
		return finBandeiraDebito == 0 ? false : true;
	}

	public void setFinBandeiraDebito(boolean finBandeiraDebito) {
		this.finBandeiraDebito = finBandeiraDebito == false ? 0 : 1;
	}
	
	public Number getId() {
		return finBandeiraId;
	}

	public void setId(Number id) {
		finBandeiraId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { finBandeiraId + "", finBandeiraDescricao, getFinBandeiraDebito() + "" };
	}
}
