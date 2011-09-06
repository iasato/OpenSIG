package br.com.opensig.core.client.controlador.comando.importar;

import java.util.Map;

import br.com.opensig.core.client.controlador.comando.ComandoAcao;
import br.com.opensig.core.shared.modelo.EArquivo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.gwtextux.client.widgets.upload.UploadDialogListenerAdapter;

/**
 * Classe abstrata que define as opcoes padroes de importacao.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public abstract class ComandoImportar extends ComandoAcao {

	/**
	 * A janela de upload do arquivo.
	 */
	protected UploadDialog uplArquivo;
	/**
	 * A funcao de retorno da resposta.
	 */
	protected AsyncCallback<String> assincrono;
	/**
	 * O tipo do arquivo.
	 */
	protected EArquivo tipo;

	public ComandoImportar(EArquivo tipo) {
		this(tipo, new AsyncCallback<String>() {
			public void onSuccess(String result) {
			}

			public void onFailure(Throwable caught) {
			}
		});
	}

	/**
	 * Construtor padrao que permite setar as opcoes de tipo e funcao de
	 * retorno.
	 * 
	 * @param tipo
	 *            define o tipo do arquivo importacao.
	 * @param assincrono
	 *            a funcao de retorno a ser disparada apos execucao.
	 */
	public ComandoImportar(EArquivo tipo, AsyncCallback<String> assincrono) {
		this.tipo = tipo;
		this.assincrono = assincrono;
	}

	/**
	 * Metodo de execucao para as exportacoes apos validao de permissao.
	 */
	protected abstract void execute();

	@Override
	public void execute(Map contexto) {
		uplArquivo = new UploadDialog();
		uplArquivo.setModal(true);
		uplArquivo.setClosable(false);
		uplArquivo.setUrl(GWT.getHostPageBaseURL() + "UploadService");
		uplArquivo.setAllowCloseOnUpload(false);
		uplArquivo.setUploadAutostart(true);
		uplArquivo.setPermittedExtensions(new String[] { tipo.toString().toLowerCase() });
		uplArquivo.setBaseParams(new UrlParam[] { new UrlParam("acao", "salvar"), new UrlParam("local", "sessao") });

		uplArquivo.addListener(new UploadDialogListenerAdapter() {
			public void onUploadSuccess(UploadDialog source, String filename, JavaScriptObject data) {
				assincrono.onSuccess(JavaScriptObjectHelper.getAttribute(data, "dados"));
			}

			public void onUploadError(UploadDialog source, String filename, JavaScriptObject data) {
				assincrono.onFailure(new Exception(JavaScriptObjectHelper.getAttribute(data, "dados")));
			}

			public void onUploadFailed(UploadDialog source, String filename) {
				assincrono.onFailure(null);
			}

			public boolean onBeforeAdd(UploadDialog source, String filename) {
				return source.getQueuedCount() == 0;
			}
		});
		uplArquivo.show();
	}

	// Gets e Seteres

	public UploadDialog getUplArquivo() {
		return uplArquivo;
	}

	public void setUplArquivo(UploadDialog uplArquivo) {
		this.uplArquivo = uplArquivo;
	}

	public AsyncCallback<String> getAssincrono() {
		return assincrono;
	}

	public void setAssincrono(AsyncCallback<String> assincrono) {
		this.assincrono = assincrono;
	}

	public EArquivo getTipo() {
		return tipo;
	}

	public void setTipo(EArquivo tipo) {
		this.tipo = tipo;
	}

}
