package br.com.opensig.fiscal.server.sped.bloco0;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0990;

public class Registro0990 extends ARegistro<Dados0990, Dados> {

	public Registro0990() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0990.xml");
	}

	@Override
	protected Dados0990 getDados(Dados dados) throws Exception {
		Dados0990 d = new Dados0990();
		d.setQtd_lin(qtdLinhas + 1);
		
		qtdLinhas = 0;
		fimBloco = true;
		return d;
	}
}
