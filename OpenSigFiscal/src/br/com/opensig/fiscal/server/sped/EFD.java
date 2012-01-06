package br.com.opensig.fiscal.server.sped;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.parametro.ParametroBinario;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.fiscal.client.servico.FiscalService;
import br.com.opensig.fiscal.server.FiscalServiceImpl;
import br.com.opensig.fiscal.shared.modelo.FisSpedBloco;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public class EFD implements Runnable {

	private File arquivo;
	private FisSpedFiscal sped;
	private FiscalService service;
	private Autenticacao auth;
	
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
			// inserindo dados
			escreverRegistros();
			// lendo dados do arquivo
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
			FiltroNumero fn = new FiltroNumero("fisSpedFiscalId", ECompara.IGUAL, sped.getFisSpedFiscalId());
			ParametroBinario pb = new ParametroBinario("fisSpedFiscalAtivo", 1);
			Sql sql = new Sql(new FisSpedFiscal(), EComando.ATUALIZAR, fn, pb);
			service.executar(new Sql[] { sql });
		} catch (Exception e) {
			UtilServer.LOG.error("Nao gerou o efd.", e);
		} finally {
			// deletando o arquivo txt
			arquivo.delete();
		}
	}

	// Metodo que recupera os registros selecionados e chama a execucao de cada um
	protected void escreverRegistros() throws Exception {
		// monta o filtro dos blocos/registros
		GrupoFiltro gf = new GrupoFiltro();
		for (Integer id : sped.getRegistros()) {
			FiltroNumero fn = new FiltroNumero("fisSpedBlocoId", ECompara.IGUAL, id);
			gf.add(fn, EJuncao.OU);
		}
		FiltroNumero fn = new FiltroNumero("fisSpedBlocoId", ECompara.IGUAL, 0);
		gf.add(fn);
		// seleciona todos os registros
		Lista<FisSpedBloco> blocos = service.selecionar(new FisSpedBloco(), 0, 0, gf, false);
		// contagem das linhas do bloco e do arquivo
		int qtdBloco = 0;
		int qtdArquivo = 0;
		FileWriter fw = new FileWriter(arquivo, true);
		// para cada registro instancia sua classe e executa o comando
		for (FisSpedBloco bloco : blocos.getLista()) {
			try {
				Class<IRegistro> classe = (Class<IRegistro>) Class.forName(bloco.getFisSpedBlocoClasse());
				IRegistro registro = classe.newInstance();
				registro.setQtdLInhas(bloco.getFisSpedBlocoClasse().endsWith("9999") ? qtdArquivo : qtdBloco);
				registro.setArquivo(fw);
				registro.setSped(sped);
				registro.setService(service);
				registro.setAuth(auth);
				registro.executar();
				// marcando as qtds
				qtdArquivo += registro.getQtdLinhas();
				qtdBloco = registro.getFimBloco() ? 0 : qtdBloco + registro.getQtdLinhas();
			} catch (Exception e) {
				UtilServer.LOG.error("Erro na execucao do registro " + bloco.getFisSpedBlocoClasse(), e);
			}
		}
		fw.flush();
		fw.close();
	}
}
