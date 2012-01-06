package br.com.opensig.fiscal.server.sped.bloco0;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpMunicipio;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0000;

public class Registro0000 extends ARegistro<Dados0000> {

	public Registro0000() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0000.xml");
	}

	@Override
	protected Dados0000 getDados() throws Exception {
		Dados0000 d = new Dados0000();
		d.setReg("0000");
		d.setCod_ver(sped.getFisSpedFiscalAno() % 2008 + 1);
		d.setCod_fin(sped.getFinalidade());

		Date inicio = new SimpleDateFormat("ddMMyyyy").parse("01" + (sped.getFisSpedFiscalMes() > 9 ? sped.getFisSpedFiscalMes() : "0" + sped.getFisSpedFiscalMes()) + sped.getFisSpedFiscalAno());
		d.setDt_ini(inicio);

		Calendar cal = Calendar.getInstance();
		cal.setTime(inicio);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		d.setDt_fin(cal.getTime());

		FiltroNumero fn = new FiltroNumero("empEmpresaId", ECompara.IGUAL, sped.getEmpEmpresa().getEmpEmpresaId());
		EmpEmpresa emp = (EmpEmpresa) service.selecionar(new EmpEmpresa(), fn, false);
		
		d.setNome(emp.getEmpEntidade().getEmpEntidadeNome1());
		if (emp.getEmpEntidade().getEmpEntidadeDocumento1().length() == 18) {
			d.setCnpj(emp.getEmpEntidade().getEmpEntidadeDocumento1().replaceAll("\\D", ""));
		} else {
			d.setCpf(emp.getEmpEntidade().getEmpEntidadeDocumento1().replaceAll("\\D", ""));
		}
		
		EmpMunicipio mun = emp.getEmpEntidade().getEmpEnderecos().get(0).getEmpMunicipio();
		d.setUf(mun.getEmpEstado().getEmpEstadoSigla());
		d.setIe(emp.getEmpEntidade().getEmpEntidadeDocumento2());
		d.setCod_mun(mun.getEmpMunicipioIbge());
		
		//TODO im e suframa adicionar ao sistema isso
		
		d.setInd_perfil(auth.getConf().get("sped.0000.ind_perfil"));
		d.setInd_ativ(Integer.valueOf(auth.getConf().get("sped.0000.ind_ativ")));
		
		normalizar(d);
		qtdLinhas = 1;
		return d;
	}

}
