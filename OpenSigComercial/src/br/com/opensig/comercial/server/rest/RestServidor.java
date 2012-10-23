package br.com.opensig.comercial.server.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import br.com.opensig.comercial.shared.modelo.ComEcfDocumento;
import br.com.opensig.comercial.shared.modelo.ComEcfNota;
import br.com.opensig.comercial.shared.modelo.ComEcfNotaProduto;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.comercial.shared.modelo.ComEcfZTotais;
import br.com.opensig.comercial.shared.rest.SisCliente;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.controlador.parametro.ParametroFormula;
import br.com.opensig.core.client.controlador.parametro.ParametroObjeto;
import br.com.opensig.core.client.servico.CoreException;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.empresa.shared.modelo.EmpCliente;
import br.com.opensig.empresa.shared.modelo.EmpEntidade;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinForma;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;
import br.com.opensig.nfe.TNFe;
import br.com.opensig.permissao.shared.modelo.SisConfiguracao;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdEstoque;
import br.com.opensig.produto.shared.modelo.ProdProduto;

/**
 * Classe que representa a comunicao do Cliente para o Servidor via Rest
 * 
 * @author Pedro H. Lira
 */
@Provider
@Path("/server")
public class RestServidor extends ARest {

	private Map<String, String> conf;
	private EmpCliente clientePadrao;

	/**
	 * Construtor padrao.
	 */
	public RestServidor() {
		super();
		log = Logger.getLogger(RestServidor.class);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Override
	public String ajuda() throws RestException {
		return super.ajuda();
	}

	/**
	 * Metodo que cadastra na base do server as notas de consumidor emitidas pelos sistemas em modo client.
	 * 
	 * @param ecfNota
	 *            um objeto do tipo Nota.
	 * @throws RestException
	 *             em caso de nao conseguir acessar a informacao.
	 */
	@Path("/nota")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setNota(ComEcfNota ecfNota) throws RestException {
		autorizar();
		try {
			// identifica o cliente
			EmpCliente cliente = getCliente(ecfNota.getSisCliente());

			// salva a nota
			List<ComEcfNotaProduto> nps = ecfNota.getComEcfNotaProdutos();
			ecfNota.setId(0);
			ecfNota.setEmpEmpresa(ecf.getEmpEmpresa());
			ecfNota.setEmpCliente(cliente);
			ecfNota.setComEcfNotaProdutos(null);
			ecfNota = (ComEcfNota) service.salvar(ecfNota);

			// salva os produtos vendidos
			List<Sql> sqls = new ArrayList<Sql>();
			for (ComEcfNotaProduto np : nps) {
				np.setId(0);
				np.setComEcfNota(ecfNota);
				Sql sql = getEstoque(np.getComEcfNotaProdutoQuantidade(), np.getProdEmbalagem(), np.getProdProduto());
				sqls.add(sql);
			}
			service.salvar(nps);

			if (ecfNota.getComEcfNotaCancelada() == false) {
				// atualiza o estoque
				service.executar(sqls.toArray(new Sql[] {}));
				conf = getConfig();

				// salva o receber
				FinReceber receber = new FinReceber();
				receber.setEmpEmpresa(ecf.getEmpEmpresa());
				receber.setEmpEntidade(cliente.getEmpEntidade());
				receber.setFinConta(new FinConta(Integer.valueOf(conf.get("conta.padrao"))));
				receber.setFinReceberCadastro(ecfNota.getComEcfNotaData());
				receber.setFinReceberCategoria(conf.get("categoria.ecf"));
				receber.setFinReceberNfe(ecfNota.getComEcfNotaNumero());
				receber.setFinReceberValor(ecfNota.getComEcfNotaLiquido());
				receber.setFinReceberObservacao("NFC emitido pelo ECF.");
				receber = (FinReceber) service.salvar(receber);

				// salva o recebimento
				FinRecebimento recebimento = new FinRecebimento();
				recebimento.setFinReceber(receber);
				recebimento.setFinForma(new FinForma(1));
				recebimento.setFinRecebimentoCadastro(ecfNota.getComEcfNotaData());
				recebimento.setFinRecebimentoConciliado(ecfNota.getComEcfNotaData());
				recebimento.setFinRecebimentoDocumento("Serie:" + ecfNota.getComEcfNotaSerie() + " Sub:" + ecfNota.getComEcfNotaSubserie() + " N:" + ecfNota.getComEcfNotaNumero());
				recebimento.setFinRecebimentoObservacao("NFC emitido pelo ECF.");
				recebimento.setFinRecebimentoParcela("01/01");
				recebimento.setFinRecebimentoRealizado(ecfNota.getComEcfNotaData());
				recebimento.setFinRecebimentoStatus("CONCILIADO");
				recebimento.setFinRecebimentoValor(ecfNota.getComEcfNotaLiquido());
				recebimento.setFinRecebimentoVencimento(ecfNota.getComEcfNotaData());
				service.salvar(recebimento);
			}
		} catch (Exception ex) {
			log.error("Erro ao salvar a nota.", ex);
			throw new RestException(ex.getMessage());
		}
	}

	/**
	 * Metodo que cadastra na base do server as nfe emitidas pelos sistemas em modo client.
	 * 
	 * @param ecfNfe
	 *            um objeto do tipo NFe.
	 * @throws RestException
	 *             em caso de nao conseguir acessar a informacao.
	 */
	@Path("/nfe")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setNfe(FisNotaSaida ecfNfe) throws RestException {
		autorizar();
		try {
			// identifica o status
			ENotaStatus status = ENotaStatus.valueOf(ecfNfe.getEcfNotaEletronicaStatus());
			FisNotaStatus ns = new FisNotaStatus(status);

			// salva a nfe
			ecfNfe.setId(0);
			ecfNfe.setEmpEmpresa(ecf.getEmpEmpresa());
			ecfNfe.setFisNotaStatus(ns);
			ecfNfe.setFisNotaSaidaCadastro(ecfNfe.getFisNotaSaidaData());
			ecfNfe.setFisNotaSaidaErro("");
			ecfNfe = (FisNotaSaida) service.salvar(ecfNfe);

			// atualiza o estoque
			if (status == ENotaStatus.AUTORIZADO) {
				String xml = ecfNfe.getFisNotaSaidaXml();
				int I = xml.indexOf("<infNFe");
				int F = xml.indexOf("</NFe>") + 6;
				xml = "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">" + xml.substring(I, F);
				TNFe nfe = UtilServer.xmlToObj(xml, "br.com.opensig.nfe");
				List<Sql> sqls = new ArrayList<Sql>();
				for (TNFe.InfNFe.Det det : nfe.getInfNFe().getDet()) {
					// achando o produto
					IFiltro filtro;
					if (det.getProd().getCEAN() == null || det.getProd().getCEAN().equals("")) {
						filtro = new FiltroNumero("prodProdutoId", ECompara.IGUAL, det.getProd().getCProd());
					} else {
						filtro = new FiltroTexto("prodProdutoBarra", ECompara.IGUAL, det.getProd().getCEAN());
					}
					ProdProduto prod = (ProdProduto) service.selecionar(new ProdProduto(), filtro, false);
					// achando a embalagem usada na venda
					FiltroTexto ft = new FiltroTexto("prodEmbalagemNome", ECompara.IGUAL, det.getProd().getUCom());
					ProdEmbalagem emb = (ProdEmbalagem) service.selecionar(new ProdEmbalagem(), ft, false);
					double qtd = Double.valueOf(det.getProd().getQCom());
					// monta a atualizacao do estoque
					sqls.add(getEstoque(qtd, emb, prod));
				}
				// remove do estoque
				service.executar(sqls.toArray(new Sql[] {}));
				conf = getConfig();

				// identifica o cliente
				String doc = nfe.getInfNFe().getDest().getCPF();
				if (doc == null || doc.equals("")) {
					doc = nfe.getInfNFe().getDest().getCNPJ();
				}
				String nome = nfe.getInfNFe().getDest().getXNome();
				SisCliente sisCliente = new SisCliente();
				sisCliente.setSisClienteDoc(doc);
				sisCliente.setSisClienteNome(nome);
				EmpCliente cliente = getCliente(sisCliente);

				// salva o receber
				FinReceber receber = new FinReceber();
				receber.setEmpEmpresa(ecf.getEmpEmpresa());
				receber.setEmpEntidade(cliente.getEmpEntidade());
				receber.setFinConta(new FinConta(Integer.valueOf(conf.get("conta.padrao"))));
				receber.setFinReceberCadastro(ecfNfe.getFisNotaSaidaCadastro());
				receber.setFinReceberCategoria(conf.get("categoria.ecf"));
				receber.setFinReceberNfe(ecfNfe.getFisNotaSaidaNumero());
				receber.setFinReceberValor(ecfNfe.getFisNotaSaidaValor());
				receber.setFinReceberObservacao("NFe emitido pelo ECF.");
				receber = (FinReceber) service.salvar(receber);

				// salva o recebimento
				FinRecebimento recebimento = new FinRecebimento();
				recebimento.setFinReceber(receber);
				recebimento.setFinForma(new FinForma(1));
				recebimento.setFinRecebimentoCadastro(ecfNfe.getFisNotaSaidaCadastro());
				recebimento.setFinRecebimentoConciliado(ecfNfe.getFisNotaSaidaCadastro());
				recebimento.setFinRecebimentoDocumento("NFe: " + ecfNfe.getFisNotaSaidaNumero());
				recebimento.setFinRecebimentoObservacao("NFe emitido pelo ECF.");
				recebimento.setFinRecebimentoParcela("01/01");
				recebimento.setFinRecebimentoRealizado(ecfNfe.getFisNotaSaidaCadastro());
				recebimento.setFinRecebimentoStatus("CONCILIADO");
				recebimento.setFinRecebimentoValor(ecfNfe.getFisNotaSaidaValor());
				recebimento.setFinRecebimentoVencimento(ecfNfe.getFisNotaSaidaCadastro());
				service.salvar(recebimento);
			}
		} catch (Exception ex) {
			log.error("Erro ao salvar a nfe.", ex);
			throw new RestException(ex.getMessage());
		}
	}

	/**
	 * Metodo que cadastra na base do server as reducoes Z, totais, vendas, produtos vendidos, pagamentos, documentos emitidos pelos sistemas em modo client.
	 * 
	 * @param ecfZ
	 *            um objeto do tipo ReducaoZ com a lista de documentos anexada.
	 * @throws RestException
	 *             em caso de nao conseguir acessar a informacao.
	 */
	@Path("/reducaoZ")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void setReducaoZ(ComEcfZ ecfZ) throws RestException {
		autorizar();
		try {
			conf = getConfig();
			FiltroNumero fn = new FiltroNumero("empClienteId", ECompara.IGUAL, conf.get("cliente.padrao"));
			clientePadrao = (EmpCliente) service.selecionar(new EmpCliente(), fn, false);

			// guarda os totais e vendas e docs
			List<ComEcfZTotais> totais = ecfZ.getComEcfZTotais();
			List<ComEcfVenda> vendas = ecfZ.getComEcfVendas();
			List<ComEcfDocumento> docs = ecfZ.getComEcfDocumentos();

			// salva a reduzaoZ
			ecfZ.setId(0);
			ecfZ.setComEcf(ecf);
			ecfZ.setComEcfZTotais(null);
			ecfZ.setComEcfVendas(null);
			ecfZ.setComEcfDocumentos(null);
			ecfZ = (ComEcfZ) service.salvar(ecfZ);

			// salva os totais
			for (ComEcfZTotais tot : totais) {
				tot.setId(0);
				tot.setComEcfZ(ecfZ);
			}
			service.salvar(totais);
			
			// salva as vendas
			for (ComEcfVenda venda : vendas) {
				venda.setComEcfZ(ecfZ);
				venda.setComEcf(ecf);
				salvarVenda(venda);
			}

			// salva os documentos
			for (ComEcfDocumento doc : docs) {
				doc.setId(0);
				doc.setComEcf(ecf);
			}
			service.salvar(docs);
		} catch (Exception ex) {
			log.error("Erro ao salvar reduzao Z.", ex);
			throw new RestException(ex.getMessage());
		}
	}

	/**
	 * Metodo que salva as vendas no sistema fazendo as validacoes.
	 * 
	 * @param venda
	 *            o objeto de venda.
	 * @throws Exception
	 *             dispara em caso de erro ao salvar.
	 */
	private void salvarVenda(ComEcfVenda venda) throws Exception {
		// identifica o cliente
		if (venda.getSisCliente() != null) {
			EmpCliente cliente = getCliente(venda.getSisCliente());
			venda.setEmpCliente(cliente);
		}

		// guarda os produtos vendidos e pagamentos
		List<ComEcfVendaProduto> vps = venda.getComEcfVendaProdutos();
		List<FinReceber> recebiveis = venda.getEcfPagamentos();

		// salva a venda
		venda.setId(0);
		venda.setComEcfVendaProdutos(null);
		venda.setEcfPagamentos(null);
		venda = (ComEcfVenda) service.salvar(venda, false);

		// salva os produtos
		List<Sql> sqls = new ArrayList<Sql>();
		for (ComEcfVendaProduto vp : vps) {
			vp.setId(0);
			vp.setComEcfVenda(venda);
			if (!vp.getComEcfVendaProdutoCancelado()) {
				Sql sql = getEstoque(vp.getComEcfVendaProdutoQuantidade(), vp.getProdEmbalagem(), vp.getProdProduto());
				sqls.add(sql);
			}
		}
		service.salvar(vps);

		// atualiza o estoque
		if (venda.getComEcfVendaCancelada() == false) {
			service.executar(sqls.toArray(new Sql[] {}));
		}

		// salva os recebimento
		salvarRecebimento(recebiveis, venda);
	}

	/**
	 * Metodo que salva os recebimento no sistema fazendo as validacoes.
	 * 
	 * @param recebiveis
	 *            lista de objetos de receber.
	 * @throws Exception
	 *             dispara em caso de erro ao salvar.
	 */
	private void salvarRecebimento(List<FinReceber> recebiveis, ComEcfVenda venda) throws Exception {
		// identifica o cliente
		EmpCliente cliente = venda.getEmpCliente() != null ? venda.getEmpCliente() : clientePadrao;

		// recupera o valor e dados de cartao
		double valor = 0.00;
		StringBuilder sb = new StringBuilder("CUPOM FISCAL:: ");
		for (FinReceber rec : recebiveis) {
			valor += rec.getFinReceberValor();
			if (rec.getFinReceberNfe() > 0) {
				sb.append("GNF: ").append(rec.getFinReceberNfe()).append(" - ").append("NSU: ").append(rec.getFinReceberCategoria()).append("\n");
			}
		}

		// salva o receber da venda
		FinReceber receber = new FinReceber();
		receber.setEmpEmpresa(ecf.getEmpEmpresa());
		receber.setEmpEntidade(cliente.getEmpEntidade());
		receber.setFinConta(new FinConta(Integer.valueOf(conf.get("conta.padrao"))));
		receber.setFinReceberCadastro(venda.getComEcfVendaData());
		receber.setFinReceberCategoria(conf.get("categoria.ecf"));
		receber.setFinReceberNfe(venda.getComEcfVendaCcf());
		receber.setFinReceberValor(valor);
		receber.setFinReceberObservacao(sb.toString());
		receber = (FinReceber) service.salvar(receber);

		// salva os recebimentos
		for (FinReceber rec : recebiveis) {
			int par = 0;
			for (FinRecebimento recebimento : rec.getFinRecebimentos()) {
				recebimento.setFinRecebimentoId(0);
				recebimento.setFinReceber(receber);
				recebimento.setFinForma(rec.getFinForma());
				recebimento.setFinRecebimentoCadastro(rec.getFinReceberCadastro());
				recebimento.setFinRecebimentoRealizado(rec.getFinReceberCadastro());
				if (rec.getFinForma().getFinFormaId() == 1) {
					recebimento.setFinRecebimentoDocumento("CCF: " + venda.getComEcfVendaCcf());
					recebimento.setFinRecebimentoConciliado(rec.getFinReceberCadastro());
					recebimento.setFinRecebimentoStatus("CONCILIADO");
				} else {
					recebimento.setFinRecebimentoStatus("REALIZADO");
				}
				recebimento.setFinRecebimentoObservacao("CUPOM FISCAL");
				par++;
				recebimento.setFinRecebimentoParcela(UtilServer.formataNumero(par, 2, 0, false) + "/" + UtilServer.formataNumero(rec.getFinRecebimentos().size(), 2, 0, false));
				service.salvar(recebimento);
			}
		}

		// vincula o receber a venda
		ParametroObjeto po = new ParametroObjeto("finReceber", receber);
		FiltroNumero fn = new FiltroNumero("comEcfVendaId", ECompara.IGUAL, venda.getId());
		Sql sql = new Sql(new ComEcfVenda(), EComando.ATUALIZAR, fn, po);
		service.executar(new Sql[] { sql });
	}

	/**
	 * Metodo que encontra o cliente dentro do sistema usando os dados do cliente enviado, como o CNPJ.
	 * 
	 * @param sisCliente
	 *            o obejto de cliente do OpenPDV.
	 * @return o cliente encontrado nesta base de dados do OpenSIG.
	 * @throws Exception
	 *             dispara em caso de erro ao selecionar.
	 */
	private EmpCliente getCliente(SisCliente sisCliente) throws Exception {
		// acha o cliente ou seta os dados
		String doc = sisCliente.getSisClienteDoc().replaceAll("[^0-9]", "");
		String mask;
		String pessoa;

		if (doc.length() == 11) {
			mask = "###.###.###-##";
			pessoa = "FÍSICA";
		} else {
			mask = "##.###.###/####-##";
			pessoa = "JURÍDICA";
		}
		doc = UtilServer.formataTexto(doc, mask);
		FiltroTexto ft = new FiltroTexto("empEntidade.empEntidadeDocumento1", ECompara.IGUAL, doc);
		EmpCliente cli = (EmpCliente) service.selecionar(new EmpCliente(), ft, false);

		// se existir retornar, senao cria um novo
		if (cli != null) {
			return cli;
		} else {
			// entidade
			EmpEntidade ent = new EmpEntidade();
			ent.setEmpEntidadeNome1(sisCliente.getSisClienteNome());
			ent.setEmpEntidadeNome2("");
			ent.setEmpEntidadeDocumento1(doc);
			ent.setEmpEntidadeDocumento2("");
			ent.setEmpEntidadePessoa(pessoa);
			ent.setEmpEntidadeAtivo(true);
			ent.setEmpEntidadeObservacao("Importado do OpenPDV, adicionar endereço e contato");
			ent = (EmpEntidade) service.salvar(ent);
			// cliente
			cli = new EmpCliente();
			cli.setEmpEntidade(ent);
			return (EmpCliente) service.salvar(cli);
		}
	}

	/**
	 * Metodo que gera o SQL de atualizacao do estoque para as vendas recebidas.
	 * 
	 * @param qtd
	 *            a quantidade de produtos vendidos.
	 * @param emb
	 *            o tipo de embalagem usada na venda.
	 * @param prod
	 *            o produto que foi vendido.
	 * @return uma instrucao de SQL no formato de objeto para ser executada.
	 * @throws CoreException
	 *             dispara caso nao consiga gerar o sql de atualizacao.
	 */
	private Sql getEstoque(double qtd, ProdEmbalagem emb, ProdProduto prod) throws CoreException {
		// fatorando a quantida no estoque
		if (emb.getProdEmbalagemId() != prod.getProdEmbalagem().getProdEmbalagemId()) {
			qtd *= emb.getProdEmbalagemUnidade();
			qtd /= prod.getProdEmbalagem().getProdEmbalagemUnidade();
		}

		// atualiza o estoque
		ParametroFormula pf = new ParametroFormula("prodEstoqueQuantidade", -1 * qtd);
		FiltroObjeto fo1 = new FiltroObjeto("prodProduto", ECompara.IGUAL, prod);
		FiltroObjeto fo2 = new FiltroObjeto("empEmpresa", ECompara.IGUAL, ecf.getEmpEmpresa());
		GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo1, fo2 });
		return new Sql(new ProdEstoque(), EComando.ATUALIZAR, gf, pf);
	}

	/**
	 * Metodo que recupera os dados de configuracoa da empresa + os padroes.
	 * 
	 * @return um mapa de chave/valor com as configuracoes completas.
	 * @throws OpenSigException
	 *             dispara caso nao consiga recuperar o config.
	 */
	private Map<String, String> getConfig() throws CoreException {
		// adicionando as configuracoes
		FiltroBinario fb = new FiltroBinario("sisConfiguracaoAtivo", ECompara.IGUAL, 1);
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, ecf.getEmpEmpresa());
		GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fb, fo });
		Lista<SisConfiguracao> lista = service.selecionar(new SisConfiguracao(), 0, 0, gf, false);

		Map<String, String> mapa = new HashMap<String, String>();
		for (SisConfiguracao conf : lista.getLista()) {
			mapa.put(conf.getSisConfiguracaoChave().toLowerCase(), conf.getSisConfiguracaoValor());
		}

		// lidos dos arquivos
		mapa.putAll(UtilServer.getConf());
		return mapa;
	}
}
