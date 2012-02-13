package br.com.opensig.fiscal.server.sped.blocoE;

import br.com.opensig.fiscal.server.sped.Bean;

public class DadosE110 extends Bean {

	private double vl_tot_debitos;
	private double vl_aj_debitos;
	private double vl_tot_aj_debitos;
	private double vl_estornos_cred;
	private double vl_tot_creditos;
	private double vl_aj_creditos;
	private double vl_tot_aj_creditos;
	private double vl_estornos_deb;
	private double vl_sld_credor_ant;
	private double vl_sld_apurado;
	private double vl_tot_ded;
	private double vl_icms_recolher;
	private double vl_sld_credor_transportar;
	private double deb_esp;
	private double vl_or;

	public DadosE110() {
		reg = "E110";
	}

	public double getVl_tot_debitos() {
		return vl_tot_debitos;
	}

	public void setVl_tot_debitos(double vl_tot_debitos) {
		this.vl_tot_debitos = vl_tot_debitos;
	}

	public double getVl_aj_debitos() {
		return vl_aj_debitos;
	}

	public void setVl_aj_debitos(double vl_aj_debitos) {
		this.vl_aj_debitos = vl_aj_debitos;
	}

	public double getVl_tot_aj_debitos() {
		return vl_tot_aj_debitos;
	}

	public void setVl_tot_aj_debitos(double vl_tot_aj_debitos) {
		this.vl_tot_aj_debitos = vl_tot_aj_debitos;
	}

	public double getVl_estornos_cred() {
		return vl_estornos_cred;
	}

	public void setVl_estornos_cred(double vl_estornos_cred) {
		this.vl_estornos_cred = vl_estornos_cred;
	}

	public double getVl_aj_creditos() {
		return vl_aj_creditos;
	}

	public void setVl_aj_creditos(double vl_aj_creditos) {
		this.vl_aj_creditos = vl_aj_creditos;
	}

	public double getVl_tot_aj_creditos() {
		return vl_tot_aj_creditos;
	}

	public void setVl_tot_aj_creditos(double vl_tot_aj_creditos) {
		this.vl_tot_aj_creditos = vl_tot_aj_creditos;
	}

	public double getVl_estornos_deb() {
		return vl_estornos_deb;
	}

	public void setVl_estornos_deb(double vl_estornos_deb) {
		this.vl_estornos_deb = vl_estornos_deb;
	}

	public double getVl_sld_credor_ant() {
		return vl_sld_credor_ant;
	}

	public void setVl_sld_credor_ant(double vl_sld_credor_ant) {
		this.vl_sld_credor_ant = vl_sld_credor_ant;
	}

	public double getVl_sld_apurado() {
		return vl_sld_apurado;
	}

	public void setVl_sld_apurado(double vl_sld_apurado) {
		this.vl_sld_apurado = vl_sld_apurado;
	}

	public double getVl_tot_creditos() {
		return vl_tot_creditos;
	}

	public void setVl_tot_creditos(double vl_tot_creditos) {
		this.vl_tot_creditos = vl_tot_creditos;
	}

	public double getVl_tot_ded() {
		return vl_tot_ded;
	}

	public void setVl_tot_ded(double vl_tot_ded) {
		this.vl_tot_ded = vl_tot_ded;
	}

	public double getVl_icms_recolher() {
		return vl_icms_recolher;
	}

	public void setVl_icms_recolher(double vl_icms_recolher) {
		this.vl_icms_recolher = vl_icms_recolher;
	}

	public double getVl_sld_credor_transportar() {
		return vl_sld_credor_transportar;
	}

	public void setVl_sld_credor_transportar(double vl_sld_credor_transportar) {
		this.vl_sld_credor_transportar = vl_sld_credor_transportar;
	}

	public double getVl_or() {
		return vl_or;
	}

	public void setVl_or(double vl_or) {
		this.vl_or = vl_or;
	}

	public double getDeb_esp() {
		return deb_esp;
	}

	public void setDeb_esp(double deb_esp) {
		this.deb_esp = deb_esp;
	}

}
