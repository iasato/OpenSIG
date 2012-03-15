package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.nfe.TNFe.InfNFe.Det;
import br.com.opensig.nfe.TNFe.InfNFe.Det.Imposto.COFINS;
import br.com.opensig.nfe.TNFe.InfNFe.Det.Imposto.ICMS.ICMS00;
import br.com.opensig.nfe.TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101;
import br.com.opensig.nfe.TNFe.InfNFe.Det.Imposto.IPI;
import br.com.opensig.nfe.TNFe.InfNFe.Det.Imposto.PIS;
import br.com.opensig.nfe.TNFe.InfNFe.Det.Prod;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class RegistroNfeC170 extends ARegistro<DadosC170, Det> {

	private ProdProduto produto;
	private String natureza;
	private String crt;

	public RegistroNfeC170() {
		super("/br/com/opensig/fiscal/server/sped/blocoC/BeanC170.xml");
	}

	@Override
	protected DadosC170 getDados(Det dados) throws Exception {
		Prod prod = dados.getProd();

		DadosC170 d = new DadosC170();
		d.setNum_item(Integer.valueOf(dados.getNItem()));
		d.setCod_item(produto.getProdProdutoId() + "");
		d.setDescr_compl("");
		d.setQtd(Double.valueOf(prod.getQCom()));
		d.setUnid(produto.getProdEmbalagem().getProdEmbalagemNome());
		d.setVl_item(Double.valueOf(prod.getVProd()));
		d.setInd_mov("0");
		int cfop = Integer.valueOf(prod.getCFOP());
		if (cfop == 5929 || cfop == 6929) {
			d.setCfop(cfop - 4827);
		} else {
			d.setCfop(cfop >= 5000 ? cfop - 4000 : cfop);
		}
		d.setCod_nat(natureza);

		if (crt.equals("1")) {
			d.setCst_icms(produto.getProdTributacao().getProdTributacaoCson());
			if (d.getCst_icms().equals("101")) {
				ICMSSN101 icms = dados.getImposto().getICMS().getICMSSN101();
				if (icms != null) {
					d.setVl_bc_icms(d.getVl_item());
					d.setAliq_icms(Double.valueOf(icms.getPCredSN()));
					d.setVl_icms(Double.valueOf(icms.getVCredICMSSN()));
				}
			}
		} else {
			d.setCst_icms("0" + produto.getProdTributacao().getProdTributacaoCst());
			if (d.getCst_icms().endsWith("00")) {
				ICMS00 icms = dados.getImposto().getICMS().getICMS00();
				if (icms != null) {
					d.setVl_bc_icms(Double.valueOf(icms.getVBC()));
					d.setAliq_icms(Double.valueOf(icms.getPICMS()));
					d.setVl_icms(Double.valueOf(icms.getVICMS()));
				}
			}
		}

		// ipi
		d.setInd_apur("0");
		d.setCod_enq("");
		IPI ipi = dados.getImposto().getIPI();
		if (ipi != null) {
			try {
				d.setCst_ipi(ipi.getIPITrib().getCST());
				d.setVl_bc_ipi(Double.valueOf(ipi.getIPITrib().getVBC()));
				d.setAliq_ipi(Double.valueOf(ipi.getIPITrib().getPIPI()));
				d.setVl_ipi(Double.valueOf(ipi.getIPITrib().getVIPI()));
			} catch (Exception e) {
				d.setCst_ipi(ipi.getIPINT().getCST());
			} finally {
				int cst_ipi = Integer.valueOf(d.getCst_ipi());
				if (cst_ipi >= 50) {
					cst_ipi -= 50;
				}
				d.setCst_ipi(UtilServer.formataNumero(cst_ipi, 2, 0, false));
			}
		} else {
			d.setCst_ipi("");
		}

		// pis
		PIS pis = dados.getImposto().getPIS();
		if (pis != null) {
			if (pis.getPISAliq() != null) {
				d.setCst_pis(pis.getPISAliq().getCST());
				d.setVl_bc_pis(Double.valueOf(pis.getPISAliq().getVBC()));
				d.setAliq_pis(Double.valueOf(pis.getPISAliq().getPPIS()));
				d.setAliq2_pis(null);
				d.setVl_pis(Double.valueOf(pis.getPISAliq().getVPIS()));
			} else if (pis.getPISNT() != null) {
				d.setCst_pis(pis.getPISNT().getCST());
			} else if (pis.getPISOutr() != null) {
				d.setCst_pis(pis.getPISOutr().getCST());
				d.setVl_bc_pis(Double.valueOf(pis.getPISOutr().getVBC()));
				d.setAliq_pis(Double.valueOf(pis.getPISOutr().getPPIS()));
				d.setAliq2_pis(null);
				d.setQuant_bc_pis(pis.getPISOutr().getQBCProd() == null ? 0.00 : Double.valueOf(pis.getPISOutr().getQBCProd()));
				d.setVl_pis(Double.valueOf(pis.getPISOutr().getVPIS()));
			} else if (pis.getPISQtde() != null) {
				d.setCst_pis(pis.getPISQtde().getCST());
				d.setAliq_pis(null);
				d.setAliq2_pis(pis.getPISQtde().getVAliqProd() == null ? 0.00 : Double.valueOf(pis.getPISQtde().getVAliqProd()));
				d.setQuant_bc_pis(pis.getPISQtde().getQBCProd() == null ? 0.00 : Double.valueOf(pis.getPISQtde().getQBCProd()));
				d.setVl_pis(Double.valueOf(pis.getPISQtde().getVPIS()));
			}
		} else {
			d.setCst_pis("");
		}

		// cofins
		COFINS cofins = dados.getImposto().getCOFINS();
		if (cofins != null) {
			if (cofins.getCOFINSAliq() != null) {
				d.setCst_cofins(cofins.getCOFINSAliq().getCST());
				d.setVl_bc_cofins(Double.valueOf(cofins.getCOFINSAliq().getVBC()));
				d.setAliq_cofins(Double.valueOf(cofins.getCOFINSAliq().getPCOFINS()));
				d.setAliq2_cofins(null);
				d.setVl_cofins(Double.valueOf(cofins.getCOFINSAliq().getVCOFINS()));
			} else if (cofins.getCOFINSNT() != null) {
				d.setCst_cofins(cofins.getCOFINSNT().getCST());
			} else if (cofins.getCOFINSOutr() != null) {
				d.setCst_cofins(cofins.getCOFINSOutr().getCST());
				d.setVl_bc_cofins(Double.valueOf(cofins.getCOFINSOutr().getVBC()));
				d.setAliq_cofins(Double.valueOf(cofins.getCOFINSOutr().getPCOFINS()));
				d.setAliq2_cofins(null);
				d.setQuant_bc_cofins(cofins.getCOFINSOutr().getQBCProd() == null ? 0.00 : Double.valueOf(cofins.getCOFINSOutr().getQBCProd()));
				d.setVl_cofins(Double.valueOf(cofins.getCOFINSOutr().getVCOFINS()));
			} else if (cofins.getCOFINSQtde() != null) {
				d.setCst_cofins(cofins.getCOFINSQtde().getCST());
				d.setAliq_cofins(null);
				d.setAliq2_cofins(cofins.getCOFINSQtde().getVAliqProd() == null ? 0.00 : Double.valueOf(cofins.getCOFINSQtde().getVAliqProd()));
				d.setQuant_bc_cofins(cofins.getCOFINSQtde().getQBCProd() == null ? 0.00 : Double.valueOf(cofins.getCOFINSQtde().getQBCProd()));
				d.setVl_cofins(Double.valueOf(cofins.getCOFINSQtde().getVCOFINS()));
			}
		} else {
			d.setCst_cofins("");
		}

		return d;
	}

	public ProdProduto getProduto() {
		return produto;
	}

	public void setProduto(ProdProduto produto) {
		this.produto = produto;
	}

	public String getNatureza() {
		return natureza;
	}

	public void setNatureza(String natureza) {
		this.natureza = natureza;
	}

	public String getCrt() {
		return crt;
	}

	public void setCrt(String crt) {
		this.crt = crt;
	}
}
