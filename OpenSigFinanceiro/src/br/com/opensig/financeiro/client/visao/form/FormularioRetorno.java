package br.com.opensig.financeiro.client.visao.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.AComando;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvar;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvarFinal;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.financeiro.client.servico.FinanceiroProxy;
import br.com.opensig.financeiro.client.visao.lista.ListagemBoletos;
import br.com.opensig.financeiro.client.visao.lista.ListagemFinanciados;
import br.com.opensig.financeiro.shared.modelo.FinConta;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.financeiro.shared.modelo.FinRetorno;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.util.JavaScriptObjectHelper;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtextux.client.widgets.grid.plugins.SummaryColumnConfig;
import com.gwtextux.client.widgets.upload.UploadDialog;
import com.gwtextux.client.widgets.upload.UploadDialogListenerAdapter;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioRetorno extends AFormulario<FinRetorno> {

	private String arquivo;
	private Hidden hdnCod;
	private ToolbarButton btnArquivo;
	private TextField txtArquivo;
	private ComboBox cmbConta;
	private ListagemBoletos boletos;
	private List<FinRecebimento> financeiros;

	public FormularioRetorno(SisFuncao funcao) {
		super(new FinRetorno(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		hdnCod = new Hidden("finRetornoId", "0");
		add(hdnCod);

		// campo
		txtArquivo = new TextField(OpenSigCore.i18n.txtArquivo(), "finRetornoArquivo", 200);
		txtArquivo.setAllowBlank(false);
		txtArquivo.setReadOnly(true);

		// botao
		btnArquivo = new ToolbarButton(OpenSigCore.i18n.txtArquivo());
		btnArquivo.setIconCls("icon-analisar");
		btnArquivo.setTooltip(OpenSigCore.i18n.msgAnalisar());
		btnArquivo.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				abrirUpload();
			}
		});

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(getConta(), 240);
		linha1.addToRow(txtArquivo, 230);
		add(linha1);

		// lista
		boletos = new ListagemBoletos();
		add(boletos);
	}
	
	@Override
	public IComando AntesDaAcao(IComando comando) {
		// salavando
		if (comando instanceof ComandoSalvar) {
			comando = new AComando(new ComandoSalvarFinal()) {
				public void execute(Map contexto) {
					super.execute(contexto);
					FinanceiroProxy proxy = new FinanceiroProxy();
					proxy.salvarRetorno(classe, financeiros, ASYNC);
				}
			};
		}
		
		return comando;
	}

	public boolean setDados() {
		financeiros = new ArrayList<FinRecebimento>();
		boolean retorno = boletos.validar(financeiros);

		if (retorno) {
			classe.setFinRetornoQuantidade(financeiros.size());
			classe.setEmpEmpresa(new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
			classe.setFinConta(new FinConta(Integer.valueOf(cmbConta.getValue())));
			classe.setFinRetornoCadastro(UtilClient.DATA);
			classe.setFinRetornoArquivo(arquivo);
		} else {
			new ToastWindow(OpenSigCore.i18n.txtListagem(), OpenSigCore.i18n.errLista()).show();
		}

		return retorno;
	}

	public void limparDados() {
		getForm().reset();
		boletos.getStore().removeAll();
	}

	public void mostrarDados() {
		if (cmbConta.getStore().getRecords().length == 0) {
			cmbConta.getStore().load();
		} else {
			mostrar();
		}
	}

	private void mostrar() {
		MessageBox.hide();
		Record rec = lista.getPanel().getSelectionModel().getSelected();
		if (rec != null) {
			CoreProxy<FinRetorno> proxy = new CoreProxy<FinRetorno>(classe);
			proxy.selecionar(rec.getAsInteger("finRetornoId"), new AsyncCallback<FinRetorno>() {
				public void onSuccess(FinRetorno result) {
					arquivo = result.getFinRetornoArquivo();
					analisar();
				}

				public void onFailure(Throwable caught) {
					arquivo = null;
					boletos.getStore().removeAll();
				}
			});
		} else {
			if (cmbConta.getStore().getRecords().length == 1) {
				cmbConta.setValue(cmbConta.getStore().getRecordAt(0).getAsString("finContaId"));
			}
		}
		cmbConta.focus();
	}

	private ComboBox getConta() {
		FieldDef[] fdConta = new FieldDef[] { new IntegerFieldDef("finContaId"), new IntegerFieldDef("empEmpresa.empEmpresaId"), new StringFieldDef("empEmpresa.empEntidade.empEntidadeNome1"),
				new IntegerFieldDef("finBanco.finBancoId"), new StringFieldDef("finBanco.finBancoDescricao"), new StringFieldDef("finContaNome") };
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		CoreProxy<FinConta> proxy =	new CoreProxy<FinConta>(new FinConta(), fo);
		final Store storeConta = new Store(proxy, new ArrayReader(new RecordDef(fdConta)), true);
		storeConta.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				mostrar();
				getTopToolbar().addSeparator();
				getTopToolbar().addButton(btnArquivo);
			}

			public void onLoadException(Throwable error) {
				MessageBox.confirm(OpenSigCore.i18n.txtEmbalagem(), OpenSigCore.i18n.msgRecarregar(), new ConfirmCallback() {
					public void execute(String btnID) {
						if (btnID.equalsIgnoreCase("yes")) {
							storeConta.load();
						}
					}
				});
			}
		});

		cmbConta = new ComboBox(OpenSigCore.i18n.txtConta(), "finConta.finContaId", 200);
		cmbConta.setAllowBlank(false);
		cmbConta.setStore(storeConta);
		cmbConta.setListWidth(200);
		cmbConta.setTriggerAction(ComboBox.ALL);
		cmbConta.setMode(ComboBox.LOCAL);
		cmbConta.setDisplayField("finContaNome");
		cmbConta.setValueField("finContaId");
		cmbConta.setForceSelection(true);
		cmbConta.setEditable(false);

		return cmbConta;
	}

	public void gerarListas() {
		// selecionado e filtro
		Record rec = lista.getPanel().getSelectionModel().getSelected();
		String[] ids = rec.getAsString("finRetornoBoletos").split(" ");
		GrupoFiltro gf = new GrupoFiltro();

		for (String bol : ids) {
			FiltroNumero fn = new FiltroNumero("finRecebimentoId", ECompara.IGUAL, bol);
			gf.add(fn, EJuncao.OU);
		}

		// boletos
		ListagemFinanciados<FinRecebimento> fin = new ListagemFinanciados<FinRecebimento>(new FinRecebimento(), false);
		Integer[] tamanhos = new Integer[fin.getModelos().getColumnCount()];
		String[] rotulos = new String[fin.getModelos().getColumnCount()];
		EBusca[] agrupamentos = new EBusca[fin.getModelos().getColumnCount()];

		for (int i = 0; i < fin.getModelos().getColumnCount(); i++) {
			if (!fin.getModelos().isHidden(i)) {
				tamanhos[i] = fin.getModelos().getColumnWidth(i);
				rotulos[i] = fin.getModelos().getColumnHeader(i);
				
				if (fin.getModelos().getColumnConfigs()[i] instanceof SummaryColumnConfig) {
					SummaryColumnConfig col = (SummaryColumnConfig) fin.getModelos().getColumnConfigs()[i];
					String tp = col.getSummaryType().equals("average") ? "AVG" : col.getSummaryType().toUpperCase();
					agrupamentos[i] = EBusca.getBusca(tp);
				}
			}
		}

		tamanhos[5] = tamanhos[4];
		tamanhos[4] = null;
		rotulos[5] = rotulos[4];
		rotulos[4] = null;
		agrupamentos[5] = agrupamentos[4];
		agrupamentos[4] = null;

		ExportacaoListagem<FinRecebimento> recebimentos = new ExportacaoListagem<FinRecebimento>();
		recebimentos.setUnidade(fin.getClasse());
		recebimentos.setFiltro(gf);
		recebimentos.setTamanhos(tamanhos);
		recebimentos.setRotulos(rotulos);
		recebimentos.setAgrupamentos(agrupamentos);
		recebimentos.setNome(boletos.getTitle());

		// sub listagens
		expLista = new ArrayList<ExportacaoListagem>();
		expLista.add(recebimentos);
	}

	private void analisar() {
		getEl().mask(OpenSigCore.i18n.txtAguarde());
		classe.setFinRetornoArquivo(arquivo);

		FinanceiroProxy proxy = new FinanceiroProxy();
		proxy.retorno(classe, new AsyncCallback<String[][]>() {
			public void onFailure(Throwable caught) {
				boletos.getStore().removeAll();
				getEl().unmask();
				MessageBox.alert(OpenSigCore.i18n.txtAnalisar(), OpenSigCore.i18n.msgAnalisar());
			}

			public void onSuccess(String[][] result) {
				if (result != null && result.length > 0) {
					MemoryProxy dados = new MemoryProxy(result);
					boletos.getStore().setDataProxy(dados);
					boletos.getStore().load();
					getEl().unmask();
				} else {
					onFailure(null);
				}
			}
		});
	}

	private void abrirUpload() {
		UploadDialog uplArquivo = new UploadDialog();
		uplArquivo.setModal(true);
		uplArquivo.setUrl(GWT.getHostPageBaseURL() + "UploadService");
		uplArquivo.setAllowCloseOnUpload(false);
		uplArquivo.setPermittedExtensions(new String[] { "ret" });
		uplArquivo.addListener(new UploadDialogListenerAdapter() {
			public void onUploadSuccess(UploadDialog source, String filename, JavaScriptObject data) {
				arquivo = JavaScriptObjectHelper.getAttribute(data, "dados");
				txtArquivo.setValue(filename);
				analisar();
				source.close();
			}

			public void onUploadError(UploadDialog source, String filename, JavaScriptObject data) {
				arquivo = null;
				txtArquivo.setValue(null);
				boletos.getStore().removeAll();
				new ToastWindow(OpenSigCore.i18n.txtErro(), JavaScriptObjectHelper.getAttribute(data, "dados")).show();
			}

			public void onUploadFailed(UploadDialog source, String filename) {
				arquivo = null;
				txtArquivo.setValue(null);
				boletos.getStore().removeAll();
			}

			public boolean onBeforeAdd(UploadDialog source, String filename) {
				return source.getQueuedCount() == 0;
			}
		});
		uplArquivo.show();
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public Hidden getHdnCod() {
		return hdnCod;
	}

	public void setHdnCod(Hidden hdnCod) {
		this.hdnCod = hdnCod;
	}

	public ToolbarButton getBtnArquivo() {
		return btnArquivo;
	}

	public void setBtnArquivo(ToolbarButton btnArquivo) {
		this.btnArquivo = btnArquivo;
	}

	public TextField getTxtArquivo() {
		return txtArquivo;
	}

	public void setTxtArquivo(TextField txtArquivo) {
		this.txtArquivo = txtArquivo;
	}

	public ListagemBoletos getBoletos() {
		return boletos;
	}

	public void setBoletos(ListagemBoletos boletos) {
		this.boletos = boletos;
	}

	public List<FinRecebimento> getFinanceiros() {
		return financeiros;
	}

	public void setFinanceiros(List<FinRecebimento> financeiros) {
		this.financeiros = financeiros;
	}

	public ComboBox getCmbConta() {
		return cmbConta;
	}

	public void setCmbConta(ComboBox cmbConta) {
		this.cmbConta = cmbConta;
	}
}
