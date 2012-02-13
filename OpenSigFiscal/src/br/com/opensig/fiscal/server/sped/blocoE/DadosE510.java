package br.com.opensig.fiscal.server.sped.blocoE;

import br.com.opensig.fiscal.server.sped.Bean;

public class DadosE510 extends Bean {

	private int cfop;
	private String cst_ipi;
	private double vl_cont_ipi;
	private double vl_bc_ipi;
	private double vl_ipi;

	public DadosE510() {
		reg = "E510";
	}

	public int getCfop() {
		return cfop;
	}

	public void setCfop(int cfop) {
		this.cfop = cfop;
	}

	public String getCst_ipi() {
		return cst_ipi;
	}

	public void setCst_ipi(String cst_ipi) {
		this.cst_ipi = cst_ipi;
	}

	public double getVl_cont_ipi() {
		return vl_cont_ipi;
	}

	public void setVl_cont_ipi(double vl_cont_ipi) {
		this.vl_cont_ipi = vl_cont_ipi;
	}

	public double getVl_bc_ipi() {
		return vl_bc_ipi;
	}

	public void setVl_bc_ipi(double vl_bc_ipi) {
		this.vl_bc_ipi = vl_bc_ipi;
	}

	public double getVl_ipi() {
		return vl_ipi;
	}

	public void setVl_ipi(double vl_ipi) {
		this.vl_ipi = vl_ipi;
	}

}
