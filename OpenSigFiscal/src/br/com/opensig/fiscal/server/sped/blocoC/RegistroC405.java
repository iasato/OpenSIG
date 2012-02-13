package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC405 extends ARegistro<DadosC405, ComEcfZ> {

	@Override
	public void executar() {
		super.executar();

		// sub totais da leitura Z
		RegistroC420 r420 = new RegistroC420();
		r420.setEsquitor(escritor);
		r420.setAuth(auth);
		r420.setEcfs(ecfs);
		r420.setTotais(dados.getComZTotais());
		r420.executar();
		qtdLinhas += r420.getQtdLinhas();
		
		// vendas da leitura Z
		RegistroC460 r460 = new RegistroC460();
		r460.setEsquitor(escritor);
		r460.setAuth(auth);
		r460.setEcfs(ecfs);
		r460.setZ(dados);
		r460.executar();
		qtdLinhas += r460.getQtdLinhas();
	}

	@Override
	protected DadosC405 getDados(ComEcfZ dados) throws Exception {
		DadosC405 d = new DadosC405();
		d.setDt_doc(dados.getComEcfZData());
		d.setCro(dados.getComEcfZCro());
		d.setCrz(dados.getComEcfZCrz());
		d.setNum_coo_fin(dados.getComEcfZCoo());
		d.setGt_fin(dados.getComEcfZTotal());
		d.setVl_brt(dados.getComEcfZBruto());
		return d;
	}

}
