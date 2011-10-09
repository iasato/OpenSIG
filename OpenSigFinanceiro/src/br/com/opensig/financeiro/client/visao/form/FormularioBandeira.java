package br.com.opensig.financeiro.client.visao.form;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.financeiro.shared.modelo.FinBandeira;

import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.TextField;

public class FormularioBandeira extends AFormulario<FinBandeira> {

	private Hidden hdnCod;
	private TextField txtDescricao;
	private Checkbox chkDebito;

	public FormularioBandeira(SisFuncao funcao) {
		super(new FinBandeira(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		hdnCod = new Hidden("finBandeiraId", "0");
		add(hdnCod);

		txtDescricao = new TextField(OpenSigCore.i18n.txtDescricao(), "finBandeiraDescricao", 300);
		txtDescricao.setAllowBlank(false);
		txtDescricao.setMaxLength(100);

		chkDebito = new Checkbox(OpenSigCore.i18n.txtDebito(), "finBandeiraDebito");

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(txtDescricao, 320);
		linha1.addToRow(chkDebito, 90);
		add(linha1);
	}

	public boolean setDados() {
		classe.setFinBandeiraId(Integer.valueOf(hdnCod.getValueAsString()));
		classe.setFinBandeiraDescricao(txtDescricao.getValueAsString());
		classe.setFinBandeiraDebito(chkDebito.getValue());
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
		txtDescricao.focus(true);
		
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

	public TextField getTxtDescricao() {
		return txtDescricao;
	}

	public void setTxtDescricao(TextField txtDescricao) {
		this.txtDescricao = txtDescricao;
	}

	public Checkbox getChkDebito() {
		return chkDebito;
	}

	public void setChkDebito(Checkbox chkDebito) {
		this.chkDebito = chkDebito;
	}
}
