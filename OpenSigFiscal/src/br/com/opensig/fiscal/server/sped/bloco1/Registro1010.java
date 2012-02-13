package br.com.opensig.fiscal.server.sped.bloco1;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class Registro1010 extends ARegistro<Dados1010, Dados> {

	@Override
	protected Dados1010 getDados(Dados dados) throws Exception {
		Dados1010 d = new Dados1010();
		d.setInd_exp(auth.getConf().get("sped.1010.ind_exp"));
		d.setInd_ccrf(auth.getConf().get("sped.1010.ind_ccrf"));
		d.setInd_comb(auth.getConf().get("sped.1010.ind_comb"));
		d.setInd_usina(auth.getConf().get("sped.1010.ind_usina"));
		d.setInd_va(auth.getConf().get("sped.1010.ind_va"));
		d.setInd_ee(auth.getConf().get("sped.1010.ind_ee"));
		d.setInd_cart(auth.getConf().get("sped.1010.ind_cart"));
		d.setInd_form(auth.getConf().get("sped.1010.ind_form"));
		d.setInd_aer(auth.getConf().get("sped.1010.ind_aer"));
		return d;
	}

}
