package br.com.opensig.comercial.client.visao.form;

import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;

import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;

public class FormularioEcf extends AFormulario<ComEcf> {

	private Hidden hdnCod;
	private Hidden hdnEmpresa;
	private TextField txtCodigo;
	private TextField txtModelo;
	private TextField txtSerie;
	private NumberField txtCaixa;

	public FormularioEcf(SisFuncao funcao) {
		super(new ComEcf(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		hdnCod = new Hidden("comEcfId", "0");
		add(hdnCod);
		hdnEmpresa = new Hidden("empEmpresa.empEmpresaId", "0");
		add(hdnEmpresa);

		txtCodigo = new TextField(OpenSigCore.i18n.txtCodigo(), "comEcfCodigo", 50);
		txtCodigo.setAllowBlank(false);
		txtCodigo.setMaxLength(2);
		txtCodigo.setMinLength(2);
		
		txtModelo = new TextField(OpenSigCore.i18n.txtModelo(), "comEcfModelo", 200);
		txtModelo.setAllowBlank(false);
		txtModelo.setMaxLength(20);

		txtSerie = new TextField(OpenSigCore.i18n.txtSerie(), "comEcfSerie", 200);
		txtSerie.setAllowBlank(false);
		txtSerie.setMinLength(20);
		txtSerie.setMaxLength(20);

		txtCaixa = new NumberField(OpenSigCore.i18n.txtCaixa(), "comEcfCaixa", 50);
		txtCaixa.setAllowBlank(false);
		txtCaixa.setAllowDecimals(false);
		txtCaixa.setAllowNegative(false);
		txtCaixa.setMaxLength(3);

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(txtCodigo, 70);
		linha1.addToRow(txtModelo, 220);
		linha1.addToRow(txtSerie, 220);
		linha1.addToRow(txtCaixa, 70);
		add(linha1);
	}

	public boolean setDados() {
		classe.setComEcfId(Integer.valueOf(hdnCod.getValueAsString()));
		classe.setComEcfCodigo(txtCodigo.getValueAsString());
		classe.setComEcfModelo(txtModelo.getValueAsString());
		classe.setComEcfSerie(txtSerie.getValueAsString());
		if (txtCaixa.getValue() != null) {
			classe.setComEcfCaixa(txtCaixa.getValue().intValue());
		}
		if (hdnEmpresa.getValueAsString().equals("0")) {
			classe.setEmpEmpresa(new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		} else {
			classe.setEmpEmpresa(new EmpEmpresa(Integer.valueOf(hdnEmpresa.getValueAsString())));
		}

		return true;
	}

	public void limparDados() {
		getForm().reset();
	}

	public void mostrarDados() {
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

	public Hidden getHdnEmpresa() {
		return hdnEmpresa;
	}

	public void setHdnEmpresa(Hidden hdnEmpresa) {
		this.hdnEmpresa = hdnEmpresa;
	}
	
	public TextField getTxtCodigo() {
		return txtCodigo;
	}

	public void setTxtCodigo(TextField txtCodigo) {
		this.txtCodigo = txtCodigo;
	}

	public TextField getTxtModelo() {
		return txtModelo;
	}

	public void setTxtModelo(TextField txtModelo) {
		this.txtModelo = txtModelo;
	}

	public TextField getTxtSerie() {
		return txtSerie;
	}

	public void setTxtSerie(TextField txtSerie) {
		this.txtSerie = txtSerie;
	}

	public NumberField getTxtCaixa() {
		return txtCaixa;
	}

	public void setTxtCaixa(NumberField txtCaixa) {
		this.txtCaixa = txtCaixa;
	}

}
