package br.com.opensig.fiscal.client.visao.lista;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.controlador.comando.FabricaComando;
import br.com.opensig.core.client.visao.abstrato.IFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.fiscal.shared.modelo.FisNotaEntrada;

import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;

public class ListagemEntrada extends AListagemNota<FisNotaEntrada> {

	public ListagemEntrada(IFormulario<FisNotaEntrada> formulario) {
		super(formulario);
		nomes.put("id", "fisNotaEntradaId");
		nomes.put("numero", "fisNotaEntradaNumero");
		nomes.put("cadastro", "fisNotaEntradaCadastro");
		nomes.put("data", "fisNotaEntradaData");
		nomes.put("valor", "fisNotaEntradaValor");
		nomes.put("chave", "fisNotaEntradaChave");
		nomes.put("icms", "fisNotaEntradaIcms");
		nomes.put("ipi", "fisNotaEntradaIpi");
		nomes.put("pis", "fisNotaEntradaPis");
		nomes.put("cofins", "fisNotaEntradaCofins");
		nomes.put("protocolo", "fisNotaEntradaProtocolo");
		nomes.put("xml", "fisNotaEntradaXml");
		nomes.put("protocoloCancelado", "fisNotaEntradaProtocoloCancelado");
		nomes.put("xmlCancelado", "fisNotaEntradaXmlCancelado");
		nomes.put("recibo", "fisNotaEntradaRecibo");
		nomes.put("erro", "fisNotaEntradaErro");
		inicializar();
	}

	protected String getXml(FisNotaEntrada result) {
		return result.getFisNotaEntradaXml();
	}

	protected String getXmlCancelado(FisNotaEntrada result) {
		return result.getFisNotaEntradaXmlCancelado();
	};

	protected String getChave(FisNotaEntrada result) {
		return result.getFisNotaEntradaChave();
	}

	protected String getErro(FisNotaEntrada result) {
		return result.getFisNotaEntradaErro();
	}

	protected void mostrarErro(FisNotaEntrada result) {
		TextArea txtErro = new TextArea(OpenSigCore.i18n.txtErro(), "fisNotaErro");
		txtErro.setSize(790, 590);
		txtErro.setReadOnly(true);
		txtErro.setValue(result.getFisNotaEntradaErro());

		Window wnd = new Window(OpenSigCore.i18n.txtErro(), 800, 600, true, true);
		wnd.add(txtErro);
		wnd.show();
	}

	@Override
	public void irPara() {
		Menu mnuContexto = new Menu();

		// compra
		String strCompra = FabricaComando.getInstancia().getComandoCompleto("ComandoCompra");
		SisFuncao compra = UtilClient.getFuncaoPermitida(strCompra);
		MenuItem itemCompra = gerarFuncao(compra, "fisNotaEntrada.fisNotaEntradaId", "fisNotaEntradaId");
		if (itemCompra != null) {
			mnuContexto.addItem(itemCompra);
		}

		if (mnuContexto.getItems().length > 0) {
			MenuItem mnuItem = getIrPara();
			mnuItem.setMenu(mnuContexto);

			getMenu().addSeparator();
			getMenu().addItem(mnuItem);
		}
	}
}
