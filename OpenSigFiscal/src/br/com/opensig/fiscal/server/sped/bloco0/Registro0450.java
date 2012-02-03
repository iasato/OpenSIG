package br.com.opensig.fiscal.server.sped.bloco0;

import java.util.ArrayList;
import java.util.List;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.comercial.shared.modelo.ComVendaProduto;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0450;
import br.com.opensig.produto.shared.modelo.ProdTributacao;

public class Registro0450 extends ARegistro<Dados0450, ProdTributacao> {

	private List<Integer> decreto;

	public Registro0450() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0450.xml");
	}

	@Override
	public void executar() {
		qtdLinhas = 0;
		decreto = new ArrayList<Integer>();

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", arquivo);
			// vendas
			for (ComVenda venda : getVendas()) {
				for (ComVendaProduto vProd : venda.getComVendaProdutos()) {
					if (!decreto.contains(vProd.getProdProduto().getProdTributacao().getProdTributacaoId()) && !vProd.getProdProduto().getProdTributacao().getProdTributacaoDecreto().equals("")) {
						out.write(getDados(vProd.getProdProduto().getProdTributacao()));
						out.flush();
						decreto.add(vProd.getProdProduto().getProdTributacao().getProdTributacaoId());
					}
				}
			}
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected Dados0450 getDados(ProdTributacao trib) {
		Dados0450 d = new Dados0450();
		d.setCod_inf(trib.getProdTributacaoId() + "");
		d.setTxt(trib.getProdTributacaoDecreto());
		
		normalizar(d);
		qtdLinhas++;
		return d;
	}
}
