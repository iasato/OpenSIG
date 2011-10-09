package br.com.opensig.financeiro.server.cobranca;

import br.com.opensig.financeiro.client.servico.FinanceiroException;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.financeiro.shared.modelo.FinRemessa;
import br.com.opensig.financeiro.shared.modelo.FinRetorno;

public interface ICobranca {
	
	public byte[] boleto(String tipo, String[] empresa, FinRecebimento finBoleto) throws FinanceiroException;
	
	public Boolean remessa(FinRemessa rem) throws FinanceiroException;
	
	public String[][] retorno(FinRetorno ret) throws FinanceiroException;

}
