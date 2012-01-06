package br.com.opensig.fiscal.server.acao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.servico.CoreService;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.server.exportar.AExportacao;
import br.com.opensig.core.shared.modelo.ExpListagem;
import br.com.opensig.core.shared.modelo.ExpRegistro;
import br.com.opensig.core.shared.modelo.sistema.SisExpImp;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public class ExportarSped extends AExportacao<FisSpedFiscal> {

	@Override
	public byte[] getArquivo(CoreService<FisSpedFiscal> service, SisExpImp modo, ExpListagem<FisSpedFiscal> exp, String[][] enderecos, String[][] contatos) {
		return null;
	}

	@Override
	public byte[] getArquivo(CoreService<FisSpedFiscal> service, SisExpImp modo, ExpRegistro<FisSpedFiscal> exp, String[][] enderecos, String[][] contatos) {
		/*
		byte[] obj = null;

		try {
			FiltroNumero fn = new FiltroNumero("fisSpedFiscalId", ECompara.IGUAL, exp.getClasse().getFisSpedFiscalId());
			FisSpedFiscal sped = service.selecionar(exp.getClasse(), fn, false);
			String cnpj = auth.getEmpresa()[5].replaceAll("\\D", "");

			String nome;
			if (sped.getFisSpedFiscalMes() > 9) {
				nome = sped.getFisSpedFiscalAno() + "" + sped.getFisSpedFiscalMes();
			} else {
				nome = sped.getFisSpedFiscalAno() + "0" + sped.getFisSpedFiscalMes();
			}
			
			String arquivo = UtilServer.PATH_EMPRESA + cnpj + "/sped/" + nome + "." + modo.getSisExpImpExtensoes();
			File arq = new File(arquivo);

			if (arq.exists()) {
				InputStream is = new FileInputStream(arq);
				obj = new byte[is.available()];
				is.read(obj);
				is.close();
			}
		} catch (Exception e) {
			obj = null;
		}
*/
		return null;
	}

}
