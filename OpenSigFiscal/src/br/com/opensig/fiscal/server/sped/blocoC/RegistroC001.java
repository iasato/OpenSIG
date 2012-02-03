package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.blocoC.DadosC001;

public class RegistroC001 extends ARegistro<DadosC001, Dados> {

	public RegistroC001() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/blocoC/BeanC001.xml");
	}

	@Override
	protected DadosC001 getDados(Dados dados) throws Exception {
		DadosC001 d = new DadosC001();
		if (sped.getCompras().length == 0 && sped.getVendas().length == 0 && sped.getEcfs().length == 0) {
			d.setInd_mov(1);
		} else {
			d.setInd_mov(0);
		}
		return d;
	}

}
