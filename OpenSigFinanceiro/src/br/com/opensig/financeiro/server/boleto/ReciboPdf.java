package br.com.opensig.financeiro.server.boleto;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;

public class ReciboPdf extends ReciboHtml implements IRecibo {

	public byte[] getRecibo(String[] empresa, FinRecebimento boleto) {
		byte[] obj = super.getRecibo(empresa, boleto);
		return UtilServer.getPDF(obj);
	}

}
