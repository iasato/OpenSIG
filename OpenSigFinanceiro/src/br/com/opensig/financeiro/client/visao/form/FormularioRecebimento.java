package br.com.opensig.financeiro.client.visao.form;

import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.financeiro.shared.modelo.FinForma;
import br.com.opensig.financeiro.shared.modelo.FinReceber;
import br.com.opensig.financeiro.shared.modelo.FinRecebimento;

public class FormularioRecebimento extends AFormularioFinanciado<FinRecebimento> {

	public FormularioRecebimento(SisFuncao funcao) {
		super(new FinRecebimento(), funcao);
		nomes.put("id", "finRecebimentoId");
		nomes.put("financeiroId", "finReceber.finReceberId");
		nomes.put("financeiroNome", "finReceber.empEntidade.empEntidadeNome1");
		nomes.put("financeiroConta", "finReceber.finConta.finContaId");
		nomes.put("financeiroEmpresa", "finReceber.empEmpresa");
		nomes.put("documento", "finRecebimentoDocumento");
		nomes.put("valor", "finRecebimentoValor");
		nomes.put("parcela", "finRecebimentoParcela");
		nomes.put("cadastro", "finRecebimentoCadastro");
		nomes.put("vencimento", "finRecebimentoVencimento");
		nomes.put("quitado", "finRecebimentoQuitado");
		nomes.put("realizado", "finRecebimentoRealizado");
		nomes.put("financeiroNfe", "finReceber.finReceberNfe");
		nomes.put("observacao", "finRecebimentoObservacao");
		inicializar();
	}

	/*
	 * @see br.com.sig.core.client.visao.lista.IFormulario#setDados()
	 */
	public boolean setDados() {
		classe.setFinForma(new FinForma(Integer.valueOf(cmbForma.getValue())));
		classe.setFinReceber(new FinReceber(Integer.valueOf(hdnFinanceiro.getValueAsString())));
		classe.setFinRecebimentoId(Integer.valueOf(hdnCod.getValueAsString()));
		classe.setFinRecebimentoParcela(txtParcela.getValueAsString());
		classe.setFinRecebimentoObservacao(txtObservacao.getValueAsString());
		classe.setFinRecebimentoQuitado(false);
		classe.setFinRecebimentoRealizado(null);
		classe.setFinRecebimentoVencimento(dtVencimento.getValue());
		classe.setFinRecebimentoCadastro(UtilClient.DATA);
		
		if (txtValor.getValue() != null) {
			classe.setFinRecebimentoValor(txtValor.getValue().doubleValue());
		}
		if (Integer.valueOf(cmbForma.getValue()) == 2) {
			classe.setFinRecebimentoDocumento(cmbBandeira.getRawValue());
		} else {
			classe.setFinRecebimentoDocumento(txtDocumento.getValueAsString());
		}
		return true;
	}

}