package br.com.opensig.core.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.swing.text.MaskFormatter;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.ErrorHandler;

import br.com.opensig.core.client.servico.OpenSigException;

public class UtilServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// letras acentuadas
	private static String acentuado = "çÇáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
	// letras sem acentos
	private static String semAcento = "cCaeiouyAEIOUYaeiouAEIOUaonaeiouyAEIOUAONaeiouAEIOU";
	// tabela com vinculos das letras
	private static char[] tabela = new char[256];
	// hora de desvio para verao
	private static int VERAO = 0;
	/**
	 * Log do sistema
	 */
	public static Logger LOG;
	/**
	 * Dados de configuracao do sistema.
	 */
	public static Map<String, String> CONF = new HashMap<String, String>();
	/**
	 * Localizacao para formatacao de lingua.
	 */
	public static Locale LOCAL = Locale.getDefault();
	/**
	 * Chave mestre para criptografar
	 */
	public static String CHAVE;
	
	
	// setando a tabela de letras
	static {
		for (int i = 0; i < tabela.length; ++i) {
			tabela[i] = (char) i;
		}
		for (int i = 0; i < acentuado.length(); ++i) {
			tabela[acentuado.charAt(i)] = semAcento.charAt(i);
		}
	}

	/**
	 * Metodo que inicializa variazeis globais
	 */
	public void init() {
		// pegando os dados de log
		Properties log4j = new Properties();
		Enumeration<String> param = getInitParameterNames();
		for (; param.hasMoreElements();) {
			String nome = param.nextElement();
			String valor = getInitParameter(nome);
			log4j.put(nome, valor);
		}
		// configurando o LOG
		PropertyConfigurator.configure(log4j);
		LOG = Logger.getLogger(UtilServer.class);

		// setando o verao
		VERAO = Integer.valueOf(getServletContext().getInitParameter("sistema.verao")).intValue();
		
		// setando a chave/senha
		CHAVE = getServletContext().getInitParameter("sistema.chave");

		// configurando o as opcoes do app
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(getServletContext().getRealPath("/WEB-INF/conf/app.conf"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			prop.load(br);
			fis.close();
		} catch (Exception ex) {
			LOG.error("Nao leu os dados de conf do App.", ex);
		} finally {
			// adicionando os valores
			for (Entry<Object, Object> entry : prop.entrySet()) {
				CONF.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}
	}

	/**
	 * Metodo que retorna o local absoluto do arquivo.
	 * 
	 * @param relativo
	 *            o local relativo do arquivo.
	 * @return o local absoluto do arquivo.
	 */
	public static String getRealPath(String relativo) {
		return System.getProperty("rootPath") + relativo;
	}

	/**
	 * Metodo que normaliza os caracteres removendo os acentos.
	 * 
	 * @param texto
	 *            o texto acentuado.
	 * @return o texto sem acentos.
	 */
	public static String normaliza(String texto) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < texto.length(); ++i) {
			char ch = texto.charAt(i);
			if (ch < 256) {
				sb.append(tabela[ch]);
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Metodo que transforma os bytes de um html em pdf.
	 * 
	 * @param obj
	 *            html em bytes.
	 * @return bytes do pdf.
	 */
	public static byte[] getPDF(byte[] obj) {
		Tidy tidy = new Tidy();
		tidy.setInputEncoding("utf-8");
		tidy.setOutputEncoding("utf-8");

		Document doc = tidy.parseDOM(new ByteArrayInputStream(obj), null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(doc, null);
		renderer.layout();

		try {
			renderer.createPDF(baos);
			return baos.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Metodo que compacta os arquivos passado em forma de bytes com seus nomes.
	 * 
	 * @param arquivos
	 *            um mapa de nomes e bytes dos arquivos a serem compactados.
	 * @return um array de bytes que representa um arquivo compactado.
	 */
	public static byte[] getZIP(Map<String, byte[]> arquivos) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zout = new ZipOutputStream(baos);

		try {
			for (Entry<String, byte[]> arquivo : arquivos.entrySet()) {
				ZipEntry e = new ZipEntry(arquivo.getKey());
				zout.putNextEntry(e);
				zout.write(arquivo.getValue());
				zout.closeEntry();
			}
		} catch (Exception ex) {
			return null;
		} finally {
			try {
				zout.close();
			} catch (IOException e) {
				// nada
			}
		}

		return baos.toByteArray();
	}

	/**
	 * Metodo que descompacta os arquivos dentro de um zip.
	 * 
	 * @param zip
	 *            arquivo compactado contendo os arquivos.
	 * @return um mapa de nomes e bytes dos arquivos descompactados.
	 */
	public static Map<String, byte[]> getArquivos(byte[] zip) {
		Map<String, byte[]> arquivos = new HashMap<String, byte[]>();
		ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(zip));

		try {
			ZipEntry arquivo;
			while ((arquivo = zin.getNextEntry()) != null) {
				if (!arquivo.isDirectory()) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					for (int c = zin.read(); c != -1; c = zin.read()) {
						baos.write(c);
					}
					arquivos.put(arquivo.getName(), baos.toByteArray());
				}
				zin.closeEntry();
			}
			zin.close();
		} catch (Exception e) {
			return null;
		}

		return arquivos;
	}

	/**
	 * Metodo que transforma em string um documento xml.
	 * 
	 * @param node
	 *            o documento xml tipo DOM.
	 * @return uma String do xml.
	 */
	public static String getXml(Node node) {
		UtilServer.LOG.debug("Doc enviado: ");
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(node), new StreamResult(os));
			return os.toString();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao formatar em string o doc.", e);
			return null;
		}
	}

	/**
	 * Metodo que transforma uma string em documento xml.
	 * 
	 * @param xml
	 *            na forma de texto.
	 * @return um DOM do xml.
	 */
	public static Document getXml(String xml) {
		return getXml(xml, null, null);
	}

	/**
	 * Metodo que transforma uma string em documento xml.
	 * 
	 * @param xml
	 *            na forma de texto.
	 * @param xsd
	 *            caminho real do arquivo xsd.
	 * @param error
	 *            interceptador de erros do parse.
	 * @return um DOM do xml.
	 */
	public static Document getXml(String xml, String xsd, ErrorHandler error) {
		UtilServer.LOG.debug("Xml enviado: " + xml);
		try {
			// gera um objeto DOM do xml
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);

			if (xsd != null) {
				dbf.setValidating(true);
				dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);
				dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xsd);
			}
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			docBuilder.setErrorHandler(error);
			return docBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao formatar a string em xml.", e);
			return null;
		}
	}

	/**
	 * Metodo que retorna a data corrigindo a hora de versao
	 * 
	 * @return a data correspondete
	 */
	public static Date getData() {
		Calendar calendario = Calendar.getInstance(LOCAL);
		calendario.setTime(new Date());
		calendario.add(Calendar.HOUR, VERAO);
		return calendario.getTime();
	}

	/**
	 * Metodo que faz o cálculo de modulo 11.
	 * 
	 * @param fonte
	 *            o numero a ser usado para calculo.
	 * @param dig
	 *            quantos digitos de retorno, 1 ou 2.
	 * @param limite
	 *            quantos digitos usados para multiplicacao.
	 * @return o dv do fonte.
	 */
	public static String modulo11(String fonte, int dig, int limite) {
		for (int n = 1; n <= dig; n++) {
			int soma = 0;
			int mult = 2;

			for (int i = fonte.length() - 1; i >= 0; i--) {
				soma += (mult * Integer.valueOf(fonte.substring(i, i + 1)));
				if (++mult > limite) {
					mult = 2;
				}
			}
			fonte += ((soma * 10) % 11) % 10;
		}
		return fonte.substring(fonte.length() - dig);
	}

	/**
	 * @see #formataNumero(double, int, int, boolean)
	 */
	public static String formataNumero(String valor, int inteiros, int decimal, boolean grupo) {
		return formataNumero(Double.valueOf(valor), inteiros, decimal, grupo);
	}

	/**
	 * Metodo que faz a formatacao de numeros com inteiros e fracoes
	 * 
	 * @param valor
	 *            o valor a ser formatado
	 * @param inteiros
	 *            o minimo de inteiros, que serao completados com ZEROS se
	 *            preciso
	 * @param decimal
	 *            o minimo de decimais, que serao completados com ZEROS se
	 *            preciso
	 * @param grupo
	 *            se sera colocado separador de grupo de milhar
	 * @return uma String com o numero formatado
	 */
	public static String formataNumero(double valor, int inteiros, int decimal, boolean grupo) {
		NumberFormat nf = NumberFormat.getIntegerInstance(LOCAL);
		nf.setMinimumIntegerDigits(inteiros);
		nf.setMinimumFractionDigits(decimal);
		nf.setMaximumFractionDigits(decimal);
		nf.setGroupingUsed(grupo);
		return nf.format(valor);
	}

	/**
	 * Metodo que formata a data.
	 * 
	 * @param data
	 *            a data do tipo Date.
	 * @param formato
	 *            o formado desejado.
	 * @return a data formatada como solicidato.
	 */
	public static String formataData(Date data, String formato) {
		if (data == null) {
			return "";
		} else {
			return new SimpleDateFormat(formato, LOCAL).format(data);
		}
	}

	/**
	 * Metodo que formata a data.
	 * 
	 * @param data
	 *            a data do tipo Date.
	 * @param formato
	 *            o formado desejado usando algum padrao local.
	 * @return a data formatada como solicidato.
	 */
	public static String formataData(Date data, int formato) {
		if (data == null) {
			return "";
		} else {
			return DateFormat.getDateInstance(formato, LOCAL).format(data);
		}
	}

	/**
	 * Metodo que formata a hora.
	 * 
	 * @param data
	 *            a data do tipo Date.
	 * @param formato
	 *            o formado desejado.
	 * @return a hora formatada como solicidato.
	 */
	public static String formataHora(Date data, String formato) {
		if (data == null) {
			return "";
		} else {
			return new SimpleDateFormat(formato, LOCAL).format(data);
		}
	}

	/**
	 * Metodo que formata a hora.
	 * 
	 * @param data
	 *            a data do tipo Date.
	 * @param formato
	 *            o formado desejado usando algum padrao local.
	 * @return a hora formatada como solicidato.
	 */
	public static String formataHora(Date data, int formato) {
		if (data == null) {
			return "";
		} else {
			return DateFormat.getTimeInstance(formato, LOCAL).format(data);
		}
	}

	/**
	 * Metodo que formata o texto.
	 * 
	 * @param texto
	 *            o texto a ser formatado.
	 * @param caracter
	 *            o caracter que sera repetido.
	 * @param tamanho
	 *            o tamanho total do texto de resposta.
	 * @param direita
	 *            a direcao onde colocar os caracteres.
	 * @return o texto formatado.
	 */
	public static String formataTexto(String texto, String caracter, int tamanho, boolean direita) {
		StringBuffer sb = new StringBuffer();
		int fim = tamanho - texto.length();
		for (int i = 0; i < fim; i++) {
			sb.append(caracter);
		}
		return direita ? texto + sb.toString() : sb.toString() + texto;
	}

	/**
	 * Metodo que formata o texto usando a mascara passada.
	 * 
	 * @param texto
	 *            o texto a ser formatado.
	 * @param mascara
	 *            a mascara a ser usada.
	 * @return o texto formatado.
	 * @throws ParseException
	 *             caso ocorra erro.
	 */
	public static String formataTexto(String texto, String mascara) throws ParseException {
		MaskFormatter mf = new MaskFormatter(mascara);
		mf.setValueContainsLiteralCharacters(false);
		return mf.valueToString(texto);
	}

	/**
	 * Metodo que retorna o valor de uma tag dentro do xml.
	 * 
	 * @param ele
	 *            elemento xml em forma de objeto.
	 * @param tag
	 *            nome da tag que deseja recuperar o valor.
	 * @param excecao
	 *            se passado true dispara a exception se ocorrer erro, se false
	 *            retorna null
	 * @return valor da tag encontrada ou NULL se nao achada.
	 * @exception NullPointerException
	 *                exceção disparada em caso de erro.
	 */
	public static String getValorTag(Element ele, String tag, boolean excecao) throws NullPointerException {
		try {
			return ele.getElementsByTagName(tag).item(0).getFirstChild().getNodeValue();
		} catch (Exception e) {
			if (excecao) {
				UtilServer.LOG.debug("Nao achou a tag -> " + tag);
				throw new NullPointerException(UtilServer.CONF.get("errInvalido") + " - > " + tag);
			}
			return null;
		}
	}

	/**
	 * Metodo que retorna o conteudo de um arquivo de conteudo textual.
	 * 
	 * @param pathArquivo
	 *            local completo fisico do arquivo.
	 * @return o texto interno do arquivo.
	 * @throws OpenSigException
	 *             exceção disparada em caso de erro.
	 */
	public static String getTextoArquivo(String pathArquivo) throws OpenSigException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(pathArquivo));
			StringBuffer sb = new StringBuffer();
			while (in.ready()) {
				sb.append(in.readLine());
			}
			in.close();
			return sb.toString();
		} catch (IOException e) {
			throw new OpenSigException(e.getMessage());
		}
	}

	/**
	 * Metodo que Converte XML em Objeto.
	 * 
	 * @param xml
	 *            o arquivo em formato string.
	 * @param pacote
	 *            o nome do pacote da classe especifica.
	 * @return T o tipo passado de Objeto.
	 * @exception JAXBException
	 *                dispara uma excecao caso ocorra erro.
	 * 
	 */
	public static <T> T xmlToObj(String xml, String pacote) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(pacote);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		JAXBElement<T> element = (JAXBElement<T>) unmarshaller.unmarshal(new StringReader(xml));
		return element.getValue();
	}

	/**
	 * Metodo que converte Objeto em XML.
	 * 
	 * @param <T>
	 *            o tipo passado de Objeto.
	 * @param element
	 *            o Objeto passado.
	 * @param pacote
	 *            o nome do pacote da classe especifica.
	 * @return o arquivo em formato String.
	 * @throws JAXBException
	 *             dispara uma excecao caso ocorra erro.
	 */
	public static <T> String objToXml(JAXBElement<T> element, String pacote) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(pacote);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		marshaller.marshal(element, sw);

		// retira ns indesejado
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw.toString();
		xml = xml.replace("ns2:", "");
		xml = xml.replace(":ns2", "");
		xml = xml.replace(" xmlns=\"http://www.w3.org/2000/09/xmldsig#\"", "");
		// retira as quebras de linhas
		xml = xml.replace("\r", "");
		xml = xml.replace("\n", "");
		// retira acentos
		xml = UtilServer.normaliza(xml);
		// remove alguns caracteres especiais
		xml = xml.replaceAll(UtilServer.CONF.get("nfe.regexp"), "");
		return xml;
	}
}
