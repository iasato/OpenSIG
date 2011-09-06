package br.com.opensig.comercial.server.acao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import br.com.opensig.comercial.client.servico.ComercialException;
import br.com.opensig.comercial.client.servico.ComercialService;
import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComCompraProduto;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.empresa.server.EmpresaServiceImpl;
import br.com.opensig.empresa.shared.modelo.EmpContato;
import br.com.opensig.empresa.shared.modelo.EmpContatoTipo;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpEndereco;
import br.com.opensig.empresa.shared.modelo.EmpEnderecoTipo;
import br.com.opensig.empresa.shared.modelo.EmpEntidade;
import br.com.opensig.empresa.shared.modelo.EmpEstado;
import br.com.opensig.empresa.shared.modelo.EmpFornecedor;
import br.com.opensig.empresa.shared.modelo.EmpMunicipio;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinForma;
import br.com.opensig.financeiro.shared.modelo.FinPagamento;
import br.com.opensig.financeiro.shared.modelo.FinPagar;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdOrigem;
import br.com.opensig.produto.shared.modelo.ProdProduto;
import br.com.opensig.produto.shared.modelo.ProdTributacao;

public class AnalisarNfe extends Chain {

	private ComercialService servico;
	private ComCompra compra;
	private List<ComCompraProduto> comProdutos;
	private String[] sEmpresa;
	private EmpEmpresa empresa;
	private EmpFornecedor fornecedor;
	private String xml;
	private Document doc;

	public AnalisarNfe(Chain next, ComercialService servico, String[] empresa, String xml) throws OpenSigException {
		super(null);
		this.servico = servico;
		this.xml = xml;
		this.sEmpresa = empresa;
		this.fornecedor = new EmpFornecedor();

		// valida os produtos
		ValidarProduto valProd = new ValidarProduto(next);
		// valida o fornecedor
		ValidarFornecedor valFor = new ValidarFornecedor(valProd);
		// valida a compra
		ValidarCompra valCom = new ValidarCompra(valFor);
		// valida a empresa
		ValidarEmpresa valEmp = new ValidarEmpresa(valCom);
		// valida o xml
		this.setNext(valEmp);
	}

	@Override
	public void execute() throws OpenSigException {
		try {
			doc = UtilServer.getXml(xml);

			// verifica se é uma NFe aprovada
			Node root = doc.getElementsByTagName("infNFe").item(0);
			Node prot = doc.getElementsByTagName("nProt").item(0);
			if (root == null || prot == null) {
				throw new Exception("Não é uma NFe válida ou não tem o protocolo da Sefaz!");
			}

			if (next != null) {
				next.execute();
			}
		} catch (Exception ex) {
			UtilServer.LOG.error("Erro identificar o xml.", ex);
			throw new ComercialException(ex.getMessage());
		}
	}

	private class ValidarEmpresa extends Chain {

		public ValidarEmpresa(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			// recupera o cnpj
			Element dest = (Element) doc.getElementsByTagName("dest").item(0);
			String cnpj = UtilServer.getValorTag(dest, "CNPJ", true);
			try {
				cnpj = UtilServer.formataTexto(cnpj, "##.###.###/####-##");
			} catch (ParseException e) {
				throw new ComercialException("O cnpj nao é valido!");
			}

			if (cnpj.equals(sEmpresa[5])) {
				EmpEntidade ent = new EmpEntidade(Integer.valueOf(sEmpresa[1]));
				ent.setEmpEntidadeNome1(sEmpresa[2]);
				ent.setEmpEntidadeNome2(sEmpresa[3]);
				ent.setEmpEntidadePessoa(sEmpresa[4]);
				ent.setEmpEntidadeDocumento1(sEmpresa[5]);
				ent.setEmpEntidadeDocumento2(sEmpresa[6]);
				empresa = new EmpEmpresa(Integer.valueOf(sEmpresa[0]));
				empresa.setEmpEntidade(ent);
			} else {
				throw new ComercialException("O destinatário não é a empresa logada!");
			}

			if (next != null) {
				next.execute();
			}
		}
	}

	private class ValidarCompra extends Chain {

		public ValidarCompra(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			// recupera o cnpj
			Element emit = (Element) doc.getElementsByTagName("emit").item(0);
			String cnpj = UtilServer.getValorTag(emit, "CNPJ", true);
			try {
				cnpj = UtilServer.formataTexto(cnpj, "##.###.###/####-##");
			} catch (ParseException e) {
				throw new ComercialException("O cnpj nao é valido!");
			}
			// recupera o numero da nf
			String numero = UtilServer.getValorTag(doc.getDocumentElement(), "nNF", true);
			// tenta achar a compra
			FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, empresa);
			FiltroTexto ft = new FiltroTexto("empFornecedor.empEntidade.empEntidadeDocumento1", ECompara.IGUAL, cnpj);
			FiltroNumero fn = new FiltroNumero("comCompraNumero", ECompara.IGUAL, numero);
			GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, ft, fn });

			compra = new ComCompra();
			compra = (ComCompra) servico.selecionar(compra, gf, false);
			if (compra != null) {
				throw new ComercialException("A compra já existe!");
			} else {
				// recupera os demais campos do xml
				String serie = UtilServer.getValorTag(doc.getDocumentElement(), "serie", true);
				String uf = UtilServer.getValorTag(doc.getDocumentElement(), "cUF", true);
				String data = UtilServer.getValorTag(doc.getDocumentElement(), "dEmi", true);
				Date dtData = null;
				try {
					dtData = new SimpleDateFormat("yyyy-MM-dd").parse(data);
				} catch (ParseException e) {
					UtilServer.LOG.debug("Data invalida.");
					throw new ComercialException(UtilServer.CONF.get("errInvalido") + " -> dEmi");
				}

				Element tot = (Element) doc.getElementsByTagName("ICMSTot").item(0);
				String baseIcms = UtilServer.getValorTag(tot, "vBC", true);
				String valorIcms = UtilServer.getValorTag(tot, "vICMS", true);
				String baseSub = UtilServer.getValorTag(tot, "vBCST", true);
				String valorSub = UtilServer.getValorTag(tot, "vST", true);
				String frete = UtilServer.getValorTag(tot, "vFrete", true);
				String seguro = UtilServer.getValorTag(tot, "vSeg", true);
				String desconto = UtilServer.getValorTag(tot, "vDesc", true);
				String ipi = UtilServer.getValorTag(tot, "vIPI", true);
				String outro = UtilServer.getValorTag(tot, "vOutro", true);
				String valorProd = UtilServer.getValorTag(tot, "vProd", true);
				String valorNota = UtilServer.getValorTag(tot, "vNF", true);

				compra = new ComCompra();
				compra.setEmpEstado(getEstado(uf));
				compra.setComCompraEmissao(dtData);
				compra.setComCompraRecebimento(UtilServer.getData());
				compra.setComCompraSerie(Integer.valueOf(serie));
				compra.setComCompraNumero(Integer.valueOf(numero));
				compra.setComCompraIcmsBase(Double.valueOf(baseIcms));
				compra.setComCompraIcmssubBase(Double.valueOf(baseSub));
				compra.setComCompraIcmsValor(Double.valueOf(valorIcms));
				compra.setComCompraIcmssubValor(Double.valueOf(valorSub));
				compra.setComCompraValorFrete(Double.valueOf(frete));
				compra.setComCompraValorSeguro(Double.valueOf(seguro));
				compra.setComCompraValorDesconto(Double.valueOf(desconto));
				compra.setComCompraValorIpi(Double.valueOf(ipi));
				compra.setComCompraValorOutros(Double.valueOf(outro));
				compra.setComCompraValorProduto(Double.valueOf(valorProd));
				compra.setComCompraValorNota(Double.valueOf(valorNota));
				compra.setComCompraPaga(true);
				compra.setFinPagar(getPagar());
			}

			if (next != null) {
				next.execute();
			}
		}

		private EmpEstado getEstado(String ibge) throws OpenSigException {
			FiltroNumero fn = new FiltroNumero("empEstadoIbge", ECompara.IGUAL, ibge);
			EmpEstado est = (EmpEstado) servico.selecionar(new EmpEstado(), fn, true);
			return est;
		}

		private FinPagar getPagar() throws OpenSigException {
			NodeList cobranca = doc.getElementsByTagName("dup");
			List<FinPagamento> pagamentos = new ArrayList<FinPagamento>();

			// pagar
			FinPagar pagar = new FinPagar();
			pagar.setFinConta(new FinConta(Integer.valueOf(UtilServer.CONF.get("conta.padrao"))));
			pagar.setFinPagamentos(pagamentos);

			for (int i = 0; i < cobranca.getLength(); i++) {
				Element cob = (Element) cobranca.item(i);
				String dc = UtilServer.getValorTag(cob, "nDup", true);
				String dt = UtilServer.getValorTag(cob, "dVenc", true);
				String vl = UtilServer.getValorTag(cob, "vDup", true);

				// data
				Date dtData = null;
				try {
					dtData = new SimpleDateFormat("yyyy-MM-dd").parse(dt);
				} catch (ParseException e) {
					dtData = UtilServer.getData();
				}

				// parcela
				int par = i + 1;
				String parcela = par < 10 ? "0" + par : "" + par;
				parcela += cobranca.getLength() < 10 ? "/0" + cobranca.getLength() : "/" + cobranca.getLength();

				// pagamentos
				FinPagamento pag = new FinPagamento();
				FinForma forma = new FinForma(4);
				forma.setFinFormaDescricao(UtilServer.CONF.get("txtboleto"));
				pag.setFinForma(forma);
				pag.setFinPagamentoDocumento(dc);
				pag.setFinPagamentoValor(Double.valueOf(vl));
				pag.setFinPagamentoParcela(parcela);
				pag.setFinPagamentoCadastro(UtilServer.getData());
				pag.setFinPagamentoVencimento(dtData);
				pag.setFinPagar(pagar);
				pagamentos.add(pag);
			}

			return pagar;
		}
	}

	private class ValidarFornecedor extends Chain {

		public ValidarFornecedor(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			// recupera o cnpj
			Element emit = (Element) doc.getElementsByTagName("emit").item(0);
			String cnpj = UtilServer.getValorTag(emit, "CNPJ", true);
			try {
				cnpj = UtilServer.formataTexto(cnpj, "##.###.###/####-##");
			} catch (ParseException e) {
				throw new ComercialException("O cnpj nao é valido!");
			}
			fornecedor = getFornecedor(cnpj);
			// caso seje um novo
			if (fornecedor.getEmpFornecedorId() == 0) {
				EmpresaServiceImpl<EmpFornecedor> servico = new EmpresaServiceImpl<EmpFornecedor>();
				servico.salvar(fornecedor);
			}
			fornecedor.anularDependencia();

			if (next != null) {
				next.execute();
			}
		}

		private EmpFornecedor getFornecedor(String cnpj) throws OpenSigException {
			FiltroTexto ft = new FiltroTexto("empEntidade.empEntidadeDocumento1", ECompara.IGUAL, cnpj);
			fornecedor = (EmpFornecedor) servico.selecionar(fornecedor, ft, false);

			if (fornecedor == null) {
				// emitente
				Element emit = (Element) doc.getElementsByTagName("emit").item(0);
				String nome = UtilServer.getValorTag(emit, "xNome", true);
				String fant = UtilServer.getValorTag(emit, "xFant", false);
				String ie = UtilServer.getValorTag(emit, "IE", true);

				// seta fornecedor
				EmpEntidade enti = new EmpEntidade();
				enti.setEmpEntidadeNome1(nome);
				String fantasia = fant != null ? fant : nome;
				if (fantasia.length() > 20) {
					fantasia = fantasia.substring(0, 20);
				}
				enti.setEmpEntidadeNome2(fantasia);
				enti.setEmpEntidadeDocumento1(cnpj);
				enti.setEmpEntidadeDocumento2(ie);
				enti.setEmpEntidadeAtivo(true);
				enti.setEmpEntidadePessoa(UtilServer.CONF.get("txtJuridica"));

				// endereco e contato
				Element ende = (Element) emit.getElementsByTagName("enderEmit").item(0);
				String logr = UtilServer.getValorTag(ende, "xLgr", true);
				String num = UtilServer.getValorTag(ende, "nro", true);
				String compl = UtilServer.getValorTag(ende, "xCpl", false);
				String bairro = UtilServer.getValorTag(ende, "xBairro", true);
				String mun = UtilServer.getValorTag(ende, "cMun", true);
				String cep = UtilServer.getValorTag(ende, "CEP", false);
				String fone = UtilServer.getValorTag(ende, "fone", false);
				String email = UtilServer.getValorTag(ende, "email", false);

				// seta o endereco
				EmpEndereco endereco = new EmpEndereco();
				endereco.setEmpEnderecoTipo(new EmpEnderecoTipo(Integer.valueOf(UtilServer.CONF.get("nfe.tipoendecom"))));
				endereco.setEmpEnderecoLogradouro(logr);
				endereco.setEmpEnderecoNumero(Integer.valueOf(num.replaceAll("\\D", "")));
				if (compl != null) {
					endereco.setEmpEnderecoComplemento(compl);
				}
				endereco.setEmpEnderecoBairro(bairro);
				FiltroNumero fn = new FiltroNumero("empMunicipioIbge", ECompara.IGUAL, mun);
				EmpMunicipio empM = (EmpMunicipio) servico.selecionar(new EmpMunicipio(), fn, false);
				endereco.setEmpMunicipio(empM);

				cep = cep != null ? cep.substring(0, 5) + "-" + cep.substring(5) : "00000-000";
				endereco.setEmpEnderecoCep(cep);

				List<EmpEndereco> ends = new ArrayList<EmpEndereco>();
				ends.add(endereco);
				enti.setEmpEnderecos(ends);

				// seta o contato telefone
				List<EmpContato> conts = new ArrayList<EmpContato>();
				if (fone != null) {
					fone = fone.substring(0, 2) + " " + fone.substring(2, 6) + "-" + fone.substring(6);

					EmpContato contato = new EmpContato();
					contato.setEmpContatoDescricao(fone);
					contato.setEmpContatoTipo(new EmpContatoTipo(Integer.valueOf(UtilServer.CONF.get("nfe.tipoconttel"))));
					conts.add(contato);
				}

				// seta o contato email
				if (email != null) {
					EmpContato contato = new EmpContato();
					contato.setEmpContatoDescricao(email);
					contato.setEmpContatoTipo(new EmpContatoTipo(Integer.valueOf(UtilServer.CONF.get("nfe.tipocontemail"))));
					conts.add(contato);
				}

				// caso tenha contato
				if (!conts.isEmpty()) {
					enti.setEmpContatos(conts);
				}

				fornecedor = new EmpFornecedor();
				fornecedor.setEmpEntidade(enti);
			}

			return fornecedor;
		}
	}

	private class ValidarProduto extends Chain {

		private List<ProdTributacao> tributacao;

		public ValidarProduto(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			// pega os tributados
			Lista<ProdTributacao> tributo = servico.selecionar(new ProdTributacao(), 0, 0, null, false);
			tributacao = tributo.getLista();

			// seta os tipos
			NodeList prods = doc.getElementsByTagName("det");
			comProdutos = new ArrayList<ComCompraProduto>();

			for (int i = 0; i < prods.getLength(); i++) {
				// verifica se ja existe
				Element item = (Element) prods.item(i);
				String cfop = UtilServer.getValorTag(item, "CFOP", true);
				String qtd = UtilServer.getValorTag(item, "qCom", true);
				String valor = UtilServer.getValorTag(item, "vUnCom", true);
				String total = UtilServer.getValorTag(item, "vProd", true);

				Element imposto = (Element) item.getElementsByTagName("imposto").item(0);
				String pIcms = UtilServer.getValorTag(imposto, "pICMS", false);
				if (pIcms == null) {
					pIcms = "0";
				}
				String pIpi = UtilServer.getValorTag(imposto, "pIPI", false);
				if (pIpi == null) {
					pIpi = "0";
				}

				// setando o produto da compra
				ComCompraProduto comProd = new ComCompraProduto();
				ProdProduto prod = getProduto(item);
				comProd.setProdProduto(prod);
				comProd.setProdEmbalagem(prod.getProdEmbalagem());
				comProd.setComCompraProdutoCfop(Integer.valueOf(cfop));
				comProd.setComCompraProdutoIcms(Double.valueOf(pIcms));
				comProd.setComCompraProdutoIpi(Double.valueOf(pIpi));
				comProd.setComCompraProdutoQuantidade(Double.valueOf(qtd));
				comProd.setComCompraProdutoValor(Double.valueOf(valor));
				comProd.setComCompraProdutoTotal(Double.valueOf(total));
				comProd.setComCompraProdutoPreco(prod.getProdProdutoPreco());
				comProd.setComCompra(compra);
				comProdutos.add(comProd);
			}

			if (next != null) {
				next.execute();
			}
		}

		private ProdProduto getProduto(Element item) throws OpenSigException {
			// pega os dados do xml
			String ref = UtilServer.getValorTag(item, "cProd", true).replaceAll("\\W", "");
			String desc = UtilServer.getValorTag(item, "xProd", true);
			String ncm = UtilServer.getValorTag(item, "NCM", true);
			String valor = UtilServer.getValorTag(item, "vUnCom", true);

			Element icms = (Element) item.getElementsByTagName("ICMS").item(0);
			String orig = UtilServer.getValorTag(icms, "orig", true);
			String cst = UtilServer.getValorTag(icms, "CST", false);
			if (cst == null) {
				cst = UtilServer.getValorTag(icms, "CSOSN", true);
			}

			Long ean = null;
			String sEan = UtilServer.getValorTag(item, "cEAN", false);
			if (sEan != null) {
				ean = Long.valueOf(sEan);
				// barra
				GrupoFiltro gf = new GrupoFiltro();
				FiltroNumero fn = new FiltroNumero("prodProdutoBarra", ECompara.IGUAL, ean);
				gf.add(fn, EJuncao.OU);
				// barra do preco
				FiltroNumero fn1 = new FiltroNumero("prodPrecoBarra", ECompara.IGUAL, ean);
				fn1.setCampoPrefixo("t2.");
				gf.add(fn1, EJuncao.OU);
				// busca
				ProdProduto prod = new ProdProduto();
				prod = (ProdProduto) servico.selecionar(prod, gf, true);
				// verifica se achou
				if (prod != null) {
					prod.setProdEmbalagem(new ProdEmbalagem(1));
					prod.setProdOrigem(new ProdOrigem(Integer.valueOf(orig) + 1));
					prod.setEmpFornecedor(fornecedor);
					prod.setEmpFabricante(fornecedor);
					prod.setProdTributacao(getTributacao(cst));
					return prod;
				}
			}

			// caso nao acha cria um novo para confirmar
			ProdProduto prod = new ProdProduto();
			prod.setProdProdutoNcm(ncm);
			prod.setProdProdutoBarra(ean);
			prod.setProdProdutoReferencia(ref);
			prod.setProdProdutoDescricao(desc);
			prod.setProdEmbalagem(new ProdEmbalagem(1));
			prod.setProdProdutoVolume(1);
			prod.setProdOrigem(new ProdOrigem(Integer.valueOf(orig)));
			prod.setEmpFornecedor(fornecedor);
			prod.setEmpFabricante(fornecedor);
			prod.setProdTributacao(getTributacao(cst));
			prod.setProdProdutoAtivo(true);
			prod.setProdProdutoCategoria(UtilServer.CONF.get("categoria.padrao") + "::");
			prod.setProdProdutoCadastrado(UtilServer.getData());
			prod.setProdProdutoCusto(Double.valueOf(valor));
			prod.setProdProdutoPreco(0.00);

			return prod;
		}

		private ProdTributacao getTributacao(String cst) {
			// percorre as tributacoes
			for (ProdTributacao trib : tributacao) {
				if (trib.getProdTributacaoCst().equals(cst)) {
					return trib;
				}
			}
			// se nao achar colocar a padrao 00
			return new ProdTributacao(1);
		}
	}

	public ComCompra getCompra() {
		compra.setEmpEmpresa(empresa);
		compra.setEmpFornecedor(fornecedor);
		compra.setComCompraProdutos(comProdutos);
		compra.getFinPagar().setEmpEmpresa(empresa);
		compra.getFinPagar().setEmpEntidade(fornecedor.getEmpEntidade());
		return compra;
	}

}
