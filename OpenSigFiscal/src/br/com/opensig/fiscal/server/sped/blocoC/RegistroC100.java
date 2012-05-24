package br.com.opensig.fiscal.server.sped.blocoC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import br.com.opensig.empresa.shared.modelo.EmpEstado;
import br.com.opensig.financeiro.shared.modelo.FinPagar;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.fiscal.server.sped.ARegistro;
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
					obj = getDados(nfe, "00", compra.getComCompraRecebimento());
				} else {
					obj = getCompra(compra);
				}
				// seta os dados padrao
				obj.setInd_oper("0");
				obj.setInd_emit(compra.getEmpFornecedor().getEmpEntidade().getEmpEntidadeDocumento1() == compra.getEmpEmpresa().getEmpEntidade().getEmpEntidadeDocumento1() ? "0" : "1");
				obj.setCod_part(compra.getEmpFornecedor().getEmpEntidade().getEmpEntidadeId() + "");
				out.write(obj);
				out.flush();

				// caso pagamento a prazo e nao for NFe
				if (!compra.getComCompraNfe() && obj.getInd_pgto().equals("1")) {
					RegistroC140 r140 = new RegistroC140<FinPagar>();
					r140.setDados(compra.getFinPagar());
					r140.setEscritor(escritor);
					r140.setAuth(auth);
					r140.executar();
					qtdLinhas += r140.getQtdLinhas();
				}

				// produtos
				if (compra.getComCompraNfe()) {
					RegistroNfeC170 r170 = new RegistroNfeC170();
					r170.setEscritor(escritor);
					r170.setAuth(auth);
					r170.setCrt(nfe.getInfNFe().getEmit().getCRT());
					r170.setNatureza(compra.getComNatureza().getComNaturezaId() + "");
					r170.setVenda(false);
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
					r170.setEscritor(escritor);
					r170.setAuth(auth);
					for (ComCompraProduto prod : compra.getComCompraProdutos()) {
						r170.setDados(prod);
						r170.executar();
						setAnalitico(r170.getBloco());
						qtdLinhas += r170.getQtdLinhas();
					}
				}

				// analitico das compras
				getAnalitico();
			}

			// processa as saidas / vendas
			for (ComVenda venda : vendas) {
				DadosC100 obj = null;
				String cod_sit = venda.getComVendaCancelada() ? "02" : "00";

				if (venda.getComVendaNfe()) {
					try {
						// pega a NFe
						String xml = venda.getFisNotaSaida().getFisNotaSaidaXml();
						int I = xml.indexOf("<infNFe");
						int F = xml.indexOf("</NFe>") + 6;
						String texto = "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">" + xml.substring(I, F);

						nfe = UtilServer.xmlToObj(texto, "br.com.opensig.nfe");
						obj = getDados(nfe, cod_sit, venda.getComVendaData());
					} catch (Exception e) {
						obj = getVenda(venda, cod_sit);
					}
				} else {
					obj = getVenda(venda, cod_sit);
				}

				obj.setInd_oper("1");
				obj.setInd_emit("0");
				if (cod_sit.equals("00")) {
					obj.setCod_part(venda.getEmpCliente().getEmpEntidade().getEmpEntidadeId() + "");
				}
				out.write(obj);
				out.flush();

				// so para vendas nao canceladas
				if (!venda.getComVendaCancelada()) {
					// caso pagamento a prazo e nao NFe
					if (!venda.getComVendaNfe() && obj.getInd_pgto().equals("1")) {
						RegistroC140 r140 = new RegistroC140<FinReceber>();
						r140.setDados(venda.getFinReceber());
						r140.setEscritor(escritor);
						r140.setAuth(auth);
						r140.executar();
						qtdLinhas += r140.getQtdLinhas();
					}

					// produtos
					if (venda.getComVendaNfe()) {
						RegistroNfeC170 r170 = new RegistroNfeC170();
						r170.setAuth(auth);
						r170.setCrt(nfe.getInfNFe().getEmit().getCRT());
						r170.setNatureza(venda.getComNatureza().getComNaturezaId() + "");
						r170.setVenda(true);
						int item = 0;
						// para NFe de saida nao precisa informar os produtos
						for (Det det : nfe.getInfNFe().getDet()) {
							r170.setProduto(venda.getComVendaProdutos().get(item++).getProdProduto());
							setAnalitico(r170.getDados(det));
						}
					} else {
						RegistroC170<ComVendaProduto> r170 = new RegistroC170<ComVendaProduto>();
						r170.setEscritor(escritor);
						r170.setAuth(auth);
						r170.setSped(sped);
						for (ComVendaProduto prod : venda.getComVendaProdutos()) {
							if (prod.getProdProduto().getProdComposicoes() == null) {
								r170.setDados(prod);
								r170.executar();
								setAnalitico(r170.getBloco());
								qtdLinhas += r170.getQtdLinhas();
							}
						}
					}

					// analitico da venda
					getAnalitico();
				}
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

	private DadosC100 getDados(TNFe nfe, String cod_sit, Date data) throws Exception {
		Ide ide = nfe.getInfNFe().getIde();
		ICMSTot icms = nfe.getInfNFe().getTotal().getICMSTot();
		Transp transp = nfe.getInfNFe().getTransp();

		DadosC100 d = new DadosC100();
		d.setCod_sit(cod_sit);
		d.setSer(ide.getSerie());
		d.setCod_mod("55");
		d.setChv_nfe(nfe.getInfNFe().getId().replace("NFe", ""));
		d.setNum_doc(Integer.valueOf(ide.getNNF()));

		if (cod_sit.equals("00")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			d.setDt_doc(sdf.parse(ide.getDEmi()));
			// data de saida/entrada
			if (ide.getDSaiEnt() != null) {
				Date e_s = sdf.parse(ide.getDSaiEnt());
				data = e_s.compareTo(data) == 1 ? e_s : data;
			} else {
				data = d.getDt_doc().compareTo(data) == 1 ? d.getDt_doc() : data;
			}
			d.setDt_e_s(data);
			d.setVl_doc(Double.valueOf(icms.getVNF()));

			// TODO em 01/07/2012 pode remover
			if (ide.getIndPag().equals("2")) {
				d.setInd_pgto("9");
			} else {
				d.setInd_pgto(ide.getIndPag());
			}

			d.setVl_desc(Double.valueOf(icms.getVDesc()));
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
		}

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private DadosC100 getCompra(ComCompra compra) throws Exception {
		DadosC100 d = new DadosC100();
		d.setSer(compra.getComCompraSerie() + "");
		d.setCod_mod("01");
		d.setChv_nfe("");
		d.setCod_sit("00");
		d.setNum_doc(compra.getComCompraNumero());
		d.setDt_doc(compra.getComCompraEmissao());
		d.setDt_e_s(compra.getComCompraRecebimento());
		d.setVl_doc(compra.getComCompraValorNota());
		// verifica se teve pagamento
		if (compra.getComCompraPaga() && compra.getFinPagar() != null) {
			if (compra.getFinPagar().getFinPagamentos().size() > 1 || compra.getFinPagar().getFinPagamentos().get(0).getFinForma().getFinFormaId() > 1) {
				d.setInd_pgto("1");
			} else {
				d.setInd_pgto("0");
			}
		} else {
			// TODO em 01/07/2012 mudar pra 2
			d.setInd_pgto("9");
		}
		d.setVl_desc(compra.getComCompraValorDesconto());
		d.setVl_merc(compra.getComCompraValorProduto());
		// definindo o frete
		ComFrete frete_compra = null;
		for (ComFrete frete : fretes) {
			if (frete.getComFreteNota() == compra.getComCompraNumero() && frete.getEmpFornecedor().getEmpFornecedorId() == compra.getEmpFornecedor().getEmpFornecedorId()) {
				frete_compra = frete;
				break;
			}
		}
		// TODO em 01/07/2012 mudar
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

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private DadosC100 getVenda(ComVenda venda, String cod_sit) throws Exception {
		DadosC100 d = new DadosC100();
		d.setCod_sit(cod_sit);
		d.setSer("");
		d.setCod_mod("01");
		d.setNum_doc(venda.getComVendaId());
		d.setChv_nfe("");

		if (cod_sit.equals("00")) {
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
				// TODO em 01/07/2012 mudar pra 2
				d.setInd_pgto("9");
			}
			double desc = venda.getComVendaValorBruto() - venda.getComVendaValorLiquido();
			d.setVl_desc(desc > 0 ? desc : 0);
			d.setVl_merc(venda.getComVendaValorLiquido());
			// TODO em 01/07/2012 mudar
			d.setInd_frt("9");
			if (venda.getComNatureza().getComNaturezaIcms()) {
				EmpEstado origem = sped.getEmpEmpresa().getEmpEntidade().getEmpEnderecos().get(0).getEmpMunicipio().getEmpEstado();
				EmpEstado destino = venda.getEmpCliente().getEmpEntidade().getEmpEnderecos().get(0).getEmpMunicipio().getEmpEstado();
				double bc = 0;
				double icms = 0;
				for (ComVendaProduto vp : venda.getComVendaProdutos()) {
					if (vp.getProdProduto().getProdTributacao().getProdTributacaoCst().equals("00")) {
						double aliq = origem.equals(destino) ? vp.getProdProduto().getProdTributacao().getProdTributacaoDentro() : vp.getProdProduto().getProdTributacao().getProdTributacaoFora();
						bc += vp.getComVendaProdutoTotalLiquido();
						icms += vp.getComVendaProdutoTotalLiquido() * aliq / 100;
					}
				}

				d.setVl_bc_icms(bc);
				d.setVl_icms(icms);
			}
		}

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

	private void getAnalitico() {
		if (!analitico.isEmpty()) {
			RegistroC190 r190 = new RegistroC190();
			r190.setEscritor(escritor);
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
