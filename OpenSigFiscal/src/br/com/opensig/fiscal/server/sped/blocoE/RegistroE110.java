package br.com.opensig.fiscal.server.sped.blocoE;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.server.sped.blocoC.RegistroC190;
import br.com.opensig.fiscal.server.sped.blocoC.RegistroC490;
import br.com.opensig.fiscal.server.sped.blocoD.RegistroD190;

public class RegistroE110 extends ARegistro<DadosE110, Dados> {

	@Override
	protected DadosE110 getDados(Dados dados) throws Exception {
		DadosE110 d = new DadosE110();
		d.setVl_tot_debitos(RegistroC190.ICMS_SAIDA + RegistroC490.ICMS_SAIDA + RegistroD190.ICMS_SAIDA);
		d.setVl_aj_debitos(0.00);
		d.setVl_tot_aj_debitos(0.00);
		d.setVl_estornos_cred(0.00);
		d.setVl_tot_creditos(RegistroC190.ICMS_ENTRADA + RegistroD190.ICMS_ENTRADA);
		d.setVl_aj_creditos(0.00);
		d.setVl_tot_aj_creditos(0.00);
		d.setVl_estornos_deb(0.00);
		d.setVl_sld_credor_ant(0.00);
		// soma de debitos
		double debitos = d.getVl_tot_debitos() + d.getVl_aj_debitos() + d.getVl_tot_aj_debitos() + d.getVl_estornos_cred();
		double creditos = d.getVl_tot_creditos() + d.getVl_aj_creditos() + d.getVl_tot_aj_creditos() + d.getVl_estornos_deb();
		double total = debitos - creditos;
		if (total >= 0) {
			d.setVl_sld_apurado(total);
			d.setVl_icms_recolher(total);
			d.setVl_sld_credor_transportar(0.00);
		} else {
			d.setVl_sld_apurado(0.00);
			d.setVl_icms_recolher(0.00);
			d.setVl_sld_credor_transportar(total * -1);
		}
		d.setVl_tot_ded(0.00);
		d.setDeb_esp(0.00);
		d.setVl_or(0.00);
		return d;
	}

}
