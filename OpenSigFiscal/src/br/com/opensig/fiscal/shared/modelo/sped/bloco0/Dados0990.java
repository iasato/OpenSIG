package br.com.opensig.fiscal.shared.modelo.sped.bloco0;

import br.com.opensig.fiscal.server.sped.Bean;

public class Dados0990 extends Bean {

	private String reg;
	private int qtd_lin;

	public String getReg() {
		return reg;
	}

	public void setReg(String reg) {
		this.reg = reg;
	}

	public int getQtd_lin() {
		return qtd_lin;
	}

	public void setQtd_lin(int qtd_lin) {
		this.qtd_lin = qtd_lin;
	}
}
