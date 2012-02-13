package br.com.opensig.fiscal.server.sped.blocoH;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroH001 extends ARegistro<DadosH001, Dados> {

	@Override
	protected DadosH001 getDados(Dados dados) throws Exception {
		DadosH001 d = new DadosH001();
		d.setInd_mov(getSubBlocos("H") > 0 ? 0 : 1);
		return d;
	}

}
