package br.com.opensig.fiscal.server.sped.blocoC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.comercial.shared.modelo.ComEcfZTotais;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC420 extends ARegistro<DadosC420, ComEcfZTotais> {

	private List<ComEcfZTotais> totais;
	private Map<Integer, ComEcfVendaProduto> fiscal = new HashMap<Integer, ComEcfVendaProduto>();

	@Override
	public void executar() {
		qtdLinhas = 0;

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);

			ComEcfZTotais total = totais.get(0);
			for (ComEcfVenda venda : ecfs) {
				if (venda.getComEcf().getComEcfId() == total.getComEcfZ().getComEcf().getComEcfId() && venda.getComEcfVendaData().compareTo(total.getComEcfZ().getComEcfZData()) == 0) {
					for (ComEcfVendaProduto prodVenda : venda.getComEcfVendaProdutos()) {
						ComEcfVendaProduto vp = fiscal.get(prodVenda.getProdProduto().getProdProdutoId());
						if (vp == null) {
							fiscal.put(prodVenda.getProdProduto().getProdProdutoId(), prodVenda);
						} else {
							vp.setComEcfVendaProdutoQuantidade(vp.getComEcfVendaProdutoQuantidade() + prodVenda.getComEcfVendaProdutoQuantidade());
							vp.setComEcfVendaProdutoTotal(vp.getComEcfVendaProdutoTotal() + prodVenda.getComEcfVendaProdutoTotal());
						}
					}
				}
			}

			RegistroC425 r425 = new RegistroC425();
			r425.setEscritor(escritor);
			r425.setAuth(auth);

			for (ComEcfZTotais tot : totais) {
				if (tot.getComEcfZTotaisValor() > 0) {
					bloco = getDados(tot);
					out.write(bloco);
					out.flush();

					// somente perfil B
					if (auth.getConf().get("sped.0000.ind_perfil").equals("B")) {
						// itens que compoem este total
						int idTributacao = 0;
						if (tot.getComEcfZTotaisCodigo().equals("01T1700")) {
							idTributacao = Integer.valueOf(auth.getConf().get("sped.c420.tributado"));
						} else if (tot.getComEcfZTotaisCodigo().equals("F1")) {
							idTributacao = Integer.valueOf(auth.getConf().get("sped.c420.substituicao"));
						} else if (tot.getComEcfZTotaisCodigo().equals("I1")) {
							idTributacao = Integer.valueOf(auth.getConf().get("sped.c420.isento"));
						} else if (tot.getComEcfZTotaisCodigo().equals("N1")) {
							idTributacao = Integer.valueOf(auth.getConf().get("sped.c420.nao_tributado"));
						}

						if (idTributacao > 0) {
							r425.setIdTributacao(idTributacao);
							r425.setProdutos(fiscal.values());
							r425.executar();
							qtdLinhas += r425.getQtdLinhas();
						}
					}
				}
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosC420 getDados(ComEcfZTotais dados) throws Exception {
		DadosC420 d = new DadosC420();
		d.setCod_tot_par(dados.getComEcfZTotaisCodigo());
		d.setVlr_acum_tot(dados.getComEcfZTotaisValor());
		if (dados.getComEcfZTotaisCodigo().length() == 7) {
			d.setNr_tot(dados.getComEcfZTotaisCodigo().substring(0, 2));
			d.setDescr_nr_tot("Tributado");
		}

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	public List<ComEcfZTotais> getTotais() {
		return totais;
	}

	public void setTotais(List<ComEcfZTotais> totais) {
		this.totais = totais;
	}

}
