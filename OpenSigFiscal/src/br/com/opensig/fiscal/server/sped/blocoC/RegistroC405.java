package br.com.opensig.fiscal.server.sped.blocoC;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.comercial.shared.modelo.ComEcfZTotais;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC405 extends ARegistro<DadosC405, ComEcfZ> {

	@Override
	public void executar() {
		// pega o Z
		qtdLinhas = 0;
		BeanWriter out = null;
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			out = factory.createWriter("EFD", escritor);
			bloco = getDados(dados);
			normalizar(bloco);
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}

		// zera dos sub-totais os itens do analitico
		for (ComEcfZTotais tot : dados.getComZTotais()) {
			if (tot.getComEcfZTotaisCodigo().equals("01T1700")) {
				tot.setComEcfZTotaisValor(0.00);
			} else if (tot.getComEcfZTotaisCodigo().equals("F1")) {
				tot.setComEcfZTotaisValor(0.00);
			} else if (tot.getComEcfZTotaisCodigo().equals("I1")) {
				tot.setComEcfZTotaisValor(0.00);
			} else if (tot.getComEcfZTotaisCodigo().equals("N1")) {
				tot.setComEcfZTotaisValor(0.00);
			}
		}

		// deixa em cache para corrigir sub-totais
		StringWriter sw = new StringWriter();

		// vendas da leitura Z
		RegistroC460 r460 = new RegistroC460();
		r460.setEsquitor(sw);
		r460.setAuth(auth);
		r460.setEcfs(ecfs);
		r460.setZ(dados);
		r460.executar();
		qtdLinhas += r460.getQtdLinhas();

		// analitico
		if (!r460.getAnalitico().isEmpty()) {
			RegistroC490 r490 = new RegistroC490();
			r490.setEsquitor(sw);
			r490.setAuth(auth);
			for (Entry<String, List<DadosC470>> entry : r460.getAnalitico().entrySet()) {
				r490.setDados(entry.getValue());
				r490.executar();
				qtdLinhas += r490.getQtdLinhas();

				DadosC490 c490 = r490.getBloco();
				if (c490.getCst_icms().endsWith("00")) {
					setSubTotal("01T1700", c490.getVl_opr());
				} else if (c490.getCst_icms().endsWith("10") || c490.getCst_icms().endsWith("60")) {
					setSubTotal("F1", c490.getVl_opr());
				} else if (c490.getCst_icms().endsWith("30") || c490.getCst_icms().endsWith("40")) {
					setSubTotal("I1", c490.getVl_opr());
				} else if (c490.getCst_icms().endsWith("41")) {
					setSubTotal("N1", c490.getVl_opr());
				}
			}
		}

		// escreve o Z com o total arrumado
		double totalZ = 0.00;
		for (ComEcfZTotais tot : dados.getComZTotais()) {
			if (tot.getComEcfZTotaisCodigo().contains("T") || tot.getComEcfZTotaisCodigo().endsWith("1")) {
				totalZ += tot.getComEcfZTotaisValor();
			}
		}
		bloco.setVl_brt(totalZ);
		out.write(bloco);
		out.flush();
		qtdLinhas++;

		// sub totais da leitura Z
		RegistroC420 r420 = new RegistroC420();
		r420.setEsquitor(escritor);
		r420.setAuth(auth);
		r420.setEcfs(ecfs);
		r420.setTotais(dados.getComZTotais());
		r420.executar();
		qtdLinhas += r420.getQtdLinhas();

		// escreve o que ficou no cache
		try {
			escritor.write(sw.toString());
		} catch (IOException e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosC405 getDados(ComEcfZ dados) throws Exception {
		DadosC405 d = new DadosC405();
		d.setDt_doc(dados.getComEcfZData());
		d.setCro(dados.getComEcfZCro());
		d.setCrz(dados.getComEcfZCrz());
		d.setNum_coo_fin(dados.getComEcfZCoo());
		d.setGt_fin(dados.getComEcfZTotal());
		d.setVl_brt(dados.getComEcfZBruto());
		return d;
	}

	private void setSubTotal(String codigo, double valor) {
		// procura se tem e atualiza
		boolean existe = false;
		for (ComEcfZTotais tot : dados.getComZTotais()) {
			if (tot.getComEcfZTotaisCodigo().equals(codigo)) {
				tot.setComEcfZTotaisValor(tot.getComEcfZTotaisValor() + valor);
				existe = true;
				break;
			}
		}

		// caso nao tenha adiciona
		if (!existe) {
			ComEcfZTotais total = new ComEcfZTotais();
			total.setComEcfZ(dados.getComZTotais().get(0).getComEcfZ());
			total.setComEcfZTotaisCodigo(codigo);
			total.setComEcfZTotaisValor(valor);
			dados.getComZTotais().add(total);
		}
	}

}
