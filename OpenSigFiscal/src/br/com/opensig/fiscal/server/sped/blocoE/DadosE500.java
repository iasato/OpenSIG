package br.com.opensig.fiscal.server.sped.blocoE;

import java.util.Date;

import br.com.opensig.fiscal.server.sped.Bean;

public class DadosE500 extends Bean {

	private String ind_apur;
	private Date dt_ini;
	private Date dt_fin;

	public DadosE500() {
		reg = "E500";
	}

	public String getInd_apur() {
		return ind_apur;
	}
	
	public void setInd_apur(String ind_apur) {
		this.ind_apur = ind_apur;
	}
	
	public Date getDt_ini() {
		return dt_ini;
	}

	public void setDt_ini(Date dt_ini) {
		this.dt_ini = dt_ini;
	}

	public Date getDt_fin() {
		return dt_fin;
	}

	public void setDt_fin(Date dt_fin) {
		this.dt_fin = dt_fin;
	}

}
