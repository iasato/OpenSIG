package br.com.opensig.fiscal.server.sped.blocoC;

import br.com.opensig.fiscal.server.sped.Bean;

public class DadosC160 extends Bean {

	private String cod_part;
	private String veic_id;
	private int qtd_vol;
	private double peso_brt;
	private double peso_liq;
	private String uf_id;
	
	public DadosC160() {
		reg = "C160";
	}

	public String getCod_part() {
		return cod_part;
	}

	public void setCod_part(String cod_part) {
		this.cod_part = cod_part;
	}

	public String getVeic_id() {
		return veic_id;
	}

	public void setVeic_id(String veic_id) {
		this.veic_id = veic_id;
	}

	public int getQtd_vol() {
		return qtd_vol;
	}

	public void setQtd_vol(int qtd_vol) {
		this.qtd_vol = qtd_vol;
	}

	public double getPeso_brt() {
		return peso_brt;
	}

	public void setPeso_brt(double peso_brt) {
		this.peso_brt = peso_brt;
	}

	public double getPeso_liq() {
		return peso_liq;
	}

	public void setPeso_liq(double peso_liq) {
		this.peso_liq = peso_liq;
	}

	public String getUf_id() {
		return uf_id;
	}

	public void setUf_id(String uf_id) {
		this.uf_id = uf_id;
	}
	
}
