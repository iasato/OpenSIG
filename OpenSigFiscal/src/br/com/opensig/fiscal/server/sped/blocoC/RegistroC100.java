package br.com.opensig.fiscal.server.sped.blocoC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComCompraProduto;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.comercial.shared.modelo.ComVendaProduto;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.financeiro.shared.modelo.FinPagar;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.server.sped.IRegistro;
import br.com.opensig.nfe.TNFe;
import br.com.opensig.nfe.TNFe.InfNFe.Det;
import br.com.opensig.nfe.TNFe.InfNFe.Ide;
import br.com.opensig.nfe.TNFe.InfNFe.Total.ICMSTot;
import br.com.opensig.nfe.TNFe.InfNFe.Transp;

public class RegistroC100 extends ARegistro<DadosC100, Dados> {

	private Map<String, List<DadosC170>> analitico = new HashMap<String, List<DadosC170>>();

	@Override
	public void executar() {
		qtdLinhas = 0;

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			TNFe nfe = null;
			IRegistro r140 = null;

			// processa as entradas / compras
			for (ComCompra compra : compras) {
				DadosC100 obj = null;
				// caso tenha nfe usa os dados dela
				if (compra.getComCompraNfe()) {
					// pega a NFe
					String xml = compra.getFisNotaEntrada().getFisNotaEntradaXml();
					int I = xml.indexOf("<infNFe");
					int F = xml.indexOf("</NFe>") + 6;
					String texto = "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">" + xml.substring(I, F);

					nfe = UtilServer.xmlToObj(texto, "br.com.opensig.nfe");
					obj = getDados(nfe);
					r140 = new RegistroNfeC140("1");
					r140.setDados(nfe);
				} else {
					obj = getCompra(compra);
					r140 = new RegistroC140<FinPagar>();
					r140.setDados(compra.getFinPagar());
				}
				// seta os dados padrao
				obj.setInd_oper("0");
				obj.setInd_emit(compra.getEmpFornecedor().getEmpEntidade().getEmpEntidadeDocumento1() == compra.getEmpEmpresa().getEmpEntidade().getEmpEntidadeDocumento1() ? "0" : "1");
				obj.setCod_part(compra.getEmpFornecedor().getEmpEntidade().getEmpEntidadeId() + "");
				obj.setCod_sit("00");
				out.write(obj);
				out.flush();

				// caso pagamento a prazo
				if (obj.getInd_pgto().equals("1")) {
					r140.setEsquitor(escritor);
					r140.setAuth(auth);
					r140.executar();
					qtdLinhas += r140.getQtdLinhas();
				}

				// produtos
				if (compra.getComCompraNfe()) {
					RegistroNfeC170 r170 = new RegistroNfeC170();
					r170.setEsquitor(escritor);
					r170.setAuth(auth);
					r170.setCrt(nfe.getInfNFe().getEmit().getCRT());
					r170.setNatureza(compra.getComNatureza().getComNaturezaId() + "");
					int item = 0;
					for (Det det : nfe.getInfNFe().getDet()) {
						r170.setProduto(compra.getComCompraProdutos().get(item++).getProdProduto());
						r170.setDados(det);
						r170.executar();
						setAnalitico(r170.getBloco());
						qtdLinhas += r170.getQtdLinhas();
					}
				} else {
					RegistroC170<ComCompraProduto> r170 = new RegistroC170<ComCompraProduto>();
					r170.setEsquitor(escritor);
					r170.setAuth(auth);
					for (ComCompraProduto prod : compra.getComCompraProdutos()) {
						r170.setDados(prod);
						r170.executar();
						setAnalitico(r170.getBloco());
						qtdLinhas += r170.getQtdLinhas();
					}
				}
				
				// analitico das compras
				getAnalitico(false);
			}

			// processa as saidas / vendas
			for (ComVenda venda : vendas) {
				DadosC100 obj = null;
				if (venda.getComVendaNfe()) {
					// pega a NFe
					String xml = venda.getFisNotaSaida().getFisNotaSaidaXml();
					int I = xml.indexOf("<infNFe");
					int F = xml.indexOf("</NFe>") + 6;
					String texto = "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">" + xml.substring(I, F);

					nfe = UtilServer.xmlToObj(texto, "br.com.opensig.nfe");
					obj = getDados(nfe);
					r140 = new RegistroNfeC140("0");
					r140.setDados(nfe);
				} else {
					obj = getVenda(venda);
					r140 = new RegistroC140<FinReceber>();
					r140.setDados(venda.getFinReceber());
				}

				obj.setInd_oper("1");
				obj.setInd_emit("0");
				obj.setCod_part(venda.getEmpCliente().getEmpEntidade().getEmpEntidadeId() + "");
				obj.setCod_sit(venda.getComVendaCancelada() ? "02" : "00");
				out.write(obj);
				out.flush();

				// caso pagamento a prazo
				if (obj.getInd_pgto().equals("1")) {
					r140.setEsquitor(escritor);
					r140.setAuth(auth);
					r140.executar();
					qtdLinhas += r140.getQtdLinhas();
				}

				// frete da nota
				if (!obj.getInd_frt().equals("9")) {
					RegistroC160 r160 = new RegistroC160();
					r160.setEsquitor(escritor);
					r160.setAuth(auth);
					r160.setDados(nfe.getInfNFe().getTransp());
					r160.executar();
					qtdLinhas += r160.getQtdLinhas();
				}

				// produtos
				if (venda.getComVendaNfe()) {
					RegistroNfeC170 r170 = new RegistroNfeC170();
					r170.setEsquitor(escritor);
					r170.setAuth(auth);
					r170.setCrt(nfe.getInfNFe().getEmit().getCRT());
					r170.setNatureza(venda.getComNatureza().getComNaturezaId() + "");
					int item = 0;
					for (Det det : nfe.getInfNFe().getDet()) {
						r170.setProduto(venda.getComVendaProdutos().get(item++).getProdProduto());
						r170.setDados(det);
						r170.executar();
						setAnalitico(r170.getBloco());
						qtdLinhas += r170.getQtdLinhas();
					}
				} else {
					RegistroC170<ComVendaProduto> r170 = new RegistroC170<ComVendaProduto>();
					r170.setEsquitor(escritor);
					r170.setAuth(auth);
					for (ComVendaProduto prod : venda.getComVendaProdutos()) {
						r170.setDados(prod);
						r170.executar();
						setAnalitico(r170.getBloco());
						qtdLinhas += r170.getQtdLinhas();
					}
				}
				
				// analitico das vendas
				getAnalitico(true);
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosC100 getDados(Dados dados) throws Exception {
		return null;
	}

	private DadosC100 getDados(TNFe nfe) throws Exception {
		Ide ide = nfe.getInfNFe().getIde();
		ICMSTot icms = nfe.getInfNFe().getTotal().getICMSTot();
		Transp transp = nfe.getInfNFe().getTransp();

		DadosC100 d = new DadosC100();
		d.setSer(ide.getSerie());
		d.setCod_mod("55");
		d.setChv_nfe(nfe.getInfNFe().getId().replace("NFe", ""));
		d.setNum_doc(Integer.valueOf(ide.getNNF()));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		d.setDt_doc(sdf.parse(ide.getDEmi()));
		d.setDt_e_s(ide.getDSaiEnt() == null ? d.getDt_doc() : sdf.parse(ide.getDSaiEnt()));
		d.setVl_doc(Double.valueOf(icms.getVNF()));
		d.setInd_pgto(ide.getIndPag());
		d.setVl_desc(Double.valueOf(icms.getVDesc()));
		d.setVl_abat_nt(0.00);
		d.setVl_merc(Double.valueOf(icms.getVProd()));
		d.setInd_frt(transp.getModFrete());
		d.setVl_frt(Double.valueOf(icms.getVFrete()));
		d.setVl_seg(Double.valueOf(icms.getVSeg()));
		d.setVl_out_da(Double.valueOf(icms.getVOutro()));
		d.setVl_bc_icms(Double.valueOf(icms.getVBC()));
		d.setVl_icms(Double.valueOf(icms.getVICMS()));
		d.setVl_bc_icms_st(Double.valueOf(icms.getVBCST()));
		d.setVl_icms_st(Double.valueOf(icms.getVST()));
		d.setVl_ipi(Double.valueOf(icms.getVIPI()));
		d.setVl_pis(Double.valueOf(icms.getVPIS()));
		d.setVl_cofins(Double.valueOf(icms.getVCOFINS()));
		d.setVl_pis_st(0.00);
		d.setVl_cofins_st(0.00);

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private DadosC100 getCompra(ComCompra compra) throws Exception {
		DadosC100 d = new DadosC100();
		d.setSer(compra.getComCompraSerie() + "");
		d.setCod_mod("01");
		d.setChv_nfe("");
		d.setNum_doc(compra.getComCompraNumero());
		d.setDt_doc(compra.getComCompraEmissao());
		d.setDt_e_s(compra.getComCompraRecebimento());
		d.setVl_doc(compra.getComCompraValorNota());
		// verifica se teve pagamento
		if (compra.getComCompraPaga()) {
			if (compra.getFinPagar().getFinPagamentos().size() > 1 || compra.getFinPagar().getFinPagamentos().get(0).getFinForma().getFinFormaId() > 1) {
				d.setInd_pgto("1");
			} else {
				d.setInd_pgto("0");
			}
		} else {
			d.setInd_pgto("9");
		}
		d.setVl_desc(compra.getComCompraValorDesconto());
		d.setVl_abat_nt(0.00);
		d.setVl_merc(compra.getComCompraValorProduto());
		// definindo o frete
		ComFrete frete_compra = null;
		for (ComFrete frete : fretes) {
			if (frete.getComFreteNota() == compra.getComCompraNumero() && frete.getEmpFornecedor().getEmpFornecedorId() == compra.getEmpFornecedor().getEmpFornecedorId()) {
				frete_compra = frete;
				break;
			}
		}
		if (frete_compra == null) {
			d.setInd_frt("9");
		} else {
			d.setInd_frt(frete_compra.getComFretePaga() ? "2" : "1");
		}
		d.setVl_frt(compra.getComCompraValorFrete());
		d.setVl_seg(compra.getComCompraValorSeguro());
		d.setVl_out_da(compra.getComCompraValorOutros());
		d.setVl_bc_icms(compra.getComCompraIcmsBase());
		d.setVl_icms(compra.getComCompraIcmsValor());
		d.setVl_bc_icms_st(compra.getComCompraIcmssubBase());
		d.setVl_icms_st(compra.getComCompraIcmssubValor());
		d.setVl_ipi(compra.getComCompraValorIpi());
		d.setVl_pis_st(0.00);
		d.setVl_cofins_st(0.00);
		d.setVl_pis(0.00);
		d.setVl_cofins(0.00);

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private DadosC100 getVenda(ComVenda venda) throws Exception {
		DadosC100 d = new DadosC100();
		d.setSer("");
		d.setCod_mod("01");
		d.setNum_doc(venda.getComVendaId());
		d.setChv_nfe("");
		d.setDt_doc(venda.getComVendaData());
		d.setDt_e_s(venda.getComVendaData());
		d.setVl_doc(venda.getComVendaValorLiquido());
		// verifica se teve recebimento
		if (venda.getComVendaRecebida()) {
			if (venda.getFinReceber().getFinRecebimentos().size() > 1 || venda.getFinReceber().getFinRecebimentos().get(0).getFinForma().getFinFormaId() > 1) {
				d.setInd_pgto("1");
			} else {
				d.setInd_pgto("0");
			}
		} else {
			d.setInd_pgto("9");
		}
		d.setVl_desc(venda.getComVendaValorBruto() - venda.getComVendaValorLiquido());
		d.setVl_abat_nt(0.00);
		d.setVl_merc(venda.getComVendaValorLiquido());
		d.setInd_frt("9");
		d.setVl_frt(0.00);
		d.setVl_seg(0.00);
		d.setVl_out_da(0.00);
		if (venda.getComNatureza().getComNaturezaIcms()) {
			d.setVl_bc_icms(venda.getComVendaValorLiquido());
			d.setVl_icms(venda.getComVendaValorLiquido() * 0.17);
		} else {
			d.setVl_bc_icms(0.00);
			d.setVl_icms(0.00);
		}
		d.setVl_bc_icms_st(0.00);
		d.setVl_icms_st(0.00);
		d.setVl_ipi(0.00);
		d.setVl_pis(0.00);
		d.setVl_cofins(0.00);
		d.setVl_pis_st(0.00);
		d.setVl_cofins_st(0.00);

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private void setAnalitico(DadosC170 d) {
		String chave = d.getCst_icms() + d.getCfop() + d.getAliq_icms();
		List<DadosC170> lista = analitico.get(chave);
		if (lista == null) {
			lista = new ArrayList<DadosC170>();
			lista.add(d);
			analitico.put(chave, lista);
		} else {
			lista.add(d);
		}
	}

	private void getAnalitico(boolean saida){
		if (!analitico.isEmpty()) {
			RegistroC190 r190 = new RegistroC190(saida);
			r190.setEsquitor(escritor);
			r190.setAuth(auth);
			for (Entry<String, List<DadosC170>> entry : analitico.entrySet()) {
				r190.setDados(entry.getValue());
				r190.executar();
				qtdLinhas += r190.getQtdLinhas();
			}
		}
		analitico.clear();
	}
}
