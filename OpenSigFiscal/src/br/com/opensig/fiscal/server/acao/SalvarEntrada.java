package br.com.opensig.fiscal.server.acao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.controlador.parametro.GrupoParametro;
import br.com.opensig.core.client.controlador.parametro.IParametro;
import br.com.opensig.core.client.controlador.parametro.ParametroBinario;
import br.com.opensig.core.client.controlador.parametro.ParametroObjeto;
import br.com.opensig.core.client.controlador.parametro.ParametroTexto;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.Sql;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.client.servico.FiscalException;
import br.com.opensig.fiscal.server.FiscalServiceImpl;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaEntrada;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;
import br.com.opensig.retconssitnfe.TRetConsSitNFe;

public class SalvarEntrada extends Chain {

	private Document doc;
	private String xml;
	private FisNotaStatus status;
	private EmpEmpresa empresa;
	private FisNotaEntrada nota;
	private FiscalServiceImpl<FisNotaEntrada> servico;

	public SalvarEntrada(Chain next, String xml, FisNotaStatus status, EmpEmpresa empresa) throws OpenSigException {
		super(next);
		this.xml = xml;
		this.status = status;
		this.empresa = empresa;
		this.servico = new FiscalServiceImpl<FisNotaEntrada>();
	}

	@Override
	public void execute() throws OpenSigException {
		// valida o plano
		new ValidarPlano(null, servico, status, empresa).execute();
		
		doc = UtilServer.getXml(xml);
		if (status.getFisNotaStatusId() == ENotaStatus.AUTORIZADO.ordinal()) {
			salvar();
		} else if (status.getFisNotaStatusId() == ENotaStatus.CANCELADO.ordinal()) {
			atualizar();
		}

		if (next != null) {
			next.execute();
		}
	}

	public FisNotaEntrada getNota(){
		return nota;
	}
	
	public void salvar() throws FiscalException {
		try {
			// recupera a chave
			String chave = UtilServer.getValorTag(doc.getDocumentElement(), "chNFe", true);
			// recupera o protocolo
			String prot = UtilServer.getValorTag(doc.getDocumentElement(), "nProt", true);
			// recupera o numero
			String numero = UtilServer.getValorTag(doc.getDocumentElement(), "nNF", true);
			// recupera o cnpj
			Element emit = (Element) doc.getElementsByTagName("emit").item(0);
			String cnpj = UtilServer.getValorTag(emit, "CNPJ", true);
			try {
				cnpj = UtilServer.formataTexto(cnpj, "##.###.###/####-##");
			} catch (ParseException e) {
				UtilServer.LOG.debug("Cnpj invalido.");
				throw new FiscalException(UtilServer.CONF.get("errInvalido") + " -> CNPJ");
			}
			// recupera a data
			String data = UtilServer.getValorTag(doc.getDocumentElement(), "dEmi", true);
			Date dtData = null;
			try {
				dtData = new SimpleDateFormat("yyyy-MM-dd").parse(data);
			} catch (ParseException e) {
				UtilServer.LOG.debug("Data invalida.");
				throw new FiscalException(UtilServer.CONF.get("errInvalido") + " -> dEmi");
			}
			// recupera os totais
			Element total = (Element) doc.getElementsByTagName("total").item(0);
			// recupera o valor
			String valor = UtilServer.getValorTag(total, "vNF", true);
			// recupera o icms
			String icms = UtilServer.getValorTag(total, "vICMS", false);
			if (icms == null) {
				icms = "0.00";
			}
			// recupera o ipi
			String ipi = UtilServer.getValorTag(total, "vIPI", false);
			if (ipi == null) {
				ipi = "0.00";
			}
			// recupera o pis
			String pis = UtilServer.getValorTag(total, "vPIS", false);
			if (pis == null) {
				pis = "0.00";
			}
			// recupera o cofins
			String cofins = UtilServer.getValorTag(total, "vCOFINS", false);
			if (cofins == null) {
				cofins = "0.00";
			}

			nota = new FisNotaEntrada();
			nota.setFisNotaStatus(status);
			nota.setEmpEmpresa(empresa);
			nota.setFisNotaEntradaNumero(Integer.valueOf(numero));
			nota.setFisNotaEntradaCadastro(new Date());
			nota.setFisNotaEntradaData(dtData);
			nota.setFisNotaEntradaValor(Double.valueOf(valor));
			nota.setFisNotaEntradaChave(chave);
			nota.setFisNotaEntradaIcms(Double.valueOf(icms));
			nota.setFisNotaEntradaIpi(Double.valueOf(ipi));
			nota.setFisNotaEntradaPis(Double.valueOf(pis));
			nota.setFisNotaEntradaCofins(Double.valueOf(cofins));
			nota.setFisNotaEntradaProtocolo(prot);
			nota.setFisNotaEntradaXml(xml);
			nota.setFisNotaEntradaProtocoloCancelado("");
			nota.setFisNotaEntradaXmlCancelado("");
			nota.setFisNotaEntradaRecibo("");
			nota.setFisNotaEntradaErro("");

			try {
				// valida na sefaz
				FiscalServiceImpl<FisNotaEntrada> fiscal = new FiscalServiceImpl<FisNotaEntrada>();
				int amb = Integer.valueOf(UtilServer.CONF.get("nfe.tipoamb"));
				String resp = fiscal.situacao(amb, chave, empresa.getEmpEmpresaId());
				TRetConsSitNFe situacao = UtilServer.xmlToObj(resp, "br.com.opensig.retconssitnfe");

				// verifica se o status na sefaz é igual ao informado
				if (situacao.getCStat().equals("100")) {
					if (status.getFisNotaStatusId() == ENotaStatus.AUTORIZADO.ordinal() && situacao.getProtNFe() != null) {
						// valida se a data da nota ainda pode ser cancelada
						int dias = Integer.valueOf(UtilServer.CONF.get("nfe.tempo_cancela"));
						Calendar cal = Calendar.getInstance();
						cal.setTime(nota.getFisNotaEntradaData());
						cal.add(Calendar.DATE, dias);

						Date hoje = new Date();
						if (hoje.compareTo(cal.getTime()) > 0) {
							nota.setFisNotaEntradaRecibo("OK");
						} else {
							nota.setFisNotaEntradaRecibo("PROVISORIO");
						}
					} else if (status.getFisNotaStatusId() == ENotaStatus.CANCELADO.ordinal() && situacao.getRetCancNFe() != null) {
						nota.setFisNotaEntradaRecibo("OK");
					} else {
						nota.setFisNotaStatus(new FisNotaStatus(ENotaStatus.ERRO));
						nota.setFisNotaEntradaErro("A nota de entrada");
					}
				} else {
					nota.setFisNotaStatus(new FisNotaStatus(ENotaStatus.ERRO));
					nota.setFisNotaEntradaErro("Nao achou a nota na sefaz ou problemas na hora do acesso ao servidor.");
				}
				// salva a entrada
				nota = fiscal.salvar(nota, false);

				// integracao com OpenSIG
				try {
					// achando a compra referente a NFe
					FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, empresa);
					FiltroTexto ft = new FiltroTexto("empFornecedor.empEntidade.empEntidadeDocumento1", ECompara.IGUAL, cnpj);
					FiltroNumero fn = new FiltroNumero("comCompraNumero", ECompara.IGUAL, numero);
					FiltroBinario fb = new FiltroBinario("comCompraNfe", ECompara.IGUAL, 0);
					GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, ft, fn, fb });
					CoreServiceImpl<Dados> core = new CoreServiceImpl<Dados>();
					Dados compra = (Dados) core.getResultado("pu_comercial", "SELECT t FROM ComCompra t", gf);

					// atualiza a compra
					if (compra != null) {
						ParametroObjeto po = new ParametroObjeto("fisNotaEntrada", nota);
						ParametroBinario pb = new ParametroBinario("comCompraNfe", 1);
						GrupoParametro gp = new GrupoParametro(new IParametro[] { po, pb });
						Sql sql = new Sql(compra, EComando.ATUALIZAR, gf, gp);
						core.executar(new Sql[] { sql });
					}
				} catch (Exception e) {
					UtilServer.LOG.info("Modulo Fiscal fora do OpenSIG.");
				}
			} catch (Exception e) {
				UtilServer.LOG.error("Erro ao salvar a entrada com chave " + chave, e);
				throw new FiscalException("Não foi possível salvar a NF de entrada ou ela já existe!", e);
			}
		} catch (OpenSigException ope) {
			UtilServer.LOG.error("Erro na indentificacao da entrada", ope);
			throw new FiscalException(ope.getMessage());
		}
	}

	public void atualizar() throws FiscalException {
		try {
			// monta o filtro
			GrupoFiltro gf = new GrupoFiltro();
			FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, empresa);
			gf.add(fo, EJuncao.E);

			String chave = UtilServer.getValorTag(doc.getDocumentElement(), "chNFe", true);
			FiltroTexto ft = new FiltroTexto("fisNotaEntradaChave", ECompara.IGUAL, chave);
			gf.add(ft);

			// recupera o protocolo
			String prot = doc.getElementsByTagName("nProt").item(1).getFirstChild().getNodeValue();

			// cria o sql
			ParametroTexto pt = new ParametroTexto("fisNotaEntradaProtocoloCancelado", prot);
			ParametroTexto pt1 = new ParametroTexto("fisNotaEntradaXmlCancelado", xml);
			ParametroObjeto po = new ParametroObjeto("fisNotaStatus", status);
			GrupoParametro gp = new GrupoParametro(new IParametro[] { pt, pt1, po });

			// atualiza a entrada
			try {
				Sql sql = new Sql(new FisNotaEntrada(), EComando.ATUALIZAR, gf, gp);
				FiscalServiceImpl<FisNotaEntrada> service = new FiscalServiceImpl<FisNotaEntrada>();
				service.executar(new Sql[] { sql });
			} catch (Exception e) {
				UtilServer.LOG.error("Erro ao atualizar a entrada com chave " + chave, e);
				throw new FiscalException("Não foi possível atualizar a NF de entrada!", e);
			}
		} catch (OpenSigException ope) {
			UtilServer.LOG.error("Erro na indentificacao da entrada", ope);
			throw new FiscalException(ope.getMessage());
		}
	}
}
