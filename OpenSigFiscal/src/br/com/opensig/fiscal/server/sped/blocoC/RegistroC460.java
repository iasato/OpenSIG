package br.com.opensig.fiscal.server.sped.blocoC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.beanio.BeanWriter;
import org.beanio.StreamFactory;

import br.com.opensig.comercial.shared.modelo.ComEcfVenda;
import br.com.opensig.comercial.shared.modelo.ComEcfVendaProduto;
import br.com.opensig.comercial.shared.modelo.ComEcfZ;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.fiscal.server.sped.ARegistro;

public class RegistroC460 extends ARegistro<DadosC460, ComEcfVenda> {

	private ComEcfZ z;
	private Map<String, List<DadosC470>> analitico = new HashMap<String, List<DadosC470>>();
	
	@Override
	public void executar() {
		qtdLinhas = 0;
		
		try {
			StreamFactory factory = StreamFactory.newInstance();
			factory.load(getClass().getResourceAsStream(bean));
			BeanWriter out = factory.createWriter("EFD", escritor);
			
			RegistroC470 r470 = new RegistroC470();
			r470.setEsquitor(escritor);
			r470.setAuth(auth);
			
			for (ComEcfVenda venda : ecfs) {
				if (venda.getComEcf().getComEcfId() == z.getComEcf().getComEcfId() && venda.getComEcfVendaData().compareTo(z.getComEcfZData()) == 0) {
					bloco = getDados(venda);
					out.write(bloco);
					out.flush();
					
					// itens das vendas
					for(ComEcfVendaProduto vp : venda.getComEcfVendaProdutos()){
						r470.setDados(vp);
						r470.executar();
						setAnalitico(r470.getBloco());
						qtdLinhas += r470.getQtdLinhas();
					}
				}
			}
			
			// analitico das compras
			getAnalitico();
		} catch (Exception e) {
			qtdLinhas = 0;
			UtilServer.LOG.error("Erro na geracao do Registro -> " + bean, e);
		}
	}
	
	@Override
	protected DadosC460 getDados(ComEcfVenda dados) throws Exception {
		DadosC460 d = new DadosC460();
		d.setCod_mod(dados.getComEcf().getComEcfCodigo());
		d.setCod_sit(dados.getComEcfVendaCancelada() ? "02" : "00");
		d.setNum_doc(dados.getComEcfVendaCoo());
		if (dados.getComEcfVendaCancelada() == false) {
			d.setVl_doc(dados.getComEcfVendaLiquido());
			d.setVl_pis(0.00);
			d.setVl_cofins(0.00);
			d.setCpf_cnpj(dados.getComEcfVendaCpf());
			d.setNom_adq(dados.getComEcfVendaNome());
		}

		normalizar(d);
		qtdLinhas++;
		return d;
	}
	
	public ComEcfZ getZ() {
		return z;
	}
	
	public void setZ(ComEcfZ z) {
		this.z = z;
	}
	
	private void setAnalitico(DadosC470 d) {
		String chave = d.getCst_icms() + d.getCfop() + d.getAliq_icms();
		List<DadosC470> lista = analitico.get(chave);
		if (lista == null) {
			lista = new ArrayList<DadosC470>();
			lista.add(d);
			analitico.put(chave, lista);
		} else {
			lista.add(d);
		}
	}

	private void getAnalitico(){
		if (!analitico.isEmpty()) {
			RegistroC490 r490 = new RegistroC490();
			r490.setEsquitor(escritor);
			r490.setAuth(auth);
			for (Entry<String, List<DadosC470>> entry : analitico.entrySet()) {
				r490.setDados(entry.getValue());
				r490.executar();
				qtdLinhas += r490.getQtdLinhas();
			}
		}
		analitico.clear();
	}
}
