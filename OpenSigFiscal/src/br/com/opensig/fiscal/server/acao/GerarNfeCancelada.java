package br.com.opensig.fiscal.server.acao;

import javax.xml.bind.JAXBElement;

import br.com.opensig.cancnfe.TCancNFe;
import br.com.opensig.cancnfe.TCancNFe.InfCanc;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.padroes.Chain;
import br.com.opensig.core.client.servico.CoreService;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.shared.modelo.ENotaStatus;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;

public class GerarNfeCancelada extends Chain {

	private CoreService servico;
	private FisNotaSaida saida;
	private String obs;
	
	public GerarNfeCancelada(Chain next, CoreService servico, FisNotaSaida saida, String obs) throws OpenSigException {
		super(next);
		this.servico = servico;
		this.saida = saida;
		this.obs = obs;
	}

	@Override
	public void execute() throws OpenSigException {
		// seleciona a nota
		FiltroNumero fn = new FiltroNumero("fisNotaSaidaId", ECompara.IGUAL, saida.getId());
		saida = (FisNotaSaida) servico.selecionar(saida, fn, false);
		// cria o xml
		String xml = getXml(saida, obs);
		// salva o registro
		SalvarSaida salvar = new SalvarSaida(next, xml, new FisNotaStatus(ENotaStatus.CANCELANDO), saida.getEmpEmpresa());
		salvar.execute();
		saida = salvar.getNota();
	}
	
	public FisNotaSaida getSaida(){
		return saida;
	}

	private String getXml(FisNotaSaida saida, String obs) throws OpenSigException {
		try {
			InfCanc infCanc = new InfCanc();
			infCanc.setId("ID" + saida.getFisNotaSaidaChave());
			infCanc.setTpAmb(UtilServer.CONF.get("nfe.tipoamb"));
			infCanc.setChNFe(saida.getFisNotaSaidaChave());
			infCanc.setNProt(saida.getFisNotaSaidaProtocolo());
			infCanc.setXJust(UtilServer.normaliza(obs));
			infCanc.setXServ("CANCELAR");
			TCancNFe cancNfe = new TCancNFe();
			cancNfe.setInfCanc(infCanc);
			cancNfe.setVersao(UtilServer.CONF.get("nfe.versao"));
			// transforma em string o xml e salva
			JAXBElement<TCancNFe> element = new br.com.opensig.cancnfe.ObjectFactory().createCancNFe(cancNfe);
			return UtilServer.objToXml(element, "br.com.opensig.cancnfe");
		} catch (Exception e) {
			throw new OpenSigException(e.getMessage());
		}
	}
}