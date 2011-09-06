package br.com.opensig.financeiro.server.boleto;

import br.com.opensig.financeiro.shared.modelo.FinRecebimento;

public interface IRecibo {

	public byte[] getRecibo(String[] empresa, FinRecebimento boleto);
	
}
