package br.com.opensig.comercial.client.visao.form;

import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;

import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
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

public class FormularioEcfZ extends AFormulario<ComEcfZ> {

	private Hidden hdnCod;
	private ComboBox cmbEcf;
	private NumberField txtCoo;
	private NumberField txtCro;
	private NumberField txtCrz;
	private DateField dtData;
	private NumberField txtBruto;
	private NumberField txtTotal;

	public FormularioEcfZ(SisFuncao funcao) {
		super(new ComEcfZ(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		hdnCod = new Hidden("comEcfId", "0");
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
	}

	public boolean setDados() {
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

		return true;
	}

	public void limparDados() {
		getForm().reset();
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
			getForm().loadRecord(rec);
		}

		if (duplicar) {
			hdnCod.setValue("0");
			duplicar = false;
		}
	}

	public void gerarListas() {
	}

	public Hidden getHdnCod() {
		return hdnCod;
	}

	public void setHdnCod(Hidden hdnCod) {
		this.hdnCod = hdnCod;
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
