package br.com.opensig.core.client.controlador.comando.exportar;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.ComandoAcao;
import br.com.opensig.core.client.controlador.comando.EModo;
import br.com.opensig.core.shared.modelo.EArquivo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.MessageBoxConfig;

/**
 * Classe abstrata que define as opcoes padroes de exportacao.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public abstract class ComandoExportar extends ComandoAcao {

	/**
	 * O tipo do arquivo.
	 */
	protected EArquivo tipo;
	/**
	 * O modo de execucao.
	 */
	protected EModo modo;
	/**
	 * A funcao de retorno da resposta.
	 */
	protected AsyncCallback<String> asyncCallback;

	/**
	 * Construtor padrao que define o arquivo como HTML e o modo como LISTAGEM.
	 */
	public ComandoExportar() {
		this(EArquivo.HTML, EModo.LISTAGEM);
	}

	/**
	 * Construtor padrao que permite setar as opcoes de tipo e modo.
	 * 
	 * @param tipo
	 *            define o tipo do arquivo exportado.
	 * @param modo
	 *            define o modo usado pelo comando para exportar.
	 */
	public ComandoExportar(EArquivo tipo, EModo modo) {
		this.tipo = tipo;
		this.modo = modo;

		asyncCallback = new AsyncCallback<String>() {

			public void onSuccess(String arg0) {
				LISTA.getPanel().getEl().unmask();
				UtilClient.exportar(GWT.getHostPageBaseURL() + "CoreService?id=" + arg0);
				if (comando != null) {
					contexto.put("nfe", arg0);
					comando.execute(contexto);
				}
			}

			public void onFailure(Throwable arg0) {
				LISTA.getPanel().getEl().unmask();
				MessageBox.alert(OpenSigCore.i18n.txtExportar(), OpenSigCore.i18n.errExportar());
			}
		};

	}

	/**
	 * Metodo de execucao para as exportacoes apos validao de permissao.
	 */
	protected abstract void execute();

	@Override
	public void execute(Map contexto) {
		super.execute(contexto);

		if (contexto.get("acao") != null) {
			modo = (EModo) contexto.get("acao");
		}

		if (modo == EModo.LISTAGEM) {
			final int limite = UtilClient.CONF.get("listagem.registro") != null ? Integer.valueOf(UtilClient.CONF.get("listagem.registro")) : 500;
			NameValuePair[] botoes = new NameValuePair[] { new NameValuePair("yes", OpenSigCore.i18n.txtIntervalo()), new NameValuePair("no", OpenSigCore.i18n.txtAtual()),
					new NameValuePair("cancel", OpenSigCore.i18n.txtTudo()) };

			MessageBoxConfig config = new MessageBoxConfig();
			config.setTitle(LISTA.getPanel().getTitle());
			config.setMsg(OpenSigCore.i18n.msgExportarPagina());
			config.setPrompt(true);
			config.setClosable(false);
			config.setButtons(botoes);
			config.setCallback(new PromptCallback() {
				public void execute(String btnID, String text) {
					int inicio = 0;
					int fim = 0;
					int tamanho = LISTA.getPaginador().getPageSize();

					try {
						if (btnID.equals("no")) {
							inicio = (LISTA.getPaginador().getCurrentPage() - 1) * tamanho;
							fim = tamanho;
						} else if (btnID.equals("yes") && text != null) {
							String[] intervalo = text.split("-");
							if (intervalo.length == 2) {
								int pi = Integer.valueOf(intervalo[0].trim());
								int pf = Integer.valueOf(intervalo[1].trim());
								inicio = (pi - 1) * tamanho;
								fim = (pf - pi) * tamanho + tamanho;
							}
						}

						if ((fim == 0 && LISTA.getPanel().getStore().getTotalCount() > limite) || fim > limite) {
							MessageBox.alert(OpenSigCore.i18n.txtExportar(), OpenSigCore.i18n.msgExportar(limite + ""));
						} else if (inicio > LISTA.getPanel().getStore().getTotalCount()) {
							MessageBox.alert(OpenSigCore.i18n.txtExportar(), OpenSigCore.i18n.msgRegistro());
						} else {
							LISTA.getPanel().getEl().mask(OpenSigCore.i18n.txtAguarde());
							LISTA.setExportacao(tipo, inicio, fim, asyncCallback);
						}
					} catch (Exception e) {
						MessageBox.alert(OpenSigCore.i18n.txtExportar(), OpenSigCore.i18n.msgRegistro());
					}
				}
			});

			if (LISTA.getPanel().getStore().getTotalCount() > LISTA.getPaginador().getPageSize()) {
				MessageBox.show(config);
			} else {
				LISTA.getPanel().getEl().mask(OpenSigCore.i18n.txtAguarde());
				LISTA.setExportacao(tipo, 0, 0, asyncCallback);
			}
		} else {
			FORM.setLista(LISTA);
			FORM.setExportacao(tipo, asyncCallback);
		}
	}

	// Gets e Seteres
	
	public EArquivo getTipo() {
		return tipo;
	}

	public void setTipo(EArquivo tipo) {
		this.tipo = tipo;
	}

	public EModo getModo() {
		return modo;
	}

	public void setModo(EModo modo) {
		this.modo = modo;
	}

	public AsyncCallback<String> getAsyncCallback() {
		return asyncCallback;
	}

	public void setAsyncCallback(AsyncCallback<String> asyncCallback) {
		this.asyncCallback = asyncCallback;
	}
}
