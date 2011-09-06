package br.com.opensig.financeiro.server;

import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.EArquivo;
import br.com.opensig.financeiro.client.servico.FinanceiroException;
import br.com.opensig.financeiro.client.servico.FinanceiroService;
import br.com.opensig.financeiro.server.acao.ExcluirPagar;
import br.com.opensig.financeiro.server.acao.ExcluirReceber;
import br.com.opensig.financeiro.server.acao.ExcluirRetorno;
import br.com.opensig.financeiro.server.acao.SalvarPagar;
import br.com.opensig.financeiro.server.acao.SalvarReceber;
import br.com.opensig.financeiro.server.acao.SalvarRetorno;
import br.com.opensig.financeiro.server.boleto.FabricaRecibo;
import br.com.opensig.financeiro.server.boleto.IRecibo;
import br.com.opensig.financeiro.server.cobranca.FabricaCobranca;
import br.com.opensig.financeiro.server.cobranca.ICobranca;
import br.com.opensig.financeiro.shared.modelo.FinCategoria;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinPagar;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.financeiro.shared.modelo.FinRemessa;
import br.com.opensig.financeiro.shared.modelo.FinRetorno;

public class FinanceiroServiceImpl extends CoreServiceImpl implements FinanceiroService {

	private static final long serialVersionUID = -1740106892594863626L;

	public String gerar(int boletoId, EArquivo tipo, boolean recibo) throws FinanceiroException {
		String retorno = "";
		HttpSession sessao = getThreadLocalRequest().getSession();
		Autenticacao autenticacao = (Autenticacao) sessao.getAttribute("Autenticacao");

		try {
			FiltroNumero fn = new FiltroNumero("finRecebimentoId", ECompara.IGUAL, boletoId);
			FinRecebimento finBoleto = (FinRecebimento) selecionar(new FinRecebimento(), fn, false);

			byte[] obj = null;
			String nome = "";

			if (recibo) {
				nome = "recibo";
				IRecibo rec = FabricaRecibo.getInstancia().getRecibo(tipo);
				obj = rec.getRecibo(autenticacao.getEmpresa(), finBoleto);
			} else {
				nome = "boleto";
				obj = getCobranca(finBoleto.getFinReceber().getFinConta()).boleto(tipo, autenticacao.getEmpresa(), finBoleto);
			}

			retorno = sessao.getId() + UtilServer.getData().getTime();
			sessao.setAttribute(retorno, obj);
			sessao.setAttribute(retorno + "arquivo", nome);
			sessao.setAttribute(retorno + "tipo", tipo);
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao gerar boleto ou recibo", e);
			throw new FinanceiroException(e.getMessage());
		}

		return retorno;
	}

	public Boolean remessa(FinRemessa remessa) throws  FinanceiroException {
		return getCobranca(remessa.getFinConta()).remessa(remessa);
	}

	public String[][] retorno(FinRetorno retorno) throws  FinanceiroException {
		return getCobranca(retorno.getFinConta()).retorno(retorno);
	}

	private ICobranca getCobranca(FinConta conta) throws FinanceiroException {
		FiltroNumero fn1 = new FiltroNumero("finContaId", ECompara.IGUAL, conta.getFinContaId());
		try {
			conta = (FinConta) selecionar(conta, fn1, false);
			return FabricaCobranca.getInstancia().getCobranca(conta);
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao gerar cobranca", e);
			throw new FinanceiroException(UtilServer.CONF.get("errInvalido"));
		}
	}

	public void excluirRetorno(FinRetorno retorno) throws FinanceiroException {
		try {
			new ExcluirRetorno(null, this, retorno).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao excluir retorno", e);
			throw new FinanceiroException(e.getMessage());
		}
	}

	public void excluirReceber(FinReceber receber) throws FinanceiroException {
		try {
			new ExcluirReceber(null, this, receber).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao excluir receber", e);
			throw new FinanceiroException(e.getMessage());
		}
	}

	public void excluirPagar(FinPagar pagar) throws FinanceiroException {
		try {
			new ExcluirPagar(null, this, pagar).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao excluir pagar", e);
			throw new FinanceiroException(e.getMessage());
		}
	}

	public FinReceber salvarReceber(FinReceber receber, List<FinCategoria> categorias) throws FinanceiroException {
		try {
			new SalvarReceber(null, this, receber, categorias).execute();
			receber.anularDependencia();
			return receber;
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao salvar receber", e);
			throw new FinanceiroException(e.getMessage());
		}
	}

	public FinPagar salvarPagar(FinPagar pagar, List<FinCategoria> categorias) throws FinanceiroException {
		try {
			new SalvarPagar(null, this, pagar, categorias).execute();
			pagar.anularDependencia();
			return pagar;
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao salvar pagar", e);
			throw new FinanceiroException(e.getMessage());
		}
	}

	public FinRetorno salvarRetorno(FinRetorno retorno, List<FinRecebimento> recebimentos) throws FinanceiroException {
		try {
			new SalvarRetorno(null, this, retorno, recebimentos).execute();
			retorno.anularDependencia();
			return retorno;
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao salvar retorno", e);
			throw new FinanceiroException(e.getMessage());
		}
	}

}
