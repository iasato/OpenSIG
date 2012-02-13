package br.com.opensig.fiscal.server.sped.blocoE;

import java.util.Date;

import br.com.opensig.fiscal.server.sped.Bean;

public class DadosE100 extends Bean {

	private Date dt_ini;
	private Date dt_fin;

	public DadosE100() {
		reg = "E100";
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
