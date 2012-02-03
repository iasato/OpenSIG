package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.blocoC.DadosC141;
import br.com.opensig.nfe.TNFe.InfNFe.Cobr.Dup;

import com.ibm.icu.text.SimpleDateFormat;

public class RegistroNfeC141 extends ARegistro<DadosC141, Dup> {

	private int parcela;

	public RegistroNfeC141() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/blocoC/BeanC141.xml");
	}

	@Override
	protected DadosC141 getDados(Dup dados) throws Exception {
		DadosC141 d = new DadosC141();
		d.setNum_parc(parcela);
		d.setDt_vcto(new SimpleDateFormat("yyyy-MM-dd").parse(dados.getDVenc()));
		d.setVl_parc(Double.valueOf(dados.getVDup()));
		return d;
	}

	public int getParcela() {
		return parcela;
	}

	public void setParcela(int parcela) {
		this.parcela = parcela;
	}
}
