package br.com.opensig.produto.client.servico;

import java.util.List;

import br.com.opensig.core.client.servico.CoreServiceAsync;
import br.com.opensig.produto.shared.modelo.ProdCategoria;
import br.com.opensig.produto.shared.modelo.ProdProduto;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ProdutoServiceAsync extends CoreServiceAsync {
	
	public abstract void salvarProduto(ProdProduto produto, List<ProdCategoria> categorias, AsyncCallback<ProdProduto> asyncCallback);
	
}
