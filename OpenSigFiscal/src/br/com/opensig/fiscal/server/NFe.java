package br.com.opensig.fiscal.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.jasypt.util.text.BasicTextEncryptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.client.servico.FiscalException;
import br.com.opensig.fiscal.shared.modelo.ENotaOriem;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisCertificado;

public class NFe {

	private NFe() {
	}

	public static String assinarXML(Document doc, ENotaStatus status, EmpEmpresa empresa) throws FiscalException {
		try {
			// monta o arquivo
			String cnpj = empresa.getEmpEntidade().getEmpEntidadeDocumento1().replaceAll("\\D", "");
			String pfx = UtilServer.getRealPath(UtilServer.CONF.get("nfe.certificado") + cnpj + ".pfx");
			// faz a busca pela senha
			FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, empresa);
			FiscalServiceImpl<FisCertificado> service = new FiscalServiceImpl<FisCertificado>();
			FisCertificado cert = new FisCertificado();
			cert = service.selecionar(cert, fo, false);

			// chaves
			PrivateKey pk = null;
			KeyInfo ki = null;
			String tag = null;
			if (status == ENotaStatus.AUTORIZANDO) {
				tag = "NFe";
			} else if (status == ENotaStatus.CANCELANDO) {
				tag = "infCanc";
			} else if (status == ENotaStatus.INUTILIZANDO) {
				tag = "infInut";
			} else {
				return null;
			}

			// descriptografa a senha
			BasicTextEncryptor seguranca = new BasicTextEncryptor();
			seguranca.setPassword(UtilServer.CHAVE);
			String senha = seguranca.decrypt(cert.getFisCertificadoSenha());
			
			// trabalhando o certificado
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			ArrayList<Transform> transformList = signatureFactory(fac);
			Object[] chaves = lerCertificado(pfx, senha, fac);
			pk = (PrivateKey) chaves[0];
			ki = (KeyInfo) chaves[1];

			// parse no xml e pega o id
			Element ele = (Element) doc.getElementsByTagName(status == ENotaStatus.AUTORIZANDO ? "infNFe" : tag).item(0);
			String id = ele.getAttribute("Id");
			// adiciona a referencia
			Reference ref = fac.newReference("#" + id, fac.newDigestMethod(DigestMethod.SHA1, null), transformList, null, null);
			// adiciona a informacao da assinatura
			SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null), fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
					Collections.singletonList(ref));
			// adiciona a assinatura
			XMLSignature signature = fac.newXMLSignature(si, ki);
			DOMSignContext dsc = new DOMSignContext(pk, status == ENotaStatus.AUTORIZANDO ? doc.getElementsByTagName(tag).item(0) : doc.getFirstChild());
			signature.sign(dsc);

			return UtilServer.getXml(doc);
		} catch (Exception e) {
			throw new FiscalException(e.getMessage());
		}
	}

	public static void validarXML(String xml, String xsd) throws FiscalException {
		try {
			final StringBuffer sb = new StringBuffer();
			ErrorHandler error = new ErrorHandler() {

				public void warning(SAXParseException arg0) throws SAXException {
				}

				public void fatalError(SAXParseException arg0) throws SAXException {
					sb.append("\nLinha: " + arg0.getLineNumber() + " Coluna: " + arg0.getColumnNumber() + " Erro = " + arg0.getMessage());
				}

				public void error(SAXParseException arg0) throws SAXException {
					sb.append("\nLinha: " + arg0.getLineNumber() + " Coluna: " + arg0.getColumnNumber() + " Erro = " + arg0.getMessage());
				}
			};

			UtilServer.getXml(xml, xsd, error);
			if (sb.length() > 0) {
				throw new FiscalException(sb.toString());
			}
		} catch (Exception e) {
			throw new FiscalException(e.getMessage());
		}
	}

	public static ENotaStatus validarEntrada(Document doc, String cnpj) throws FiscalException {
		ENotaStatus status = validarStatus(doc);

		// valida o status
		if (status == ENotaStatus.AUTORIZADO) {
			// valida o cnpj
			ENotaOriem origem = NFe.validarCNPJ(doc, cnpj);
			if (origem != ENotaOriem.DESTINATARIO) {
				throw new FiscalException("Arquivo de XML nao e valido, o destinatario nao e sua empresa!");
			}
		} else if (status != ENotaStatus.CANCELADO) {
			throw new FiscalException("Arquivo de XML nao e valido, pois entradas somente sao aceitas do tipo Autorizada ou Cancelada, as duas com protocolos!");
		}

		return status;
	}

	public static ENotaStatus validarSaida(Document doc, String cnpj) throws FiscalException {
		ENotaStatus status = validarStatus(doc);

		// valida o status
		if (status == ENotaStatus.AUTORIZANDO || status == ENotaStatus.AUTORIZADO) {
			// valida o cnpj
			ENotaOriem origem = NFe.validarCNPJ(doc, cnpj);
			if (origem != ENotaOriem.EMISSOR) {
				throw new FiscalException("Arquivo de XML nao e valido, o emissor nao e sua empresa!");
			}
		}

		return status;
	}

	public static Map<String, Object> getFaturas(Document doc) {
		Map<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < doc.getElementsByTagName("dup").getLength(); i++) {
			// seta uma duplicata
			Node dup = doc.getElementsByTagName("dup").item(i);
			// seta o numero
			Node nDoc = dup.getChildNodes().item(0);
			String numero = nDoc.getFirstChild().getNodeValue();
			map.put("FAT_NUMERO" + (i + 1), numero);
			// seta a data
			Node nData = dup.getChildNodes().item(1);
			String data = nData.getFirstChild().getNodeValue();
			try {
				map.put("FAT_VENCIMENTO" + (i + 1), new SimpleDateFormat("yyyy-MM-dd").parse(data));
			} catch (Exception e) {
				map.put("FAT_VENCIMENTO" + (i + 1), null);
			}
			// seta o valor
			Node nValor = dup.getChildNodes().item(2);
			String valor = nValor.getFirstChild().getNodeValue();
			map.put("FAT_VALOR" + (i + 1), Double.parseDouble(valor));
		}

		return map;
	}

	private static ENotaStatus validarStatus(Document doc) {
		ENotaStatus status;

		// pega o protocolo
		String recibo = UtilServer.getValorTag(doc.getDocumentElement(), "dhRecbto", false);

		// verifica se é uma NFe
		Node root = doc.getElementsByTagName("infNFe").item(0);
		if (root != null) {
			status = recibo == null ? ENotaStatus.AUTORIZANDO : ENotaStatus.AUTORIZADO;
		} else {
			// verifica se é um cancelamento
			root = doc.getElementsByTagName("infCanc").item(0);
			if (root != null) {
				status = recibo == null ? ENotaStatus.CANCELANDO : ENotaStatus.CANCELADO;
			} else {
				// verifica se é uma inutilização
				root = doc.getElementsByTagName("infInut").item(0);
				if (root != null) {
					status = recibo == null ? ENotaStatus.INUTILIZANDO : ENotaStatus.INUTILIZADO;
				} else {
					UtilServer.LOG.debug("Nao e um arquivo valido!");
					status = ENotaStatus.ERRO;
				}
			}
		}

		return status;
	}

	private static ENotaOriem validarCNPJ(Document doc, String cnpj) {
		ENotaOriem resp;

		try {
			// recupera o cnpj do emitente
			Element emit = (Element) doc.getElementsByTagName("emit").item(0);
			String cnpjEmit = UtilServer.getValorTag(emit, "CNPJ", true);
			// recupera o cnpj do destinatario
			Element dest = (Element) doc.getElementsByTagName("dest").item(0);
			String cnpjDest = UtilServer.getValorTag(dest, "CNPJ", true);
			// remove caracteres diferentes especiais
			cnpj = cnpj.replaceAll("\\D", "");

			// compara
			if (cnpj.equals(cnpjEmit)) {
				resp = ENotaOriem.EMISSOR;
			} else if (cnpj.equals(cnpjDest)) {
				resp = ENotaOriem.DESTINATARIO;
			} else {
				resp = ENotaOriem.NENHUM;
			}
		} catch (NullPointerException e) {
			resp = ENotaOriem.NENHUM;
		}

		return resp;
	}

	private static ArrayList<Transform> signatureFactory(XMLSignatureFactory signatureFactory) throws Exception {
		// seta as transformacaoes e envelopes
		ArrayList<Transform> transformList = new ArrayList<Transform>();
		TransformParameterSpec tps = null;
		Transform envelopedTransform = signatureFactory.newTransform(Transform.ENVELOPED, tps);
		Transform c14NTransform = signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", tps);

		transformList.add(envelopedTransform);
		transformList.add(c14NTransform);
		return transformList;
	}

	private static Object[] lerCertificado(String certificado, String senha, XMLSignatureFactory signatureFactory) throws Exception {
		// chave e informacao da chave
		PrivateKey pk = null;
		KeyInfo ki = null;

		// le o certificado
		InputStream entrada = new FileInputStream(certificado);
		KeyStore ks = KeyStore.getInstance("PKCS12");
		try {
			ks.load(entrada, senha.toCharArray());
		} catch (IOException e) {
			throw new Exception("Senha do Certificado Digital incorreta ou Certificado invalido.");
		}

		// acha a chave
		KeyStore.PrivateKeyEntry pkEntry = null;
		Enumeration<String> aliasesEnum = ks.aliases();
		while (aliasesEnum.hasMoreElements()) {
			String alias = (String) aliasesEnum.nextElement();
			if (ks.isKeyEntry(alias)) {
				pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(senha.toCharArray()));
				pk = pkEntry.getPrivateKey();
				break;
			}
		}

		// gera a assinatura
		X509Certificate cert = (X509Certificate) pkEntry.getCertificate();
		KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
		List<X509Certificate> x509Content = new ArrayList<X509Certificate>();
		// gera a informacao
		x509Content.add(cert);
		X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
		ki = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

		return new Object[] { pk, ki };
	}
}
