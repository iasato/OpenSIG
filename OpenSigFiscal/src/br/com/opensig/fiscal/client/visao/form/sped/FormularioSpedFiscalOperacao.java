package br.com.opensig.fiscal.client.visao.form.sped;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

import com.gwtext.client.core.Position;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.MultiFieldPanel;

public class FormularioSpedFiscalOperacao extends AFormulario<FisSpedFiscal> {

	private ComboBox cmbSped;
	private ComboBox cmbAno;
	private ComboBox cmbMes;
	private ComboBox cmbCompras;
	private ComboBox cmbVendas;
	private Checkbox chkFinalidade;

	public FormularioSpedFiscalOperacao(FisSpedFiscal classe, SisFuncao funcao) {
		super(classe, funcao);
		inicializar();
	}

	@Override
	public void inicializar() {
		setTitle(OpenSigCore.i18n.txtSped() + " " + OpenSigCore.i18n.txtOperacao(), "icon-sped");
		setAutoScroll(true);
		setLabelAlign(Position.TOP);
		setButtonAlign(Position.CENTER);
		setPaddings(5);
		setMargins(1);
		setWidth(350);
		setHeight(150);

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(getSped(), 110);
		linha1.addToRow(getAno(), 110);
		linha1.addToRow(getMes(), 110);
		add(linha1);

		chkFinalidade = new Checkbox(OpenSigCore.i18n.txtSubstituicao(), "chkFinalidade");
		chkFinalidade.disable();

		MultiFieldPanel linha2 = new MultiFieldPanel();
		linha2.setBorder(false);
		linha2.addToRow(getCompras(), 110);
		linha2.addToRow(getVendas(), 110);
		linha2.addToRow(chkFinalidade, 110);
		add(linha2);
	}

	@Override
	public boolean setDados() {
		classe.setFisSpedFiscalTipo(cmbSped.getValue());
		classe.setFisSpedFiscalAno(Integer.valueOf(cmbAno.getValue()));
		classe.setFisSpedFiscalMes(Integer.valueOf(cmbMes.getValue()));
		classe.setFisSpedFiscalCompras(Integer.valueOf(cmbCompras.getValue()));
		classe.setFisSpedFiscalVendas(Integer.valueOf(cmbVendas.getValue()));
		classe.setFinalidade(chkFinalidade.getValue() ? 1 : 0);

		contexto.put("classe", classe);
		return true;
	}

	@Override
	public void mostrarDados() {
		if (classe.getFisSpedFiscalAno() > 0) {
			chkFinalidade.setChecked(true);
			cmbSped.setValue(classe.getFisSpedFiscalTipo());
			cmbAno.setValue(classe.getFisSpedFiscalAno() + "");
			cmbMes.setValue(classe.getFisSpedFiscalMes() > 9 ? classe.getFisSpedFiscalMes() + "" : "0" + classe.getFisSpedFiscalMes());
		
			cmbSped.disable();
			cmbAno.disable();
			cmbMes.disable();
		}
	}

	@Override
	public void limparDados() {
		cmbSped.reset();
		cmbAno.reset();
		cmbMes.reset();
		cmbCompras.reset();
		cmbVendas.reset();
	}

	@Override
	public void gerarListas() {
	}

	private ComboBox getSped() {
		final String valor1 = "ICMS_IPI";
		// final String valor2 = "PIS_COFINS";

		Store store = new SimpleStore(new String[] { "id", "valor" }, new String[][] { new String[] { valor1, valor1 } });
		store.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				cmbSped.setValue(valor1);
			}
		});

		cmbSped = new ComboBox(OpenSigCore.i18n.txtSped(), "cmbSped", 90);
		cmbSped.setForceSelection(true);
		cmbSped.setEditable(false);
		cmbSped.setStore(store);
		cmbSped.setDisplayField("valor");
		cmbSped.setValueField("id");
		cmbSped.setMode(ComboBox.LOCAL);
		cmbSped.setTriggerAction(ComboBox.ALL);
		cmbSped.setAllowBlank(false);

		store.load();
		return cmbSped;
	}

	private ComboBox getAno() {
		List<String[]> anos = new ArrayList<String[]>();
		final int atual = new Date().getYear() + 1900;
		for (int ano = 2010; ano <= atual; ano++) {
			String[] valor = new String[] { ano + "", ano + "" };
			anos.add(valor);
		}

		Store store = new SimpleStore(new String[] { "id", "valor" }, anos.toArray(new String[][] {}));
		cmbAno = new ComboBox(OpenSigCore.i18n.txtAno(), "cmbAno", 90);
		cmbAno.setForceSelection(true);
		cmbAno.setEditable(false);
		cmbAno.setStore(store);
		cmbAno.setDisplayField("valor");
		cmbAno.setValueField("id");
		cmbAno.setMode(ComboBox.LOCAL);
		cmbAno.setTriggerAction(ComboBox.ALL);
		cmbAno.setAllowBlank(false);

		store.load();
		return cmbAno;
	}

	private ComboBox getMes() {
		List<String[]> meses = new ArrayList<String[]>();
		for (int mes = 1; mes <= 12; mes++) {
			String strMes = mes < 10 ? "0" + mes : mes + "";
			String[] valor = new String[] { strMes + "", strMes };
			meses.add(valor);
		}

		Store store = new SimpleStore(new String[] { "id", "valor" }, meses.toArray(new String[][] {}));
		cmbMes = new ComboBox(OpenSigCore.i18n.txtMes(), "cmbMes", 90);
		cmbMes.setForceSelection(true);
		cmbMes.setEditable(false);
		cmbMes.setStore(store);
		cmbMes.setDisplayField("valor");
		cmbMes.setValueField("id");
		cmbMes.setMode(ComboBox.LOCAL);
		cmbMes.setTriggerAction(ComboBox.ALL);
		cmbMes.setAllowBlank(false);
		
		store.load();
		return cmbMes;
	}

	private ComboBox getCompras() {
		String valor0 = OpenSigCore.i18n.txtTudo();
		String valor1 = OpenSigCore.i18n.txtNfe() + " = " + OpenSigCore.i18n.txtSim();
		String valor2 = OpenSigCore.i18n.txtNfe() + " = " + OpenSigCore.i18n.txtNao();
		Store store = new SimpleStore(new String[] { "id", "valor" }, new String[][] { new String[] { "-1", valor0 }, new String[] { "1", valor1 }, new String[] { "0", valor2 } });
		store.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				cmbCompras.setValue("1");
			}
		});

		cmbCompras = new ComboBox(OpenSigCore.i18n.txtCompra(), "cmbCompras", 90);
		cmbCompras.setForceSelection(true);
		cmbCompras.setEditable(false);
		cmbCompras.setStore(store);
		cmbCompras.setDisplayField("valor");
		cmbCompras.setValueField("id");
		cmbCompras.setMode(ComboBox.LOCAL);
		cmbCompras.setTriggerAction(ComboBox.ALL);
		cmbCompras.setAllowBlank(false);

		store.load();
		return cmbCompras;
	}

	private ComboBox getVendas() {
		String valor0 = OpenSigCore.i18n.txtTudo();
		String valor1 = OpenSigCore.i18n.txtNfe() + " = " + OpenSigCore.i18n.txtSim();
		String valor2 = OpenSigCore.i18n.txtNfe() + " = " + OpenSigCore.i18n.txtNao();
		Store store = new SimpleStore(new String[] { "id", "valor" }, new String[][] { new String[] { "-1", valor0 }, new String[] { "1", valor1 }, new String[] { "0", valor2 } });
		store.addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				cmbVendas.setValue("1");
			}
		});

		cmbVendas = new ComboBox(OpenSigCore.i18n.txtVenda(), "cmbVendas", 90);
		cmbVendas.setForceSelection(true);
		cmbVendas.setEditable(false);
		cmbVendas.setStore(store);
		cmbVendas.setDisplayField("valor");
		cmbVendas.setValueField("id");
		cmbVendas.setMode(ComboBox.LOCAL);
		cmbVendas.setTriggerAction(ComboBox.ALL);
		cmbVendas.setAllowBlank(false);

		store.load();
		return cmbVendas;
	}

	public ComboBox getCmbSped() {
		return cmbSped;
	}

	public void setCmbSped(ComboBox cmbSped) {
		this.cmbSped = cmbSped;
	}

	public ComboBox getCmbAno() {
		return cmbAno;
	}

	public void setCmbAno(ComboBox cmbAno) {
		this.cmbAno = cmbAno;
	}

	public ComboBox getCmbMes() {
		return cmbMes;
	}

	public void setCmbMes(ComboBox cmbMes) {
		this.cmbMes = cmbMes;
	}

	public ComboBox getCmbCompras() {
		return cmbCompras;
	}

	public void setCmbCompras(ComboBox cmbCompras) {
		this.cmbCompras = cmbCompras;
	}

	public ComboBox getCmbVendas() {
		return cmbVendas;
	}

	public void setCmbVendas(ComboBox cmbVendas) {
		this.cmbVendas = cmbVendas;
	}

	public Checkbox getChkFinalidade() {
		return chkFinalidade;
	}

	public void setChkFinalidade(Checkbox chkFinalidade) {
		this.chkFinalidade = chkFinalidade;
	}

}
