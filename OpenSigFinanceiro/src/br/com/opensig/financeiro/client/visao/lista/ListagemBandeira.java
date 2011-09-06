package br.com.opensig.financeiro.client.visao.lista;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.abstrato.AListagem;
import br.com.opensig.core.client.visao.abstrato.IFormulario;
import br.com.opensig.financeiro.shared.modelo.FinBandeira;

import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class ListagemBandeira extends AListagem<FinBandeira> {

	public ListagemBandeira(IFormulario formulario) {
		super(formulario);
		inicializar();
	}

	public void inicializar() {
		// campos
		FieldDef[] fd = new FieldDef[] { new IntegerFieldDef("finBandeiraId"), new StringFieldDef("finBandeiraDescricao"), new BooleanFieldDef("finBandeiraDebito") };
		campos = new RecordDef(fd);

		// colunas
		ColumnConfig ccId = new ColumnConfig(OpenSigCore.i18n.txtCod(), "finBandeiraId", 50, true);
		ColumnConfig ccDescricao = new ColumnConfig(OpenSigCore.i18n.txtDescricao(), "finBandeiraDescricao", 300, true);
		ColumnConfig ccDebito = new ColumnConfig(OpenSigCore.i18n.txtDebito(), "finBandeiraDebito", 75, true, BOLEANO);

		BaseColumnConfig[] bcc = new BaseColumnConfig[] { ccId, ccDescricao, ccDebito };
		modelos = new ColumnModel(bcc);

		super.inicializar();
	}
}
