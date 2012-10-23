package br.com.opensig.comercial.shared.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.empresa.shared.modelo.EmpMunicipio;

/**
 * Classe modelo que representa a empresa no sistema OpenPDV.
 *
 * @author Pedro H. Lira
 */
@XmlRootElement
public class SisEmpresa extends Dados implements Serializable {

    private Integer sisEmpresaId;
    private String sisEmpresaRazao;
    private String sisEmpresaFantasia;
    private String sisEmpresaCnpj;
    private String sisEmpresaIe;
    private String sisEmpresaIm;
    private String sisEmpresaLogradouro;
    private int sisEmpresaNumero;
    private String sisEmpresaComplemento;
    private String sisEmpresaBairro;
    private String sisEmpresaCep;
    private String sisEmpresaResponsavel;
    private String sisEmpresaFone;
    private String sisEmpresaEmail;
    private boolean sisEmpresaContador;
    private EmpMunicipio sisMunicipio;

    /**
     * Construtor padrao
     */
    public SisEmpresa() {
        this(0);
    }

    /**
     * Construtor padrao passando o id
     *
     * @param sisEmpresaId o id da empresa
     */
    public SisEmpresa(Integer sisEmpresaId) {
        super("", "", "");
        this.sisEmpresaId = sisEmpresaId;
    }

    @Override
    public Number getId() {
        return this.sisEmpresaId;
    }

    @Override
    public void setId(Number id) {
        this.sisEmpresaId = id.intValue();
    }

    // GETs e SETs
    public Integer getSisEmpresaId() {
        return sisEmpresaId;
    }

    public void setSisEmpresaId(Integer sisEmpresaId) {
        this.sisEmpresaId = sisEmpresaId;
    }

    public String getSisEmpresaRazao() {
        return sisEmpresaRazao;
    }

    public void setSisEmpresaRazao(String sisEmpresaRazao) {
        this.sisEmpresaRazao = sisEmpresaRazao;
    }

    public String getSisEmpresaFantasia() {
        return sisEmpresaFantasia;
    }

    public void setSisEmpresaFantasia(String sisEmpresaFantasia) {
        this.sisEmpresaFantasia = sisEmpresaFantasia;
    }

    public String getSisEmpresaCnpj() {
        return sisEmpresaCnpj;
    }

    public void setSisEmpresaCnpj(String sisEmpresaCnpj) {
        this.sisEmpresaCnpj = sisEmpresaCnpj;
    }

    public String getSisEmpresaIe() {
        return sisEmpresaIe;
    }

    public void setSisEmpresaIe(String sisEmpresaIe) {
        this.sisEmpresaIe = sisEmpresaIe;
    }

    public String getSisEmpresaIm() {
        return sisEmpresaIm;
    }

    public void setSisEmpresaIm(String sisEmpresaIm) {
        this.sisEmpresaIm = sisEmpresaIm;
    }

    public String getSisEmpresaLogradouro() {
        return sisEmpresaLogradouro;
    }

    public void setSisEmpresaLogradouro(String sisEmpresaLogradouro) {
        this.sisEmpresaLogradouro = sisEmpresaLogradouro;
    }

    public int getSisEmpresaNumero() {
        return sisEmpresaNumero;
    }

    public void setSisEmpresaNumero(int sisEmpresaNumero) {
        this.sisEmpresaNumero = sisEmpresaNumero;
    }

    public String getSisEmpresaComplemento() {
        return sisEmpresaComplemento;
    }

    public void setSisEmpresaComplemento(String sisEmpresaComplemento) {
        this.sisEmpresaComplemento = sisEmpresaComplemento;
    }

    public String getSisEmpresaBairro() {
        return sisEmpresaBairro;
    }

    public void setSisEmpresaBairro(String sisEmpresaBairro) {
        this.sisEmpresaBairro = sisEmpresaBairro;
    }

    public String getSisEmpresaCep() {
        return sisEmpresaCep;
    }

    public void setSisEmpresaCep(String sisEmpresaCep) {
        this.sisEmpresaCep = sisEmpresaCep;
    }

    public String getSisEmpresaResponsavel() {
        return sisEmpresaResponsavel;
    }

    public void setSisEmpresaResponsavel(String sisEmpresaResponsavel) {
        this.sisEmpresaResponsavel = sisEmpresaResponsavel;
    }

    public String getSisEmpresaFone() {
        return sisEmpresaFone;
    }

    public void setSisEmpresaFone(String sisEmpresaFone) {
        this.sisEmpresaFone = sisEmpresaFone;
    }

    public String getSisEmpresaEmail() {
        return sisEmpresaEmail;
    }

    public void setSisEmpresaEmail(String sisEmpresaEmail) {
        this.sisEmpresaEmail = sisEmpresaEmail;
    }

    public boolean isSisEmpresaContador() {
		return sisEmpresaContador;
	}

	public void setSisEmpresaContador(boolean sisEmpresaContador) {
		this.sisEmpresaContador = sisEmpresaContador;
	}

	public EmpMunicipio getSisMunicipio() {
        return sisMunicipio;
    }

    public void setSisMunicipio(EmpMunicipio sisMunicipio) {
        this.sisMunicipio = sisMunicipio;
    }

	@Override
	public String[] toArray() {
		return null;
	}
}
