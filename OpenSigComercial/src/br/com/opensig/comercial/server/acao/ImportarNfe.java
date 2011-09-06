package br.com.opensig.comercial.server.acao;

import br.com.opensig.comercial.client.servico.ComercialException;
import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComCompraProduto;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.produto.server.acao.SalvarProduto;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class ImportarNfe extends Chain {

	private CoreServiceImpl servico;
	private ComCompra compra;

	public ImportarNfe(Chain next, CoreServiceImpl servico, ComCompra compra) throws OpenSigException {
		super(null);
		this.compra = compra;
		this.servico = servico;
		
		// salva a compra
		SalvarCompra salCompra = new SalvarCompra(next, servico, compra);
		// valida os produtos
		ValidarProduto valProd = new ValidarProduto(salCompra);
		// valida o xml
		this.setNext(valProd);
	}

	@Override
	public void execute() throws OpenSigException {
		if (next != null) {
			next.execute();
		}
	}

	private class ValidarProduto extends Chain {

		public ValidarProduto(Chain next) throws OpenSigException {
			super(next);
		}

		@Override
		public void execute() throws OpenSigException {
			try {
				// salva os produtos novos
				for (ComCompraProduto comProd : compra.getComCompraProdutos()) {
					ProdProduto prod = comProd.getProdProduto();
					if (prod.getProdProdutoId() == 0) {
						SalvarProduto salProduto = new SalvarProduto(null, servico, prod, null);
						salProduto.execute();
					}
				}

				if (next != null) {
					next.execute();
				}
			} catch (Exception ex) {
				UtilServer.LOG.error("Erro ao validar o produto.", ex);
				throw new ComercialException(ex.getMessage());
			}
		}
	}
}
