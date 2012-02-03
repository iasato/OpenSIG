package br.com.opensig.fiscal.server.sped.bloco0;

import java.util.ArrayList;
import java.util.List;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.comercial.shared.modelo.ComNatureza;
import br.com.opensig.comercial.shared.modelo.ComVenda;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0400;

public class Registro0400 extends ARegistro<Dados0400, ComNatureza> {

	private List<Integer> naturezas;

	public Registro0400() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0400.xml");
	}

	@Override
	public void executar() {
		qtdLinhas = 0;
		naturezas = new ArrayList<Integer>();

		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", arquivo);
			// compras
			for (ComCompra compra : getCompras()) {
				if (!naturezas.contains(compra.getComNatureza().getComNaturezaId())) {
					out.write(getDados(compra.getComNatureza()));
					out.flush();
					naturezas.add(compra.getComNatureza().getComNaturezaId());
				}
			}
			// vendas
			for (ComVenda venda : getVendas()) {
				if (!naturezas.contains(venda.getComNatureza().getComNaturezaId())) {
					out.write(getDados(venda.getComNatureza()));
					out.flush();
					naturezas.add(venda.getComNatureza().getComNaturezaId());
				}
			}
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected Dados0400 getDados(ComNatureza nat) {
		Dados0400 d = new Dados0400();
		d.setCod_nat(nat.getComNaturezaId() + "");
		d.setDescr_nat(nat.getComNaturezaDescricao());

		normalizar(d);
		qtdLinhas++;
		return d;
	}

}
