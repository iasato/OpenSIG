package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class RegistroC470 extends ARegistro<DadosC470, ComEcfVendaProduto> {

	@Override
	protected DadosC470 getDados(ComEcfVendaProduto dados) throws Exception {
		ProdProduto produto = dados.getProdProduto();
		DadosC470 d = new DadosC470();
		d.setCod_item(produto.getProdProdutoId() + "");
		d.setQtd(dados.getComEcfVendaProdutoQuantidade());
		d.setQtd_canc(dados.getComEcfVendaProdutoCancelado() ? dados.getComEcfVendaProdutoQuantidade() : 0);
		d.setUnid(dados.getProdEmbalagem().getProdEmbalagemNome());
		d.setCfop(produto.getProdTributacao().getProdTributacaoCfop());
		d.setVl_item(dados.getComEcfVendaProdutoTotal());
		if (auth.getConf().get("nfe.crt").equals("1")) {
			d.setCst_icms(produto.getProdTributacao().getProdTributacaoCson());
			d.setAliq_icms(0.00);
		} else {
			d.setCst_icms((produto.getProdOrigem().getProdOrigemId() - 1) + produto.getProdTributacao().getProdTributacaoCst());
			d.setAliq_icms(produto.getProdTributacao().getProdTributacaoDentro());
		}
		d.setVl_pis(0.00);
		d.setVl_cofins(0.00);

		return d;
	}
}
