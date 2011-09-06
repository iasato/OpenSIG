package br.com.opensig.financeiro.server.boleto;

import java.text.DateFormat;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.server.exportar.ExportacaoHtml;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;

public class ReciboHtml extends ExportacaoHtml implements IRecibo {

	public byte[] getRecibo(String[] empresa, FinRecebimento boleto) {
		// inicio do arquivo
		StringBuffer sb = new StringBuffer("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html xmlns='http://www.w3.org/1999/xhtml'>");
		// estilo do arquivo
		sb.append(getEstilo("portrait", "recibo"));
		// cabecalho da empresa
		sb.append(getCabecalhoEmpresa(empresa));
		// inicio do registro
		sb.append("<table>");
		// cabeçalho do registro
		ExportacaoRegistro reg = new ExportacaoRegistro();
		reg.setNome("RECIBO");
		sb.append(reg);
		// corpo do registro
		sb.append(getCorpoRegistro(boleto));
		// fim do registro
		sb.append("</table>");
		// fim do arquivo
		sb.append("</body></html>");
		// normaliza
		return UtilServer.normaliza(sb.toString()).getBytes();
	}

	public String getCorpoRegistro(FinRecebimento boleto) {
		StringBuffer sb = new StringBuffer("<tbody>");
		sb.append("<tr><td><h2>Referente ao " + boleto.getFinForma().getFinFormaDescricao() + " - " + UtilServer.formataNumero(boleto.getFinRecebimentoId(), 9, 0, false)
				+ "</h2><br /><br /></td></tr>");
		sb.append("<tr><td><b>Documento :: </b>" + boleto.getFinRecebimentoDocumento() + "<br /><br />");
		sb.append("<tr><td><b>Parcela :: </b>" + boleto.getFinRecebimentoParcela() + "<br /><br />");
		sb.append("<tr><td><b>Entidade :: </b>" + boleto.getFinReceber().getEmpEntidade().getEmpEntidadeNome1() + "<br /><br />");
		sb.append("<b>Data Vencimento :: </b>" + UtilServer.formataData(boleto.getFinRecebimentoVencimento(), DateFormat.MEDIUM) + "<br /><br />");
		sb.append("<b>Data Quitação :: </b>" + UtilServer.formataData(boleto.getFinRecebimentoRealizado(), DateFormat.MEDIUM) + "<br /><br />");
		sb.append("<b>Valor :: </b>" + UtilServer.formataNumero(boleto.getFinRecebimentoValor(), 1, 2, true) + "<br /><br />");
		if (boleto.getFinRecebimentoObservacao() != null && !boleto.getFinRecebimentoObservacao().equals("")) {
			sb.append("<b>Observação :: </b>" + boleto.getFinRecebimentoObservacao() + "<br /><br />");
		}
		sb.append("<hr style='border:none;border-bottom:1px dashed' /></tr></td></tbody>");
		return sb.toString();
	}

}
