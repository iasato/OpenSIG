package br.com.opensig.fiscal.server.sped.blocoC;

import java.util.ArrayList;
import java.util.List;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComEcf;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroData;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC400 extends ARegistro<DadosC400, ComEcf> {

	@Override
	public void executar() {
		qtdLinhas = 0;
		List<Integer> ecfId = new ArrayList<Integer>();

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			for (ComEcfVenda ecf : ecfs) {
				if (!ecfId.contains(ecf.getComEcf().getComEcfId())) {
					ecfId.add(ecf.getComEcf().getComEcfId());
					bloco = getDados(ecf.getComEcf());
					out.write(bloco);
					out.flush();
					setEcfZ(ecf.getComEcf());
				}
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosC400 getDados(ComEcf dados) throws Exception {
		DadosC400 d = new DadosC400();
		d.setCod_mod(dados.getComEcfCodigo());
		d.setEcf_mod(dados.getComEcfModelo());
		d.setEcf_fab(dados.getComEcfSerie());
		d.setEcf_cx(dados.getComEcfCaixa());

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	private void setEcfZ(ComEcf ecf) throws Exception {
		GrupoFiltro gf = new GrupoFiltro();
		FiltroData fd1 = new FiltroData("comEcfZData", ECompara.MAIOR_IGUAL, inicio);
		gf.add(fd1, EJuncao.E);
		FiltroData fd2 = new FiltroData("comEcfZData", ECompara.MENOR_IGUAL, fim);
		gf.add(fd2, EJuncao.E);
		FiltroObjeto fo = new FiltroObjeto("comEcf", ECompara.IGUAL, ecf);
		gf.add(fo);
		Lista<ComEcfZ> ecfz = service.selecionar(new ComEcfZ(), 0, 0, gf, false);

		RegistroC405 r405 = new RegistroC405();
		r405.setEscritor(escritor);
		r405.setAuth(auth);
		r405.setEcfs(ecfs);

		for (ComEcfZ z : ecfz.getLista()) {
			if (z.getComEcfZBruto() > 0.00) {
				r405.setDados(z);
				r405.executar();
				qtdLinhas += r405.getQtdLinhas();
			}
		}
	}

}
