package br.com.opensig.fiscal.server.sped;

import java.io.Writer;

import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.fiscal.client.servico.FiscalService;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public interface IRegistro {

	public Writer getArquivo();

	public void setArquivo(Writer arquivo);

	public FisSpedFiscal getSped();

	public void setSped(FisSpedFiscal sped);

	public FiscalService getService();

	public void setService(FiscalService service);

	public Autenticacao getAuth();
	
	public void setAuth(Autenticacao auth);
	
	public int getQtdLinhas();
	
	public void setQtdLInhas(int qtdLinhas);
	
	public boolean getFimBloco();
	
	public void setFimBloco(boolean fimBloco);
	
	public void executar();

}
