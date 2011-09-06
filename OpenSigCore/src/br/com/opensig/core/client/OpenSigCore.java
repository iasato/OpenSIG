package br.com.opensig.core.client;

import br.com.opensig.core.client.controlador.comando.ComandoAcao;
import br.com.opensig.core.client.controlador.comando.ComandoFuncao;
import br.com.opensig.core.client.controlador.comando.FabricaComando;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportar;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportarCsv;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportarHtml;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportarPdf;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportarXls;
import br.com.opensig.core.client.controlador.comando.exportar.ComandoExportarXml;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportar;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportarCsv;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportarXls;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportarXml;
import br.com.opensig.core.client.controlador.comando.lista.ComandoEditar;
import br.com.opensig.core.client.controlador.comando.lista.ComandoEditarFiltrados;
import br.com.opensig.core.client.controlador.comando.lista.ComandoExcluir;
import br.com.opensig.core.client.controlador.comando.lista.ComandoExcluirFiltrados;
import br.com.opensig.core.client.controlador.comando.lista.ComandoImprimir;
import br.com.opensig.core.client.controlador.comando.lista.ComandoNovo;
import br.com.opensig.core.client.controlador.comando.lista.ComandoNovoDuplicar;
import br.com.opensig.core.client.controlador.comando.lista.ComandoPermiteEmpresa;
import br.com.opensig.core.client.controlador.comando.lista.ComandoPermiteUsuario;
import br.com.opensig.core.client.controlador.comando.lista.ComandoVisualizar;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Classe que inicializa o módulo SigCore.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class OpenSigCore implements EntryPoint {

	public static final I18N i18n = (I18N) GWT.create(I18N.class);

	/**
	 * Metodo que é disparado ao iniciar o projeto que contém este módulo. Usar
	 * este método para adicionar as classes de comando na fábrica de comandos.
	 */
	public void onModuleLoad() {
		FabricaComando fc = FabricaComando.getInstancia();
		// visao
		fc.addComando(ComandoVisualizar.class.getName(), (IComando) GWT.create(ComandoVisualizar.class));
		fc.addComando(ComandoPermiteUsuario.class.getName(), (IComando) GWT.create(ComandoPermiteUsuario.class));
		fc.addComando(ComandoPermiteEmpresa.class.getName(), (IComando) GWT.create(ComandoPermiteEmpresa.class));
		fc.addComando(ComandoAcao.class.getName(), (IComando) GWT.create(ComandoAcao.class));
		fc.addComando(ComandoFuncao.class.getName(), (IComando) GWT.create(ComandoFuncao.class));
		// edicao
		fc.addComando(ComandoNovo.class.getName(), (IComando) GWT.create(ComandoNovo.class));
		fc.addComando(ComandoNovoDuplicar.class.getName(), (IComando) GWT.create(ComandoNovoDuplicar.class));
		fc.addComando(ComandoEditar.class.getName(), (IComando) GWT.create(ComandoEditar.class));
		fc.addComando(ComandoEditarFiltrados.class.getName(), (IComando) GWT.create(ComandoEditarFiltrados.class));
		fc.addComando(ComandoExcluir.class.getName(), (IComando) GWT.create(ComandoExcluir.class));
		fc.addComando(ComandoExcluirFiltrados.class.getName(), (IComando) GWT.create(ComandoExcluirFiltrados.class));
		// exportar
		fc.addComando(ComandoImprimir.class.getName(), (IComando) GWT.create(ComandoImprimir.class));
		fc.addComando(ComandoExportar.class.getName(), (IComando) GWT.create(ComandoExportarPdf.class));
		fc.addComando(ComandoExportarPdf.class.getName(), (IComando) GWT.create(ComandoExportarPdf.class));
		fc.addComando(ComandoExportarXls.class.getName(), (IComando) GWT.create(ComandoExportarXls.class));
		fc.addComando(ComandoExportarCsv.class.getName(), (IComando) GWT.create(ComandoExportarCsv.class));
		fc.addComando(ComandoExportarXml.class.getName(), (IComando) GWT.create(ComandoExportarXml.class));
		fc.addComando(ComandoExportarHtml.class.getName(), (IComando) GWT.create(ComandoExportarHtml.class));
		// importar
		fc.addComando(ComandoImportar.class.getName(), (IComando) GWT.create(ComandoImportarXls.class));
		fc.addComando(ComandoImportarXls.class.getName(), (IComando) GWT.create(ComandoImportarXls.class));
		fc.addComando(ComandoImportarCsv.class.getName(), (IComando) GWT.create(ComandoImportarCsv.class));
		fc.addComando(ComandoImportarXml.class.getName(), (IComando) GWT.create(ComandoImportarXml.class));
	}
}