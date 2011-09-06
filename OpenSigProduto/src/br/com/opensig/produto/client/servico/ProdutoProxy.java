package br.com.opensig.produto.client.servico;

import java.util.List;

import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.produto.shared.modelo.ProdCategoria;
import br.com.opensig.produto.shared.modelo.ProdProduto;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class ProdutoProxy extends CoreProxy implements ProdutoServiceAsync {

	private ProdutoServiceAsync async = (ProdutoServiceAsync) GWT.create(ProdutoService.class);
	private ServiceDefTarget sdf = (ServiceDefTarget) async;

	public ProdutoProxy() {
		sdf.setServiceEntryPoint(GWT.getHostPageBaseURL() + "ProdutoService");
	}

	public void salvarProduto(ProdProduto produto, List<ProdCategoria> categorias, AsyncCallback<ProdProduto> asyncCallback) {
		async.salvarProduto(produto, categorias, asyncCallback);
	}
}
