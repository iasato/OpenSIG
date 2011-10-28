package br.com.opensig.produto.client.visao.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.AComando;
import br.com.opensig.core.client.controlador.comando.IComando;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvar;
import br.com.opensig.core.client.controlador.comando.form.ComandoSalvarFinal;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.core.client.visao.Arvore;
import br.com.opensig.core.client.visao.ComboEntidade;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.core.shared.modelo.ExpListagem;
import br.com.opensig.core.shared.modelo.ExpMeta;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpFornecedor;
import br.com.opensig.produto.client.servico.ProdutoProxy;
import br.com.opensig.produto.client.visao.lista.ListagemPreco;
import br.com.opensig.produto.shared.modelo.ProdCategoria;
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdEstoque;
import br.com.opensig.produto.shared.modelo.ProdOrigem;
import br.com.opensig.produto.shared.modelo.ProdPreco;
import br.com.opensig.produto.shared.modelo.ProdProduto;
import br.com.opensig.produto.shared.modelo.ProdTributacao;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
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
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtextux.client.widgets.grid.plugins.SummaryColumnConfig;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioProduto extends AFormulario<ProdProduto> {

	private Hidden hdnFornecedor;
	private Hidden hdnFabricante;
	private Hidden hdnCod;
	private Hidden hdnSinc;
	private TextField txtDescricao;
	private TextField txtReferencia;
	private NumberField txtCusto;
	private NumberField txtPreco;
	private NumberField txtVolume;
	private NumberField txtEstoque;
	private TextField txtNcm;
	private NumberField txtBarra;
	private ComboBox cmbFornecedor;
	private ComboBox cmbFabricante;
	private ComboBox cmbTributacao;
	private ComboBox cmbOrigem;
	private ComboBox cmbEmbalagem;
	private Checkbox chkAtivo;
	private Checkbox chkIncentivo;
	private Arvore<ProdCategoria> treeCategoria;
	private Date dtCadastro;
	private ListagemPreco gridPrecos;
	private List<ProdPreco> precos;
	private List<ProdCategoria> categorias;

	public FormularioProduto(SisFuncao funcao) {
		super(new ProdProduto(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();
		Panel coluna1 = new Panel();
		coluna1.setBorder(false);
		coluna1.setLayout(new FormLayout());

		hdnFornecedor = new Hidden("empFornecedor.empFornecedorId", "0");
		add(hdnFornecedor);

		hdnFabricante = new Hidden("empFabricante.empFornecedorId", "0");
		add(hdnFabricante);

		hdnCod = new Hidden("prodProdutoId", "0");
		add(hdnCod);

		hdnSinc = new Hidden("prodProdutoSinc", "0");
		add(hdnSinc);

		txtNcm = new TextField(OpenSigCore.i18n.txtNcm(), "prodProdutoNcm", 80);
		txtNcm.setAllowBlank(false);
		txtNcm.setMinLength(2);
		txtNcm.setMaxLength(8);
		txtNcm.setRegex("^\\d{2}$|^\\d{8}$");

		txtBarra = new NumberField(OpenSigCore.i18n.txtBarra(), "prodProdutoBarra", 110);
		txtBarra.setAllowDecimals(false);
		txtBarra.setAllowNegative(false);
		txtBarra.setMinLength(6);
		txtBarra.setMaxLength(18);

		txtVolume = new NumberField(OpenSigCore.i18n.txtQtdCx(), "prodProdutoVolume", 60);
		txtVolume.setAllowBlank(false);
		txtVolume.setAllowDecimals(false);
		txtVolume.setAllowNegative(false);
		txtVolume.setMaxLength(10);
		txtVolume.setValue(1);
		txtVolume.setMinValue(1);

		txtCusto = new NumberField(OpenSigCore.i18n.txtCusto(), "prodProdutoCusto", 80);
		txtCusto.setAllowBlank(false);
		txtCusto.setAllowDecimals(true);
		txtCusto.setAllowNegative(false);
		txtCusto.setDecimalPrecision(2);
		txtCusto.setMaxLength(13);
		txtCusto.setValue(0);

		txtPreco = new NumberField(OpenSigCore.i18n.txtPreco(), "prodProdutoPreco", 80);
		txtPreco.setAllowBlank(false);
		txtPreco.setAllowDecimals(true);
		txtPreco.setAllowNegative(false);
		txtPreco.setDecimalPrecision(2);
		txtPreco.setMaxLength(13);
		txtPreco.setValue(0);

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(txtNcm, 100);
		linha1.addToRow(txtBarra, 130);
		linha1.addToRow(getEmbalagem(), 80);
		linha1.addToRow(txtVolume, 80);
		linha1.addToRow(txtCusto, 100);
		linha1.addToRow(txtPreco, 100);
		coluna1.add(linha1);

		txtReferencia = new TextField(OpenSigCore.i18n.txtRef(), "prodProdutoReferencia", 80);
		txtReferencia.setRegex("^\\w*$");
		txtReferencia.setMaxLength(20);

		txtDescricao = new TextField(OpenSigCore.i18n.txtDescricao(), "prodProdutoDescricao", 320);
		txtDescricao.setAllowBlank(false);
		txtDescricao.setMaxLength(100);

		MultiFieldPanel linha2 = new MultiFieldPanel();
		linha2.setBorder(false);
		linha2.addToRow(txtReferencia, 100);
		linha2.addToRow(txtDescricao, 340);
		linha2.addToRow(getOrigem(), 150);
		coluna1.add(linha2);

		MultiFieldPanel linha3 = new MultiFieldPanel();
		linha3.setBorder(false);
		linha3.addToRow(getFornecedor(), 330);
		linha3.addToRow(getTributacao(), 240);
		coluna1.add(linha3);

		txtEstoque = new NumberField(OpenSigCore.i18n.txtEstoque(), "t1.prodEstoqueQuantidade", 60);
		txtEstoque.setAllowBlank(false);
		txtEstoque.setAllowNegative(false);
		txtEstoque.setMaxLength(10);
		txtEstoque.setValue(0);

		chkAtivo = new Checkbox(OpenSigCore.i18n.txtAtivo(), "prodProdutoAtivo");
		chkAtivo.setChecked(true);

		chkIncentivo = new Checkbox(OpenSigCore.i18n.txtIncentivo(), "prodProdutoIncentivo");

		MultiFieldPanel linha4 = new MultiFieldPanel();
		linha4.setBorder(false);
		linha4.addToRow(getFabricante(), 330);
		linha4.addToRow(txtEstoque, 80);
		linha4.addToRow(chkAtivo, 50);
		linha4.addToRow(chkIncentivo, 80);
		coluna1.add(linha4);

		Panel formColuna = new Panel();
		formColuna.setBorder(false);
		formColuna.setLayout(new ColumnLayout());
		formColuna.add(coluna1, new ColumnLayoutData(.75));
		formColuna.add(getCategoria(), new ColumnLayoutData(.25));
		add(formColuna);

		add(new HTML("<br/>"));

		gridPrecos = new ListagemPreco(true);
		gridPrecos.setHeight(150);
		add(gridPrecos);

		super.inicializar();
	}

	@Override
	public IComando AntesDaAcao(IComando comando) {
		// salavando
		if (comando instanceof ComandoSalvar) {
			comando = new AComando(new ComandoSalvarFinal()) {
				public void execute(Map contexto) {
					super.execute(contexto);
					ProdutoProxy proxy = new ProdutoProxy();
					proxy.salvarProduto(classe, categorias, ASYNC);
				}
			};
		}

		return comando;
	}

	public boolean setDados() {
		precos = new ArrayList<ProdPreco>();
		categorias = new ArrayList<ProdCategoria>();
		Collection<String[]> valores = new ArrayList<String[]>();
		boolean retorno = treeCategoria.validarCategoria(valores);
		String strCategorias = "";

		for (String[] valor : valores) {
			strCategorias += valor[1] + "::";
			if (valor[0].equals("0")) {
				ProdCategoria categoria = new ProdCategoria();
				categoria.setProdCategoriaDescricao(valor[1]);
				categorias.add(categoria);
			}
		}

		if (!gridPrecos.validar(precos)) {
			retorno = false;
			new ToastWindow(OpenSigCore.i18n.txtListagem(), OpenSigCore.i18n.errLista()).show();
		}

		classe.setProdPrecos(precos);
		classe.setProdProdutoId(Integer.valueOf(hdnCod.getValueAsString()));
		classe.setProdProdutoNcm(txtNcm.getValueAsString());
		if (txtBarra.getValue() != null) {
			classe.setProdProdutoBarra(txtBarra.getValue().longValue());
		} else {
			classe.setProdProdutoBarra(null);
		}
		classe.setProdProdutoReferencia(txtReferencia.getValueAsString() == null ? "" : txtReferencia.getValueAsString());
		if (txtCusto.getValue() != null) {
			classe.setProdProdutoCusto(txtCusto.getValue().doubleValue());
		}
		if (txtPreco.getValue() != null) {
			classe.setProdProdutoPreco(txtPreco.getValue().doubleValue());
		}
		if (txtVolume.getValue() != null) {
			classe.setProdProdutoVolume(txtVolume.getValue().intValue());
		}
		classe.setProdProdutoDescricao(txtDescricao.getValueAsString());
		classe.setProdProdutoAtivo(chkAtivo.getValue());
		classe.setProdProdutoIncentivo(chkIncentivo.getValue());
		classe.setProdProdutoSinc(Integer.valueOf(hdnSinc.getValueAsString()));
		if (!hdnFornecedor.getValueAsString().equals("0")) {
			EmpFornecedor fornecedor = new EmpFornecedor(Integer.valueOf(hdnFornecedor.getValueAsString()));
			classe.setEmpFornecedor(fornecedor);
		} else {
			classe.setEmpFornecedor(null);
		}
		if (!hdnFabricante.getValueAsString().equals("0")) {
			EmpFornecedor fabricante = new EmpFornecedor(Integer.valueOf(hdnFabricante.getValueAsString()));
			classe.setEmpFabricante(fabricante);
		} else {
			classe.setEmpFabricante(null);
		}
		if (cmbTributacao.getValue() != null) {
			ProdTributacao tributacao = new ProdTributacao(Integer.valueOf(cmbTributacao.getValue()));
			classe.setProdTributacao(tributacao);
		}
		classe.setProdProdutoCategoria(strCategorias);
		if (classe.getProdProdutoId() == 0) {
			classe.setProdProdutoCadastrado(UtilClient.DATA);
			classe.setProdProdutoAlterado(UtilClient.DATA);
		} else {
			classe.setProdProdutoCadastrado(dtCadastro);
			classe.setProdProdutoAlterado(UtilClient.DATA);
		}
		if (cmbOrigem.getValue() != null) {
			ProdOrigem origem = new ProdOrigem(Integer.valueOf(cmbOrigem.getValue()));
			classe.setProdOrigem(origem);
		}
		if (cmbEmbalagem.getValue() != null) {
			ProdEmbalagem volume = new ProdEmbalagem(Integer.valueOf(cmbEmbalagem.getValue()));
			classe.setProdEmbalagem(volume);
		}
		if (txtEstoque.getValue() != null) {
			ProdEstoque estoque = new ProdEstoque();
			estoque.setProdEstoqueQuantidade(txtEstoque.getValue().doubleValue());
			estoque.setEmpEmpresa(new EmpEmpresa(Ponte.getLogin().getEmpresaId()));

			List<ProdEstoque> estoques = new ArrayList<ProdEstoque>();
			estoques.add(estoque);
			classe.setProdEstoques(estoques);
		}

		return retorno;
	}

	public void limparDados() {
		getForm().reset();
		FiltroNumero fn = new FiltroNumero("prodPrecoId", ECompara.IGUAL, 0);
		gridPrecos.getProxy().setFiltroPadrao(fn);
		gridPrecos.getStore().removeAll();
		treeCategoria.getLblValidacao().hide();
		treeCategoria.selecionar(null);
	}

	public void mostrarDados() {
		if (cmbOrigem.getStore().getRecords().length == 0) {
			cmbOrigem.getStore().load();
		} else {
			mostrar();
		}
	}

	private void mostrar() {
		MessageBox.hide();
		Record rec = lista.getPanel().getSelectionModel().getSelected();
		if (rec != null) {
			getForm().loadRecord(rec);

			dtCadastro = rec.getAsDate("prodProdutoCadastrado");
			txtCusto.setValue(txtCusto.getValue().doubleValue());
			txtPreco.setValue(txtPreco.getValue().doubleValue());

			classe.setProdProdutoId(rec.getAsInteger("prodProdutoId"));
			FiltroObjeto fo = new FiltroObjeto("prodProduto", ECompara.IGUAL, classe);
			gridPrecos.getProxy().setFiltroPadrao(fo);
			gridPrecos.getStore().reload();
			String[] objs = rec.getAsString("prodProdutoCategoria").split("::");
			treeCategoria.selecionar(objs);
		} else {
			cmbOrigem.setValue("1");
			cmbTributacao.setValue("1");
			cmbEmbalagem.setValue("1");
		}
		txtNcm.focus(true);

		if (duplicar) {
			hdnCod.setValue("0");
			hdnSinc.setValue("0");
			duplicar = false;
		}
	}

	private ComboBox getFornecedor() {
		cmbFornecedor = UtilClient.getComboEntidade(new ComboEntidade(new EmpFornecedor()));
		cmbFornecedor.setName("empFornecedor.empEntidade.empEntidadeNome1");
		cmbFornecedor.setLabel(OpenSigCore.i18n.txtFornecedor());
		cmbFornecedor.addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {
				hdnFornecedor.setValue(comboBox.getValue());
			}

			public void onBlur(Field field) {
				if (cmbFornecedor.getRawValue().equals("")) {
					hdnFornecedor.setValue("0");
				}
			}
		});

		return cmbFornecedor;
	}

	private ComboBox getFabricante() {
		cmbFabricante = UtilClient.getComboEntidade(new ComboEntidade(new EmpFornecedor()));
		cmbFabricante.setName("empFabricante.empEntidade.empEntidadeNome1");
		cmbFabricante.setLabel(OpenSigCore.i18n.txtFabricante());
		cmbFabricante.addListener(new ComboBoxListenerAdapter() {
			public void onSelect(ComboBox comboBox, Record record, int index) {
				hdnFabricante.setValue(comboBox.getValue());
			}

			public void onBlur(Field field) {
				if (cmbFabricante.getRawValue().equals("")) {
					hdnFabricante.setValue("0");
				}
			}
		});

		return cmbFabricante;
	}

	private ComboBox getOrigem() {
		FieldDef[] fdOrigem = new FieldDef[] { new IntegerFieldDef("prodOrigemId"), new StringFieldDef("prodOrigemDescricao") };
		CoreProxy<ProdOrigem> proxy = new CoreProxy<ProdOrigem>(new ProdOrigem());
		final Store storeOrigem = new Store(proxy, new ArrayReader(new RecordDef(fdOrigem)), true);
		storeOrigem.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				cmbEmbalagem.getStore().load();
			}

			public void onLoadException(Throwable error) {
				MessageBox.confirm(OpenSigCore.i18n.txtOrigem(), OpenSigCore.i18n.msgRecarregar(), new ConfirmCallback() {
					public void execute(String btnID) {
						if (btnID.equalsIgnoreCase("yes")) {
							storeOrigem.load();
						}
					}
				});
			}
		});

		cmbOrigem = new ComboBox(OpenSigCore.i18n.txtOrigem(), "prodOrigem.prodOrigemId", 130);
		cmbOrigem.setAllowBlank(false);
		cmbOrigem.setStore(storeOrigem);
		cmbOrigem.setTriggerAction(ComboBox.ALL);
		cmbOrigem.setMode(ComboBox.LOCAL);
		cmbOrigem.setDisplayField("prodOrigemDescricao");
		cmbOrigem.setValueField("prodOrigemId");
		cmbOrigem.setForceSelection(true);
		cmbOrigem.setListWidth(250);
		cmbOrigem.setEditable(false);

		return cmbOrigem;
	}

	private ComboBox getEmbalagem() {
		FieldDef[] fdEmbalagem = new FieldDef[] { new IntegerFieldDef("prodEmbalagemId"), new StringFieldDef("prodEmbalagemNome"), new IntegerFieldDef("prodEmbalagemUnidade"),
				new StringFieldDef("prodEmbalagemDescricao") };
		CoreProxy<ProdEmbalagem> proxy = new CoreProxy<ProdEmbalagem>(new ProdEmbalagem());
		final Store storeEmbalagem = new Store(proxy, new ArrayReader(new RecordDef(fdEmbalagem)), true);
		storeEmbalagem.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				cmbTributacao.getStore().load();
			}

			public void onLoadException(Throwable error) {
				MessageBox.confirm(OpenSigCore.i18n.txtEmbalagem(), OpenSigCore.i18n.msgRecarregar(), new ConfirmCallback() {
					public void execute(String btnID) {
						if (btnID.equalsIgnoreCase("yes")) {
							storeEmbalagem.load();
						}
					}
				});
			}
		});

		cmbEmbalagem = new ComboBox(OpenSigCore.i18n.txtEmbalagem(), "prodEmbalagem.prodEmbalagemId", 60);
		cmbEmbalagem.setAllowBlank(false);
		cmbEmbalagem.setStore(storeEmbalagem);
		cmbEmbalagem.setTriggerAction(ComboBox.ALL);
		cmbEmbalagem.setMode(ComboBox.LOCAL);
		cmbEmbalagem.setDisplayField("prodEmbalagemNome");
		cmbEmbalagem.setValueField("prodEmbalagemId");
		cmbEmbalagem.setTpl("<div class=\"x-combo-list-item\"><b>{prodEmbalagemNome}</b> - <i>" + OpenSigCore.i18n.txtUnidade() + " [{prodEmbalagemUnidade}]</i></div>");
		cmbEmbalagem.setForceSelection(true);
		cmbEmbalagem.setListWidth(150);
		cmbEmbalagem.setEditable(false);

		return cmbEmbalagem;
	}

	private ComboBox getTributacao() {
		FieldDef[] fdTributacao = new FieldDef[] { new IntegerFieldDef("prodTributacaoId"), new StringFieldDef("prodTributacaoNome"), new StringFieldDef("prodTributacaoCst"),
				new StringFieldDef("prodTributacaoCson"), new IntegerFieldDef("prodTributacaoCfop") };
		CoreProxy<ProdTributacao> proxy = new CoreProxy<ProdTributacao>(new ProdTributacao());
		final Store storeTributacao = new Store(proxy, new ArrayReader(new RecordDef(fdTributacao)), false);
		storeTributacao.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				treeCategoria.carregar(null, new AsyncCallback<Lista<ProdCategoria>>() {

					public void onSuccess(Lista<ProdCategoria> result) {
						mostrar();
					}

					public void onFailure(Throwable caught) {
						new ToastWindow(OpenSigCore.i18n.txtCategoria(), OpenSigCore.i18n.errListagem());

					}
				});
			}

			public void onLoadException(Throwable error) {
				MessageBox.confirm(OpenSigCore.i18n.txtTributacao(), OpenSigCore.i18n.msgRecarregar(), new ConfirmCallback() {
					public void execute(String btnID) {
						if (btnID.equalsIgnoreCase("yes")) {
							storeTributacao.load();
						}
					}
				});
			}
		});

		cmbTributacao = new ComboBox(OpenSigCore.i18n.txtTributacao(), "prodTributacao.prodTributacaoId", 220);
		cmbTributacao.setAllowBlank(false);
		cmbTributacao.setStore(storeTributacao);
		cmbTributacao.setTriggerAction(ComboBox.ALL);
		cmbTributacao.setMode(ComboBox.LOCAL);
		cmbTributacao.setDisplayField("prodTributacaoNome");
		cmbTributacao.setValueField("prodTributacaoId");
		cmbTributacao.setTpl("<div class=\"x-combo-list-item\"><b>{prodTributacaoNome}</b> - <i>" + OpenSigCore.i18n.txtCfop() + " [{prodTributacaoCfop}], " + OpenSigCore.i18n.txtCst()
				+ " [{prodTributacaoCst}]</i></div>");
		cmbTributacao.setForceSelection(true);
		cmbTributacao.setListWidth(450);
		cmbTributacao.setEditable(false);

		return cmbTributacao;
	}

	private Arvore getCategoria() {
		treeCategoria = new Arvore(new ProdCategoria(), OpenSigCore.i18n.txtCategoria());
		treeCategoria.setTitle(OpenSigCore.i18n.txtCategoria());
		treeCategoria.setIconeNode("icon-categoria");
		treeCategoria.setFiltrar(true);
		treeCategoria.setEditar(true);
		treeCategoria.setWidth(200);
		treeCategoria.setHeight(150);
		treeCategoria.setBodyBorder(true);
		treeCategoria.inicializar();
		treeCategoria.getTxtFiltro().setMaxLength(20);

		return treeCategoria;
	}

	public void gerarListas() {
		// precos
		List<ExpMeta> metadados = new ArrayList<ExpMeta>();
		for (int i = 0; i < gridPrecos.getModelos().getColumnCount(); i++) {
			if (gridPrecos.getModelos().isHidden(i)) {
				metadados.add(null);
			} else {
				ExpMeta meta = new ExpMeta(gridPrecos.getModelos().getColumnHeader(i), gridPrecos.getModelos().getColumnWidth(i), null);
				if (gridPrecos.getModelos().getColumnConfigs()[i] instanceof SummaryColumnConfig) {
					SummaryColumnConfig col = (SummaryColumnConfig) gridPrecos.getModelos().getColumnConfigs()[i];
					String tp = col.getSummaryType().equals("average") ? "AVG" : col.getSummaryType().toUpperCase();
					meta.setGrupo(EBusca.getBusca(tp));
				}
				metadados.add(meta);
			}
		}

		// alterando campos visiveis
		metadados.set(2, metadados.get(1));
		metadados.set(1, null);

		SortState ordem = gridPrecos.getStore().getSortState();
		ProdPreco preco = new ProdPreco();
		preco.setCampoOrdem(ordem.getField());
		preco.setOrdemDirecao(EDirecao.valueOf(ordem.getDirection().getDirection()));
		// filtro
		int id = UtilClient.getSelecionado(lista.getPanel());
		FiltroObjeto filtro = new FiltroObjeto("proProduto", ECompara.IGUAL, new ProdProduto(id));
		
		ExpListagem<ProdPreco> precos = new ExpListagem<ProdPreco>();
		precos.setClasse(preco);
		precos.setMetadados(metadados);
		precos.setNome(gridPrecos.getTitle());
		precos.setFiltro(filtro);

		// sub listagens
		expLista = new ArrayList<ExpListagem>();
		expLista.add(precos);
	}

	public Hidden getHdnFornecedor() {
		return hdnFornecedor;
	}

	public void setHdnFornecedor(Hidden hdnFornecedor) {
		this.hdnFornecedor = hdnFornecedor;
	}

	public Hidden getHdnFabricante() {
		return hdnFabricante;
	}

	public void setHdnFabricante(Hidden hdnFabricante) {
		this.hdnFabricante = hdnFabricante;
	}

	public Hidden getHdnSinc() {
		return hdnSinc;
	}

	public void setHdnSinc(Hidden hdnSinc) {
		this.hdnSinc = hdnSinc;
	}

	public NumberField getTxtEstoque() {
		return txtEstoque;
	}

	public void setTxtEstoque(NumberField txtEstoque) {
		this.txtEstoque = txtEstoque;
	}

	public ComboBox getCmbFabricante() {
		return cmbFabricante;
	}

	public void setCmbFabricante(ComboBox cmbFabricante) {
		this.cmbFabricante = cmbFabricante;
	}

	public TextField getTxtNcm() {
		return txtNcm;
	}

	public void setTxtNcm(TextField txtNcm) {
		this.txtNcm = txtNcm;
	}

	public Hidden getHdnCod() {
		return hdnCod;
	}

	public void setHdnCod(Hidden hdnCod) {
		this.hdnCod = hdnCod;
	}

	public TextField getTxtDescricao() {
		return txtDescricao;
	}

	public void setTxtDescricao(TextField txtDescricao) {
		this.txtDescricao = txtDescricao;
	}

	public TextField getTxtReferencia() {
		return txtReferencia;
	}

	public void setTxtReferencia(TextField txtReferencia) {
		this.txtReferencia = txtReferencia;
	}

	public NumberField getTxtCusto() {
		return txtCusto;
	}

	public void setTxtCusto(NumberField txtCusto) {
		this.txtCusto = txtCusto;
	}

	public NumberField getTxtPreco() {
		return txtPreco;
	}

	public void setTxtPreco(NumberField txtPreco) {
		this.txtPreco = txtPreco;
	}

	public NumberField getTxtBarra() {
		return txtBarra;
	}

	public void setTxtBarra(NumberField txtBarra) {
		this.txtBarra = txtBarra;
	}

	public ComboBox getCmbFornecedor() {
		return cmbFornecedor;
	}

	public void setCmbFornecedor(ComboBox cmbFornecedor) {
		this.cmbFornecedor = cmbFornecedor;
	}

	public ComboBox getCmbTributacao() {
		return cmbTributacao;
	}

	public void setCmbTributacao(ComboBox cmbTributacao) {
		this.cmbTributacao = cmbTributacao;
	}

	public Checkbox getChkAtivo() {
		return chkAtivo;
	}

	public void setChkAtivo(Checkbox chkAtivo) {
		this.chkAtivo = chkAtivo;
	}

	public Arvore getTreeCategoria() {
		return treeCategoria;
	}

	public Date getDtCadastro() {
		return dtCadastro;
	}

	public void setDtCadastro(Date dtCadastro) {
		this.dtCadastro = dtCadastro;
	}

	public ListagemPreco getGridPrecos() {
		return gridPrecos;
	}

	public void setGridPrecos(ListagemPreco gridPrecos) {
		this.gridPrecos = gridPrecos;
	}

	public List<ProdPreco> getPrecos() {
		return precos;
	}

	public NumberField getTxtVolume() {
		return txtVolume;
	}

	public void setTxtVolume(NumberField txtVolume) {
		this.txtVolume = txtVolume;
	}

	public ComboBox getCmbOrigem() {
		return cmbOrigem;
	}

	public void setCmbOrigem(ComboBox cmbOrigem) {
		this.cmbOrigem = cmbOrigem;
	}

	public ComboBox getCmbEmbalagem() {
		return cmbEmbalagem;
	}

	public void setCmbEmbalagem(ComboBox cmbEmbalagem) {
		this.cmbEmbalagem = cmbEmbalagem;
	}

	public Checkbox getChkIncentivo() {
		return chkIncentivo;
	}

	public void setChkIncentivo(Checkbox chkIncentivo) {
		this.chkIncentivo = chkIncentivo;
	}

	public void setPrecos(List<ProdPreco> precos) {
		this.precos = precos;
	}

	public List<ProdCategoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<ProdCategoria> categorias) {
		this.categorias = categorias;
	}

	public void setTreeCategoria(Arvore<ProdCategoria> treeCategoria) {
		this.treeCategoria = treeCategoria;
	}
}
