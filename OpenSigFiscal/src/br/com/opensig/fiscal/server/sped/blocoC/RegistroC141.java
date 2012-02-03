package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.financeiro.shared.modelo.FinPagamento;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.blocoC.DadosC141;

public class RegistroC141<T extends Dados> extends ARegistro<DadosC141, T> {

	public RegistroC141() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/blocoC/BeanC141.xml");
	}

	@Override
	protected DadosC141 getDados(T dados) throws Exception {
		if (dados instanceof FinRecebimento) {
			return getReceber((FinRecebimento) dados);
		} else if (dados instanceof FinPagamento) {
			return getPagar((FinPagamento) dados);
		} else {
			return null;
		}
	}

	private DadosC141 getReceber(FinRecebimento receber) {
		DadosC141 d = new DadosC141();
		d.setNum_parc(Integer.valueOf(receber.getFinRecebimentoParcela().substring(0, 2)));
		d.setDt_vcto(receber.getFinRecebimentoVencimento());
		d.setVl_parc(receber.getFinRecebimentoValor());
		return d;
	}

	private DadosC141 getPagar(FinPagamento pagar) {
		DadosC141 d = new DadosC141();
		d.setNum_parc(Integer.valueOf(pagar.getFinPagamentoParcela().substring(0, 2)));
		d.setDt_vcto(pagar.getFinPagamentoVencimento());
		d.setVl_parc(pagar.getFinPagamentoValor());
		return d;
	}
}
