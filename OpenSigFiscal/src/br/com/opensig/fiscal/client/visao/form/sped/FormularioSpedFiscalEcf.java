package br.com.opensig.fiscal.client.visao.form.sped;

import java.util.ArrayList;
import java.util.List;

import br.com.opensig.comercial.client.visao.lista.ListagemEcf;
import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

import com.gwtext.client.core.Position;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxColumnConfig;
import com.gwtext.client.widgets.grid.CheckboxSelectionModel;
import com.gwtext.client.widgets.grid.ColumnModel;

public class FormularioSpedFiscalEcf extends AFormulario<ComEcf> {

	private ListagemEcf gridEcf;
	private FisSpedFiscal spedFiscal;

	public FormularioSpedFiscalEcf(SisFuncao funcao) {
		super(new ComEcf(), funcao);
		inicializar();
	}

	@Override
	public void inicializar() {
		setTitle(OpenSigCore.i18n.txtSped() + " " + OpenSigCore.i18n.txtEcf(), "icon-sped");
		setLabelAlign(Position.TOP);
		setButtonAlign(Position.CENTER);
		setMargins(1);
		setWidth(600);
		setHeight(200);

		add(new Hidden("id", "0"));
		gridEcf = new ListagemEcf(this) {
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
		gridEcf.getStore().addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				super.onLoad(store, records);
				gridEcf.getSelectionModel().selectRecords(records);
			}
		});

		add(gridEcf);
	}

	@Override
	public boolean setDados() {
		List<Integer> ecfs = new ArrayList<Integer>();
		for (Record rec : gridEcf.getSelectionModel().getSelections()) {
			ecfs.add(rec.getAsInteger("comEcfId"));
		}

		spedFiscal.setEcfs(ecfs.toArray(new Integer[] {}));
		contexto.put("classe", spedFiscal);

		return true;
	}

	@Override
	public void mostrarDados() {
		spedFiscal = (FisSpedFiscal) contexto.get("classe");

		// filtro
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		gridEcf.getProxy().setFiltroPadrao(fo);
		gridEcf.getStore().reload();
	}

	@Override
	public void limparDados() {
	}

	@Override
	public void gerarListas() {
	}

}