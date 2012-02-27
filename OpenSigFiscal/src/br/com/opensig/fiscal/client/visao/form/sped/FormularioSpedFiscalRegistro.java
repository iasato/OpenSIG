package br.com.opensig.fiscal.client.visao.form.sped;

import java.util.ArrayList;
import java.util.List;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.client.servico.FiscalProxy;
import br.com.opensig.fiscal.client.visao.lista.ListagemSpedRegistro;
import br.com.opensig.fiscal.shared.modelo.FisSpedBloco;
import br.com.opensig.fiscal.shared.modelo.FisSpedBlocoEmpresa;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioSpedFiscalRegistro extends AFormulario<FisSpedBloco> {

	private ListagemSpedRegistro gridRegistro;
	private FisSpedFiscal spedFiscal;
	private int[] preSelecionados;

	public FormularioSpedFiscalRegistro(SisFuncao funcao) {
		super(new FisSpedBloco(), funcao);
		inicializar();
	}

	@Override
	public void inicializar() {
		setTitle(OpenSigCore.i18n.txtSped() + " " + OpenSigCore.i18n.txtRegistro(), "icon-sped");
		setLabelAlign(Position.TOP);
		setButtonAlign(Position.CENTER);
		setMargins(1);
		setWidth(800);
		setHeight(600);

		add(new Hidden("id", "0"));
		gridRegistro = new ListagemSpedRegistro(this);

		// marcando os obrigatorios
		gridRegistro.getStore().addStoreListener(new StoreListenerAdapter() {
			public void onLoad(Store store, Record[] records) {
				super.onLoad(store, records);
				List<Record> selecionados = new ArrayList<Record>();

				for (Record rec : records) {
					if (rec.getAsBoolean("fisSpedBlocoObrigatorio")) {
						selecionados.add(rec);
					} else if (preSelecionados != null) {
						int id = rec.getAsInteger("fisSpedBlocoId");
						for (int i = 0; i < preSelecionados.length; i++) {
							if (preSelecionados[i] == id) {
								selecionados.add(rec);
								break;
							}
						}
					}
				}
				gridRegistro.getSelectionModel().selectRecords(selecionados.toArray(new Record[] {}));
			}
		});

		// impedindo de mudar os obrigatorios
		gridRegistro.getSelectionModel().addListener(new RowSelectionListenerAdapter() {
			public void onRowDeselect(RowSelectionModel sm, int rowIndex, Record record) {
				if (record.getAsBoolean("fisSpedBlocoObrigatorio")) {
					gridRegistro.getSelectionModel().selectRecords(record);
				}
			}
		});
		add(gridRegistro);
	}

	@Override
	public boolean setDados() {
		List<Integer> registros = new ArrayList<Integer>();
		for (Record rec : gridRegistro.getSelectionModel().getSelections()) {
			registros.add(rec.getAsInteger("fisSpedBlocoId"));
		}

		spedFiscal.setRegistros(registros.toArray(new Integer[] {}));
		contexto.put("classe", spedFiscal);

		if (registros.isEmpty()) {
			new ToastWindow(OpenSigCore.i18n.txtValidar(), OpenSigCore.i18n.errLista()).show();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void mostrarDados() {
		spedFiscal = (FisSpedFiscal) contexto.get("classe");
		
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		FiscalProxy<FisSpedBlocoEmpresa> proxy = new FiscalProxy<FisSpedBlocoEmpresa>(new FisSpedBlocoEmpresa());
		proxy.selecionar(fo, new AsyncCallback<Lista<FisSpedBlocoEmpresa>>() {

			public void onSuccess(Lista<FisSpedBlocoEmpresa> result) {
				preSelecionados = new int[result.getTotal()];
				for (int i = 0; i < result.getTotal(); i++) {
					preSelecionados[i] = Integer.valueOf(result.getDados()[i][1]);
				}
				mostrar();
			}

			public void onFailure(Throwable caught) {
				mostrar();
			}
		});
	}

	private void mostrar() {
		// filtro
		GrupoFiltro gf = new GrupoFiltro();
		FiltroBinario fb = new FiltroBinario(spedFiscal.getFisSpedFiscalTipo().contains("ICMS") ? "fisSpedBlocoIcmsIpi" : "fisSpedBlocoPisCofins", ECompara.IGUAL, 1);
		gf.add(fb, EJuncao.E);
		FiltroTexto ft = new FiltroTexto("fisSpedBlocoClasse", ECompara.DIFERENTE, "NULL");
		gf.add(ft);

		gridRegistro.getProxy().setFiltroPadrao(gf);
		gridRegistro.getStore().reload();
	}

	@Override
	public void limparDados() {
	}

	@Override
	public void gerarListas() {
	}

}