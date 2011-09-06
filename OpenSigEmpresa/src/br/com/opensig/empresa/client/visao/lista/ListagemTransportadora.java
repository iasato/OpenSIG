package br.com.opensig.empresa.client.visao.lista;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.abstrato.IFormulario;
import br.com.opensig.empresa.shared.modelo.EmpTransportadora;

import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class ListagemTransportadora extends AListagemEntidade<EmpTransportadora> {

	public ListagemTransportadora(IFormulario<EmpTransportadora> formulario) {
		super(formulario);
		inicializar();
	}

	public void inicializar() {
		listaCampos.add(new IntegerFieldDef("empTransportadoraId"));
		listaColunas.add(new ColumnConfig(OpenSigCore.i18n.txtCod(), "empTransportadoraId", 50, true));
		super.configurar("empEntidade");

		campos = new RecordDef(listaCampos.toArray(new FieldDef[] {}));
		modelos = new ColumnModel(listaColunas.toArray(new BaseColumnConfig[] {}));
		super.inicializar();
	}
}
