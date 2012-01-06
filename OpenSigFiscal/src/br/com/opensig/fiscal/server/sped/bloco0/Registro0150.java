package br.com.opensig.fiscal.server.sped.bloco0;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComCompra;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.empresa.shared.modelo.EmpEndereco;
import br.com.opensig.empresa.shared.modelo.EmpEntidade;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0150;

public class Registro0150 extends ARegistro<Dados0150> {

	public Registro0150() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0150.xml");
		qtdLinhas = 0;
	}

	@Override
	public void executar() {
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", arquivo);
			gerarFornecedores(out);
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected Dados0150 getDados() throws Exception {
		return null;
	}

	private void gerarFornecedores(BeanWriter out) {
		try {
			// monta o filtro dos blocos/registros
			GrupoFiltro gf = new GrupoFiltro();
			for (Integer id : sped.getCompras()) {
				FiltroNumero fn = new FiltroNumero("comCompraId", ECompara.IGUAL, id);
				gf.add(fn, EJuncao.OU);
			}
			FiltroNumero fn = new FiltroNumero("comCompraId", ECompara.IGUAL, 0);
			gf.add(fn);
			// seleciona todos as compras
			Lista<ComCompra> compras = service.selecionar(new ComCompra(), 0, 0, gf, false);
			for (ComCompra compra : compras.getLista()) {
				out.write(getDados(compra.getEmpFornecedor().getEmpEntidade()));
				out.flush();
			}
		} catch (Exception e) {
			UtilServer.LOG.error("Erro na geracao dos fornecedores.", e);
		}
	}

	private Dados0150 getDados(EmpEntidade ent) {
		Dados0150 d = new Dados0150();
		d.setReg("0150");
		d.setCod_part(ent.getEmpEntidadeId() + "");
		d.setNome(ent.getEmpEntidadeNome1());

		if (ent.getEmpEntidadeDocumento1().length() == 18) {
			d.setCnpj(ent.getEmpEntidadeDocumento1().replaceAll("\\D", ""));
			d.setIe(ent.getEmpEntidadeDocumento2());
		} else {
			d.setCpf(ent.getEmpEntidadeDocumento1().replaceAll("\\D", ""));
		}

		// TODO suframa adicionar ao sistema isso

		for (EmpEndereco end : ent.getEmpEnderecos()) {
			d.setEnd(end.getEmpEnderecoLogradouro());
			d.setNum(end.getEmpEnderecoNumero() + "");
			d.setCompl(end.getEmpEnderecoComplemento());
			d.setBairro(end.getEmpEnderecoBairro());
			d.setCod_mun(end.getEmpMunicipio().getEmpMunicipioIbge());
			d.setCod_pais(end.getEmpMunicipio().getEmpEstado().getEmpPais().getEmpPaisIbge());
			break;
		}

		qtdLinhas++;
		return d;
	}
}
