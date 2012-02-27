package br.com.opensig.comercial.client.visao.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.opensig.comercial.client.servico.ComercialProxy;
import br.com.opensig.comercial.client.visao.lista.ListagemEcfVendaProdutos;
import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvar;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvarFinal;
import br.com.opensig.core.client.controlador.comando.lista.ComandoAdicionar;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.core.client.visao.PermitirSistema;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.core.shared.modelo.ExpListagem;
import br.com.opensig.core.shared.modelo.ExpMeta;
import br.com.opensig.core.shared.modelo.ILogin;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpCliente;
import br.com.opensig.permissao.shared.modelo.SisUsuario;
import br.com.opensig.produto.client.controlador.comando.ComandoPesquisa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.event.EditorGridListenerAdapter;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtextux.client.widgets.grid.plugins.SummaryColumnConfig;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioEcfVenda extends AFormulario<ComEcfVenda> {

	private Hidden hdnCod;
	private Hidden hdnCliente;
	private Hidden hdnUsuario;
	private Hidden hdnEcf;
	private ComboBox cmbEcf;
	private NumberField txtCoo;
	private NumberField txtBruto;
	private NumberField txtDesc;
	private NumberField txtLiquido;
	private TextArea txtObservacao;
	private Label lblRegistros;
	private ListagemEcfVendaProdutos gridProdutos;
	private List<ComEcfVendaProduto> produtos;

	private boolean autorizado;
	private boolean autosave;
	private boolean importada;
	private double max;
	private AsyncCallback asyncSalvar;
	private AsyncCallback<ILogin> asyncLogin;
	private ComandoPesquisa cmdPesquisa;
	private Date data;

	public FormularioEcfVenda(SisFuncao funcao) {
		super(new ComEcfVenda(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		hdnCod = new Hidden("comEcfVendaId", "0");
		add(hdnCod);
		hdnEcf = new Hidden("comEcf.comEcfId", "0");
		add(hdnEcf);
		hdnUsuario = new Hidden("sisUsuario.sisUsuarioId", "0");
		add(hdnUsuario);
		hdnCliente = new Hidden("empCliente.empClienteId", "0");
		add(hdnCliente);

		txtCoo = new NumberField(OpenSigCore.i18n.txtCoo(), "comEcfVendaCoo", 60);
		txtCoo.setAllowBlank(false);
		txtCoo.setAllowDecimals(false);
		txtCoo.setAllowNegative(false);
		txtCoo.setMaxLength(6);

		txtBruto = new NumberField(OpenSigCore.i18n.txtBruto(), "comEcfVendaBruto", 100, 0);
		txtBruto.setReadOnly(true);

		txtDesc = new NumberField(OpenSigCore.i18n.txtDesconto() + "%", "comEcfVendaDesconto", 50, 0);
		txtDesc.setReadOnly(true);

		txtLiquido = new NumberField(OpenSigCore.i18n.txtLiquido(), "comEcfVendaLiquido", 100, 0);
		txtLiquido.setReadOnly(true);

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(getEcf(), 180);
		linha1.addToRow(txtCoo, 80);
		linha1.addToRow(txtBruto, 110);
		linha1.addToRow(txtDesc, 60);
		linha1.addToRow(txtLiquido, 110);
		add(linha1);
		lblRegistros = new Label();

		final AsyncCallback<Record> asyncPesquisa = new AsyncCallback<Record>() {

			public void onFailure(Throwable arg0) {
				// nada
			}

			public void onSuccess(Record result) {
				if (importada) {
					Record reg = gridProdutos.getSelectionModel().getSelected();
					reg.set("prodProdutoId", result.getAsInteger("prodProdutoId"));
					reg.set("prodProduto.prodProdutoBarra", result.getAsString("prodProdutoBarra"));
					reg.set("prodProduto.prodProdutoDescricao", result.getAsString("prodProdutoDescricao"));
					reg.set("prodProduto.prodProdutoReferencia", result.getAsString("prodProdutoReferencia"));
				} else {

					gridProdutos.stopEditing();
					int pos;

					for (pos = 0; pos < gridProdutos.getStore().getCount(); pos++) {
						if (gridProdutos.getStore().getAt(pos).getAsInteger("prodProdutoId") == result.getAsInteger("prodProdutoId")) {
							break;
						}
					}

					if (pos == gridProdutos.getStore().getCount()) {
						double bruto = result.getAsDouble("prodProdutoPreco");

						Record reg = gridProdutos.getCampos().createRecord(new Object[gridProdutos.getCampos().getFields().length]);
						reg.set("comEcfVendaProdutoId", 0);
						reg.set("prodProdutoId", result.getAsInteger("prodProdutoId"));
						reg.set("prodProduto.prodProdutoBarra", result.getAsString("prodProdutoBarra"));
						reg.set("prodProduto.prodProdutoDescricao", result.getAsString("prodProdutoDescricao"));
						reg.set("prodProduto.prodProdutoReferencia", result.getAsString("prodProdutoReferencia"));
						reg.set("comEcfVendaProdutoQuantidade", 0);
						reg.set("prodEmbalagem.prodEmbalagemId", result.getAsInteger("prodEmbalagem.prodEmbalagemId"));
						reg.set("prodEmbalagem.prodEmbalagemNome", result.getAsString("prodEmbalagem.prodEmbalagemNome"));
						reg.set("comEcfVendaProdutoBruto", bruto);
						reg.set("comEcfVendaProdutoDesconto", 0);
						reg.set("comEcfVendaProdutoLiquido", bruto);
						reg.set("comEcfVendaProdutoTotal", 0);
						gridProdutos.getStore().add(reg);
					} else {
						new ToastWindow(getTitle(), OpenSigCore.i18n.errExiste()).show();
					}

					for (int col = 0; col < gridProdutos.getModelos().getColumnCount(); col++) {
						if (gridProdutos.getModelos().getDataIndex(col).equals("comEcfVendaProdutoQuantidade")) {
							gridProdutos.startEditing(pos, col);
							break;
						}
					}

					totalizar("");
				}
			}
		};
		cmdPesquisa = new ComandoPesquisa(asyncPesquisa);

		gridProdutos = new ListagemEcfVendaProdutos(true) {
			public IComando AntesDoComando(IComando comando) {
				if (comando instanceof ComandoAdicionar) {
					int registros = gridProdutos.getStore().getRecords().length;
					int minimo = UtilClient.CONF.get("listagem.autosave") != null ? Integer.valueOf(UtilClient.CONF.get("listagem.autosave")) : 0;

					if (registros > 0 && minimo > 0 && registros % minimo == 0 && getForm().isValid() && setDados()) {
						autoSave();
						return null;
					} else {
						return cmdPesquisa;
					}
				} else {
					return comando;
				}
			};
		};
		gridProdutos.getContexto().put("dados", funcao);
		gridProdutos.getTopToolbar().addText(OpenSigCore.i18n.txtRegistro() + ": ");
		gridProdutos.getTopToolbar().addElement(lblRegistros.getElement());
		gridProdutos.getTopToolbar().addSpacer();
		add(gridProdutos);

		txtObservacao = new TextArea(OpenSigCore.i18n.txtObservacao(), "comEcfVendaObservacao");
		txtObservacao.setMaxLength(255);
		txtObservacao.setWidth("95%");
		add(txtObservacao);

		gridProdutos.addEditorGridListener(new EditorGridListenerAdapter() {
			public void onAfterEdit(GridPanel grid, Record record, String field, Object newValue, Object oldValue, int rowIndex, int colIndex) {
				totalizar("");
			}
		});

		gridProdutos.addGridRowListener(new GridRowListenerAdapter() {
			public void onRowContextMenu(GridPanel grid, int rowIndex, EventObject e) {
				cmdPesquisa.execute(gridProdutos.getContexto());
			}
		});

		gridProdutos.addEditorGridListener(new EditorGridListenerAdapter() {
			public boolean doBeforeEdit(GridPanel grid, Record record, String field, Object value, int rowIndex, int colIndex) {
				return importada == false;
			}
		});

		gridProdutos.getStore().addStoreListener(new StoreListenerAdapter() {
			public void onRemove(Store store, Record record, int index) {
				totalizar("");
			}

			public void onLoad(Store store, Record[] records) {
				totalizar("");
				importada = false;
				for (Record rec : records) {
					if (hdnCod.getValueAsString().equals("0")) {
						rec.set(rec.getFields()[0], 0);
					}
					importada |= rec.getAsInteger("prodProdutoId") == 0;
				}

				if (importada) {
					gridProdutos.getTopToolbar().hide();
				} else {
					gridProdutos.getTopToolbar().show();
				}
			}
		});

		ToolTip tip = new ToolTip(OpenSigCore.i18n.msgCompraProduto());
		tip.applyTo(gridProdutos);

		asyncSalvar = new AsyncCallback<ComEcfVenda>() {
			public void onFailure(Throwable caught) {
				contexto.put("erro", caught);
				ComandoSalvarFinal fim = new ComandoSalvarFinal();
				if (autosave) {
					fim.setProximo(cmdPesquisa);
				}
				fim.execute(contexto);
				autosave = false;
			};

			public void onSuccess(ComEcfVenda result) {
				contexto.put("resultado", result);
				hdnCod.setValue(result.getComEcfVendaId() + "");
				ComandoSalvarFinal fim = new ComandoSalvarFinal();
				if (autosave) {
					fim.setProximo(cmdPesquisa);
				}
				fim.execute(contexto);
				autosave = false;
			};
		};

		asyncLogin = new AsyncCallback<ILogin>() {
			public void onSuccess(ILogin result) {
				if (result != null && result.getDesconto() > max) {
					autorizado = true;
					ComercialProxy proxy = new ComercialProxy();
					proxy.salvarEcfVenda(classe, asyncSalvar);
				} else {
					autorizado = false;
					MessageBox.hide();
					MessageBox.alert(OpenSigCore.i18n.txtAcesso(), OpenSigCore.i18n.txtAcessoNegado());
				}
			}

			public void onFailure(Throwable caught) {
				autorizado = false;
				MessageBox.hide();
				MessageBox.alert(OpenSigCore.i18n.txtSalvar(), OpenSigCore.i18n.errSalvar());
			}
		};
	}

	private void autoSave() {
		autosave = true;
		AntesDaAcao(new ComandoSalvar());
	}

	@Override
	public IComando AntesDaAcao(IComando comando) {
		// salavando
		if (comando instanceof ComandoSalvar) {
			MessageBox.wait(OpenSigCore.i18n.txtAguarde(), OpenSigCore.i18n.txtSalvar());
			max = 0.00;
			for (Record rec : gridProdutos.getStore().getRecords()) {
				max = rec.getAsDouble("comEcfVendaProdutoDesconto") > max ? rec.getAsDouble("comEcfVendaProdutoDesconto") : max;
			}

			if (max <= Ponte.getLogin().getDesconto() || autorizado) {
				ComercialProxy proxy = new ComercialProxy();
				proxy.salvarEcfVenda(classe, asyncSalvar);
			} else {
				PermitirSistema permitir = (PermitirSistema) GWT.create(PermitirSistema.class);
				permitir.setInfo(OpenSigCore.i18n.msgPermissaoDesconto(Ponte.getLogin().getDesconto() + ""));
				permitir.executar(asyncLogin);
			}
			comando = null;
		}

		return comando;
	}

	/*
	 * @see br.com.sig.core.client.visao.lista.IFormulario#setDados()
	 */
	public boolean setDados() {
		boolean retorno = true;
		produtos = new ArrayList<ComEcfVendaProduto>();

		if (!gridProdutos.validar(produtos)) {
			retorno = false;
			new ToastWindow(OpenSigCore.i18n.txtListagem(), OpenSigCore.i18n.errLista()).show();
		}

		classe.setComEcfVendaProdutos(produtos);
		classe.setComEcfVendaId(Integer.valueOf(hdnCod.getValueAsString()));
		classe.setComEcf(new ComEcf(Integer.valueOf(hdnEcf.getValueAsString())));

		if (hdnCliente.getValueAsString().equals("0")) {
			EmpCliente cliente = new EmpCliente(Integer.valueOf(UtilClient.CONF.get("cliente.padrao")));
			classe.setEmpCliente(cliente);
		} else {
			EmpCliente cliente = new EmpCliente(Integer.valueOf(hdnCliente.getValueAsString()));
			classe.setEmpCliente(cliente);
		}

		if (hdnUsuario.getValueAsString().equals("0")) {
			classe.setSisUsuario(new SisUsuario(Ponte.getLogin().getId()));
		} else {
			classe.setSisUsuario(new SisUsuario(Integer.valueOf(hdnUsuario.getValueAsString())));
		}

		if (txtCoo.getValue() != null) {
			classe.setComEcfVendaCoo(txtCoo.getValue().intValue());
		}
		classe.setComEcfVendaData(data);
		classe.setComEcfVendaBruto(txtBruto.getValue().doubleValue());
		classe.setComEcfVendaDesconto(txtDesc.getValue().doubleValue());
		classe.setComEcfVendaLiquido(txtLiquido.getValue().doubleValue());
		classe.setComEcfVendaObservacao(txtObservacao.getValueAsString() == null ? "" : txtObservacao.getValueAsString());

		return retorno;
	}

	public void limparDados() {
		getForm().reset();
		FiltroNumero fn = new FiltroNumero("comEcfVendaProdutoId", ECompara.IGUAL, 0);
		gridProdutos.getProxy().setFiltroPadrao(fn);
		gridProdutos.getStore().removeAll();
		lblRegistros.setText("0");
		autorizado = false;
	}

	public void mostrarDados() {
		if (cmbEcf.getStore().getRecords().length == 0) {
			cmbEcf.getStore().load();
		} else {
			mostrar();
		}
	}

	private void mostrar() {
		MessageBox.hide();
		Record rec = lista.getPanel().getSelectionModel().getSelected();
		if (rec != null) {
			data = rec.getAsDate("comEcfVendaData");
			getForm().loadRecord(rec);
			classe.setComEcfVendaId(Integer.valueOf(hdnCod.getValueAsString()));
			FiltroObjeto fo = new FiltroObjeto("comEcfVenda", ECompara.IGUAL, classe);
			gridProdutos.getProxy().setFiltroPadrao(fo);
			gridProdutos.getStore().reload();
		} else {
			data = new Date();
			gridProdutos.getTopToolbar().show();
		}
		cmbEcf.focus(true);

		if (duplicar) {
			hdnCod.setValue("0");
			hdnUsuario.setValue("0");
			classe.setComEcfVendaFechada(false);
			classe.setComEcfVendaCancelada(false);
			duplicar = false;
		}
	}

	private ComboBox getEcf() {
		FieldDef[] fdEcf = new FieldDef[] { new IntegerFieldDef("comEcfId"), new IntegerFieldDef("empEmpresa.empEmpresaId"), new StringFieldDef("empEmpresa.empEntidade.empEntidadeNome1"),
				new StringFieldDef("comEcfCodigo"), new StringFieldDef("comEcfModelo"), new StringFieldDef("comEcfSerie"), new IntegerFieldDef("comEcfCaixa") };
		CoreProxy<ComEcf> proxy = new CoreProxy<ComEcf>(new ComEcf());
		final Store storeEcf = new Store(proxy, new ArrayReader(new RecordDef(fdEcf)), true);
		storeEcf.addStoreListener(new StoreListenerAdapter() {
			public void onLoadException(Throwable error) {
				MessageBox.confirm(OpenSigCore.i18n.txtEcf(), OpenSigCore.i18n.msgRecarregar(), new ConfirmCallback() {
					public void execute(String btnID) {
						if (btnID.equalsIgnoreCase("yes")) {
							storeEcf.load();
						}
					}
				});
			}

			public void onLoad(Store store, Record[] records) {
				mostrar();
			}
		});

		cmbEcf = new ComboBox(OpenSigCore.i18n.txtEcf(), "comEcf.comEcfSerie", 150);
		cmbEcf.setListWidth(250);
		cmbEcf.setAllowBlank(false);
		cmbEcf.setStore(storeEcf);
		cmbEcf.setTriggerAction(ComboBox.ALL);
		cmbEcf.setMode(ComboBox.LOCAL);
		cmbEcf.setDisplayField("comEcfSerie");
		cmbEcf.setValueField("comEcfId");
		cmbEcf.setTpl("<div class=\"x-combo-list-item\"><b>{comEcfSerie}</b> - <i>" + OpenSigCore.i18n.txtCaixa() + "[{comEcfCaixa}]</i></div>");
		cmbEcf.setForceSelection(true);
		cmbEcf.setEditable(false);
		cmbEcf.addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {
				hdnEcf.setValue(comboBox.getValue());
			}

			public void onBlur(Field field) {
				if (cmbEcf.getRawValue().equals("")) {
					hdnEcf.setValue("0");
				}
			}
		});

		return cmbEcf;
	}

	public void gerarListas() {
		// produtos
		List<ExpMeta> metadados = new ArrayList<ExpMeta>();
		for (int i = 0; i < gridProdutos.getModelos().getColumnCount(); i++) {
			if (gridProdutos.getModelos().isHidden(i)) {
				metadados.add(null);
			} else {
				ExpMeta meta = new ExpMeta(gridProdutos.getModelos().getColumnHeader(i), gridProdutos.getModelos().getColumnWidth(i), null);
				if (gridProdutos.getModelos().getColumnConfigs()[i] instanceof SummaryColumnConfig) {
					SummaryColumnConfig col = (SummaryColumnConfig) gridProdutos.getModelos().getColumnConfigs()[i];
					String tp = col.getSummaryType().equals("average") ? "AVG" : col.getSummaryType().toUpperCase();
					meta.setGrupo(EBusca.getBusca(tp));
				}
				metadados.add(meta);
			}
		}

		SortState ordem = gridProdutos.getStore().getSortState();
		ComEcfVendaProduto venProd = new ComEcfVendaProduto();
		venProd.setCampoOrdem(ordem.getField());
		venProd.setOrdemDirecao(EDirecao.valueOf(ordem.getDirection().getDirection()));
		// filtro
		int id = UtilClient.getSelecionado(lista.getPanel());
		FiltroObjeto filtro = new FiltroObjeto("comEcfVenda", ECompara.IGUAL, new ComEcfVenda(id));

		ExpListagem<ComEcfVendaProduto> produtos = new ExpListagem<ComEcfVendaProduto>();
		produtos.setClasse(venProd);
		produtos.setMetadados(metadados);
		produtos.setNome(gridProdutos.getTitle());
		produtos.setFiltro(filtro);

		// sub listagens
		expLista = new ArrayList<ExpListagem>();
		expLista.add(produtos);
	}

	private void totalizar(String field) {
		double bruto = 0;
		double liquido = 0;

		lblRegistros.setText(gridProdutos.getStore().getRecords().length + "");
		for (Record rec : gridProdutos.getStore().getRecords()) {
			int qtd = rec.getAsInteger("comEcfVendaProdutoQuantidade");
			bruto += qtd * rec.getAsDouble("comEcfVendaProdutoBruto");
			liquido += qtd * rec.getAsDouble("comEcfVendaProdutoLiquido");
		}

		double desc = (bruto - liquido) / bruto * 100;
		txtBruto.setValue(bruto);
		txtDesc.setValue(desc);
		txtLiquido.setValue(liquido);
	}

	public Hidden getHdnCod() {
		return hdnCod;
	}

	public void setHdnCod(Hidden hdnCod) {
		this.hdnCod = hdnCod;
	}

	public Hidden getHdnCliente() {
		return hdnCliente;
	}

	public void setHdnCliente(Hidden hdnCliente) {
		this.hdnCliente = hdnCliente;
	}

	public Hidden getHdnUsuario() {
		return hdnUsuario;
	}

	public void setHdnUsuario(Hidden hdnUsuario) {
		this.hdnUsuario = hdnUsuario;
	}

	public TextArea getTxtObservacao() {
		return txtObservacao;
	}

	public void setTxtObservacao(TextArea txtObservacao) {
		this.txtObservacao = txtObservacao;
	}

	public Label getLblRegistros() {
		return lblRegistros;
	}

	public void setLblRegistros(Label lblRegistros) {
		this.lblRegistros = lblRegistros;
	}

	public ListagemEcfVendaProdutos getGridProdutos() {
		return gridProdutos;
	}

	public void setGridProdutos(ListagemEcfVendaProdutos gridProdutos) {
		this.gridProdutos = gridProdutos;
	}

	public List<ComEcfVendaProduto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<ComEcfVendaProduto> produtos) {
		this.produtos = produtos;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public Hidden getHdnEcf() {
		return hdnEcf;
	}

	public void setHdnEcf(Hidden hdnEcf) {
		this.hdnEcf = hdnEcf;
	}

	public ComboBox getCmbEcf() {
		return cmbEcf;
	}

	public void setCmbEcf(ComboBox cmbEcf) {
		this.cmbEcf = cmbEcf;
	}

	public NumberField getTxtCoo() {
		return txtCoo;
	}

	public void setTxtCoo(NumberField txtCoo) {
		this.txtCoo = txtCoo;
	}

	public NumberField getTxtBruto() {
		return txtBruto;
	}

	public void setTxtBruto(NumberField txtBruto) {
		this.txtBruto = txtBruto;
	}

	public NumberField getTxtDesc() {
		return txtDesc;
	}

	public void setTxtDesc(NumberField txtDesc) {
		this.txtDesc = txtDesc;
	}

	public NumberField getTxtLiquido() {
		return txtLiquido;
	}

	public void setTxtLiquido(NumberField txtLiquido) {
		this.txtLiquido = txtLiquido;
	}
}
