package br.com.opensig.fiscal.server.sped.blocoH;

import java.io.IOException;
import java.io.StringWriter;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroH005 extends ARegistro<DadosH005, Dados> {

	private double total;

	@Override
	public void executar() {
		StringWriter sw = new StringWriter(1000000);
		RegistroH010 r010 = new RegistroH010();
		r010.setEscritor(sw);
		r010.setAuth(auth);
		r010.setService(service);
		r010.setSped(sped);
		r010.setEstoque(estoque);
		r010.executar();

		this.total = r010.getTotal();
		super.executar();
		qtdLinhas += r010.getQtdLinhas();
		try {
			escritor.append(sw.getBuffer());
			escritor.flush();
		} catch (IOException e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosH005 getDados(Dados dados) throws Exception {
		DadosH005 d = new DadosH005();
		d.setDt_inv(fim);
		//TODO colocar em 01/07/2012 
		//d.setMot_inv("01");
		d.setVl_inv(total);
		return d;
	}

}
