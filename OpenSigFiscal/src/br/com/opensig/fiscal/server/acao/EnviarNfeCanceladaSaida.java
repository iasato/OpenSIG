package br.com.opensig.fiscal.server.acao;

import org.w3c.dom.Document;

import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.fiscal.server.FiscalServiceImpl;
import br.com.opensig.fiscal.server.NFe;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;
import br.com.opensig.retcancnfe.TRetCancNFe;

public class EnviarNfeCanceladaSaida extends Chain {

	private FiscalServiceImpl servico;
	private FisNotaSaida saida;
	private Autenticacao auth;
	
	public EnviarNfeCanceladaSaida(Chain next, FiscalServiceImpl servico, FisNotaSaida saida, Autenticacao auth) throws OpenSigException {
		super(next);
		this.servico = servico;
		this.saida = saida;
		this.auth = auth;
	}

	@Override
	public void execute() throws OpenSigException {
		String para = null;
		try {
			// valida se ja esta assinada, senao assina
			String xml = saida.getFisNotaSaidaXmlCancelado();
			Document doc = UtilServer.getXml(xml);
			if (doc.getElementsByTagName("Signature").item(0) == null) {
				// assina
				xml = NFe.assinarXML(doc, ENotaStatus.CANCELANDO, auth);
			}
			// valida
			String xsd = UtilServer.getRealPath(auth.getConf().get("nfe.xsd_cancelando"));
			NFe.validarXML(xml, xsd);
			// envia para sefaz
			String canc = servico.cancelar(xml, saida.getEmpEmpresa().getEmpEmpresaId());
			// analisa o retorno e seta os status
			TRetCancNFe ret = UtilServer.xmlToObj(canc, "br.com.opensig.retcancnfe");
			// verifica se sucesso
			if (ret.getInfCanc().getCStat().equals("101")) {
				saida.setFisNotaStatus(new FisNotaStatus(ENotaStatus.CANCELADO));
				saida.setFisNotaSaidaProtocoloCancelado(ret.getInfCanc().getNProt());
				saida.setFisNotaSaidaXmlCancelado(montaProcCancNfe(saida.getFisNotaSaidaXmlCancelado(), canc, auth.getConf().get("nfe.versao")));

				Document doc1 = UtilServer.getXml(saida.getFisNotaSaidaXml());
				para = UtilServer.getValorTag(doc1.getDocumentElement(), "email", false);
			} else {
				saida.setFisNotaStatus(new FisNotaStatus(ENotaStatus.ERRO));
				saida.setFisNotaSaidaErro(ret.getInfCanc().getXMotivo());
			}
		} catch (Exception e) {
			saida.setFisNotaStatus(new FisNotaStatus(ENotaStatus.ERRO));
			saida.setFisNotaSaidaErro(e.getMessage());
		} finally {
			servico.salvar(saida, false);
			if (para != null) {
				EnviarEmail email = new EnviarEmail(servico, saida, auth);
				Thread enviar = new Thread(email);
				enviar.start();
			}
		}
		
		if (next != null) {
			next.execute();
		}
	}
	
	public static String montaProcCancNfe(String canc, String proc, String versao) throws OpenSigException {
		// transforma em doc
		Document doc1 = UtilServer.getXml(canc);
		Document doc2 = UtilServer.getXml(proc);

		// pega as tags corretas
		canc = UtilServer.getXml(doc1.getElementsByTagName("cancNFe").item(0));
		proc = UtilServer.getXml(doc2.getElementsByTagName("retCancNFe").item(0));

		// unifica
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<procCancNFe versao=\"" + versao + "\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">");
		sb.append(canc.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
		sb.append(proc.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", ""));
		sb.append("</procCancNFe>");

		return sb.toString();
	}

}