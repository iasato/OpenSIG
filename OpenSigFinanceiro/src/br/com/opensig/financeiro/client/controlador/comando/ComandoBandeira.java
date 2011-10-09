package br.com.opensig.financeiro.client.controlador.comando;

import java.util.Map;

import br.com.opensig.core.client.controlador.comando.ComandoFuncao;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.financeiro.client.visao.form.FormularioBandeira;
import br.com.opensig.financeiro.client.visao.lista.ListagemBandeira;

public class ComandoBandeira extends ComandoFuncao {

	
	public void execute(Map contexto) {
		DADOS = (SisFuncao) contexto.get("dados");
		FORM = new FormularioBandeira(DADOS);
		LISTA = new ListagemBandeira(FORM);
		super.execute(contexto);
	}
}
