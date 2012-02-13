package br.com.opensig.fiscal.server.sped.blocoC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC190 extends ARegistro<DadosC190, List<DadosC170>> {

	public static double ICMS_SAIDA = 0.00;
	public static double ICMS_ENTRADA = 0.00;
	public static Map<String, List<DadosC170>> IPI = new HashMap<String, List<DadosC170>>();
	private boolean saida;

	public RegistroC190(boolean saida) {
		super();
		this.saida = saida;
	}

	@Override
	protected DadosC190 getDados(List<DadosC170> dados) throws Exception {
		DadosC190 d = new DadosC190();
		for (DadosC170 c170 : dados) {
			d.setCst_icms(c170.getCst_icms());
			d.setCfop(c170.getCfop());
			d.setAliq_icms(c170.getAliq_icms());
			d.setVl_opr(d.getVl_opr() + c170.getVl_item());
			d.setVl_bc_icms(d.getVl_bc_icms() + c170.getVl_bc_icms());
			d.setVl_icms(d.getVl_icms() + c170.getVl_icms());
			d.setVl_bc_icms_st(d.getVl_bc_icms_st() + c170.getVl_bc_icms_st());
			d.setVl_icms_st(d.getVl_icms_st() + c170.getVl_icms_st());
			d.setVl_red_bc(d.getVl_red_bc() + (c170.getVl_item() - c170.getVl_bc_icms()));
			d.setVl_ipi(d.getVl_ipi() + c170.getVl_ipi());
			d.setCod_obs("");

			// insere para controle de ipi
			if (auth.getConf().get("sped.0000.ind_ativ").equals("0")) {
				String chave = c170.getCfop() + c170.getCst_ipi();
				List<DadosC170> lista = IPI.get(chave);
				if (lista != null) {
					lista.add(c170);
				} else {
					lista = new ArrayList<DadosC170>();
					lista.add(c170);
					IPI.put(chave, lista);
				}
			}
		}

		// gerando dados para o registro E110
		if (saida) {
			ICMS_SAIDA += d.getVl_icms();
		} else {
			ICMS_ENTRADA += d.getVl_icms();
		}

		return d;
	}

}
