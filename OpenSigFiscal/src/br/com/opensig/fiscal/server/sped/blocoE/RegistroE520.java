package br.com.opensig.fiscal.server.sped.blocoE;

import java.util.List;
import java.util.Map.Entry;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.server.sped.blocoC.DadosC170;
import br.com.opensig.fiscal.server.sped.blocoC.RegistroC190;

public class RegistroE520 extends ARegistro<DadosE520, Dados> {

	@Override
	protected DadosE520 getDados(Dados dados) throws Exception {
		DadosE520 d = new DadosE520();
		d.setVl_sd_ant_ipi(0.00);
		// somando as entradas e saidas de ipi
		double saida = 0.00;
		double entrada = 0.00;
		for (Entry<String, List<DadosC170>> entry : RegistroC190.IPI.entrySet()) {
			for (DadosC170 c170 : entry.getValue()) {
				if (c170.getCfop() >= 5000) {
					saida += c170.getVl_ipi();
				} else {
					entrada += c170.getVl_ipi();
				}
			}
		}
		d.setVl_deb_ipi(saida);
		d.setVl_cred_ipi(entrada);
		d.setVl_od_ipi(0.00);
		d.setVl_oc_ipi(0.00);
		// totais
		double debitos = d.getVl_deb_ipi() + d.getVl_od_ipi();
		double creditos = d.getVl_cred_ipi() + d.getVl_oc_ipi() + d.getVl_sd_ant_ipi();
		double total = debitos - creditos;
		if (total < 0) {
			d.setVl_sc_ipi(total * -1);
			d.setVl_sd_ipi(0.00);
		} else {
			d.setVl_sc_ipi(0.00);
			d.setVl_sd_ipi(total);
		}

		return d;
	}

}
