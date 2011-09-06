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
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;

public class SalvarReceber extends Chain {

	private CoreServiceImpl servico;
	private FinReceber receber;
	private List<FinCategoria> categorias;

	public SalvarReceber(Chain next, CoreServiceImpl servico, FinReceber receber, List<FinCategoria> categorias) throws OpenSigException {
		super(next);
		this.servico = servico;
		this.receber = receber;
		this.categorias = categorias;
	}

	@Override
	public void execute() throws OpenSigException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			// recupera uma instância do gerenciador de entidades
			emf = Conexao.getInstancia(receber.getPu());
			em = emf.createEntityManager();
			em.getTransaction().begin();

			// salva
			List<FinRecebimento> recebimentos = receber.getFinRecebimentos();
			receber.setFinRecebimentos(null);
			servico.salvar(em, receber);

			// deleta
			FiltroObjeto fo = new FiltroObjeto("finReceber", ECompara.IGUAL, receber);
			Sql sql = new Sql(new FinRecebimento(), EComando.EXCLUIR, fo);
			servico.executar(em, sql);

			// insere
			double valor = 0.00;
			for (FinRecebimento finPag : recebimentos) {
				if (finPag.getFinRecebimentoId() == 0 && finPag.getFinRecebimentoQuitado()) {
					valor += finPag.getFinRecebimentoValor();
				}
				finPag.setFinRecebimentoId(0);
				finPag.setFinReceber(receber);
			}
			servico.salvar(em, recebimentos);

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
				ParametroFormula pf = new ParametroFormula("finContaSaldo", valor);
				FiltroNumero fn = new FiltroNumero("finContaId", ECompara.IGUAL, receber.getFinConta().getFinContaId());
				Sql sqlConta = new Sql(new FinConta(), EComando.ATUALIZAR, fn, pf);
				servico.executar(new Sql[] { sqlConta });
			}
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao salvar receber", ex);
			throw new FinanceiroException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

}