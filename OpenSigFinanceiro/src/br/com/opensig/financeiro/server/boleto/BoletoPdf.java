package br.com.opensig.financeiro.server.boleto;

import org.jboleto.Banco;
import org.jboleto.JBoletoBean;
import org.jboleto.control.PDFGenerator;

public class BoletoPdf implements IBoleto {

	public byte[] getBoleto(JBoletoBean bean, Banco banco) {
		PDFGenerator boleto = new PDFGenerator(bean, banco);
		boleto.addBoleto();
		return boleto.closeBoleto();
	}
}
