package br.com.opensig.comercial.client.controlador.comando;

import java.util.Map;

import br.com.opensig.comercial.client.visao.grafico.GraficoEcfVenda;
import br.com.opensig.comercial.client.visao.lista.ListagemEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.core.client.controlador.comando.ComandoFuncao;
import br.com.opensig.core.client.visao.FormularioVazio;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;

public class ComandoEcfVenda extends ComandoFuncao {

	public void execute(Map contexto) {
		DADOS = (SisFuncao) contexto.get("dados");
		FORM = new FormularioVazio<ComEcfVenda>(new ComEcfVenda(), DADOS);
		LISTA = new ListagemEcfVenda(FORM);
		GRAFICO = new GraficoEcfVenda(LISTA);
		super.execute(contexto);
	}
}
