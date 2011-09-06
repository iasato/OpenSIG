package br.com.opensig.fiscal.client.controlador.comando.acao;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.comando.ComandoAcao;
import br.com.opensig.core.shared.modelo.Dados;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.Window;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.gwtextux.client.widgets.upload.UploadDialogListenerAdapter;
import com.gwtextux.client.widgets.window.ToastWindow;

public abstract class ComandoRecuperar<E extends Dados> extends ComandoAcao<E> {

	protected int ok;
	protected int erro;
	protected String classe;
	protected String erros;

	public ComandoRecuperar(String classe) {
		this.classe = classe;
	}

	/**
	 * @see ComandoAcao#execute(Map)
	 */
	public void execute(final Map contexto) {
		super.execute(contexto, new AsyncCallback() {
			public void onSuccess(Object result) {
				abrirUpload();
			}

			public void onFailure(Throwable caught) {
			}
		});
	}

	private void abrirUpload() {
		UploadDialog uplArquivo = new UploadDialog();
		uplArquivo.setModal(true);
		uplArquivo.setWidth(700);
		uplArquivo.setUrl(GWT.getHostPageBaseURL() + "UploadNfe");
		uplArquivo.setAllowCloseOnUpload(false);
		uplArquivo.setPermittedExtensions(new String[] { "xml", "zip" });
		uplArquivo.setBaseParams(new UrlParam[] { new UrlParam("acao", "salvar"), new UrlParam("local", "sessao"), new UrlParam("classe", classe) });
		uplArquivo.addListener(new UploadDialogListenerAdapter() {
			public void onUploadStart(UploadDialog source) {
				ok = 0;
				erro = 0;
				erros = "";
			}

			public void onUploadSuccess(UploadDialog source, String filename, JavaScriptObject data) {
				ok++;
			}

			public void onUploadFailed(UploadDialog source, String filename) {
				erro++;
			}

			public void onUploadError(UploadDialog source, String filename, JavaScriptObject data) {
				erro++;
				erros += JavaScriptObjectHelper.getAttribute(data, "dados") + "-----------\n";
			}

			public void onUploadComplete(UploadDialog source) {
				if (erro > 0) {
					MessageBox.confirm(OpenSigCore.i18n.txtErro(), OpenSigCore.i18n.txtAnalisar() + "?", new ConfirmCallback() {
						public void execute(String btnID) {
							if (btnID.equals("yes")) {
								TextArea ta = new TextArea();
								ta.setReadOnly(true);
								ta.setValue(erros);

								Window wnd = new Window(OpenSigCore.i18n.txtErro(), 300, 300, false, true);
								wnd.setLayout(new FitLayout());
								wnd.add(ta);
								wnd.doLayout();
								wnd.show();
							}
						}
					});
				} else {
					LISTA.getPanel().getStore().reload();
					new ToastWindow(OpenSigCore.i18n.txtNfe(), OpenSigCore.i18n.txtOk() + " = " + ok + " - " + OpenSigCore.i18n.txtErro() + " = " + erro).show();
				}
			}

		});
		uplArquivo.show();
	}
}
