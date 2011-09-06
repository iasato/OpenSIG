package br.com.opensig.comercial.client.servico;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComValorProduto;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.client.servico.CoreService;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;

public interface ComercialService extends CoreService {

	public FisNotaSaida gerarNfe(ComVenda venda, ComFrete frete) throws ComercialException;
	
	public ComCompra analisarNfe(String nomeArquivo) throws ComercialException;
	
	public void importarNfe(String nomeArquivo, ComCompra compra) throws ComercialException;

	public void fecharCompra(ComCompra compra) throws ComercialException;

	public String[][] fecharVenda(ComVenda venda) throws ComercialException;

	public void fecharFrete(ComFrete frete) throws ComercialException;

	public ComCompra salvarCompra(ComCompra compra) throws ComercialException;

	public ComVenda salvarVenda(ComVenda venda) throws ComercialException;

	public ComValorProduto salvarValor(ComValorProduto valor) throws ComercialException;

	public void excluirCompra(ComCompra compra) throws ComercialException;

	public void excluirVenda(ComVenda venda) throws ComercialException;
	
	public void cancelarVenda(ComVenda venda) throws ComercialException;

	public void excluirFrete(ComFrete frete) throws ComercialException;
}
