package br.com.opensig.fiscal.server.acao;

import java.util.List;
import java.util.Map;

import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.importar.IImportacao;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.sistema.SisExpImp;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public class ImportarSped implements IImportacao<FisSpedFiscal> {

	@Override
	public Map<String, List<FisSpedFiscal>> setArquivo(Autenticacao auth, Map<String, byte[]> arquivos, SisExpImp modo) throws OpenSigException {
		return null;
	}

}
