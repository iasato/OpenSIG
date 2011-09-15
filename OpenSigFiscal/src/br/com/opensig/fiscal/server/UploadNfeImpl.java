package br.com.opensig.fiscal.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.w3c.dom.Document;

import br.com.opensig.core.server.UploadServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpEntidade;
import br.com.opensig.fiscal.server.acao.SalvarEntrada;
import br.com.opensig.fiscal.server.acao.SalvarSaida;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;

public class UploadNfeImpl extends UploadServiceImpl {

	protected void finalizar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession sessao = req.getSession();
		boolean sucesso = true;
		int Erros = 0;
		int OK = 0;
		String dados = "";

		// valida o tipo do arquivo
		Map<String, byte[]> arquivos = new HashMap<String, byte[]>();
		if (nomeArquivo.endsWith(".zip")) {
			arquivos = UtilServer.getArquivos((byte[]) sessao.getAttribute(nomeArquivo));
		} else {
			byte[] obj = (byte[]) sessao.getAttribute(nomeArquivo);
			arquivos.put(nomeArquivo, obj);
		}

		for (Entry<String, byte[]> arquivo : arquivos.entrySet()) {
			try {
				// gera um objeto DOM do xml
				String xml = new String(arquivo.getValue());
				Document doc = UtilServer.getXml(xml);
				// recupera a empresa logada
				Autenticacao auto = (Autenticacao) sessao.getAttribute("Autenticacao");
				EmpEmpresa empresa = new EmpEmpresa(Integer.valueOf(auto.getEmpresa()[0]));
				String cnpj = UtilServer.normaliza(auto.getEmpresa()[5]);

				// verifica se entrada ou saida
				if (params.get("classe").equalsIgnoreCase("FisNotaSaida")) {
					ENotaStatus status = NFe.validarSaida(doc, cnpj);
					// valida o xml com o xsd
					if (status == ENotaStatus.AUTORIZANDO || status == ENotaStatus.CANCELANDO || status == ENotaStatus.INUTILIZANDO) {
						// caso o xml nao esteja assinado
						if (doc.getElementsByTagName("Signature").item(0) == null) {
							// assina
							EmpEntidade ent = new EmpEntidade();
							ent.setEmpEntidadeDocumento1(cnpj);
							empresa.setEmpEntidade(ent);
							xml = NFe.assinarXML(doc, status, empresa);
						}

						String xsd = UtilServer.getRealPath(UtilServer.CONF.get("nfe.xsd_" + status.toString().toLowerCase()));
						NFe.validarXML(xml, xsd);
					}
					FisNotaStatus nfStatus = new FisNotaStatus(status);
					new SalvarSaida(null, xml, nfStatus, empresa).execute();
				} else if (params.get("classe").equalsIgnoreCase("FisNotaEntrada")) {
					ENotaStatus status = NFe.validarEntrada(doc, cnpj);
					FisNotaStatus nfStatus = new FisNotaStatus(status);
					new SalvarEntrada(null, xml, nfStatus, empresa).execute();
				}
				OK++;
			} catch (Exception ex) {
				Erros++;
				sucesso = false;
				dados = ex.getMessage() + "\n";
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		}

		try {
			json.put("error", "OK=" + OK + " - Erro=" + Erros);
			json.put("success", sucesso);
			json.put("dados", dados);
		} catch (JSONException e) {
			// nada
		}

		Writer w = new OutputStreamWriter(resp.getOutputStream());
		w.write(json.toString());
		w.close();
		baos.close();
		sessao.removeAttribute(nomeArquivo);
	}
}
