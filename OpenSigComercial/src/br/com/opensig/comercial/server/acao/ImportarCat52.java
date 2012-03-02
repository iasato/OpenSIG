package br.com.opensig.comercial.server.acao;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

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
import br.com.opensig.core.client.controlador.parametro.ParametroException;
import br.com.opensig.core.client.servico.CoreException;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.Conexao;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.server.importar.IImportacao;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.sistema.SisExpImp;
import br.com.opensig.empresa.shared.modelo.EmpCliente;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.permissao.shared.modelo.SisUsuario;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;

public class ImportarCat52 implements IImportacao<Cat52> {

	private List<ProdEmbalagem> embalagem;
	private ComercialServiceImpl service;
	private Autenticacao auth;
	private EmpCliente cliente;
	private SisUsuario usuario;
	private List<Cat52> oks;
	private List<Cat52> err;
	private int primeiraVenda;

	@Override
	public Map<String, List<Cat52>> setArquivo(Autenticacao auth, Map<String, byte[]> arquivos, SisExpImp modo) throws OpenSigException {
		this.service = new ComercialServiceImpl(auth);
		this.auth = auth;
		this.oks = new ArrayList<Cat52>();
		this.err = new ArrayList<Cat52>();

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
			cliente = (EmpCliente) service.selecionar(new EmpCliente(), fn, false);

			// pega o usuario atual
			usuario = new SisUsuario(Integer.valueOf(auth.getUsuario()[0]));

			// setando os objetos
			Object rec;
			ComEcf ecf;
			ComEcfZ ecfZ;
			BeanReader in;
			Map<Integer, ComEcfVenda> mapVenda;

			for (Entry<String, byte[]> arquivo : arquivos.entrySet()) {
				Cat52 cat52 = new Cat52();
				cat52.setArquivo(arquivo.getKey());
				// setando os objetos
				rec = null;
				ecf = null;
				ecfZ = null;
				in = null;
				primeiraVenda = 0;
				mapVenda = new HashMap<Integer, ComEcfVenda>();

				try {
					// lendo os dados do arquivo
					String texto = new String(arquivo.getValue());
					// limpando
					arquivo.setValue(null);
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
							ecf = getEcf((ComEcf) rec);
							// leitura z
						} else if (rec instanceof ComEcfZ) {
							if (ecfZ == null) {
								ecfZ = (ComEcfZ) rec;
							} else {
								ecfZ = getEcfZ((ComEcfZ) rec, ecf, ecfZ.getComEcfZTotal());
								if (ecfZ == null) {
									break;
								}
							}
							// totais da leitura z
						} else if (rec instanceof ComEcfZTotais) {
							ComEcfZTotais total = (ComEcfZTotais) rec;
							if (total.getComEcfZTotaisValor() > 0.00) {
								total.setComEcfZTotaisValor(total.getComEcfZTotaisValor() / 100);
								ecfZ.getComZTotais().add(total);
							}
							// venda
						} else if (rec instanceof ComEcfVenda) {
							ComEcfVenda venda = getVenda((ComEcfVenda) rec, ecf);
							mapVenda.put(venda.getComEcfVendaCoo(), venda);
							// produtos
						} else if (rec instanceof ComEcfVendaProduto) {
							ComEcfVendaProduto prod = (ComEcfVendaProduto) rec;
							ComEcfVenda venda = mapVenda.get(prod.getComEcfVendaProdutoCoo());
							prod = getProduto(prod, venda.getComEcfVendaDesconto());
							if (venda.getComEcfVendaCancelada()) {
								prod.setComEcfVendaProdutoCancelado(true);
							}
							venda.getComEcfVendaProdutos().add(prod);
						}
					} catch (Exception e) {
						// faz nada, pois sao linhas nao reconhecidas
					}
				} while (rec != null);

				// limpando
				in.close();
				in = null;
				System.gc();

				// valida ecfZ
				if (ecfZ != null && ecfZ.getComZTotais() != null && !ecfZ.getComZTotais().isEmpty()) {
					// salva z
					service.salvarEcfZ(ecfZ);
					// salva as vendas
					salvaVendas(mapVenda, cat52);
					// atauliza os produtos das vendas
					atualizarProdutos();
					// recupera as vendas salvas
					FiltroNumero fnVenda = new FiltroNumero("comEcfVendaId", ECompara.MAIOR_IGUAL, primeiraVenda);
					Lista<ComEcfVenda> vendasEcf = service.selecionar(new ComEcfVenda(), 0, 0, fnVenda, false);
					// fechando as vendas
					fecharVendas(vendasEcf, cat52);
					// limpando
					mapVenda = null;
					vendasEcf = null;
					System.gc();
				} else {
					cat52.setErro("Dados ja existentes no sistema ou arquivo sem registros!");
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

	// verifica se pode fechar as vendas
	private void fecharVendas(Lista<ComEcfVenda> vendasEcf, Cat52 cat52) {
		// zera os contadores
		int vendas = 0;
		int vendasNfechadas = 0;
		int prodNachados = 0;
		String estoque = auth.getConf().get("estoque.ativo");
		if (estoque.equalsIgnoreCase("sim")) {
			auth.getConf().put("estoque.ativo", "nao");
		}

		try {
			FecharEcfVenda fecharVenda;
			ArrayList<String[]> invalidos = new ArrayList<String[]>();

			// percorre as vendas
			for (ComEcfVenda venda : vendasEcf.getLista()) {
				vendas++;
				if (!venda.getComEcfVendaCancelada()) {
					// verifica se pode fechar a venda
					boolean fechar = true;
					for (ComEcfVendaProduto venProd : venda.getComEcfVendaProdutos()) {
						if (venProd.getProdProduto() == null) {
							prodNachados++;
							fechar = false;
						}
					}

					// valida se fecha a venda
					if (fechar) {
						fecharVenda = new FecharEcfVenda(null, service, venda, invalidos, auth);
						fecharVenda.execute();
					} else {
						vendasNfechadas++;
					}
				}
			}

			cat52.setVendas(vendas);
			cat52.setVendaNfechadas(vendasNfechadas);
			cat52.setProdNachados(prodNachados);
			oks.add(cat52);
		} catch (Exception e) {
			cat52.setErro(e.getMessage());
			err.add(cat52);
		} finally {
			auth.getConf().put("estoque.ativo", estoque);
		}
	}

	// encontra a ecf no sistema
	private ComEcf getEcf(ComEcf ecf) throws ParametroException, CoreException {
		// encontra a ecf no sistema
		EmpEmpresa empresa = new EmpEmpresa(Integer.valueOf(auth.getEmpresa()[0]));
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, empresa);
		FiltroTexto ft = new FiltroTexto("comEcfSerie", ECompara.IGUAL, ecf.getComEcfSerie());
		GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, ft });
		ComEcf ecf2 = (ComEcf) service.selecionar(ecf, gf, false);

		if (ecf2 == null) {
			ecf.setComEcfCodigo("2D");
			ecf.setEmpEmpresa(empresa);
			service.salvar(ecf);
		} else {
			ecf = ecf2;
		}

		return ecf;
	}

	// seta os dados da Z
	private ComEcfZ getEcfZ(ComEcfZ ecfZ, ComEcf ecf, double total) throws ParametroException, CoreException {
		ecfZ.setComEcf(ecf);
		ecfZ.setComEcfZBruto(ecfZ.getComEcfZBruto() / 100);
		ecfZ.setComEcfZTotal(total / 100);
		ecfZ.setComZTotais(new ArrayList<ComEcfZTotais>());

		// valida Z
		FiltroObjeto fo = new FiltroObjeto("comEcf", ECompara.IGUAL, ecf);
		FiltroData fd = new FiltroData("comEcfZData", ECompara.IGUAL, ecfZ.getComEcfZData());
		GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, fd });
		if (total == 0 || service.selecionar(ecfZ, gf, false) != null) {
			ecfZ = null;
		}

		return ecfZ;
	}

	// coloca os dados da venda
	private ComEcfVenda getVenda(ComEcfVenda venda, ComEcf ecf) {
		venda.setComEcf(ecf);
		venda.setSisUsuario(usuario);
		venda.setEmpCliente(cliente);
		venda.setComEcfVendaBruto(venda.getComEcfVendaBruto() / 100);
		double desc = venda.getComEcfVendaBruto() > 0 ? venda.getComEcfVendaDesconto() / venda.getComEcfVendaBruto() : 0.00;
		venda.setComEcfVendaDesconto(desc);
		venda.setComEcfVendaLiquido(venda.getComEcfVendaLiquido() / 100);
		venda.setComEcfVendaProdutos(new ArrayList<ComEcfVendaProduto>());
		venda.setComEcfVendaCancelada(venda.getCancelada().equalsIgnoreCase("S"));
		venda.setComEcfVendaFechada(venda.getComEcfVendaCancelada());
		if (Long.valueOf(venda.getComEcfVendaCpf()) != 0) {
			venda.setComEcfVendaObservacao("Nome=" + venda.getComEcfVendaNome() + "-CPF=" + venda.getComEcfVendaCpf());
		} else {
			venda.setComEcfVendaObservacao("");
		}
		return venda;
	}

	// coloco os dados do produto
	private ComEcfVendaProduto getProduto(ComEcfVendaProduto prod, double desc) {
		prod.setProdEmbalagem(getEmbalagem(prod.getComEcfVendaProdutoUnd()));
		prod.setComEcfVendaProdutoBruto(prod.getComEcfVendaProdutoBruto() / 100);
		prod.setComEcfVendaProdutoDesconto(desc);
		double liquido = desc > 0 ? prod.getComEcfVendaProdutoBruto() - (prod.getComEcfVendaProdutoBruto() / desc) : prod.getComEcfVendaProdutoBruto();
		prod.setComEcfVendaProdutoLiquido(liquido);
		prod.setComEcfVendaProdutoQuantidade(prod.getComEcfVendaProdutoQuantidade() / 1000);
		double total = liquido * prod.getComEcfVendaProdutoQuantidade();
		prod.setComEcfVendaProdutoTotal(total);
		prod.setComEcfVendaProdutoCancelado(prod.getCancelado().equalsIgnoreCase("S"));
		return prod;
	}

	// salva as vendas no sistema
	private void salvaVendas(Map<Integer, ComEcfVenda> mapVenda, Cat52 cat52) {
		// percorre as vendas
		for (Entry<Integer, ComEcfVenda> venda : mapVenda.entrySet()) {
			try {
				ComEcfVenda ecfVenda = service.salvarEcfVenda(venda.getValue());
				if (primeiraVenda == 0) {
					primeiraVenda = ecfVenda.getComEcfVendaId();
				}
			} catch (Exception e) {
				cat52.setErro(e.getMessage());
				err.add(cat52);
			}
		}
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

	// atualiza os produtos pelo codigo
	private void atualizarProdutos() {
		// TODO exececao para ser removida
		EntityManagerFactory emf = null;
		EntityManager em = null;
		try {
			// abre a conexao
			emf = Conexao.getInstancia(new ComEcfVendaProduto().getPu());
			em = emf.createEntityManager();
			// inicia a transacao
			em.getTransaction().begin();
			// atualiza pelo codigo de barras
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE com_ecf_venda_produto, prod_produto");
			sql.append(" SET com_ecf_venda_produto.prod_produto_id = prod_produto.prod_produto_id");
			sql.append(" WHERE com_ecf_venda_produto.prod_produto_id is null");
			sql.append(" AND com_ecf_venda_produto.com_ecf_venda_produto_codigo = prod_produto.prod_produto_barra");
			Query rs = em.createNativeQuery(sql.toString());
			// efetiva a transacao
			int total = rs.executeUpdate();
			em.getTransaction().commit();

			// inicia a transacao
			em.getTransaction().begin();
			// atualiza pela descricao e valor
			sql = new StringBuffer();
			sql.append("UPDATE com_ecf_venda_produto, prod_produto");
			sql.append(" SET com_ecf_venda_produto.prod_produto_id = prod_produto.prod_produto_id");
			sql.append(" WHERE com_ecf_venda_produto.prod_produto_id is null");
			sql.append(" AND com_ecf_venda_produto.com_ecf_venda_produto_descricao = prod_produto.prod_produto_descricao");
			sql.append(" AND com_ecf_venda_produto.com_ecf_venda_produto_bruto = prod_produto.prod_produto_preco");
			rs = em.createNativeQuery(sql.toString());
			// efetiva a transacao
			total += rs.executeUpdate();
			em.getTransaction().commit();
			UtilServer.LOG.debug("Total de produtos atualizados = " + total);
		} catch (Exception ex) {
			// volta ao estado anterior
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			em.close();
			emf.close();
		}
	}

}
