package br.com.opensig.comercial.client.visao.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.opensig.comercial.client.servico.ComercialProxy;
import br.com.opensig.comercial.client.visao.lista.ListagemEcfZTotais;
import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.comercial.shared.modelo.ComEcfZTotais;
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
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.core.shared.modelo.ExpListagem;
import br.com.opensig.core.shared.modelo.ExpMeta;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;

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
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtextux.client.widgets.grid.plugins.SummaryColumnConfig;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioEcfZ extends AFormulario<ComEcfZ> {

	private Hidden hdnCod;
	private ComboBox cmbEcf;
	private NumberField txtCoo;
	private NumberField txtCro;
	private NumberField txtCrz;
	private DateField dtData;
	private NumberField txtBruto;
	private NumberField txtTotal;
	private ListagemEcfZTotais gridTotais;

	public FormularioEcfZ(SisFuncao funcao) {
		super(new ComEcfZ(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		hdnCod = new Hidden("comEcfZId", "0");
		add(hdnCod);

		txtCoo = new NumberField(OpenSigCore.i18n.txtCoo(), "comEcfZCoo", 50);
		txtCoo.setAllowBlank(false);
		txtCoo.setAllowDecimals(false);
		txtCoo.setAllowNegative(false);
		txtCoo.setMaxLength(6);

		txtCro = new NumberField(OpenSigCore.i18n.txtCro(), "comEcfZCro", 50);
		txtCro.setAllowBlank(false);
		txtCro.setAllowDecimals(false);
		txtCro.setAllowNegative(false);
		txtCro.setMaxLength(3);

		txtCrz = new NumberField(OpenSigCore.i18n.txtCrz(), "comEcfZCrz", 50);
		txtCrz.setAllowBlank(false);
		txtCrz.setAllowDecimals(false);
		txtCrz.setAllowNegative(false);
		txtCrz.setMaxLength(6);

		dtData = new DateField(OpenSigCore.i18n.txtData(), "comEcfZData", 80);
		dtData.setAllowBlank(false);

		txtBruto = new NumberField(OpenSigCore.i18n.txtBruto(), "comEcfZBruto", 100);
		txtBruto.setAllowBlank(false);
		txtBruto.setAllowNegative(false);
		txtBruto.setMaxLength(11);

		txtTotal = new NumberField(OpenSigCore.i18n.txtTotal(), "comEcfZTotal", 100);
		txtTotal.setAllowBlank(false);
		txtTotal.setAllowNegative(false);
		txtTotal.setMaxLength(11);

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(getEcf(), 180);
		linha1.addToRow(txtCoo, 70);
		linha1.addToRow(txtCro, 70);
		linha1.addToRow(txtCrz, 70);
		linha1.addToRow(dtData, 110);
		linha1.addToRow(txtBruto, 120);
		linha1.addToRow(txtTotal, 120);
		add(linha1);
		
		gridTotais = new ListagemEcfZTotais(true);
		add(gridTotais);
	}

	@Override
	public IComando AntesDaAcao(IComando comando) {
		// salavando
		if (comando instanceof ComandoSalvar) {
			comando = new AComando(new ComandoSalvarFinal()) {
				public void execute(Map contexto) {
					super.execute(contexto);
					ComercialProxy proxy = new ComercialProxy();
					proxy.salvarEcfZ(classe, ASYNC);
				}
			};
		}

		return comando;
	}
	
	public boolean setDados() {
		boolean retorno = true;
		List<ComEcfZTotais> totais = new ArrayList<ComEcfZTotais>();

		if (!gridTotais.validar(totais)) {
			retorno = false;
			new ToastWindow(OpenSigCore.i18n.txtListagem(), OpenSigCore.i18n.errLista()).show();
		}
		
		classe.setComZTotais(totais);
		classe.setComEcfZId(Integer.valueOf(hdnCod.getValueAsString()));
		if (cmbEcf.getValue() != null) {
			ComEcf ecf = new ComEcf(Integer.valueOf(cmbEcf.getValue()));
			classe.setComEcf(ecf);
		}
		if (txtCoo.getValue() != null) {
			classe.setComEcfZCoo(txtCoo.getValue().intValue());
		}
		if (txtCro.getValue() != null) {
			classe.setComEcfZCro(txtCro.getValue().intValue());
		}
		if (txtCrz.getValue() != null) {
			classe.setComEcfZCrz(txtCrz.getValue().intValue());
		}
		classe.setComEcfZData(dtData.getValue());
		if (txtBruto.getValue() != null) {
			classe.setComEcfZBruto(txtBruto.getValue().doubleValue());
		}
		if (txtTotal.getValue() != null) {
			classe.setComEcfZTotal(txtTotal.getValue().doubleValue());
		}

		return retorno;
	}

	public void limparDados() {
		getForm().reset();
		FiltroNumero fn = new FiltroNumero("comEcfZTotaisId", ECompara.IGUAL, 0);
		gridTotais.getProxy().setFiltroPadrao(fn);
		gridTotais.getStore().removeAll();
	}

	public void mostrarDados() {
		if (cmbEcf.getStore().getRecords().length == 0) {
			cmbEcf.getStore().load();
		} else {
			mostrar();
		}
	}

	private void mostrar() {
		Record rec = lista.getPanel().getSelectionModel().getSelected();
		if (rec != null) {
			getForm().loadRecord(rec);
			classe.setComEcfZId(Integer.valueOf(hdnCod.getValueAsString()));
			FiltroObjeto fo = new FiltroObjeto("comEcfZ", ECompara.IGUAL, classe);
			gridTotais.getProxy().setFiltroPadrao(fo);
			gridTotais.getStore().reload();
		}

		if (duplicar) {
			hdnCod.setValue("0");
			duplicar = false;
		}
	}

	public void gerarListas() {
		// produtos
		List<ExpMeta> metadados = new ArrayList<ExpMeta>();
		for (int i = 0; i < gridTotais.getModelos().getColumnCount(); i++) {
			if (gridTotais.getModelos().isHidden(i)) {
				metadados.add(null);
			} else {
				ExpMeta meta = new ExpMeta(gridTotais.getModelos().getColumnHeader(i), gridTotais.getModelos().getColumnWidth(i), null);
				if (gridTotais.getModelos().getColumnConfigs()[i] instanceof SummaryColumnConfig) {
					SummaryColumnConfig col = (SummaryColumnConfig) gridTotais.getModelos().getColumnConfigs()[i];
					String tp = col.getSummaryType().equals("average") ? "AVG" : col.getSummaryType().toUpperCase();
					meta.setGrupo(EBusca.getBusca(tp));
				}
				metadados.add(meta);
			}
		}

		SortState ordem = gridTotais.getStore().getSortState();
		ComEcfZTotais zTotal = new ComEcfZTotais();
		zTotal.setCampoOrdem(ordem.getField());
		zTotal.setOrdemDirecao(EDirecao.valueOf(ordem.getDirection().getDirection()));
		// filtro
		int id = UtilClient.getSelecionado(lista.getPanel());
		FiltroObjeto filtro = new FiltroObjeto("comEcfZ", ECompara.IGUAL, new ComEcfZ(id));
		
		ExpListagem<ComEcfZTotais> totais = new ExpListagem<ComEcfZTotais>();
		totais.setClasse(zTotal);
		totais.setMetadados(metadados);
		totais.setNome(gridTotais.getTitle());
		totais.setFiltro(filtro);

		// sub listagens
		expLista = new ArrayList<ExpListagem>();
		expLista.add(totais);
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

		cmbEcf = new ComboBox(OpenSigCore.i18n.txtEcf(), "comEcf.comEcfId", 150);
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

		return cmbEcf;
	}

	public Hidden getHdnCod() {
		return hdnCod;
	}

	public void setHdnCod(Hidden hdnCod) {
		this.hdnCod = hdnCod;
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

	public NumberField getTxtCro() {
		return txtCro;
	}

	public void setTxtCro(NumberField txtCro) {
		this.txtCro = txtCro;
	}

	public NumberField getTxtCrz() {
		return txtCrz;
	}

	public void setTxtCrz(NumberField txtCrz) {
		this.txtCrz = txtCrz;
	}

	public DateField getDtData() {
		return dtData;
	}

	public void setDtData(DateField dtData) {
		this.dtData = dtData;
	}

	public NumberField getTxtBruto() {
		return txtBruto;
	}

	public void setTxtBruto(NumberField txtBruto) {
		this.txtBruto = txtBruto;
	}

	public NumberField getTxtTotal() {
		return txtTotal;
	}

	public void setTxtTotal(NumberField txtTotal) {
		this.txtTotal = txtTotal;
	}

}
