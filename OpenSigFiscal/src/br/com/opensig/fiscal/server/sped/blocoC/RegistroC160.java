package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.nfe.TNFe.InfNFe.Transp;
import br.com.opensig.nfe.TNFe.InfNFe.Transp.Vol;

public class RegistroC160 extends ARegistro<DadosC160, Transp> {

	@Override
	protected DadosC160 getDados(Transp dados) throws Exception {
		DadosC160 d = new DadosC160();
		d.setCod_part("");
		d.setVeic_id("");
		d.setUf_id("");
		d.setQtd_vol(1);
		d.setPeso_brt(0.00);
		d.setPeso_liq(0.00);

		// TODO refazer usando os dados da nova tabela aux de frete da nota
		if (dados != null) {
			try {
				Vol vol = dados.getVol().get(0);
				d.setQtd_vol(Integer.valueOf(vol.getQVol()));
				d.setPeso_brt(Double.valueOf(vol.getPesoB()));
				d.setPeso_liq(Double.valueOf(vol.getPesoL()));
			} catch (Exception e) {
				// mantem o padrao e os modificados
			}
		}

		return d;
	}
}
