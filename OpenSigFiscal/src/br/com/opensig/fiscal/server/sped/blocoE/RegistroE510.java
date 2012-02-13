package br.com.opensig.fiscal.server.sped.blocoE;

import java.util.List;
import java.util.Map.Entry;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.server.sped.blocoC.DadosC170;
import br.com.opensig.fiscal.server.sped.blocoC.RegistroC190;

public class RegistroE510 extends ARegistro<DadosE510, List<DadosC170>> {

	@Override
	public void executar() {
		qtdLinhas = 0;

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);

			for (Entry<String, List<DadosC170>> entry : RegistroC190.IPI.entrySet()) {
				bloco = getDados(entry.getValue());
				out.write(bloco);
				out.flush();
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosE510 getDados(List<DadosC170> dados) throws Exception {
		DadosE510 d = new DadosE510();
		for (DadosC170 c170 : dados) {
			d.setCfop(c170.getCfop());
			d.setCst_ipi(c170.getCst_ipi());
			d.setVl_cont_ipi(d.getVl_cont_ipi() + c170.getVl_item());
			d.setVl_bc_ipi(d.getVl_bc_ipi() + c170.getVl_bc_ipi());
			d.setVl_ipi(d.getVl_ipi() + c170.getVl_ipi());
		}

		qtdLinhas++;
		normalizar(d);
		return d;
	}

}
