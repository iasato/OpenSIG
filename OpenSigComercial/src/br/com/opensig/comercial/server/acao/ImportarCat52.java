package br.com.opensig.comercial.server.acao;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beanio.BeanReader;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.server.ComercialServiceImpl;
import br.com.opensig.comercial.shared.modelo.Cat52;
import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.comercial.shared.modelo.ComEcfZTotais;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroData;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.importar.IImportacao;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.sistema.SisExpImp;
import br.com.opensig.empresa.shared.modelo.EmpCliente;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.permissao.shared.modelo.SisUsuario;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class ImportarCat52 implements IImportacao<Cat52> {

	private List<ProdEmbalagem> embalagem;
	private ComercialServiceImpl service;

	@Override
	public Map<String, List<Cat52>> setArquivo(Autenticacao auth, Map<String, byte[]> arquivos, SisExpImp modo) throws OpenSigException {
		this.service = new ComercialServiceImpl(auth);
		List<Cat52> oks = new ArrayList<Cat52>();
		List<Cat52> err = new ArrayList<Cat52>();

		try {
			// lendo as definicoes
			StreamFactory factory = StreamFactory.newInstance();
			ByteArrayInputStream bais = new ByteArrayInputStream(modo.getSisExpImpModelo().getBytes());
			factory.load(bais);

			// pega as embalagens
			Lista<ProdEmbalagem> emb = service.selecionar(new ProdEmbalagem(), 0, 0, null, false);
			embalagem = emb.getLista();

			// pega o cliente padrao
			FiltroNumero fn = new FiltroNumero("empClienteId", ECompara.IGUAL, auth.getConf().get("cliente.padrao"));
			EmpCliente cliente = (EmpCliente) service.selecionar(new EmpCliente(), fn, false);

			// pega o usuario atual
			SisUsuario usuario = new SisUsuario(Integer.valueOf(auth.getUsuario()[0]));

			for (Entry<String, byte[]> arquivo : arquivos.entrySet()) {
				Cat52 cat52 = new Cat52();
				cat52.setArquivo(arquivo.getKey());
				// setando os objetos
				Object rec = null;
				ComEcf ecf = null;
				ComEcfZ ecfZ = null;
				BeanReader in = null;
				Map<Integer, ComEcfVenda> mapVenda = new HashMap<Integer, ComEcfVenda>();

				try {
					// lendo os dados do arquivo
					String texto = new String(arquivo.getValue());
					StringReader sr = new StringReader(texto);
					in = factory.createReader("cat52", sr);
				} catch (Exception e) {
					cat52.setErro(e.getMessage());
					err.add(cat52);
					continue;
				}

				do {
					try {
						rec = in.read();
						// a impressora
						if (rec instanceof ComEcf) {
							ecf = (ComEcf) rec;
							// encontra a ecf no sistema
							FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(Integer.valueOf(auth.getEmpresa()[0])));
							FiltroTexto ft = new FiltroTexto("comEcfSerie", ECompara.IGUAL, ecf.getComEcfSerie());
							GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, ft });
							ecf = (ComEcf) service.selecionar(ecf, gf, false);
							if (ecf == null) {
								break;
							}
							// leitura z
						} else if (rec instanceof ComEcfZ) {
							if (ecfZ == null) {
								ecfZ = (ComEcfZ) rec;
							} else {
								double total = ecfZ.getComEcfZTotal();
								ecfZ = (ComEcfZ) rec;
								ecfZ.setComEcf(ecf);
								ecfZ.setComEcfZTotal(total);
								ecfZ.setComZTotais(new ArrayList<ComEcfZTotais>());

								// valida Z
								FiltroObjeto fo = new FiltroObjeto("comEcf", ECompara.IGUAL, ecf);
								FiltroData fd = new FiltroData("comEcfZData", ECompara.IGUAL, ecfZ.getComEcfZData());
								GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, fd });
								if (service.selecionar(ecfZ, gf, false) != null) {
									break;
								}
							}
							// totais da leitura z
						} else if (rec instanceof ComEcfZTotais) {
							ComEcfZTotais total = (ComEcfZTotais) rec;
							ecfZ.getComZTotais().add(total);
							// venda
						} else if (rec instanceof ComEcfVenda) {
							ComEcfVenda venda = (ComEcfVenda) rec;
							venda.setComEcf(ecf);
							venda.setSisUsuario(usuario);
							venda.setEmpCliente(cliente);
							venda.setComEcfVendaDesconto(venda.getComEcfVendaBruto() - venda.getComEcfVendaLiquido());
							venda.setComEcfVendaProdutos(new ArrayList<ComEcfVendaProduto>());
							if (Long.valueOf(venda.getComEcfVendaCpf()) != 0) {
								venda.setComEcfVendaObservacao("Nome=" + venda.getComEcfVendaNome() + "-CPF=" + venda.getComEcfVendaCpf());
							}
							mapVenda.put(venda.getComEcfVendaCoo(), venda);
							// produtos
						} else if (rec instanceof ComEcfVendaProduto) {
							ComEcfVendaProduto prod = (ComEcfVendaProduto) rec;
							prod.setProdProduto(getProduto(prod.getComEcfVendaProdutoCodigo()));
							prod.setProdEmbalagem(getEmbalagem(prod.getComEcfVendaProdutoUnd()));
							prod.setComEcfVendaProdutoQuantidade(prod.getComEcfVendaProdutoQuantidade() / 1000);
							prod.setComEcfVendaProdutoLiquido(prod.getComEcfVendaProdutoBruto() - prod.getComEcfVendaProdutoDesconto());
							mapVenda.get(prod.getComEcfVendaProdutoCoo()).getComEcfVendaProdutos().add(prod);
						}
					} catch (Exception e) {
						// faz nada, pois sao linhas nao reconhecidas
					}
				} while (rec != null);
				in.close();

				// valida ecf
				if (ecf != null) {
					try {
						service.salvarEcfZ(ecfZ);
						int vendas = 0;
						int vendasNfechadas = 0;
						int prodNachados = 0;

						// percorre as vendas
						for (Entry<Integer, ComEcfVenda> venda : mapVenda.entrySet()) {
							vendas++;
							// coloca valor caso esteja cancelada
							if (venda.getValue().getComEcfVendaCancelada()) {
								double valor = 0.00;
								for (ComEcfVendaProduto prod : venda.getValue().getComEcfVendaProdutos()) {
									valor += prod.getComEcfVendaProdutoLiquido();
									prod.setComEcfVendaProdutoCancelado(true);
								}
								venda.getValue().setComEcfVendaBruto(valor);
								venda.getValue().setComEcfVendaLiquido(valor);
							}

							// verifica se pode fechar a venda
							boolean fechar = true;
							for (ComEcfVendaProduto venProd : venda.getValue().getComEcfVendaProdutos()) {
								if (venProd.getProdProduto() == null) {
									prodNachados++;
									fechar = false;
								}
							}
							ComEcfVenda ecfVenda = service.salvarEcfVenda(venda.getValue());
							
							// valida se fecha a venda
							if (fechar) {
								String estoque = auth.getConf().get("estoque.ativo");
								auth.getConf().put("estoque.ativo", "nao");
								List<String[]> invalidos = new ArrayList<String[]>();
								new FecharEcfVenda(null, service, new ComEcfVenda(ecfVenda.getComEcfVendaId()), invalidos, auth).execute();
								auth.getConf().put("estoque.ativo", estoque);
							} else {
								vendasNfechadas++;
							}
						}

						cat52.setVendas(vendas);
						cat52.setVendaNfechadas(vendasNfechadas);
						cat52.setProdNachados(prodNachados);
						oks.add(cat52);
					} catch (Exception e) {
						cat52.setErro("Dados ja existentes no sistema!");
						err.add(cat52);
					}
				} else {
					cat52.setErro("Nao foi encontrada a ECF no sistema!");
					err.add(cat52);
				}
			}
		} catch (Exception e) {
			throw new OpenSigException(e.getMessage());
		}

		Map<String, List<Cat52>> resp = new HashMap<String, List<Cat52>>();
		resp.put("ok", oks);
		resp.put("erro", err);
		return resp;
	}

	// recupera o produto
	private ProdProduto getProduto(String codigo) throws Exception {
		GrupoFiltro gf = new GrupoFiltro();
		// codigo
		FiltroNumero fn = new FiltroNumero("prodProdutoSinc", ECompara.IGUAL, codigo.replaceAll("\\W", ""));
		gf.add(fn, EJuncao.OU);
		// barra
		FiltroNumero fn1 = new FiltroNumero("prodProdutoBarra", ECompara.IGUAL, codigo.replaceAll("\\W", ""));
		gf.add(fn1, EJuncao.OU);
		// barra do preco
		FiltroNumero fn2 = new FiltroNumero("prodPrecoBarra", ECompara.IGUAL, codigo.replaceAll("\\W", ""));
		fn2.setCampoPrefixo("t2.");
		gf.add(fn2);
		// seleciona
		ProdProduto prod = (ProdProduto) service.selecionar(new ProdProduto(), gf, false);
		return prod;
	}

	// recupera a embalagem
	private ProdEmbalagem getEmbalagem(String nome) {
		// se nao achar colocar a padrao UND
		ProdEmbalagem resp = new ProdEmbalagem(1);

		// percorre as embalagens
		for (ProdEmbalagem emb : embalagem) {
			if (emb.getProdEmbalagemNome().equalsIgnoreCase(nome)) {
				resp = emb;
				break;
			}
		}

		return resp;
	}
}
