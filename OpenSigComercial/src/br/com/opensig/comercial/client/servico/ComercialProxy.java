package br.com.opensig.comercial.client.servico;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComValorProduto;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class ComercialProxy extends CoreProxy implements ComercialServiceAsync {

	private ComercialServiceAsync async = (ComercialServiceAsync) GWT.create(ComercialService.class);
	private ServiceDefTarget sdf = (ServiceDefTarget) async;

	public ComercialProxy() {
		sdf.setServiceEntryPoint(GWT.getHostPageBaseURL() + "ComercialService");
	}

	public void gerarNfe(ComVenda venda, ComFrete frete, AsyncCallback<FisNotaSaida> asyncCallback) {
		async.gerarNfe(venda, frete, asyncCallback);
	}

	public void analisarNfe(String nomeArquivo, AsyncCallback<ComCompra> asyncCallback){
		async.analisarNfe(nomeArquivo, asyncCallback);
	}
	
	public void importarNfe(String nomeArquivo, ComCompra compra, AsyncCallback asyncCallback) {
		async.importarNfe(nomeArquivo, compra, asyncCallback);
	}
	
	public void fecharCompra(ComCompra compra, AsyncCallback asyncCallback) {
		async.fecharCompra(compra, asyncCallback);
	}

	public void fecharVenda(ComVenda venda, AsyncCallback<String[][]> asyncCallback) {
		async.fecharVenda(venda, asyncCallback);
	}

	public void fecharFrete(ComFrete frete, AsyncCallback asyncCallback) {
		async.fecharFrete(frete, asyncCallback);
	}

	public void salvarCompra(ComCompra compra, AsyncCallback<ComCompra> asyncCallback) {
		async.salvarCompra(compra, asyncCallback);
	}

	public void salvarVenda(ComVenda venda, AsyncCallback<ComVenda> asyncCallback) {
		async.salvarVenda(venda, asyncCallback);
	}

	public void salvarValor(ComValorProduto valor, AsyncCallback<ComValorProduto> asyncCallback) {
		async.salvarValor(valor, asyncCallback);
	}

	public void excluirCompra(ComCompra compra, AsyncCallback asyncCallback) {
		async.excluirCompra(compra, asyncCallback);
	}

	public void excluirVenda(ComVenda venda, AsyncCallback asyncCallback) {
		async.excluirVenda(venda, asyncCallback);
	}
	
	public void cancelarVenda(ComVenda venda, AsyncCallback asyncCallback) {
		async.cancelarVenda(venda, asyncCallback);
	}

	public void excluirFrete(ComFrete frete, AsyncCallback asyncCallback) {
		async.excluirFrete(frete, asyncCallback);
	}

}
