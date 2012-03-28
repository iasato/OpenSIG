package br.com.opensig.fiscal.client.visao.form.sped;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.opensig.comercial.client.visao.lista.ListagemCompra;
import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
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

public class FormularioSpedFiscalCompra extends AFormulario<ComCompra> {

	private ListagemCompra gridCompra;
	private FisSpedFiscal spedFiscal;

	public FormularioSpedFiscalCompra(SisFuncao funcao) {
		super(new ComCompra(), funcao);
		inicializar();
	}

	@Override
	public void inicializar() {
		setTitle(OpenSigCore.i18n.txtSped() + " " + OpenSigCore.i18n.txtCompra(), "icon-sped");
		setLabelAlign(Position.TOP);
		setButtonAlign(Position.CENTER);
		setMargins(1);
		setWidth(1000);
		setHeight(700);

		add(new Hidden("id", "0"));
		gridCompra = new ListagemCompra(this) {
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
				setHeight(660);
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
		gridCompra.getStore().addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				super.onLoad(store, records);
				gridCompra.getSelectionModel().selectRecords(records);
			}
		});

		add(gridCompra);
	}

	@Override
	public boolean setDados() {
		List<Integer> compras = new ArrayList<Integer>();
		for (Record rec : gridCompra.getSelectionModel().getSelections()) {
			compras.add(rec.getAsInteger("comCompraId"));
		}

		spedFiscal.setCompras(compras.toArray(new Integer[] {}));
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
		FiltroBinario fb = new FiltroBinario("comCompraNfe", ECompara.IGUAL, spedFiscal.getFisSpedFiscalCompras());
		gf.add(fb, EJuncao.E);
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		gf.add(fo, EJuncao.E);
		FiltroData fdInicio = new FiltroData("comCompraRecebimento", ECompara.MAIOR_IGUAL, inicio);
		gf.add(fdInicio, EJuncao.E);
		FiltroData fdFim = new FiltroData("comCompraRecebimento", ECompara.MENOR, fim);
		gf.add(fdFim);

		gridCompra.getProxy().setFiltroPadrao(gf);
		gridCompra.getStore().reload();
	}

	@Override
	public void limparDados() {
	}

	@Override
	public void gerarListas() {
	}

}
