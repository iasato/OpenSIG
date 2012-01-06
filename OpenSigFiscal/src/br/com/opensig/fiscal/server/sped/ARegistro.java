package br.com.opensig.fiscal.server.sped;

import java.io.Writer;
import java.lang.reflect.Method;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.fiscal.client.servico.FiscalService;
import br.com.opensig.fiscal.shared.modelo.FisSpedFiscal;

public abstract class ARegistro<E extends Bean> implements IRegistro {

	protected Writer arquivo;
	protected FisSpedFiscal sped;
	protected FiscalService service;
	protected Autenticacao auth;
	protected String bean;
	protected int qtdLinhas;
	protected boolean fimBloco;

	public ARegistro(String bean) {
		this.bean = bean;
	}

	@Override
	public void executar() {
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", arquivo);
			out.write(getDados());
			out.flush();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	protected abstract E getDados() throws Exception;

	@Override
	public Writer getArquivo() {
		return arquivo;
	}

	@Override
	public void setArquivo(Writer arquivo) {
		this.arquivo = arquivo;
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

	protected void normalizar(E dados) {
		for (Method metodo : dados.getClass().getMethods()) {
			try {
				if (UtilServer.isGetter(metodo)) {
					Object valorMetodo = metodo.invoke(dados, new Object[] {});

					if (metodo.getReturnType() == String.class) {
						String nomeMetodo = metodo.getName().replaceFirst("get", "set");
						Method set = dados.getClass().getMethod(nomeMetodo, new Class[] { String.class });
						String valor = valorMetodo == null ? "" : valorMetodo.toString();
						valor = UtilServer.normaliza(valor).replaceAll(auth.getConf().get("nfe.regexp"), "");
						set.invoke(dados, new Object[] { valor.trim() });
					}
				}
			} catch (Exception ex) {
				UtilServer.LOG.debug("Erro ao padronizar. " + metodo.getName(), ex);
			}
		}
	}
}
