package br.com.opensig.comercial.server.rest;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.rest.SisEmpresa;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
import br.com.opensig.core.client.controlador.filtro.FiltroData;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.controlador.parametro.ParametroException;
import br.com.opensig.core.client.servico.CoreException;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.empresa.shared.modelo.EmpContato;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpEndereco;
import br.com.opensig.empresa.shared.modelo.EmpEntidade;
import br.com.opensig.empresa.shared.modelo.EmpFuncionario;
import br.com.opensig.financeiro.shared.modelo.FinForma;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.permissao.shared.modelo.SisConfiguracao;
import br.com.opensig.permissao.shared.modelo.SisUsuario;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdEstoque;
import br.com.opensig.produto.shared.modelo.ProdProduto;

/**
 * Classe que representa a comunicacao do Servidor para o Cliente via Rest
 * 
 * @author Pedro H. Lira
 */
@Provider
@Path("/host")
public class RestCliente extends ARest {

	/**
	 * Construtor padrao.
	 */
	public RestCliente() {
		super();
		log = Logger.getLogger(RestCliente.class);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Override
	public String ajuda() throws RestException {
		return super.ajuda();
	}

    /**
     * Metodo que retorna o proximo numero de NFe a ser usado.
     *
     * @return uma string com o nuemro da NFe.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/nfe")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getNfe() throws RestException {
		autorizar();
		try {
			FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, ecf.getEmpEmpresa());
			Number nfe = service.buscar(new FisNotaSaida(), "fisNotaSaidaNumero", EBusca.MAXIMO, fo);
			Integer resp;
			if (nfe != null && nfe.intValue() > 0) {
				resp = nfe.intValue() + 1;
			} else {
				FiltroTexto ft = new FiltroTexto("sisConfiguracaoChave", ECompara.IGUAL, "NFE.NUMERO");
				SisConfiguracao config = (SisConfiguracao) service.selecionar(new SisConfiguracao(), ft, false);
				resp = Integer.valueOf(config.getSisConfiguracaoValor()) + 1;
			}
			return resp.toString();
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna os dados da empresa, com base no cnpj informado como
     * usuario no cabecalho de autorizacao.
     *
     * @return um objeto tipo empresa no formato JSON.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/empresa")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SisEmpresa getEmpresa() throws RestException {
		autorizar();
		try {
			FiltroTexto ft = getFiltroCnpj("empEntidade.empEntidadeDocumento1");
			EmpEmpresa emp = (EmpEmpresa) service.selecionar(new EmpEmpresa(), ft, false);
			if (emp != null) {
				SisEmpresa sis = setValoresEmpresa(emp.getEmpEmpresaId(), emp.getEmpEntidade());
				sis.setSisEmpresaContador(false);
				return sis;
			} else {
				return null;
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna os dados do contador, com base no cnpj informado como
     * usuario no cabecalho de autorizacao.
     *
     * @return um objeto tipo empresa no formato JSON.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/contador")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SisEmpresa getContador() throws RestException {
		autorizar();
		try {
			// config
			GrupoFiltro gf = new GrupoFiltro();
			FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, ecf.getEmpEmpresa());
			gf.add(fo, EJuncao.E);
			FiltroTexto ft = new FiltroTexto("sisConfiguracaoChave", ECompara.IGUAL, "SPED.0100.ID_FUNCIONARIO");
			gf.add(ft);
			SisConfiguracao config = (SisConfiguracao) service.selecionar(new SisConfiguracao(), gf, false);
			// funcionario ou contador
			FiltroNumero fn = new FiltroNumero("empFuncionarioId", ECompara.IGUAL, config.getSisConfiguracaoValor());
			EmpFuncionario fun = (EmpFuncionario) service.selecionar(new EmpFuncionario(), fn, false);
			if (fun != null) {
				SisEmpresa sis = setValoresEmpresa(fun.getEmpFuncionarioId(), fun.getEmpEntidade());
				sis.setSisEmpresaContador(true);
				return sis;
			} else {
				return null;
			}
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna os dados do ECF, com base no numero de serie informado
     * como senha no cabecalho de autorizacao.
     *
     * @return um objeto tipo impressora no formato JSON.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/impressora")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ComEcf getImpressora() throws RestException {
		autorizar();
		return ecf;
	}

    /**
     * Metodo que retorna a lista de usuario permitidos ao acesso ao sistema.
     *
     * @return uma lista de objetos usuario em formato JSON.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/usuario")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SisUsuario> getUsuario() throws RestException {
		autorizar();
		try {
			FiltroTexto ft = getFiltroCnpj("empEntidade.empEntidadeDocumento1");
			ft.setCampoPrefixo("t1.");
			List<SisUsuario> usuarios = service.selecionar(new SisUsuario(), 0, 0, ft, false).getLista();

			for (SisUsuario usuario : usuarios) {
				if (usuario.getSisUsuarioDesconto() > 0) {
					usuario.setSisUsuarioGerente(true);
				}
			}
			return usuarios;
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna a lista de tipos de pagamento cadastrados no sistema.
     *
     * @return uma lista de objetos tipos de pagamento em formato JSON.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/tipo_pagamento")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<FinForma> getPagamentoTipo() throws RestException {
		autorizar();
		try {
			FiltroBinario fb = new FiltroBinario("finFormaReceber", ECompara.IGUAL, 1);
			return service.selecionar(new FinForma(), 0, 0, fb, false).getLista();
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna a lista de embalagens cadastradas no sistema.
     *
     * @return uma lista de objetos embalagem em formato JSON.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/embalagem")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProdEmbalagem> getEmbalagem() throws RestException {
		autorizar();
		try {
			return service.selecionar(new ProdEmbalagem(), 0, 0, null, false).getLista();
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna a lista de novos produtos cadastrados no sistema.
     *
     * @param data data usada como corte para considerar novo produto.
     * @param pagina numero da pagina de retorno dos dados comecando pelo ZERO.
     * @param limite limite de registros a serem retornados.
     * @return uma lista de produtos novos cadastrados no sistema.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/produtoNovo")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProdProduto> getProdutoNovo(@QueryParam("data") String data, @QueryParam("pagina") int pagina, @QueryParam("limite") int limite) throws RestException {
		autorizar();
		try {
			Date cadastro = UtilServer.formataData(data, "dd/MM/yyyy HH:mm:ss");
			IFiltro filtro = null;
			if (cadastro != null) {
				filtro = new FiltroData("prodProdutoCadastrado", ECompara.MAIOR, cadastro);
			}
			ProdProduto prod = new ProdProduto();
			prod.setCampoOrdem("prodProdutoCadastrado");

			List<ProdProduto> produtos = service.selecionar(prod, pagina * limite, limite, filtro, false).getLista();
			setValoresProduto(produtos);
			return produtos;
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

    /**
     * Metodo que retorna a lista de novos produtos atualizados no sistema.
     *
     * @param data data usada como corte para considerar produto atualizado.
     * @param pagina numero da pagina de retorno dos dados comecando pelo ZERO.
     * @param limite limite de registros a serem retornados.
     * @return uma lista de produtos novos cadastrados no sistema.
     * @throws RestException em caso de nao conseguir acessar a informacao.
     */
	@Path("/produtoAtualizado")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProdProduto> getProdutoAtualizado(@QueryParam("data") String data, @QueryParam("pagina") int pagina, @QueryParam("limite") int limite) throws RestException {
		autorizar();
		try {
			Date alterado = UtilServer.formataData(data, "dd/MM/yyyy HH:mm:ss");
			IFiltro filtro = null;
			if (alterado != null) {
				FiltroData fd1 = new FiltroData("prodProdutoAlterado", ECompara.MAIOR, alterado);
				FiltroData fd2 = new FiltroData("prodProdutoCadastrado", ECompara.MENOR, alterado);
				filtro = new GrupoFiltro(EJuncao.E, new IFiltro[] { fd1, fd2 });
			}
			ProdProduto prod = new ProdProduto();
			prod.setCampoOrdem("prodProdutoAlterado");

			List<ProdProduto> produtos = service.selecionar(prod, pagina * limite, limite, filtro, false).getLista();
			setValoresProduto(produtos);
			return produtos;
		} catch (Exception ex) {
			log.error(ex);
			throw new RestException(ex);
		}
	}

	/**
	 * Metodo que gera o filtro de empresa usando o cnpj da autorizacao.
	 * 
	 * @param campo
	 *            o nome do campo a ser usadao.
	 * @return Um objeto do tipo filtro de texto.
	 * @throws ParseException
	 *             dispara uma exececao caso nao consiga executar.
	 */
	private FiltroTexto getFiltroCnpj(String campo) throws ParseException {
		cnpj = cnpj.replaceAll("[^0-9]", "");
		cnpj = UtilServer.formataTexto(cnpj, "##.###.###/####-##");
		return new FiltroTexto(campo, ECompara.IGUAL, cnpj);
	}

	/**
	 * Metodo que seta os valores de acordo com o esperado pelo ECF.
	 * 
	 * @param produtos
	 *            uma lista de produtos.
	 * @throws CoreException
	 * @throws ParametroException
	 */
	private void setValoresProduto(List<ProdProduto> produtos) throws CoreException {
		FiltroTexto ft = new FiltroTexto("sisConfiguracaoChave", ECompara.IGUAL, "NFE.CRT");
		SisConfiguracao config = (SisConfiguracao) service.selecionar(new SisConfiguracao(), ft, false);
		for (ProdProduto produto : produtos) {
			// seta o arrendondamento e fabricacao
			produto.setProdProdutoIat('A');
			produto.setProdProdutoIppt('T');
			// verifica se a empresa e simples
			if (config.getSisConfiguracaoValor().equals("1")) {
				produto.setProdProdutoCstCson(produto.getProdTributacao().getProdTributacaoCson());
			} else {
				produto.setProdProdutoCstCson(produto.getProdTributacao().getProdTributacaoCst());
			}
			// verifica a tributacao do produto na ecf
			if (produto.getProdTributacao().getProdTributacaoEcf().length() > 2) {
				produto.setProdProdutoTributacao(produto.getProdTributacao().getProdTributacaoEcf().charAt(2));
			} else {
				produto.setProdProdutoTributacao(produto.getProdTributacao().getProdTributacaoEcf().charAt(0));
			}
			// icms ou issqn
			if (produto.getProdProdutoTributacao() == 'S') {
				produto.setProdProdutoIssqn(produto.getProdTributacao().getProdTributacaoDentro());
				produto.setProdProdutoIcms(0.00);
			} else {
				produto.setProdProdutoIssqn(0.00);
				produto.setProdProdutoIcms(produto.getProdTributacao().getProdTributacaoDentro());
			}
			// estoque do produto nesta empresa
			for (ProdEstoque est : produto.getProdEstoques()) {
				if (cnpj.equals(est.getEmpEmpresa().getEmpEntidade().getEmpEntidadeDocumento1().replaceAll("[^0-9]", ""))) {
					produto.setProdProdutoEstoque(est.getProdEstoqueQuantidade());
					break;
				}
			}
		}
	}

	/**
	 * Metodo que seta os valores da empresa/contador como esperados pelo ECF.
	 * 
	 * @param id
	 *            o identificador do objeto.
	 * @param ent
	 *            o objeto de ententidade da empresa ou funcionario.
	 * @return um objeto no padrao do ECF.
	 */
	private SisEmpresa setValoresEmpresa(int id, EmpEntidade ent) {
		// basicos
		SisEmpresa sis = new SisEmpresa(id);
		sis.setSisEmpresaRazao(ent.getEmpEntidadeNome1());
		sis.setSisEmpresaFantasia(ent.getEmpEntidadeNome2());
		sis.setSisEmpresaCnpj(ent.getEmpEntidadeDocumento1().replaceAll("[^0-9]", ""));
		sis.setSisEmpresaIe(ent.getEmpEntidadeDocumento2().replaceAll("[^0-9]", ""));
		sis.setSisEmpresaIm("ISENTO");
		// endereco
		EmpEndereco ende = ent.getEmpEnderecos().get(0);
		sis.setSisEmpresaLogradouro(ende.getEmpEnderecoLogradouro());
		sis.setSisEmpresaNumero(ende.getEmpEnderecoNumero());
		sis.setSisEmpresaComplemento(ende.getEmpEnderecoComplemento());
		sis.setSisEmpresaBairro(ende.getEmpEnderecoBairro());
		sis.setSisEmpresaCep(ende.getEmpEnderecoCep().replaceAll("[^0-9]", ""));
		sis.setSisMunicipio(ende.getEmpMunicipio());
		// contato
		for (EmpContato cont : ent.getEmpContatos()) {
			if (cont.getEmpContatoDescricao().contains("@")) {
				sis.setSisEmpresaEmail(cont.getEmpContatoDescricao());
			} else {
				sis.setSisEmpresaFone(cont.getEmpContatoDescricao().replaceAll("[^0-9]", ""));
				sis.setSisEmpresaResponsavel(cont.getEmpContatoPessoa());
			}
		}

		return sis;
	}
}
