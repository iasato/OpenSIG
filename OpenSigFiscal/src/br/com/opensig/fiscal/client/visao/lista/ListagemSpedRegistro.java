package br.com.opensig.fiscal.client.visao.lista;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.abstrato.AListagem;
import br.com.opensig.core.client.visao.abstrato.IFormulario;
import br.com.opensig.fiscal.shared.modelo.FisSpedBloco;

import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;

public class ListagemSpedRegistro extends AListagem<FisSpedBloco> {

	public ListagemSpedRegistro(IFormulario<FisSpedBloco> formulario) {
		super(formulario);
		inicializar();
	}

	public void inicializar() {
		// campos
		FieldDef[] fd = new FieldDef[] { new IntegerFieldDef("fisSpedBlocoId"), new BooleanFieldDef("fisSpedBlocoIcmsIpi"), new BooleanFieldDef("fisSpedBlocoPisCofins"),
				new StringFieldDef("fisSpedBlocoLetra"), new StringFieldDef("fisSpedBlocoDescricao"), new StringFieldDef("fisSpedBlocoRegistro"), new BooleanFieldDef("fisSpedBlocoObrigatorio"),
				new StringFieldDef("fisSpedBlocoClasse"), new IntegerFieldDef("fisSpedBlocoOrdem"), new IntegerFieldDef("fisSpedBlocoNivel") };
		campos = new RecordDef(fd);

		// selected
		CheckboxSelectionModel model = new CheckboxSelectionModel();
		CheckboxColumnConfig check = new CheckboxColumnConfig(model);

		// colunas
		ColumnConfig ccBloco = new ColumnConfig(OpenSigCore.i18n.txtBloco(), "fisSpedBlocoLetra", 50, true);
		ColumnConfig ccDescricao = new ColumnConfig(OpenSigCore.i18n.txtDescricao(), "fisSpedBlocoDescricao", 300, true);
		ColumnConfig ccRegistro = new ColumnConfig(OpenSigCore.i18n.txtRegistro(), "fisSpedBlocoRegistro", 70, true);

		BaseColumnConfig[] bcc = new BaseColumnConfig[] { check, ccBloco, ccRegistro, ccDescricao };
		modelos = new ColumnModel(bcc);

		// remove opcoes
		barraTarefa = false;
		paginar = false;
		agrupar = false;
		super.inicializar();

		// seta atributos
		setHeader(false);
		setAutoScroll(true);
		setHeight(560);
		setMargins(0);
		setLoadMask(true);
		setStripeRows(true);
		setSelectionModel(model);
		setAutoExpandColumn(ccDescricao.getId());
	}

	@Override
	public void setGridFiltro() {
	}
}
