package br.com.opensig.fiscal.client.visao.form.sped;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.opensig.comercial.client.visao.lista.ListagemFrete;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroData;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnModel;

public class FormularioSpedFiscalFrete extends AFormulario<ComFrete> {

	private ListagemFrete gridFrete;
	private FisSpedFiscal spedFiscal;

	public FormularioSpedFiscalFrete(SisFuncao funcao) {
		super(new ComFrete(), funcao);
		inicializar();
	}

	@Override
	public void inicializar() {
		setTitle(OpenSigCore.i18n.txtSped() + " " + OpenSigCore.i18n.txtFrete(), "icon-sped");
		setLabelAlign(Position.TOP);
		setButtonAlign(Position.CENTER);
		setMargins(1);
		setWidth(800);
		setHeight(600);

		add(new Hidden("id", "0"));
		gridFrete = new ListagemFrete(this) {
			public void inicializar() {
				// remove opcoes
				barraTarefa = false;
				paginar = false;
				agrupar = false;
				super.inicializar();

				// selected
				CheckboxSelectionModel model = new CheckboxSelectionModel();
				CheckboxColumnConfig check = new CheckboxColumnConfig(model);

				BaseColumnConfig[] bcc = modelos.getColumnConfigs();
				bcc[0] = check;
				modelos = new ColumnModel(bcc);

				// seta atributos
				setHeader(false);
				setAutoScroll(true);
				setHeight(600);
				setMargins(0);
				setLoadMask(true);
				setStripeRows(true);
				setColumnModel(modelos);
				setSelectionModel(model);
			};

			public void setGridFiltro() {
			};
		};

		// marcando todos
		gridFrete.getStore().addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				super.onLoad(store, records);
				gridFrete.getSelectionModel().selectRecords(records);
			}
		});

		add(gridFrete);
	}

	@Override
	public boolean setDados() {
		List<Integer> fretes = new ArrayList<Integer>();
		for (Record rec : gridFrete.getSelectionModel().getSelections()) {
			fretes.add(rec.getAsInteger("comFreteId"));
		}

		spedFiscal.setFretes(fretes.toArray(new Integer[] {}));
		contexto.put("classe", spedFiscal);

		return true;
	}

	@Override
	public void mostrarDados() {
		spedFiscal = (FisSpedFiscal) contexto.get("classe");

		// datas
		Date inicio = DateTimeFormat.getFormat("d-M-yyyy").parse("01-" + spedFiscal.getFisSpedFiscalMes() + "-" + spedFiscal.getFisSpedFiscalAno());
		Date fim = new Date(inicio.getTime());
		if (fim.getMonth() == 11) {
			fim.setMonth(0);
			fim.setYear(fim.getYear() + 1);
		} else {
			fim.setMonth(fim.getMonth() + 1);
		}

		// filtro
		GrupoFiltro gf = new GrupoFiltro();
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		gf.add(fo, EJuncao.E);
		FiltroData fdInicio = new FiltroData("comFreteRecebimento", ECompara.MAIOR_IGUAL, inicio);
		gf.add(fdInicio, EJuncao.E);
		FiltroData fdFim = new FiltroData("comFreteRecebimento", ECompara.MENOR, fim);
		gf.add(fdFim);

		gridFrete.getProxy().setFiltroPadrao(gf);
		gridFrete.getStore().reload();
	}

	@Override
	public void limparDados() {
	}

	@Override
	public void gerarListas() {
	}

}
