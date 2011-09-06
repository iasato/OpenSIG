package br.com.opensig.financeiro.server.acao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.parametro.ParametroFormula;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.Conexao;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.financeiro.client.servico.FinanceiroException;
import br.com.opensig.financeiro.shared.modelo.FinCategoria;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinPagamento;
import br.com.opensig.financeiro.shared.modelo.FinPagar;

public class SalvarPagar extends Chain {

	private CoreServiceImpl servico;
	private FinPagar pagar;
	private List<FinCategoria> categorias;

	public SalvarPagar(Chain next, CoreServiceImpl servico, FinPagar pagar, List<FinCategoria> categorias) throws OpenSigException {
		super(next);
		this.servico = servico;
		this.pagar = pagar;
		this.categorias = categorias;
	}

	@Override
	public void execute() throws OpenSigException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			// recupera uma instância do gerenciador de entidades
			emf = Conexao.getInstancia(pagar.getPu());
			em = emf.createEntityManager();
			em.getTransaction().begin();

			// salva
			List<FinPagamento> pagamentos = pagar.getFinPagamentos();
			pagar.setFinPagamentos(null);
			servico.salvar(em, pagar);

			// deleta
			FiltroObjeto fo = new FiltroObjeto("finPagar", ECompara.IGUAL, pagar);
			Sql sql = new Sql(new FinPagamento(), EComando.EXCLUIR, fo);
			servico.executar(em, sql);

			// insere
			double valor = 0.00;
			for (FinPagamento finPag : pagamentos) {
				if (finPag.getFinPagamentoId() == 0 && finPag.getFinPagamentoQuitado()) {
					valor += finPag.getFinPagamentoValor();
					finPag.setFinPagamentoObservacao("");
				}
				finPag.setFinPagamentoId(0);
				finPag.setFinPagar(pagar);
			}
			servico.salvar(em, pagamentos);
			
			// categorias
			if (categorias != null && !categorias.isEmpty()) {
				servico.salvar(em, categorias);
			}

			if (next != null) {
				next.execute();
			}
			em.getTransaction().commit();
			
			// trata a conta
			if (valor > 0.00) {
				ParametroFormula pf = new ParametroFormula("finContaSaldo", valor * -1);
				FiltroNumero fn = new FiltroNumero("finContaId", ECompara.IGUAL, pagar.getFinConta().getFinContaId());
				Sql sqlConta = new Sql(new FinConta(), EComando.ATUALIZAR, fn, pf);
				servico.executar(new Sql[] { sqlConta });
			}
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao salvar pagar", ex);
			throw new FinanceiroException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

}