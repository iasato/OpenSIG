package br.com.opensig.fiscal.client;

import java.util.ArrayList;
import java.util.Collection;

import br.com.opensig.core.client.controlador.comando.FabricaComando;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportar;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportarCsv;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportarXls;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportarXml;
import br.com.opensig.core.client.controlador.comando.lista.ComandoEditar;
import br.com.opensig.core.client.controlador.comando.lista.ComandoEditarFiltrados;
import br.com.opensig.core.client.controlador.comando.lista.ComandoNovo;
import br.com.opensig.core.client.controlador.comando.lista.ComandoNovoDuplicar;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.fiscal.client.controlador.comando.ComandoCadastro;
import br.com.opensig.fiscal.client.controlador.comando.ComandoCertificado;
import br.com.opensig.fiscal.client.controlador.comando.ComandoEntrada;
import br.com.opensig.fiscal.client.controlador.comando.ComandoIncentivo;
import br.com.opensig.fiscal.client.controlador.comando.ComandoSaida;
import br.com.opensig.fiscal.client.controlador.comando.ComandoSituacao;
import br.com.opensig.fiscal.client.controlador.comando.ComandoStatus;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoBackupEntrada;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoBackupSaida;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoInutilizar;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoRecuperarEntrada;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoRecuperarSaida;
import br.com.opensig.fiscal.client.controlador.comando.acao.ComandoValidar;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Classe que inicializa o modulo SigFiscal.
 * 
 * @author Pedro H. Lira
 * @since 20/07/2010
 * @version 1.0
 */
public class OpenSigFiscal implements EntryPoint {

	/**
	 * Metodo que é disparado ao iniciar o projeto que contém este módulo. Usar
	 * este método para adicionar as classes de comando na fábrica de comandos.
	 */
	public void onModuleLoad() {
		FabricaComando fc = FabricaComando.getInstancia();
		fc.addComando(ComandoEntrada.class.getName(), (IComando) GWT.create(ComandoEntrada.class));
		fc.addComando(ComandoSaida.class.getName(), (IComando) GWT.create(ComandoSaida.class));
		fc.addComando(ComandoCertificado.class.getName(), (IComando) GWT.create(ComandoCertificado.class));
		fc.addComando(ComandoStatus.class.getName(), (IComando) GWT.create(ComandoStatus.class));
		fc.addComando(ComandoSituacao.class.getName(), (IComando) GWT.create(ComandoSituacao.class));
		fc.addComando(ComandoCadastro.class.getName(), (IComando) GWT.create(ComandoCadastro.class));
		fc.addComando(ComandoIncentivo.class.getName(), (IComando) GWT.create(ComandoIncentivo.class));
		fc.addComando(ComandoRecuperarSaida.class.getName(), (IComando) GWT.create(ComandoRecuperarSaida.class));
		fc.addComando(ComandoRecuperarEntrada.class.getName(), (IComando) GWT.create(ComandoRecuperarEntrada.class));
		fc.addComando(ComandoBackupSaida.class.getName(), (IComando) GWT.create(ComandoBackupSaida.class));
		fc.addComando(ComandoBackupEntrada.class.getName(), (IComando) GWT.create(ComandoBackupEntrada.class));
		fc.addComando(ComandoInutilizar.class.getName(), (IComando) GWT.create(ComandoInutilizar.class));
		fc.addComando(ComandoValidar.class.getName(), (IComando) GWT.create(ComandoValidar.class));

		// acoes proibidas do certificado
		Collection<Class> acoes = new ArrayList<Class>();
		acoes.add(ComandoEditarFiltrados.class);
		acoes.add(ComandoNovoDuplicar.class);
		acoes.add(ComandoImportar.class);
		acoes.add(ComandoImportarCsv.class);
		acoes.add(ComandoImportarXls.class);
		acoes.add(ComandoImportarXml.class);
		
		// acoes proibidas para notas fiscais
		Collection<Class> acoes2 = new ArrayList<Class>();
		acoes2.addAll(acoes);
		acoes2.add(ComandoNovo.class);
		acoes2.add(ComandoEditar.class);

		Ponte.setAcoesProibidas(ComandoEntrada.class.getName(), acoes2);
		Ponte.setAcoesProibidas(ComandoSaida.class.getName(), acoes2);
		Ponte.setAcoesProibidas(ComandoCertificado.class.getName(), acoes);
	}
}
