package br.com.opensig.financeiro.server.acao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.parametro.GrupoParametro;
import br.com.opensig.core.client.controlador.parametro.IParametro;
import br.com.opensig.core.client.controlador.parametro.ParametroBinario;
import br.com.opensig.core.client.controlador.parametro.ParametroData;
import br.com.opensig.core.client.controlador.parametro.ParametroFormula;
import br.com.opensig.core.client.controlador.parametro.ParametroNumero;
import br.com.opensig.core.client.controlador.parametro.ParametroTexto;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.Conexao;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.financeiro.client.servico.FinanceiroException;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.financeiro.shared.modelo.FinRetorno;

public class SalvarRetorno extends Chain {

	private CoreServiceImpl servico;
	private FinRetorno retorno;
	private List<FinRecebimento> recebimentos;

	public SalvarRetorno(Chain next, CoreServiceImpl servico, FinRetorno retorno, List<FinRecebimento> recebimentos) throws OpenSigException {
		super(next);
		this.servico = servico;
		this.retorno = retorno;
		this.recebimentos = recebimentos;
	}

	@Override
	public void execute() throws OpenSigException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			// recupera uma instância do gerenciador de entidades
			emf = Conexao.getInstancia(retorno.getPu());
			em = emf.createEntityManager();
			em.getTransaction().begin();

			// acumuladores
			double valor = 0.00;
			String ids = "";

			// quitando
			for (FinRecebimento fin : recebimentos) {
				valor += fin.getFinRecebimentoValor();
				ids += fin.getFinRecebimentoId() + " ";

				FiltroNumero fn = new FiltroNumero("finRecebimentoId", ECompara.IGUAL, fin.getFinRecebimentoId());

				ParametroNumero pn = new ParametroNumero("finRecebimentoValor", fin.getFinRecebimentoValor());
				ParametroData pd = new ParametroData("finRecebimentoRealizado", fin.getFinRecebimentoRealizado());
				ParametroBinario pb = new ParametroBinario("finRecebimentoQuitado", 1);
				ParametroTexto pt = new ParametroTexto("finRecebimentoObservacao", UtilServer.CONF.get("txtAuto"));
				GrupoParametro gp = new GrupoParametro(new IParametro[] { pn, pd, pb, pt });

				Sql sql = new Sql(fin, EComando.ATUALIZAR, fn, gp);
				servico.executar(em, sql);
			}

			// atualizando a conta
			FinConta conta = recebimentos.get(0).getFinReceber().getFinConta();
			FiltroNumero fn = new FiltroNumero("finContaId", ECompara.IGUAL, conta.getId());
			ParametroFormula pf = new ParametroFormula("finContaSaldo", valor);
			Sql sql = new Sql(new FinConta(), EComando.ATUALIZAR, fn, pf);
			servico.executar(em, sql);

			// salva retorno
			retorno.setFinConta(conta);
			retorno.setFinRetornoBoletos(ids);
			retorno.setFinRetornoValor(valor);
			servico.salvar(em, retorno);
			
			if (next != null) {
				next.execute();
			}
			em.getTransaction().commit();
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao salvar retorno", ex);
			throw new FinanceiroException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}
}
