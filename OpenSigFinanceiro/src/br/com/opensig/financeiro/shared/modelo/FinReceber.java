package br.com.opensig.financeiro.shared.modelo;

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
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpEntidade;

/**
 * Classe que representa um a receber no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 * @since 18/11/2009
 */
@Entity
@Table(name = "fin_receber")
public class FinReceber extends Dados implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fin_receber_id")
	private int finReceberId;

	@Column(name = "fin_receber_valor")
	private Double finReceberValor;

	@Column(name = "fin_receber_categoria")
	private String finReceberCategoria;
	
	@Column(name = "fin_receber_nfe")
	private int finReceberNfe;

	@Column(name = "fin_receber_cadastro")
	@Temporal(TemporalType.DATE)
	private Date finReceberCadastro;

	@Column(name = "fin_receber_observacao")
	private String finReceberObservacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_empresa_id")
	private EmpEmpresa empEmpresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_entidade_id")
	private EmpEntidade empEntidade;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fin_conta_id")
	private FinConta finConta;

	@OneToMany(mappedBy = "finReceber", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<FinRecebimento> finRecebimentos;
	
	public FinReceber() {
		this(0);
	}

	public FinReceber(int finReceberId) {
		super("pu_financeiro", "FinReceber", "finReceberId", "finReceberCadastro", EDirecao.DESC);
		this.finReceberId = finReceberId;
	}

	public int getFinReceberId() {
		return finReceberId;
	}

	public void setFinReceberId(int finReceberId) {
		this.finReceberId = finReceberId;
	}

	public Double getFinReceberValor() {
		return finReceberValor;
	}

	public void setFinReceberValor(Double finReceberValor) {
		this.finReceberValor = finReceberValor;
	}

	public String getFinReceberCategoria() {
		return finReceberCategoria;
	}

	public void setFinReceberCategoria(String finReceberCategoria) {
		this.finReceberCategoria = finReceberCategoria;
	}

	public int getFinReceberNfe() {
		return finReceberNfe;
	}

	public void setFinReceberNfe(int finReceberNfe) {
		this.finReceberNfe = finReceberNfe;
	}

	public Date getFinReceberCadastro() {
		return finReceberCadastro;
	}

	public void setFinReceberCadastro(Date finReceberCadastro) {
		this.finReceberCadastro = finReceberCadastro;
	}

	public EmpEmpresa getEmpEmpresa() {
		return empEmpresa;
	}

	public void setEmpEmpresa(EmpEmpresa empEmpresa) {
		this.empEmpresa = empEmpresa;
	}

	public EmpEntidade getEmpEntidade() {
		return empEntidade;
	}

	public void setEmpEntidade(EmpEntidade empEntidade) {
		this.empEntidade = empEntidade;
	}

	public FinConta getFinConta() {
		return finConta;
	}

	public void setFinConta(FinConta finConta) {
		this.finConta = finConta;
	}

	public List<FinRecebimento> getFinRecebimentos() {
		return finRecebimentos;
	}

	public void setFinRecebimentos(List<FinRecebimento> finRecebimentos) {
		this.finRecebimentos = finRecebimentos;
	}

	public String getFinReceberObservacao() {
		return finReceberObservacao;
	}

	public void setFinReceberObservacao(String finReceberObservacao) {
		this.finReceberObservacao = finReceberObservacao;
	}

	public Number getId() {
		return finReceberId;
	}

	public void setId(Number id) {
		finReceberId = id.intValue();
	}

	public String[] toArray() {
		return new String[] { finReceberId + "", empEmpresa.getEmpEmpresaId() + "", empEmpresa.getEmpEntidade().getEmpEntidadeNome1(), empEntidade.getEmpEntidadeId() + "",
				empEntidade.getEmpEntidadeNome1(), finConta.getFinContaId() + "", finConta.getFinContaNome(), finReceberValor.toString(), UtilClient.getDataGrid(finReceberCadastro),
				finReceberCategoria, finReceberNfe + "", finReceberObservacao };
	}

	public Dados getObjeto(String campo) {
		if (campo.startsWith("empEmpresa")) {
			return new EmpEmpresa();
		} else if (campo.startsWith("finConta")) {
			return new FinConta();
		} else {
			return null;
		}
	}

	public void anularDependencia() {
		empEmpresa = null;
		empEntidade = null;
		finConta = null;
		finRecebimentos = null;
	}
}