package br.com.opensig.fiscal.server.acao;

import java.util.Date;

import org.w3c.dom.Document;

import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.FiscalServiceImpl;
import br.com.opensig.fiscal.server.NFe;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;
import br.com.opensig.retenvinfe.TRetEnviNFe;

public class EnviarNfe extends Chain {

	private FiscalServiceImpl servico;
	private FisNotaSaida saida;

	public EnviarNfe(Chain next, FiscalServiceImpl servico, FisNotaSaida saida) throws OpenSigException {
		super(next);
		this.servico = servico;
		this.saida = saida;
	}

	@Override
	public void execute() throws OpenSigException {
		try {
			// valida se ja esta assinada, senao assina
			String xml = saida.getFisNotaSaidaXml();
			long id = new Date().getTime();

			// adicionando dados ao xml
			xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			xml = xml.replace(" xmlns=\"http://www.portalfiscal.inf.br/nfe\"", "");
			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><enviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"" + UtilServer.CONF.get("nfe.versao") + "\"><idLote>" + id + "</idLote>"
					+ xml + "</enviNFe>";
			// assina
			Document doc = UtilServer.getXml(xml);
			if (doc.getElementsByTagName("Signature").item(0) == null) {
				xml = NFe.assinarXML(doc, ENotaStatus.AUTORIZANDO, saida.getEmpEmpresa());
			}
			// valida
			String xsd = UtilServer.getRealPath(UtilServer.CONF.get("nfe.xsd_enviando"));
			NFe.validarXML(xml, xsd);
			// envia para sefaz
			String recibo = servico.enviarNFe(xml, saida.getEmpEmpresa().getEmpEmpresaId());
			// analisa o retorno e seta os status
			TRetEnviNFe ret = UtilServer.xmlToObj(recibo, "br.com.opensig.retenvinfe");
			// verifica se sucesso
			if (ret.getCStat().equals("103")) {
				saida.setFisNotaSaidaXml(xml);
				saida.setFisNotaSaidaRecibo(ret.getInfRec().getNRec());
			} else {
				saida.setFisNotaStatus(new FisNotaStatus(ENotaStatus.ERRO));
				saida.setFisNotaSaidaErro(ret.getXMotivo());
			}
			// solicita o retorno 
			int espera = Integer.valueOf(UtilServer.CONF.get("nfe.tempo_retorno"));
			RetornarNfe retorno = new RetornarNfe(servico, saida, espera);
			Thread retornar = new Thread(retorno);
			retornar.start();
		} catch (Exception e) {
			saida.setFisNotaStatus(new FisNotaStatus(ENotaStatus.ERRO));
			saida.setFisNotaSaidaErro(e.getMessage());
		} finally {
			servico.salvar(saida, false);
		}
	}

}
