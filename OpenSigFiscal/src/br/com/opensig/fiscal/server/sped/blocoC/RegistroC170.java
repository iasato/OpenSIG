package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.comercial.shared.modelo.ComCompraProduto;
import br.com.opensig.comercial.shared.modelo.ComVendaProduto;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.blocoC.DadosC170;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class RegistroC170<T extends Dados> extends ARegistro<DadosC170, T> {

	private ProdProduto produto;
	private int item = 1;

	public RegistroC170() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/blocoC/BeanC170.xml");
	}

	@Override
	protected DadosC170 getDados(T dados) throws Exception {
		if (dados instanceof ComCompraProduto) {
			return getCompra((ComCompraProduto) dados);
		} else if (dados instanceof ComVendaProduto) {
			return getVenda((ComVendaProduto) dados);
		} else {
			return null;
		}
	}

	private DadosC170 getCompra(ComCompraProduto compra) {
		produto = compra.getProdProduto();
		DadosC170 d = new DadosC170();
		d.setNum_item(item++);
		d.setCod_item(produto.getProdProdutoId() + "");
		d.setDescr_compl("");
		d.setQtd(compra.getComCompraProdutoQuantidade());
		d.setUnid(produto.getProdEmbalagem().getProdEmbalagemId() + "");
		d.setVl_item(compra.getComCompraProdutoTotal());
		d.setVl_desc(0.00);
		d.setInd_mov("0");
		d.setCfop(compra.getComCompraProdutoCfop());
		d.setCod_nat(compra.getComCompra().getComNatureza().getComNaturezaId() + "");
		d.setCst_icms(produto.getProdTributacao().getProdTributacaoCst());

		// icms
		if (compra.getComCompraProdutoIcms() > 0) {
			d.setVl_bc_icms(Double.valueOf(compra.getComCompraProdutoTotal()));
			d.setAliq_icms(Double.valueOf(compra.getComCompraProdutoIcms()));
			double valor = compra.getComCompraProdutoTotal() * compra.getComCompraProdutoIcms() / 100;
			d.setVl_icms(Double.valueOf(valor));
		}

		// ipi
		d.setInd_apur("0");
		d.setCod_enq("");
		d.setCst_ipi("");
		if (compra.getComCompraProdutoIpi() > 0) {
			d.setCst_ipi("99");
			d.setVl_bc_ipi(compra.getComCompraProdutoTotal());
			d.setAliq_ipi(compra.getComCompraProdutoIpi());
			double valor = compra.getComCompraProdutoTotal() * compra.getComCompraProdutoIpi() / 100;
			d.setVl_ipi(valor);
		}

		// pis e cofins zerados, pois nao tem dados na manual
		d.setCst_pis("");
		d.setCst_cofins("");

		return d;
	}

	private DadosC170 getVenda(ComVendaProduto venda) {
		produto = venda.getProdProduto();
		DadosC170 d = new DadosC170();
		d.setNum_item(item++);
		d.setCod_item(produto.getProdProdutoId() + "");
		d.setDescr_compl("");
		d.setQtd(venda.getComVendaProdutoQuantidade());
		d.setUnid(produto.getProdEmbalagem().getProdEmbalagemId() + "");
		d.setVl_item(venda.getComVendaProdutoTotalLiquido());
		d.setVl_desc(0.00);
		d.setInd_mov("0");
		d.setCfop(produto.getProdTributacao().getProdTributacaoCfop());
		d.setCod_nat(venda.getComVenda().getComNatureza().getComNaturezaId() + "");
		d.setCst_icms(produto.getProdTributacao().getProdTributacaoCst());

		// icms
		if (venda.getComVendaProdutoIcms() > 0) {
			d.setVl_bc_icms(Double.valueOf(venda.getComVendaProdutoTotalLiquido()));
			d.setAliq_icms(Double.valueOf(venda.getComVendaProdutoIcms()));
			double valor = venda.getComVendaProdutoTotalLiquido() * venda.getComVendaProdutoIcms() / 100;
			d.setVl_icms(Double.valueOf(valor));
		}

		// ipi
		d.setInd_apur("0");
		d.setCod_enq("");
		d.setCst_ipi("");
		if (venda.getComVendaProdutoIpi() > 0) {
			d.setCst_ipi("99");
			d.setVl_bc_ipi(venda.getComVendaProdutoTotalLiquido());
			d.setAliq_ipi(venda.getComVendaProdutoIpi());
			double valor = venda.getComVendaProdutoTotalLiquido() * venda.getComVendaProdutoIpi() / 100;
			d.setVl_ipi(valor);
		}

		// pis e cofins zerados, pois nao tem dados na manual
		d.setCst_pis("");
		d.setCst_cofins("");

		return d;
	}
}
