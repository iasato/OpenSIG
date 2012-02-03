package br.com.opensig.fiscal.server.sped;

import java.io.Writer;
import java.util.Date;
import java.util.List;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.fiscal.client.servico.FiscalService;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public interface IRegistro<E,T> {

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
	
	public Date getInicio();
	
	public void setInicio(Date inicio);
	
	public Date getFim();
	
	public void setFim(Date fim);
	
	public boolean getFimBloco();
	
	public void setFimBloco(boolean fimBloco);
	
	public List<ComCompra> getCompras();
	
	public void setCompras(List<ComCompra> compras);
	
	public List<ComFrete> getFretes();
	
	public void setFretes(List<ComFrete> fretes);
	
	public List<ComVenda> getVendas();
	
	public void setVendas(List<ComVenda> vendas);
	
	public List<ComEcfVenda> getEcfs();
	
	public void setEcfs(List<ComEcfVenda> ecfs);
	
	public T getDados();
	
	public void setDados(T dados);
	
	public E getBloco();
	
	public void setBloco(E bloco);
	
	public void executar();

}
