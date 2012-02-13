package br.com.opensig.fiscal.server.sped.blocoH;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.produto.shared.modelo.ProdEstoque;
import br.com.opensig.produto.shared.modelo.ProdProduto;

public class RegistroH010 extends ARegistro<DadosH010, ProdProduto> {

	private double total;
	
	@Override
	public void executar() {
		qtdLinhas = 0;
		
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);

			// seleciona todos os produtos com estoque maior que ZERO
			FiltroObjeto fo = new FiltroObjeto("t1.empEmpresa", ECompara.IGUAL, sped.getEmpEmpresa());
			FiltroNumero fn = new FiltroNumero("t1.prodEstoqueQuantidade", ECompara.MAIOR, 0);
			GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fo, fn });
			Lista<ProdProduto> lista = service.selecionar(new ProdProduto(), 0, 0, gf, false);
			for (ProdProduto prod : lista.getLista()) {
				bloco = getDados(prod);
				out.write(bloco);
				out.flush();
			}
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}

	@Override
	protected DadosH010 getDados(ProdProduto dados) throws Exception {
		DadosH010 d = new DadosH010();
		d.setCod_item(dados.getProdProdutoId() + "");
		d.setUnid(dados.getProdEmbalagem().getProdEmbalagemNome());
		double qtd = 0.00;
		for (ProdEstoque est : dados.getProdEstoques()) {
			if (est.getEmpEmpresa().getEmpEmpresaId() == sped.getEmpEmpresa().getEmpEmpresaId()) {
				qtd = est.getProdEstoqueQuantidade();
				break;
			}
		}
		d.setQtd(qtd);
		d.setVl_unit(dados.getProdProdutoPreco());
		d.setVl_item(qtd * dados.getProdProdutoPreco());
		this.total += d.getVl_item();
		d.setInd_prop("0");
		d.setCod_part("");
		d.setTxt_compl("");
		d.setCod_cta("");

		normalizar(d);
		qtdLinhas++;
		return d;
	}
	
	public double getTotal() {
		return total;
	}
	
	public void setTotal(double total) {
		this.total = total;
	}
}
