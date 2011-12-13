package br.com.opensig.comercial.server.acao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.com.opensig.comercial.client.servico.ComercialException;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.controlador.parametro.ParametroFormula;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.Conexao;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.financeiro.server.acao.SalvarReceber;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinForma;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdEstoque;

public class FecharEcfVenda extends Chain {

	private CoreServiceImpl servico;
	private ComEcfVenda venda;
	private FinReceber receber;
	private List<String[]> invalidos;
	private Autenticacao auth;

	public FecharEcfVenda(Chain next, CoreServiceImpl servico, ComEcfVenda venda, List<String[]> invalidos, Autenticacao auth) throws OpenSigException {
		super(null);
		this.servico = servico;
		this.venda = venda;
		this.invalidos = invalidos;
		this.auth = auth;

		// atualiza venda
		AtualizarVenda atuVen = new AtualizarVenda(next);
		// gerar receber padrao
		AtualizarReceber atuRec = new AtualizarReceber(atuVen);
		// atauliza estoque
		AtualizarEstoque atuEst = new AtualizarEstoque(atuRec);
		// valida o estoque
		ValidarEstoque valEst = new ValidarEstoque(atuEst);
		if (auth.getConf().get("estoque.ativo").equalsIgnoreCase("sim")) {
			this.next = valEst;
		} else {
			this.next = atuEst;
		}
	}

	@Override
	public void execute() throws OpenSigException {
		FiltroNumero fn = new FiltroNumero("comEcfVendaId", ECompara.IGUAL, venda.getId());
		venda = (ComEcfVenda) servico.selecionar(venda, fn, false);

		if (next != null) {
			next.execute();
		}
	}

	private class ValidarEstoque extends Chain {

		private List<ProdEmbalagem> embalagens;

		public ValidarEstoque(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			try {
				for (ComEcfVendaProduto venProd : venda.getComEcfVendaProdutos()) {
					if (venProd.getProdProduto() != null) {
						// formando o filtro
						FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, venda.getComEcf().getEmpEmpresa());
						FiltroObjeto fo1 = new FiltroObjeto("prodProduto", ECompara.IGUAL, venProd.getProdProduto());
						GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, fo1 });
						// busca o item
						double estQtd = servico.buscar(new ProdEstoque(), "t.prodEstoqueQuantidade", EBusca.SOMA, gf).doubleValue();
						// fatorando a quantida no estoque
						double qtd = venProd.getComEcfVendaProdutoQuantidade();
						if (venProd.getProdEmbalagem().getProdEmbalagemId() != venProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId()) {
							qtd *= getQtdEmbalagem(venProd.getProdEmbalagem().getProdEmbalagemId());
							qtd /= getQtdEmbalagem(venProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId());
						}
						// verificar a qtd do estoque
						if (qtd > estQtd) {
							invalidos.add(new String[] { venProd.getProdProduto().getProdProdutoDescricao(), venProd.getProdProduto().getProdProdutoReferencia(), estQtd + "", qtd + "" });
						} else {
							venProd.setComEcfVendaProdutoQuantidade(qtd);
						}
					} else {
						invalidos.add(new String[] { venProd.getComEcfVendaProdutoDescricao(), "", 0 + "", venProd.getComEcfVendaProdutoQuantidade() + "" });
					}
				}
			} catch (Exception ex) {
				UtilServer.LOG.error("Erro ao validar o estoque.", ex);
			}

			if (next != null && invalidos.isEmpty()) {
				next.execute();
			}
		}

		private int getQtdEmbalagem(int embalagemId) throws Exception {
			int unid = 1;
			if (embalagens == null) {
				embalagens = servico.selecionar(new ProdEmbalagem(), 0, 0, null, false).getLista();
			}

			for (ProdEmbalagem emb : embalagens) {
				if (emb.getProdEmbalagemId() == embalagemId) {
					unid = emb.getProdEmbalagemUnidade();
					break;
				}
			}
			return unid;
		}
	}

	private class AtualizarEstoque extends Chain {

		public AtualizarEstoque(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			EntityManagerFactory emf = null;
			EntityManager em = null;

			try {
				emf = Conexao.getInstancia(new ProdEstoque().getPu());
				em = emf.createEntityManager();
				em.getTransaction().begin();

				// recupera uma instância do gerenciador de entidades
				FiltroObjeto fo1 = new FiltroObjeto("empEmpresa", ECompara.IGUAL, venda.getComEcf().getEmpEmpresa());
				for (ComEcfVendaProduto comProd : venda.getComEcfVendaProdutos()) {
					// formando os parametros
					ParametroFormula pn1 = new ParametroFormula("prodEstoqueQuantidade", -1 * comProd.getComEcfVendaProdutoQuantidade());
					// formando o filtro
					FiltroObjeto fo2 = new FiltroObjeto("prodProduto", ECompara.IGUAL, comProd.getProdProduto());
					GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo1, fo2 });
					// busca o item
					ProdEstoque est = new ProdEstoque();
					// formando o sql
					Sql sql = new Sql(est, EComando.ATUALIZAR, gf, pn1);
					servico.executar(em, sql);
				}

				if (next != null) {
					next.execute();
				}
				em.getTransaction().commit();
			} catch (Exception ex) {
				if (em != null && em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}

				UtilServer.LOG.error("Erro ao atualizar o estoque.", ex);
				throw new ComercialException(ex.getMessage());
			} finally {
				em.close();
				emf.close();
			}
		}
	}

	private class AtualizarReceber extends Chain {

		public AtualizarReceber(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			// forma dinheiro
			FinForma forma = new FinForma(1);

			// unica parcela ja quitada
			FinRecebimento parcela = new FinRecebimento();
			parcela.setFinForma(forma);
			parcela.setFinRecebimentoCadastro(venda.getComEcfVendaData());
			parcela.setFinRecebimentoDocumento(venda.getComEcfVendaCoo() + "");
			parcela.setFinRecebimentoParcela("01/01");
			parcela.setFinRecebimentoQuitado(true);
			parcela.setFinRecebimentoRealizado(venda.getComEcfVendaData());
			parcela.setFinRecebimentoValor(venda.getComEcfVendaLiquido());
			parcela.setFinRecebimentoVencimento(venda.getComEcfVendaData());
			parcela.setFinRecebimentoObservacao(auth.getConf().get("categoria.ecf"));

			// conta padrao
			FinConta conta = new FinConta(Integer.valueOf(auth.getConf().get("conta.padrao")));
			List<FinRecebimento> parcelas = new ArrayList<FinRecebimento>();
			parcelas.add(parcela);

			// gera o receber
			receber = new FinReceber();
			receber.setEmpEmpresa(venda.getComEcf().getEmpEmpresa());
			receber.setEmpEntidade(venda.getEmpCliente().getEmpEntidade());
			receber.setFinConta(conta);
			receber.setFinRecebimentos(parcelas);
			receber.setFinReceberCadastro(venda.getComEcfVendaData());
			receber.setFinReceberCategoria(auth.getConf().get("categoria.ecf"));
			receber.setFinReceberValor(venda.getComEcfVendaLiquido());
			receber.setFinReceberNfe(venda.getComEcfVendaCoo());

			// efetua o salvamento do receber
			SalvarReceber salvar = new SalvarReceber(null, servico, receber, null);
			salvar.execute();

			if (next != null) {
				next.execute();
			}
		}

	}

	private class AtualizarVenda extends Chain {

		public AtualizarVenda(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			EntityManagerFactory emf = null;
			EntityManager em = null;

			try {
				// recupera uma instância do gerenciador de entidades
				emf = Conexao.getInstancia(venda.getPu());
				em = emf.createEntityManager();
				em.getTransaction().begin();
				// atualiza o status para fechada
				venda.setComEcfVendaFechada(true);
				venda.setFinReceber(receber);
				servico.salvar(em, venda);

				if (next != null) {
					next.execute();
				}
				em.getTransaction().commit();
			} catch (Exception ex) {
				if (em != null && em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}

				UtilServer.LOG.error("Erro ao atualizar a venda.", ex);
				throw new ComercialException(ex.getMessage());
			} finally {
				em.close();
				emf.close();
			}
		}
	}
}
