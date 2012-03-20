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
import br.com.opensig.produto.shared.modelo.ProdEmbalagem;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class Registro0220 extends ARegistro<Dados0220, ProdEmbalagem> {

	private List<Integer> embalagens;

	@Override
	public void executar() {
		qtdLinhas = 0;
		embalagens = new ArrayList<Integer>();

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			// compras
			for (ComCompra compra : getCompras()) {
				for (ComCompraProduto cProd : compra.getComCompraProdutos()) {
					if (!embalagens.contains(cProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId())) {
						out.write(getDados(cProd.getProdProduto().getProdEmbalagem()));
						out.flush();
						embalagens.add(cProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId());
					}
				}
			}
			// vendas
			for (ComVenda venda : getVendas()) {
				if (!venda.getComVendaCancelada() && !venda.getComVendaNfe()) {
					for (ComVendaProduto vProd : venda.getComVendaProdutos()) {
						if (!embalagens.contains(vProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId())) {
							out.write(getDados(vProd.getProdProduto().getProdEmbalagem()));
							out.flush();
							embalagens.add(vProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId());
						}
					}
				}
			}
			// ecfs
			for (ComEcfVenda venda : getEcfs()) {
				if (!venda.getComEcfVendaCancelada()) {
					for (ComEcfVendaProduto eProd : venda.getComEcfVendaProdutos()) {
						if (!eProd.getComEcfVendaProdutoCancelado() && !embalagens.contains(eProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId())) {
							out.write(getDados(eProd.getProdProduto().getProdEmbalagem()));
							out.flush();
							embalagens.add(eProd.getProdProduto().getProdEmbalagem().getProdEmbalagemId());
						}
					}
				}
			}
			// estoque
			if (estoque != null) {
				for (ProdProduto prod : estoque) {
					if (!embalagens.contains(prod.getProdEmbalagem().getProdEmbalagemId())) {
						out.write(getDados(prod.getProdEmbalagem()));
						out.flush();
						embalagens.add(prod.getProdEmbalagem().getProdEmbalagemId());
					}
				}
			}
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected Dados0220 getDados(ProdEmbalagem emb) {
		Dados0220 d = new Dados0220();
		d.setUnid_conv(emb.getProdEmbalagemNome());
		d.setFat_conv(emb.getProdEmbalagemUnidade());

		normalizar(d);
		qtdLinhas++;
		return d;
	}
}
