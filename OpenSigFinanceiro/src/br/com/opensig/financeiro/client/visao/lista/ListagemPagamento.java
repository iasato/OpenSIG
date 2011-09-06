package br.com.opensig.financeiro.client.visao.lista;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.FabricaComando;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.financeiro.client.controlador.comando.ComandoPagar;
import br.com.opensig.financeiro.client.visao.form.AFormularioFinanciado;
import br.com.opensig.financeiro.shared.modelo.FinPagamento;

import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;

public class ListagemPagamento extends AListagemFinanciado<FinPagamento> {

	public ListagemPagamento(AFormularioFinanciado<FinPagamento> formulario) {
		super(formulario);
		inicializar();
	}
	
	@Override
	public void irPara() {
		Menu mnuContexto = new Menu();

		// compra
		String strCompra = FabricaComando.getInstancia().getComandoCompleto("ComandoCompra");
		SisFuncao compra = UtilClient.getFuncaoPermitida(strCompra);
		MenuItem itemCompra = gerarFuncao(compra, "finPagar.finPagarId", "finPagar.finPagarId");
		if (itemCompra != null) {
			mnuContexto.addItem(itemCompra);
		}

		// frete
		String strFrete = FabricaComando.getInstancia().getComandoCompleto("ComandoFrete");
		SisFuncao frete = UtilClient.getFuncaoPermitida(strFrete);
		MenuItem itemFrete = gerarFuncao(frete, "finPagar.finPagarId", "finPagar.finPagarId");
		if (itemFrete != null) {
			mnuContexto.addItem(itemFrete);
		}
		
		// pagar
		SisFuncao pagar = UtilClient.getFuncaoPermitida(ComandoPagar.class);
		MenuItem itemPagar = gerarFuncao(pagar, "finPagarId", "finPagar.finPagarId");
		if (itemPagar != null) {
			mnuContexto.addItem(itemPagar);
		}

		if (mnuContexto.getItems().length > 0) {
			MenuItem mnuItem = getIrPara();
			mnuItem.setMenu(mnuContexto);

			getMenu().addSeparator();
			getMenu().addItem(mnuItem);
		}
	}
}
