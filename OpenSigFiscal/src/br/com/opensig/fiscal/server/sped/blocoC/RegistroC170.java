package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.comercial.shared.modelo.ComCompraProduto;
import br.com.opensig.comercial.shared.modelo.ComVendaProduto;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.empresa.shared.modelo.EmpEstado;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class RegistroC170<T extends Dados> extends ARegistro<DadosC170, T> {

	private ProdProduto produto;
	private int item = 1;

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
		d.setUnid(produto.getProdEmbalagem().getProdEmbalagemNome());
		d.setVl_item(compra.getComCompraProdutoTotal());
		d.setInd_mov("0");
		int cfop = compra.getComCompraProdutoCfop(); 
		d.setCfop(cfop >= 5000 ? cfop - 4000 : cfop);
		d.setCod_nat(compra.getComCompra().getComNatureza().getComNaturezaId() + "");
		d.setCst_icms((produto.getProdOrigem().getProdOrigemId() - 1) + produto.getProdTributacao().getProdTributacaoCst());

		// icms
		if (compra.getComCompraProdutoIcms() > 0) {
			d.setVl_bc_icms(compra.getComCompraProdutoTotal());
			d.setAliq_icms(compra.getComCompraProdutoIcms());
			d.setVl_icms(d.getVl_bc_icms() * d.getAliq_icms() / 100);
		}

		// ipi
		d.setInd_apur("0");
		d.setCod_enq("");
		d.setCst_ipi(produto.getProdIpi().getProdIpiCstEntrada());
		d.setVl_bc_ipi(compra.getComCompraProdutoTotal());
		d.setAliq_ipi(compra.getComCompraProdutoIpi());
		double valor = compra.getComCompraProdutoTotal() * compra.getComCompraProdutoIpi() / 100;
		d.setVl_ipi(valor);

		// pis e cofins zerados, pois nao tem dados no manual
		d.setCst_pis("");
		d.setCst_cofins("");
		return d;
	}

	private DadosC170 getVenda(ComVendaProduto vp) {
		EmpEstado origem = sped.getEmpEmpresa().getEmpEntidade().getEmpEnderecos().get(0).getEmpMunicipio().getEmpEstado();
		EmpEstado destino = vp.getComVenda().getEmpCliente().getEmpEntidade().getEmpEnderecos().get(0).getEmpMunicipio().getEmpEstado();
		produto = vp.getProdProduto();
		DadosC170 d = new DadosC170();
		d.setNum_item(item++);
		d.setCod_item(produto.getProdProdutoId() + "");
		d.setDescr_compl("");
		d.setQtd(vp.getComVendaProdutoQuantidade());
		d.setUnid(produto.getProdEmbalagem().getProdEmbalagemNome());
		d.setVl_item(vp.getComVendaProdutoTotalLiquido());
		d.setInd_mov("0");
		int cfop = origem.equals(destino) ? produto.getProdTributacao().getProdTributacaoCfop() : produto.getProdTributacao().getProdTributacaoCfop() + 1000;
		d.setCfop(cfop);
		d.setCod_nat(vp.getComVenda().getComNatureza().getComNaturezaId() + "");
		if (vp.getComVenda().getComNatureza().getComNaturezaIcms()) {
			d.setCst_icms((produto.getProdOrigem().getProdOrigemId() - 1) + produto.getProdTributacao().getProdTributacaoCst());
		} else {
			d.setCst_icms(produto.getProdTributacao().getProdTributacaoCson());
		}

		// icms caso informado na venda
		if (vp.getComVendaProdutoIcms() > 0) {
			d.setVl_bc_icms(vp.getComVendaProdutoTotalLiquido());
			d.setAliq_icms(vp.getComVendaProdutoIcms());
			d.setVl_icms(d.getVl_bc_icms() * d.getAliq_icms() / 100);
			// caso destaque icms
		} else if (vp.getComVenda().getComNatureza().getComNaturezaIcms()) {
			if (d.getCst_icms().endsWith("00")) {
				double aliq = origem.equals(destino) ? vp.getProdProduto().getProdTributacao().getProdTributacaoDentro() : vp.getProdProduto().getProdTributacao().getProdTributacaoFora();
				d.setVl_bc_icms(vp.getComVendaProdutoTotalLiquido());
				d.setAliq_icms(aliq);
				d.setVl_icms(d.getVl_bc_icms() * aliq / 100);
			}
		}

		// ipi
		d.setInd_apur("0");
		d.setCod_enq("");
		d.setCst_ipi(produto.getProdIpi().getProdIpiCstSaida());
		if (vp.getComVendaProdutoIpi() > 0) {
			d.setVl_bc_ipi(vp.getComVendaProdutoTotalLiquido());
			d.setAliq_ipi(vp.getComVendaProdutoIpi());
			d.setVl_ipi(d.getVl_bc_ipi() * d.getAliq_ipi() / 100);
		} else if (auth.getConf().get("sped.0000.ind_ativ").equals("0")) {
			d.setCod_enq(auth.getConf().get("nfe.ipi_enq"));
			d.setVl_bc_ipi(vp.getComVendaProdutoTotalLiquido());
			d.setAliq_ipi(produto.getProdIpi().getProdIpiAliquota());
			d.setVl_ipi(d.getVl_bc_ipi() * d.getAliq_ipi() / 100);
		}

		// pis e cofins zerados, pois nao tem dados no manual
		d.setCst_pis("");
		d.setCst_cofins("");
		return d;
	}
}
