package br.com.opensig.fiscal.server.sped.bloco0;

import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0001;

public class Registro0001 extends ARegistro<Dados0001> {

	public Registro0001() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0001.xml");
	}

	@Override
	protected Dados0001 getDados() throws Exception {
		Dados0001 d = new Dados0001();
		d.setReg("0001");
		d.setInd_mov(1);
		qtdLinhas = 1;
		return d;
	}

}
