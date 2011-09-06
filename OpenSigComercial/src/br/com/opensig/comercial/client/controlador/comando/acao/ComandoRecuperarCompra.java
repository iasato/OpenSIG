package br.com.opensig.comercial.client.controlador.comando.acao;

import java.util.List;
import java.util.Map;

import br.com.opensig.comercial.client.servico.ComercialProxy;
import br.com.opensig.comercial.client.visao.lista.ListagemValidarProduto;
import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.comando.importar.ComandoImportar;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.shared.modelo.EArquivo;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.produto.client.OpenSigProduto;
import br.com.opensig.produto.client.controlador.comando.ComandoPesquisa;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.widgets.MessageBox;
import com.gwtextux.client.widgets.window.ToastWindow;

public class ComandoRecuperarCompra extends ComandoImportar {

	public ComandoRecuperarCompra() {
		super(EArquivo.XML);
		assincrono = new AsyncCallback<String>() {
			public void onSuccess(String result) {
				getUplArquivo().close();
				analisar(result);
			}

			public void onFailure(Throwable caught) {
				new ToastWindow(OpenSigCore.i18n.txtImportar(), caught.getMessage()).show();
			}
		};
	}

	public void execute(final Map contexto) {
		super.execute(contexto, new AsyncCallback() {
			public void onSuccess(Object result) {
				execute();
			}

			public void onFailure(Throwable caught) {
			}
		});
	}

	protected void execute() {
		super.execute(contexto);
	}
	
	private void analisar(final String arquivo) {
		MessageBox.wait(OpenSigCore.i18n.txtAguarde(), OpenSigCore.i18n.txtAnalisar());
		ComercialProxy comercial = new ComercialProxy();
		comercial.analisarNfe(arquivo, new AsyncCallback<ComCompra>() {

			public void onSuccess(ComCompra result) {
				MessageBox.hide();
				List<SisFuncao> funcoes = Ponte.getLogin().getFuncoes(OpenSigProduto.class.getName());
				for (SisFuncao funcao : funcoes) {
					if (funcao.getSisFuncaoClasse().equalsIgnoreCase(ComandoPesquisa.class.getName())) {
						new ListagemValidarProduto(LISTA, arquivo, result, funcao);
						break;
					}
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(OpenSigCore.i18n.txtNfe(), caught.toString());
				new ToastWindow(OpenSigCore.i18n.txtImportar(), OpenSigCore.i18n.errImportar()).show();
			}
		});
	}
}
