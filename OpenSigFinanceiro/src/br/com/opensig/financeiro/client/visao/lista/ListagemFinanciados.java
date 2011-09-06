package br.com.opensig.financeiro.client.visao.lista;

import java.util.Date;
import java.util.List;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.core.client.visao.abstrato.AListagemEditor;
import br.com.opensig.core.client.visao.abstrato.IListagem;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.financeiro.shared.modelo.FinBandeira;
import br.com.opensig.financeiro.shared.modelo.FinForma;
import br.com.opensig.financeiro.shared.modelo.FinPagamento;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;

import com.gwtext.client.core.Ext;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.BooleanFieldDef;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.FloatFieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.DateField;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.EditorGridListenerAdapter;
import com.gwtextux.client.widgets.grid.plugins.GridSummaryPlugin;
import com.gwtextux.client.widgets.grid.plugins.SummaryColumnConfig;
import com.gwtextux.client.widgets.window.ToastWindow;

public class ListagemFinanciados<E extends Dados> extends AListagemEditor<E> {

	private Store storeForma;
	private String cartoes = "";

	public ListagemFinanciados(E classe, boolean barraTarefa) {
		super(classe, barraTarefa);
		inicializar();
		addPlugin(new GridSummaryPlugin());
	}

	public void inicializar() {
		// campos
		FieldDef[] fd = new FieldDef[] { new IntegerFieldDef("id"), new IntegerFieldDef("financeiroId"), new IntegerFieldDef("empresaId"), new StringFieldDef("empresaNome"),
				new StringFieldDef("nome"), new IntegerFieldDef("conta"), new IntegerFieldDef("finFormaId"), new StringFieldDef("finFormaDescricao"), new StringFieldDef("documento"),
				new FloatFieldDef("valor"), new StringFieldDef("parcela"), new DateFieldDef("cadastro"), new DateFieldDef("vencimento"), new BooleanFieldDef("quitado"), new DateFieldDef("realizado"),
				new IntegerFieldDef("nfe"), new StringFieldDef("observacao") };
		campos = new RecordDef(fd);

		FieldDef[] fdForma = new FieldDef[] { new IntegerFieldDef("finFormaId"), new StringFieldDef("finFormaDescricao") };
		CoreProxy<FinForma> proxy = new CoreProxy<FinForma>(new FinForma());
		storeForma = new Store(proxy, new ArrayReader(new RecordDef(fdForma)), false);
		storeForma.load();

		// colunas
		ColumnConfig ccId = new ColumnConfig("", "id", 10, false);
		ccId.setHidden(true);
		ccId.setFixed(true);

		ColumnConfig ccFinanceiroId = new ColumnConfig("", "financeiroId", 10, false);
		ccFinanceiroId.setHidden(true);
		ccFinanceiroId.setFixed(true);

		ColumnConfig ccEmpresaId = new ColumnConfig("", "empresaId", 10, false);
		ccEmpresaId.setHidden(true);
		ccEmpresaId.setFixed(true);

		ColumnConfig ccEmpresaNome = new ColumnConfig("", "empresaNome", 10, false);
		ccEmpresaNome.setHidden(true);
		ccEmpresaNome.setFixed(true);

		ColumnConfig ccNome = new ColumnConfig("", "nome", 10, false);
		ccNome.setHidden(true);
		ccNome.setFixed(true);

		ColumnConfig ccConta = new ColumnConfig("", "conta", 10, false);
		ccConta.setHidden(true);
		ccConta.setFixed(true);

		ColumnConfig ccTipoId = new ColumnConfig(OpenSigCore.i18n.txtTipo(), "finFormaId", 150, false, new Renderer() {
			public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
				if (value != null) {
				    storeForma.filter("finFormaId", value.toString());
					Record reg = storeForma.getAt(0);
					storeForma.clearFilter();
					return reg.getAsString("finFormaDescricao");
				} else {
					return "";
				}
			}
		});
		ccTipoId.setEditor(new GridEditor(getForma()));

		ColumnConfig ccTipo = new ColumnConfig("", "finFormaDescricao", 10, false);
		ccTipo.setHidden(true);
		ccTipo.setFixed(true);

		ColumnConfig ccDocumento = new ColumnConfig(OpenSigCore.i18n.txtDocumento(), "documento", 200, false);
		ccDocumento.setEditor(new GridEditor(getDocumento()));

		ColumnConfig ccParcela = new ColumnConfig(OpenSigCore.i18n.txtParcela(), "parcela", 50, false);

		ColumnConfig ccCadastro = new ColumnConfig("", "cadastro", 10, false);
		ccCadastro.setHidden(true);
		ccCadastro.setFixed(true);

		ColumnConfig ccVencimento = new ColumnConfig(OpenSigCore.i18n.txtVencimento(), "vencimento", 100, false, IListagem.DATA);
		ccVencimento.setEditor(new GridEditor(getVencimento()));

		ColumnConfig ccQuitado = new ColumnConfig(OpenSigCore.i18n.txtQuitado(), "quitado", 75, false, IListagem.BOLEANO);
		ccQuitado.setEditor(new GridEditor(new Checkbox()));

		ColumnConfig ccRealizado = new ColumnConfig("", "realizado", 10, false);
		ccRealizado.setHidden(true);
		ccRealizado.setFixed(true);

		ColumnConfig ccNfe = new ColumnConfig("", "nfe", 10, false);
		ccNfe.setHidden(true);
		ccNfe.setFixed(true);

		ColumnConfig ccObservacao = new ColumnConfig("", "observacao", 10, false);
		ccObservacao.setHidden(true);
		ccObservacao.setFixed(true);
		
		// sumarios
		ColumnConfig ccValor = new ColumnConfig(OpenSigCore.i18n.txtValor(), "valor", 75, false, IListagem.DINHEIRO);
		ccValor.setEditor(new GridEditor(getValor()));
		SummaryColumnConfig sumValor = new SummaryColumnConfig(SummaryColumnConfig.SUM, ccValor, IListagem.DINHEIRO);

		BaseColumnConfig[] bcc = new BaseColumnConfig[] { ccId, ccFinanceiroId, ccEmpresaId, ccEmpresaNome, ccNome, ccConta, ccTipoId, ccTipo, ccDocumento, sumValor, ccParcela, ccCadastro,
				ccVencimento, ccQuitado, ccRealizado, ccNfe, ccObservacao };
		modelos = new ColumnModel(bcc);

		addEditorGridListener(new EditorGridListenerAdapter() {
			public boolean doBeforeEdit(GridPanel grid, Record record, String field, Object value, int rowIndex, int colIndex) {
				if ((record.getAsBoolean("quitado") || field.equalsIgnoreCase("quitado")) && record.getAsInteger("id") > 0) {
					MessageBox.alert(OpenSigCore.i18n.txtAcesso(), OpenSigCore.i18n.txtAcessoNegado());
					return false;
				} else {
					return true;
				}
			}

			public void onAfterEdit(GridPanel grid, Record record, String field, Object newValue, Object oldValue, int rowIndex, int colIndex) {
				if (field.equalsIgnoreCase("quitado")) {
					if (Boolean.valueOf(newValue.toString())) {
						record.set("realizado", UtilClient.DATA);
					} else {
						record.set("realizado", (Date) null);
					}
				}
			}
		});

		// cartoes
		if (cartoes == null || cartoes.equals("")) {
			FieldDef[] fdBandeira = new FieldDef[] { new IntegerFieldDef("finBandeiraId"), new StringFieldDef("finBandeiraDescricao") };
			CoreProxy<FinBandeira> proxy1 = new CoreProxy<FinBandeira>(new FinBandeira());
			Store storeBandeira = new Store(proxy1, new ArrayReader(new RecordDef(fdBandeira)), false);
			storeBandeira.addStoreListener(new StoreListenerAdapter() {
				public void onLoad(Store store, Record[] records) {
					for (Record rec : records) {
						cartoes += rec.getAsString("finBandeiraDescricao") + " | ";
					}
				}
			});
			storeBandeira.load();
		}

		filtroPadrao = new FiltroNumero(classe.getCampoId(), ECompara.IGUAL, 0);
		setTitle(OpenSigCore.i18n.txtParcela(), "icon-preco");
		setHeight(Ext.getBody().getHeight() - 420);
		super.inicializar();
	}

	public boolean validar(List<E> lista) {
		boolean valida = true;
		Record[] recs = getStore().getRecords();

		for (Record rec : recs) {
			try {
				int id = rec.getAsInteger("id");
				int formaId = rec.getAsInteger("finFormaId");
				FinForma forma = new FinForma(formaId);
				String documento = rec.getAsString("documento");
				double valor = rec.getAsDouble("valor");
				String parcela = rec.getAsString("parcela");
				Date vencimento = rec.getAsDate("vencimento");
				boolean quitado = rec.getAsBoolean("quitado");
				Date realizado = rec.getAsDate("realizado");
				String obs = rec.getAsString("observacao");

				if (formaId < 1 || documento == null || valor < 0.01 || parcela.equals("") || vencimento == null) {
					throw new Exception();
				} else if (formaId == 2 && !cartoes.contains(documento.toUpperCase())) {
					new ToastWindow(OpenSigCore.i18n.msgCampoInvalido(), OpenSigCore.i18n.txtDocumento() + " = " + cartoes).show();
					throw new Exception();
				}

				if (classe instanceof FinPagamento) {
					FinPagamento fin = new FinPagamento();
					fin.setFinPagamentoId(id);
					fin.setFinForma(forma);
					fin.setFinPagamentoDocumento(documento);
					fin.setFinPagamentoValor(valor);
					fin.setFinPagamentoParcela(parcela);
					fin.setFinPagamentoCadastro(UtilClient.DATA);
					fin.setFinPagamentoVencimento(vencimento);
					fin.setFinPagamentoQuitado(quitado);
					fin.setFinPagamentoRealizado(realizado);
					fin.setFinPagamentoObservacao(obs);
					lista.add((E) fin);
				} else {
					FinRecebimento fin = new FinRecebimento();
					fin.setFinRecebimentoId(id);
					fin.setFinForma(forma);
					fin.setFinRecebimentoDocumento(documento);
					fin.setFinRecebimentoValor(valor);
					fin.setFinRecebimentoParcela(parcela);
					fin.setFinRecebimentoCadastro(UtilClient.DATA);
					fin.setFinRecebimentoVencimento(vencimento);
					fin.setFinRecebimentoQuitado(quitado);
					fin.setFinRecebimentoRealizado(realizado);
					fin.setFinRecebimentoObservacao(obs);
					lista.add((E) fin);
				}
			} catch (Exception ex) {
				valida = false;
				int row = getStore().indexOf(rec);
				getView().getRow(row).getStyle().setColor("red");
			}
		}

		return getStore().getRecords().length > 0 && valida;
	}

	private ComboBox getForma() {
		ComboBox cmbForma = new ComboBox();
		cmbForma.setAllowBlank(false);
		cmbForma.setStore(storeForma);
		cmbForma.setTriggerAction(ComboBox.ALL);
		cmbForma.setMode(ComboBox.LOCAL);
		cmbForma.setDisplayField("finFormaDescricao");
		cmbForma.setValueField("finFormaId");
		cmbForma.setForceSelection(true);
		cmbForma.setEditable(false);
		return cmbForma;
	}

	private TextField getDocumento() {
		TextField txtDocumento = new TextField();
		txtDocumento.setAllowBlank(false);
		txtDocumento.setSelectOnFocus(true);
		txtDocumento.setMaxLength(50);
		return txtDocumento;
	}

	private NumberField getValor() {
		NumberField txtValor = new NumberField();
		txtValor.setAllowBlank(false);
		txtValor.setAllowNegative(false);
		txtValor.setSelectOnFocus(true);
		txtValor.setDecimalPrecision(2);
		txtValor.setMaxLength(13);
		return txtValor;
	}

	private DateField getVencimento() {
		DateField dtVencimento = new DateField();
		dtVencimento.setAllowBlank(false);
		dtVencimento.setSelectOnFocus(true);
		return dtVencimento;
	}

	public Store getStoreForma() {
		return storeForma;
	}

	public void setStoreForma(Store storeForma) {
		this.storeForma = storeForma;
	}

	public String getCartoes() {
		return cartoes;
	}

	public void setCartoes(String cartoes) {
		this.cartoes = cartoes;
	}
}
