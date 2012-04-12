package br.com.opensig.fiscal.server.sped;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
import br.com.opensig.core.client.controlador.filtro.FiltroData;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.controlador.parametro.ParametroBinario;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.client.servico.FiscalService;
import br.com.opensig.fiscal.server.FiscalServiceImpl;
import br.com.opensig.fiscal.shared.modelo.FisSpedBloco;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class EFD implements Runnable {

	private File arquivo;
	private FisSpedFiscal sped;
	private FiscalService service;
	private Autenticacao auth;
	private Date inicio;
	private Date fim;
	private List<FisSpedBloco> blocos;
	private List<ComCompra> compras;
	private List<ComFrete> fretes;
	private List<ComVenda> vendas;
	private List<ComEcfVenda> ecfs;
	private List<ProdProduto> estoque;

	public EFD(File arquivo, FisSpedFiscal sped, Autenticacao auth) {
		this.arquivo = arquivo;
		this.sped = sped;
		this.service = new FiscalServiceImpl(auth);
		this.auth = auth;
	}

	@Override
	public void run() {
		try {
			// criando o arquivo novo
			arquivo.createNewFile();
			// setando a empresa no sped
			FiltroNumero fn = new FiltroNumero("empEmpresaId", ECompara.IGUAL, sped.getEmpEmpresa().getEmpEmpresaId());
			EmpEmpresa emp = (EmpEmpresa) service.selecionar(new EmpEmpresa(), fn, false);
			sped.setEmpEmpresa(emp);
			// datas
			inicio = new SimpleDateFormat("ddMMyyyy").parse("01" + (sped.getFisSpedFiscalMes() > 9 ? sped.getFisSpedFiscalMes() : "0" + sped.getFisSpedFiscalMes()) + sped.getFisSpedFiscalAno());
			Calendar cal = Calendar.getInstance();
			cal.setTime(inicio);
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			fim = cal.getTime();
			// prepara os dados
			blocos = getBlocos();
			compras = getCompras();
			fretes = getFretes();
			vendas = getVendas();
			ecfs = getEcfs();
			estoque = getEstoque();
			// lendo dados do arquivo
			escreverRegistros();
			InputStream is = new FileInputStream(arquivo);
			byte[] obj = new byte[is.available()];
			is.read(obj);
			is.close();
			// gerando o zip
			Map<String, byte[]> zip = new HashMap<String, byte[]>();
			zip.put(arquivo.getName(), obj);
			obj = UtilServer.getZIP(zip);
			// salvando o zip em arquivo fisico
			OutputStream os = new FileOutputStream(arquivo.getPath().replace("TXT", "ZIP"));
			os.write(obj);
			os.flush();
			os.close();
			// atualizando o status do registro
			FiltroNumero fn1 = new FiltroNumero("fisSpedFiscalId", ECompara.IGUAL, sped.getFisSpedFiscalId());
			ParametroBinario pb = new ParametroBinario("fisSpedFiscalAtivo", 1);
			Sql sql = new Sql(new FisSpedFiscal(), EComando.ATUALIZAR, fn1, pb);
			service.executar(new Sql[] { sql });
		} catch (Exception e) {
			UtilServer.LOG.error("Nao gerou o efd.", e);
		} finally {
			// deletando o arquivo txt
			arquivo.delete();
		}
	}

	// metodo que recupera os blocos
	private List<FisSpedBloco> getBlocos() throws Exception {
		// monta o filtro dos blocos/registros
		GrupoFiltro gf = new GrupoFiltro();
		for (Integer id : sped.getRegistros()) {
			FiltroNumero fn = new FiltroNumero("fisSpedBlocoId", ECompara.IGUAL, id);
			gf.add(fn, EJuncao.OU);
		}
		FiltroNumero fn = new FiltroNumero("fisSpedBlocoId", ECompara.IGUAL, 0);
		gf.add(fn);
		// seleciona todos os registros
		return service.selecionar(new FisSpedBloco(), 0, 0, gf, false).getLista();
	}

	// metodo que recupera as compras
	private List<ComCompra> getCompras() throws Exception {
		if (sped.getCompras().length > 0) {
			// monta o filtro dos blocos/registros
			GrupoFiltro gf = new GrupoFiltro();
			for (Integer id : sped.getCompras()) {
				FiltroNumero fn = new FiltroNumero("comCompraId", ECompara.IGUAL, id);
				gf.add(fn, EJuncao.OU);
			}
			// seleciona as compras
			return service.selecionar(new ComCompra(), 0, 0, gf, false).getLista();
		} else {
			return new ArrayList<ComCompra>();
		}
	}

	// metodo que recupera os fretes
	private List<ComFrete> getFretes() throws Exception {
		if (sped.getFretes().length > 0) {
			// monta o filtro dos blocos/registros
			GrupoFiltro gf = new GrupoFiltro();
			for (Integer id : sped.getFretes()) {
				FiltroNumero fn = new FiltroNumero("comFreteId", ECompara.IGUAL, id);
				gf.add(fn, EJuncao.OU);
			}
			// seleciona os fretes
			return service.selecionar(new ComFrete(), 0, 0, gf, false).getLista();
		} else {
			return new ArrayList<ComFrete>();
		}
	}

	// metodo que recupera as vendas
	private List<ComVenda> getVendas() throws Exception {
		if (sped.getVendas().length > 0) {
			// monta o filtro dos blocos/registros
			GrupoFiltro gf = new GrupoFiltro();
			for (Integer id : sped.getVendas()) {
				FiltroNumero fn = new FiltroNumero("comVendaId", ECompara.IGUAL, id);
				gf.add(fn, EJuncao.OU);
			}
			// seleciona todos as vendas
			return service.selecionar(new ComVenda(), 0, 0, gf, false).getLista();
		} else {
			return new ArrayList<ComVenda>();
		}
	}

	// metodo que recupera as vendas das ecfs
	private List<ComEcfVenda> getEcfs() throws Exception {
		if (sped.getEcfs().length > 0) {
			// filtro da data
			GrupoFiltro gfData = new GrupoFiltro();
			FiltroData fd1 = new FiltroData("comEcfVendaData", ECompara.MAIOR_IGUAL, inicio);
			gfData.add(fd1, EJuncao.E);
			FiltroData fd2 = new FiltroData("comEcfVendaData", ECompara.MENOR_IGUAL, fim);
			gfData.add(fd2, EJuncao.E);

			// monta o filtro dos das ecfs
			GrupoFiltro gfEcf = new GrupoFiltro();
			for (Integer id : sped.getEcfs()) {
				FiltroObjeto fo = new FiltroObjeto("comEcf", ECompara.IGUAL, new ComEcf(id));
				gfEcf.add(fo, EJuncao.OU);
			}
			
			FiltroBinario fb = new FiltroBinario("comEcfVendaFechada", ECompara.IGUAL, 1);
			GrupoFiltro gf = new GrupoFiltro();
			gf.add(gfData, EJuncao.E);
			gf.add(fb, EJuncao.E);
			gf.add(gfEcf);

			// seleciona todos as vendas da ecf
			return service.selecionar(new ComEcfVenda(), 0, 0, gf, false).getLista();
		} else {
			return new ArrayList<ComEcfVenda>();
		}
	}

	// metodo que recupera os produtos do estoque
	private List<ProdProduto> getEstoque() throws Exception {
		// valida se tem bloco H com itens
		int tot = 0;
		for (FisSpedBloco bl : blocos) {
			if (bl.getFisSpedBlocoLetra().equals("H") && bl.getFisSpedBlocoNivel() > 1) {
				tot++;
			}
		}

		if (tot > 0) {
			// seleciona todos os produtos com estoque maior que ZERO
			FiltroObjeto fo = new FiltroObjeto("t1.empEmpresa", ECompara.IGUAL, sped.getEmpEmpresa());
			FiltroNumero fn = new FiltroNumero("t1.prodEstoqueQuantidade", ECompara.MAIOR, 0);
			GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, fn });
			Lista<ProdProduto> lista = service.selecionar(new ProdProduto(), 0, 0, gf, false);
			return lista.getLista();
		} else {
			return null;
		}
	}

	// Metodo que recupera os registros selecionados e chama a execucao de cada um
	private void escreverRegistros() throws Exception {
		// contagem das linhas do bloco e do arquivo
		int qtdBloco = 0;
		int qtdArquivo = 0;
		FileWriter escritor = new FileWriter(arquivo, true);
		// para cada registro instancia sua classe e executa o comando
		for (FisSpedBloco bloco : blocos) {
			if (bloco.getFisSpedBlocoNivel() < 3) {
				try {
					Class<IRegistro> classe = (Class<IRegistro>) Class.forName(bloco.getFisSpedBlocoClasse());
					IRegistro registro = classe.newInstance();
					registro.setQtdLInhas(bloco.getFisSpedBlocoClasse().endsWith("9999") ? qtdArquivo : qtdBloco);
					registro.setLeitor(arquivo);
					registro.setEscritor(escritor);
					registro.setSped(sped);
					registro.setService(service);
					registro.setAuth(auth);
					registro.setInicio(inicio);
					registro.setFim(fim);
					registro.setBlocos(blocos);
					registro.setCompras(compras);
					registro.setFretes(fretes);
					registro.setVendas(vendas);
					registro.setEcfs(ecfs);
					registro.setEstoque(estoque);
					registro.executar();
					// marcando as qtds
					qtdArquivo += registro.getQtdLinhas();
					qtdBloco = registro.getFimBloco() ? 0 : qtdBloco + registro.getQtdLinhas();
				} catch (Exception e) {
					UtilServer.LOG.error("Erro na execucao do registro " + bloco.getFisSpedBlocoClasse(), e);
				}
			}
		}
		escritor.flush();
		escritor.close();
	}
}
