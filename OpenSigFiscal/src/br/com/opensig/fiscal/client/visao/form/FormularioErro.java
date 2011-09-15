package br.com.opensig.fiscal.client.visao.form;

import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.AComando;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvar;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.client.servico.FiscalProxy;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioErro extends AFormulario<FisNotaSaida> {

	private TextArea txtErro;
	private TextArea txtXml;
	private ToolbarButton btnSituacao;
	private Window janela;

	public FormularioErro(FisNotaSaida classe, SisFuncao funcao, Window janela) {
		super(classe, funcao);
		this.janela = janela;
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		btnSalvar.setMenu(new Menu());
		enable();
		setHeader(false);

		txtErro = new TextArea(OpenSigCore.i18n.txtErro(), "fisNotaSaidaErro");
		txtErro.setReadOnly(true);
		txtErro.setWidth("98%");
		txtErro.setHeight(230);
		add(txtErro);

		txtXml = new TextArea(OpenSigCore.i18n.txtNfe(), "fisNotaSaidaXml");
		txtXml.setAllowBlank(false);
		txtXml.setWidth("98%");
		txtXml.setHeight(230);
		add(txtXml);

		btnSituacao = new ToolbarButton(OpenSigCore.i18n.txtAnalisar());
		btnSituacao.setIconCls("icon-analisar");
		btnSituacao.setTooltip(OpenSigCore.i18n.msgPedidoSituacao());
		btnSituacao.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				situacao();
			}
		});

		addListener(new FormPanelListenerAdapter() {
			public void onRender(Component component) {
				getTopToolbar().addSeparator();
				getTopToolbar().addButton(btnSituacao);
			}
		});
	}

	@Override
	public void DepoisDaAcao(IComando comando) {
		janela.close();
	}

	public boolean setDados() {
		if (classe.getFisNotaSaidaProtocolo().equals("")) {
			classe.setFisNotaSaidaXml(txtXml.getValueAsString());
			if (classe.getFisNotaSaidaChave().length() == 44) {
				classe.setFisNotaStatus(new FisNotaStatus(ENotaStatus.AUTORIZANDO));
			} else {
				classe.setFisNotaStatus(new FisNotaStatus(ENotaStatus.INUTILIZANDO));
			}
		} else {
			classe.setFisNotaSaidaXmlCancelado(txtXml.getValueAsString());
			classe.setFisNotaStatus(new FisNotaStatus(ENotaStatus.CANCELANDO));
		}
		classe.setFisNotaSaidaErro("");
		classe.setEmpEmpresa(new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		return true;
	}

	public void mostrarDados() {
		txtErro.setValue(classe.getFisNotaSaidaErro());
		if (classe.getFisNotaSaidaProtocolo().equals("")) {
			txtXml.setValue(classe.getFisNotaSaidaXml());
		} else {
			txtXml.setValue(classe.getFisNotaSaidaXmlCancelado());
		}
	}

	public void limparDados() {
	}

	public void gerarListas() {
	}

	@Override
	public IComando AntesDaAcao(IComando comando) {
		if (comando instanceof ComandoSalvar) {
			return new AComando() {
				public void execute(Map contexto) {
					salvar();
				}
			};
		} else {
			return comando;
		}
	}

	private void situacao() {
		MessageBox.wait(OpenSigCore.i18n.txtAguarde(), OpenSigCore.i18n.txtSituacao());
		setDados();
		FiscalProxy<FisNotaSaida> proxy = new FiscalProxy<FisNotaSaida>();
		proxy.analisarNFe(classe, new AsyncCallback<Map<String, String>>() {

			public void onSuccess(Map<String, String> result) {
				MessageBox.hide();
				janela.close();
				getLista().getPanel().getStore().reload();
				new ToastWindow(OpenSigCore.i18n.txtSituacao(), OpenSigCore.i18n.msgSalvarOK()).show();
			}

			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(OpenSigCore.i18n.txtSituacao(), caught.getMessage());
			}
		});
	}

	private void salvar() {
		MessageBox.wait(OpenSigCore.i18n.txtAguarde(), OpenSigCore.i18n.txtSalvar());
		setDados();
		FiscalProxy<FisNotaSaida> proxy = new FiscalProxy<FisNotaSaida>();

		proxy.salvarSaida(txtXml.getValueAsString(), classe.getFisNotaStatus(), classe.getEmpEmpresa(), new AsyncCallback<Map<String, String>>() {
			public void onSuccess(Map<String, String> result) {
				MessageBox.hide();
				ENotaStatus status = ENotaStatus.valueOf(result.get("status"));

				if (status == ENotaStatus.ERRO) {
					MessageBox.alert(OpenSigCore.i18n.txtSalvar(), result.get("msg"));
				} else {
					if (status == ENotaStatus.AUTORIZANDO) {
						Timer tempo = new Timer() {
							public void run() {
								getLista().getPanel().getStore().reload();
								janela.close();
							}
						};
						int espera = Integer.valueOf(UtilClient.CONF.get("nfe.tempo_retorno"));
						tempo.schedule(1000 * espera);
					} else {
						getLista().getPanel().getStore().reload();
						janela.close();
					}
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.hide();
				MessageBox.alert(OpenSigCore.i18n.txtSalvar(), caught.getMessage());
			}
		});
	}
}
