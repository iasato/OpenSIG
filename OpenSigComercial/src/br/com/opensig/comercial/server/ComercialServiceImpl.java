package br.com.opensig.comercial.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import br.com.opensig.comercial.client.servico.ComercialException;
import br.com.opensig.comercial.client.servico.ComercialService;
import br.com.opensig.comercial.server.acao.AnalisarNfe;
import br.com.opensig.comercial.server.acao.CancelarVenda;
import br.com.opensig.comercial.server.acao.ExcluirCompra;
import br.com.opensig.comercial.server.acao.ExcluirFrete;
import br.com.opensig.comercial.server.acao.ExcluirVenda;
import br.com.opensig.comercial.server.acao.FecharCompra;
import br.com.opensig.comercial.server.acao.FecharFrete;
import br.com.opensig.comercial.server.acao.FecharVenda;
import br.com.opensig.comercial.server.acao.GerarNfe;
import br.com.opensig.comercial.server.acao.ImportarNfe;
import br.com.opensig.comercial.server.acao.SalvarCompra;
import br.com.opensig.comercial.server.acao.SalvarValor;
import br.com.opensig.comercial.server.acao.SalvarVenda;
import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComValorProduto;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.parametro.GrupoParametro;
import br.com.opensig.core.client.controlador.parametro.IParametro;
import br.com.opensig.core.client.controlador.parametro.ParametroBinario;
import br.com.opensig.core.client.controlador.parametro.ParametroObjeto;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.financeiro.server.acao.SalvarPagar;
import br.com.opensig.financeiro.shared.modelo.FinCategoria;
import br.com.opensig.financeiro.shared.modelo.FinPagar;
import br.com.opensig.fiscal.server.acao.SalvarEntrada;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;

public class ComercialServiceImpl extends CoreServiceImpl implements ComercialService {

	private static final long serialVersionUID = 2394832515672850909L;

	public FisNotaSaida gerarNfe(ComVenda venda, ComFrete frete) throws ComercialException {
		try {
			GerarNfe gerar = new GerarNfe(null, this, venda, frete);
			gerar.execute();
			return gerar.getNota();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando gerarNfe.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public ComCompra analisarNfe(String nomeArquivo) throws ComercialException {
		try {
			HttpSession sessao = getThreadLocalRequest().getSession();
			Autenticacao at = (Autenticacao) sessao.getAttribute("Autenticacao");
			byte[] xml = (byte[]) sessao.getAttribute(nomeArquivo);

			AnalisarNfe analisar = new AnalisarNfe(null, this, at.getEmpresa(), new String(xml));
			analisar.execute();
			return analisar.getCompra();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando analisarNfe.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void importarNfe(String nomeArquivo, ComCompra compra) throws ComercialException {
		try {
			HttpSession sessao = getThreadLocalRequest().getSession();
			byte[] xml = (byte[]) sessao.getAttribute(nomeArquivo);
			FinPagar pagar = compra.getFinPagar();
			
			// salva a compra e produtos
			new ImportarNfe(null, this, compra).execute();
			// fecha a compra
			if (compra.getComCompraFechada()) {
				new FecharCompra(null, this, new ComCompra(compra.getComCompraId())).execute();
				// paga a compra
				if (compra.getComCompraPaga()) {
					new SalvarPagar(null, this, pagar, new ArrayList<FinCategoria>()).execute();
					// atauliza a compra
					FiltroNumero fn = new FiltroNumero("comCompraId", ECompara.IGUAL, compra.getComCompraId());
					ParametroObjeto po = new ParametroObjeto("finPagar", pagar);
					ParametroBinario pb = new ParametroBinario("comCompraPaga", 1);
					GrupoParametro gp = new GrupoParametro(new IParametro[] { po, pb });

					Sql sql = new Sql(new ComCompra(compra.getComCompraId()), EComando.ATUALIZAR, fn, gp);
					executar(new Sql[] { sql });
				}
			}
			// salva a nota
			new SalvarEntrada(null, new String(xml), new FisNotaStatus(ENotaStatus.AUTORIZADO), compra.getEmpEmpresa()).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando importarNfe.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void fecharCompra(ComCompra compra) throws ComercialException {
		try {
			new FecharCompra(null, this, compra).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando fecharCompra.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public String[][] fecharVenda(ComVenda venda) throws ComercialException {
		try {
			List<String[]> invalidos = new ArrayList<String[]>();
			new FecharVenda(null, this, venda, invalidos).execute();
			return invalidos.toArray(new String[][] {});
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando fecharVenda.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void fecharFrete(ComFrete frete) throws ComercialException {
		try {
			new FecharFrete(null, this, frete).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando fecharFrete.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public ComCompra salvarCompra(ComCompra compra) throws ComercialException {
		try {
			new SalvarCompra(null, this, compra).execute();
			compra.anularDependencia();
			return compra;
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando salvarCompra.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public ComVenda salvarVenda(ComVenda venda) throws ComercialException {
		try {
			new SalvarVenda(null, this, venda).execute();
			venda.anularDependencia();
			return venda;
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando salvarVenda.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public ComValorProduto salvarValor(ComValorProduto valor) throws ComercialException {
		try {
			new SalvarValor(null, this, valor).execute();
			valor.anularDependencia();
			return valor;
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando salvarValor.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void excluirCompra(ComCompra compra) throws ComercialException {
		try {
			new ExcluirCompra(null, this, compra).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando excluirCompra.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void excluirVenda(ComVenda venda) throws ComercialException {
		try {
			new ExcluirVenda(null, this, venda).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando excluirVenda.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void cancelarVenda(ComVenda venda) throws ComercialException {
		try {
			new CancelarVenda(null, this, venda).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando cancelarVenda.", e);
			throw new ComercialException(e.getMessage());
		}
	}

	public void excluirFrete(ComFrete frete) throws ComercialException {
		try {
			new ExcluirFrete(null, this, frete).execute();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro no comando excluirFrete.", e);
			throw new ComercialException(e.getMessage());
		}
	}

}
