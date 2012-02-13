package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.nfe.TNFe;
import br.com.opensig.nfe.TNFe.InfNFe.Cobr.Dup;

public class RegistroNfeC140 extends ARegistro<DadosC140, TNFe> {

	private String emitente;

	public RegistroNfeC140(String emitente) {
		super("/br/com/opensig/fiscal/server/sped/blocoC/BeanC140.xml");
		this.emitente = emitente;
	}

	@Override
	public void executar() {
		super.executar();
		RegistroNfeC141 r141 = new RegistroNfeC141();
		int par = 1;
		
		for (Dup rec : dados.getInfNFe().getCobr().getDup()) {
			r141.setEsquitor(escritor);
			r141.setAuth(auth);
			r141.setDados(rec);
			r141.setParcela(par++);
			r141.executar();
			qtdLinhas += r141.getQtdLinhas();
		}
	}

	@Override
	protected DadosC140 getDados(TNFe dados) throws Exception {
		DadosC140 d = new DadosC140();
		d.setInd_emit(emitente);
		d.setInd_tit("00");
		d.setDesc_tit("");
		d.setNum_tit(dados.getInfNFe().getIde().getNNF());
		d.setQtd_parc(Integer.valueOf(dados.getInfNFe().getCobr().getDup().size()));
		d.setVl_tit(Double.valueOf(dados.getInfNFe().getTotal().getICMSTot().getVNF()));
		return d;
	}

	public void setEmitente(String emitente) {
		this.emitente = emitente;
	}

	public String getEmitente() {
		return emitente;
	}
}
