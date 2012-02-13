package br.com.opensig.fiscal.server.sped.blocoE;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroE500 extends ARegistro<DadosE500, Dados> {

	@Override
	public void executar() {
		if (auth.getConf().get("sped.0000.ind_ativ").equals("0")) {
			super.executar();
			RegistroE510 r510 = new RegistroE510();
			r510.setEsquitor(escritor);
			r510.setAuth(auth);
			r510.executar();
			qtdLinhas += r510.getQtdLinhas();

			RegistroE520 r520 = new RegistroE520();
			r520.setEsquitor(escritor);
			r520.setAuth(auth);
			r520.executar();
			qtdLinhas += r520.getQtdLinhas();
		} else {
			qtdLinhas = 0;
		}
	}

	@Override
	protected DadosE500 getDados(Dados dados) throws Exception {
		DadosE500 d = new DadosE500();
		d.setInd_apur("0");
		d.setDt_ini(inicio);
		d.setDt_fin(fim);
		return d;
	}

}
