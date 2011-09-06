package br.com.opensig.comercial.client.visao.lista;

import br.com.opensig.core.client.OpenSigCore;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.layout.FitLayout;

public class ListagemValidarEstoque {

	public ListagemValidarEstoque(String[][] dados) {
		// janela
		final Window wnd = new Window();
		wnd.setLayout(new FitLayout());
		wnd.setModal(true);
		wnd.setWidth(600);
		wnd.setHeight(300);
		wnd.setClosable(false);
		wnd.setButtonAlign(Position.CENTER);
		wnd.setTitle(OpenSigCore.i18n.txtEstoque() + " -> " + OpenSigCore.i18n.txtFechar(), "icon-estoque");

		// dados
		FieldDef[] fd = new FieldDef[] { new StringFieldDef("descricao"), new StringFieldDef("referencia"), new IntegerFieldDef("estoque"), new IntegerFieldDef("quantidade") };

		// colunas
		ColumnConfig ccDescricao = new ColumnConfig(OpenSigCore.i18n.txtDescricao(), "descricao", 300, true);
		ColumnConfig ccEstoque = new ColumnConfig(OpenSigCore.i18n.txtEstoque(), "estoque", 75, true, new Renderer() {

			public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
				return "<span style='color:blue;'>" + value + "</span>";
			}
		});

		ColumnConfig ccQtd = new ColumnConfig(OpenSigCore.i18n.txtQtd(), "quantidade", 75, true, new Renderer() {

			public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
				return "<span style='color:red;'>" + value + "</span>";
			}
		});
		ColumnConfig ccRef = new ColumnConfig(OpenSigCore.i18n.txtRef(), "referencia", 100, true);

		BaseColumnConfig[] bcc = new BaseColumnConfig[] { ccDescricao, ccRef, ccEstoque, ccQtd };

		MemoryProxy proxy = new MemoryProxy(dados);
		ArrayReader reader = new ArrayReader(new RecordDef(fd));
		Store store = new Store(proxy, reader);
		store.load();

		// listagem
		GridPanel grid = new GridPanel();
		grid.setAutoScroll(true);
		grid.setStripeRows(true);
		grid.setStore(store);
		grid.setColumnModel(new ColumnModel(bcc));
		grid.setSelectionModel(new RowSelectionModel(true));

		// botao
		Button btn = new Button();
		btn.setText(OpenSigCore.i18n.txtCancelar());
		btn.setIconCls("icon-cancelar");
		btn.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				wnd.close();
			}
		});

		wnd.add(grid);
		wnd.addButton(btn);
		wnd.show();
	}

}
