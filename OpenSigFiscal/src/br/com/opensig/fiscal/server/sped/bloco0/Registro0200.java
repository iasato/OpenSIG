package br.com.opensig.fiscal.server.sped.bloco0;

import java.util.ArrayList;
import java.util.List;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComCompraProduto;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.comercial.shared.modelo.ComVendaProduto;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class Registro0200 extends ARegistro<Dados0200, ProdProduto> {

	private List<Integer> produtos;

	@Override
	public void executar() {
		qtdLinhas = 0;
		produtos = new ArrayList<Integer>();

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			// compras
			for (ComCompra compra : getCompras()) {
				for (ComCompraProduto cProd : compra.getComCompraProdutos()) {
					if (!produtos.contains(cProd.getProdProduto().getProdProdutoId())) {
						out.write(getDados(cProd.getProdProduto()));
						out.flush();
						produtos.add(cProd.getProdProduto().getProdProdutoId());
					}
				}
			}
			// vendas
			for (ComVenda venda : getVendas()) {
				for (ComVendaProduto vProd : venda.getComVendaProdutos()) {
					if (!produtos.contains(vProd.getProdProduto().getProdProdutoId())) {
						out.write(getDados(vProd.getProdProduto()));
						out.flush();
						produtos.add(vProd.getProdProduto().getProdProdutoId());
					}
				}
			}
			// ecfs
			for (ComEcfVenda venda : getEcfs()) {
				for (ComEcfVendaProduto eProd : venda.getComEcfVendaProdutos()) {
					if (!produtos.contains(eProd.getProdProduto().getProdProdutoId())) {
						out.write(getDados(eProd.getProdProduto()));
						out.flush();
						produtos.add(eProd.getProdProduto().getProdProdutoId());
					}
				}
			}
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected Dados0200 getDados(ProdProduto prod) {
		Dados0200 d = new Dados0200();
		d.setCod_item(prod.getProdProdutoId() + "");
		d.setDescr_item(prod.getProdProdutoDescricao());
		d.setCod_barra(prod.getProdProdutoBarra());
		d.setCod_ant_item("");
		d.setUnid_inv(prod.getProdEmbalagem().getProdEmbalagemNome());
		d.setTipo_item(0); // TODO colocar tabela auxiliar e habilitar no sistema
		if (prod.getProdProdutoNcm().length() == 8) {
			d.setCod_ncm(prod.getProdProdutoNcm());
		}
		d.setEx_ipi("");
		d.setAliq_icms(prod.getProdTributacao().getProdTributacaoDentro());

		normalizar(d);
		qtdLinhas++;
		return d;
	}

}
