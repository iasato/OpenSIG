package br.com.opensig.fiscal.shared.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;

@Entity
@Table(name = "fis_sped_bloco_empresa")
public class FisSpedBlocoEmpresa extends Dados implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fis_sped_bloco_empresa_id")
	private int fisSpedBlocoEmpresaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_empresa_id")
	private EmpEmpresa empEmpresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fis_sped_bloco_id")
	private FisSpedBloco fisSpedBloco;
	
	public FisSpedBlocoEmpresa() {
		this(0);
	}
	
	public FisSpedBlocoEmpresa(int fisSpedBlocoEmpresaId) {
		super("pu_fiscal", "FisSpedBlocoEmpresa", "fisSpedBlocoEmpresaId");
		this.fisSpedBlocoEmpresaId = fisSpedBlocoEmpresaId;
	}

	public int getFisSpedBlocoEmpresaId() {
		return this.fisSpedBlocoEmpresaId;
	}

	public void setFisSpedBlocoEmpresaId(int fisSpedBlocoEmpresaId) {
		this.fisSpedBlocoEmpresaId = fisSpedBlocoEmpresaId;
	}

	public EmpEmpresa getEmpEmpresa() {
		return empEmpresa;
	}

	public void setEmpEmpresa(EmpEmpresa empEmpresa) {
		this.empEmpresa = empEmpresa;
	}

	public FisSpedBloco getFisSpedBloco() {
		return fisSpedBloco;
	}

	public void setFisSpedBloco(FisSpedBloco fisSpedBloco) {
		this.fisSpedBloco = fisSpedBloco;
	}

	@Override
	public Number getId() {
		return this.fisSpedBlocoEmpresaId;
	}

	@Override
	public void setId(Number id) {
		this.fisSpedBlocoEmpresaId = id.intValue();
	}

	@Override
	public String[] toArray() {
		return new String[] {fisSpedBlocoEmpresaId + "", empEmpresa.getEmpEmpresaId() + "", fisSpedBloco.getFisSpedBlocoId() + ""} ;
	}

	@Override
	public void anularDependencia() {
		empEmpresa = null;
		fisSpedBloco = null;
	}
}