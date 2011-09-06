package br.com.opensig.comercial.client.servico;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComValorProduto;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.client.servico.CoreServiceAsync;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ComercialServiceAsync extends CoreServiceAsync {
	
	public abstract void gerarNfe(ComVenda venda, ComFrete frete, AsyncCallback<FisNotaSaida> asyncCallback);
	
	public abstract void analisarNfe(String nomeArquivo, AsyncCallback<ComCompra> asyncCallback);
	
	public abstract void importarNfe(String nomeArquivo, ComCompra compra, AsyncCallback asyncCallback);

	public abstract void fecharCompra(ComCompra compra, AsyncCallback asyncCallback);
	
	public abstract void fecharVenda(ComVenda venda, AsyncCallback<String[][]> asyncCallback);
	
	public abstract void fecharFrete(ComFrete frete, AsyncCallback asyncCallback);
	
	public abstract void salvarCompra(ComCompra compra, AsyncCallback<ComCompra> asyncCallback);
	
	public abstract void salvarVenda(ComVenda venda, AsyncCallback<ComVenda> asyncCallback);
	
	public abstract void salvarValor(ComValorProduto valor, AsyncCallback<ComValorProduto> asyncCallback);
	
	public abstract void excluirCompra(ComCompra compra, AsyncCallback asyncCallback);
	
	public abstract void excluirVenda(ComVenda venda, AsyncCallback asyncCallback);
	
	public abstract void cancelarVenda(ComVenda venda, AsyncCallback asyncCallback);
	
	public abstract void excluirFrete(ComFrete frete, AsyncCallback asyncCallback);
}
