package br.com.opensig.fiscal.server.sped;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComFrete;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.fiscal.client.servico.FiscalService;
import br.com.opensig.fiscal.shared.modelo.FisSpedBloco;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public abstract class ARegistro<E extends Bean, T> implements IRegistro<E, T> {

	protected File leitor;
	protected Writer escritor;
	protected FisSpedFiscal sped;
	protected FiscalService service;
	protected Autenticacao auth;
	protected String bean;
	protected int qtdLinhas;
	protected boolean fimBloco;
	protected Date inicio;
	protected Date fim;
	protected List<FisSpedBloco> blocos;
	protected List<ComCompra> compras;
	protected List<ComFrete> fretes;
	protected List<ComVenda> vendas;
	protected List<ComEcfVenda> ecfs;
	protected E bloco;
	protected T dados;

	public ARegistro() {
		bean = "/" + getClass().getName().replace("Registro", "Bean").replace('.', '/') + ".xml";
	}

	public ARegistro(String bean) {
		this.bean = bean;
	}

	@Override
	public void executar() {
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			bloco = getDados(dados);
			normalizar(bloco);
			out.write(bloco);
			out.flush();
			qtdLinhas = 1;
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	protected void normalizar(E bloco) {
		for (Method metodo : bloco.getClass().getMethods()) {
			try {
				if (UtilServer.isGetter(metodo)) {
					Object valorMetodo = metodo.invoke(bloco, new Object[] {});

					if (metodo.getReturnType() == String.class) {
						String nomeMetodo = metodo.getName().replaceFirst("get", "set");
						Method set = bloco.getClass().getMethod(nomeMetodo, new Class[] { String.class });
						String valor = valorMetodo == null ? "" : valorMetodo.toString();
						valor = UtilServer.normaliza(valor).replaceAll(auth.getConf().get("nfe.regexp"), "");
						set.invoke(bloco, new Object[] { valor.trim() });
					}
				}
			} catch (Exception ex) {
				UtilServer.LOG.debug("Erro ao padronizar. " + metodo.getName(), ex);
			}
		}
	}

	protected int getSubBlocos(String letra) {
		int tot = 0;
		for (FisSpedBloco bl : blocos) {
			if (bl.getFisSpedBlocoLetra().equals(letra) && bl.getFisSpedBlocoNivel() > 1) {
				tot++;
			}
		}
		return tot;
	}

	protected abstract E getDados(T dados) throws Exception;

	@Override
	public File getLeitor(){
		return this.leitor;
	}
	
	@Override
	public void setLeitor(File leitor){
		this.leitor = leitor;
	}
	
	@Override
	public Writer getEscritor() {
		return escritor;
	}

	@Override
	public void setEsquitor(Writer arquivo) {
		this.escritor = arquivo;
	}

	@Override
	public FisSpedFiscal getSped() {
		return sped;
	}

	@Override
	public void setSped(FisSpedFiscal sped) {
		this.sped = sped;
	}

	@Override
	public FiscalService getService() {
		return service;
	}

	@Override
	public void setService(FiscalService service) {
		this.service = service;
	}

	@Override
	public Autenticacao getAuth() {
		return auth;
	}

	@Override
	public void setAuth(Autenticacao auth) {
		this.auth = auth;
	}

	@Override
	public int getQtdLinhas() {
		return qtdLinhas;
	}

	@Override
	public void setQtdLInhas(int qtdLinhas) {
		this.qtdLinhas = qtdLinhas;
	}

	@Override
	public boolean getFimBloco() {
		return fimBloco;
	}

	@Override
	public void setFimBloco(boolean fimBloco) {
		this.fimBloco = fimBloco;
	}

	@Override
	public Date getInicio() {
		return this.inicio;
	}

	@Override
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	@Override
	public Date getFim() {
		return this.fim;
	}

	@Override
	public void setFim(Date fim) {
		this.fim = fim;
	}

	@Override
	public List<FisSpedBloco> getBlocos() {
		return this.blocos;
	}

	@Override
	public void setBlocos(List<FisSpedBloco> blocos) {
		this.blocos = blocos;
	}

	@Override
	public List<ComCompra> getCompras() {
		return this.compras;
	}

	@Override
	public void setCompras(java.util.List<ComCompra> compras) {
		this.compras = compras;
	}

	@Override
	public List<ComFrete> getFretes() {
		return this.fretes;
	}

	@Override
	public void setFretes(List<ComFrete> fretes) {
		this.fretes = fretes;
	}

	@Override
	public List<ComVenda> getVendas() {
		return this.vendas;
	}

	@Override
	public void setVendas(List<ComVenda> vendas) {
		this.vendas = vendas;
	}

	@Override
	public List<ComEcfVenda> getEcfs() {
		return this.ecfs;
	}

	@Override
	public void setEcfs(List<ComEcfVenda> ecfs) {
		this.ecfs = ecfs;
	}

	@Override
	public T getDados() {
		return this.dados;
	}

	@Override
	public void setDados(T dados) {
		this.dados = dados;
	}

	@Override
	public E getBloco() {
		return this.bloco;
	}

	@Override
	public void setBloco(E bloco) {
		this.bloco = bloco;
	};
}
