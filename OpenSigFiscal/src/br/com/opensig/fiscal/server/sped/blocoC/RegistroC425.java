package br.com.opensig.fiscal.server.sped.blocoC;

import java.util.Collection;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC425 extends ARegistro<DadosC425, ComEcfVendaProduto> {

	private int idTributacao;
	private Collection<ComEcfVendaProduto> produtos;

	@Override
	public void executar() {
		qtdLinhas = 0;

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			for (ComEcfVendaProduto prod : produtos) {
				if (prod.getProdProduto().getProdTributacao().getProdTributacaoId() == idTributacao) {
					bloco = getDados(prod);
					out.write(bloco);
					out.flush();
				}
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosC425 getDados(ComEcfVendaProduto dados) throws Exception {
		DadosC425 d = new DadosC425();
		d.setCod_item(dados.getProdProduto().getProdProdutoId() + "");
		d.setQtd(dados.getComEcfVendaProdutoQuantidade());
		d.setUnid(dados.getProdEmbalagem().getProdEmbalagemNome());
		d.setVl_item(dados.getComEcfVendaProdutoTotal());

		normalizar(d);
		qtdLinhas++;
		return d;
	}

	public int getIdTributacao() {
		return idTributacao;
	}

	public void setIdTributacao(int idTributacao) {
		this.idTributacao = idTributacao;
	}

	public Collection<ComEcfVendaProduto> getProdutos() {
		return produtos;
	}

	public void setProdutos(Collection<ComEcfVendaProduto> produtos) {
		this.produtos = produtos;
	}
}
