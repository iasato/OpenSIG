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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fin_forma_id")
	private int finFormaId;

	@Column(name = "fin_forma_descricao")
	private String finFormaDescricao;

	@Column(name = "fin_forma_codigo")
	private String finFormaCodigo;

	@Column(name = "fin_forma_tef")
	private int finFormaTef;

	@Column(name = "fin_forma_vinculado")
	private int finFormaVinculado;

	@Column(name = "fin_forma_debito")
	private int finFormaDebito;

	@Column(name = "fin_forma_rede")
	private String finFormaRede;

	@Column(name = "fin_forma_pagar")
	private int finFormaPagar;

	@Column(name = "fin_forma_receber")
	private int finFormaReceber;

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

	public String getFinFormaCodigo() {
		return finFormaCodigo;
	}

	public void setFinFormaCodigo(String finFormaCodigo) {
		this.finFormaCodigo = finFormaCodigo;
	}

	public boolean getFinFormaTef() {
		return finFormaTef == 0 ? false : true;
	}

	public void setFinFormaTef(boolean finFormaTef) {
		this.finFormaTef = finFormaTef == false ? 0 : 1;
	}

	public boolean getFinFormaVinculado() {
		return finFormaVinculado == 0 ? false : true;
	}

	public void setFinFormaVinculado(boolean finFormaVinculado) {
		this.finFormaVinculado = finFormaVinculado == false ? 0 : 1;
	}

	public boolean getFinFormaDebito() {
		return finFormaDebito == 0 ? false : true;
	}

	public void setFinFormaDebito(boolean finFormaDebito) {
		this.finFormaDebito = finFormaDebito == false ? 0 : 1;
	}

	public String getFinFormaRede() {
		return finFormaRede;
	}

	public void setFinFormaRede(String finFormaRede) {
		this.finFormaRede = finFormaRede;
	}

	public boolean getFinFormaPagar() {
		return finFormaPagar == 0 ? false : true;
	}

	public void setFinFormaPagar(boolean finFormaPagar) {
		this.finFormaPagar = finFormaPagar == false ? 0 : 1;
	}

	public boolean getFinFormaReceber() {
		return finFormaReceber == 0 ? false : true;
	}

	public void setFinFormaReceber(boolean finFormaReceber) {
		this.finFormaReceber = finFormaReceber == false ? 0 : 1;
	}

	public String[] toArray() {
		return new String[] { finFormaId + "", finFormaDescricao, finFormaCodigo, getFinFormaTef() + "", getFinFormaVinculado() + "", getFinFormaDebito() + "", finFormaRede, getFinFormaPagar() + "",
				getFinFormaReceber() + "" };
	}
}
