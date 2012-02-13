package br.com.opensig.fiscal.server.sped.blocoE;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroE100 extends ARegistro<DadosE100, Dados> {

	@Override
	public void executar() {
		super.executar();
		RegistroE110 r110 = new RegistroE110();
		r110.setEsquitor(escritor);
		r110.setAuth(auth);
		r110.executar();
		qtdLinhas += r110.getQtdLinhas();
	}

	@Override
	protected DadosE100 getDados(Dados dados) throws Exception {
		DadosE100 d = new DadosE100();
		d.setDt_ini(inicio);
		d.setDt_fin(fim);
		return d;
	}

}
