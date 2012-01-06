package br.com.opensig.fiscal.server.sped.bloco0;

import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.empresa.shared.modelo.EmpContato;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpEndereco;
import br.com.opensig.fiscal.server.sped.ARegistro;
import br.com.opensig.fiscal.shared.modelo.sped.bloco0.Dados0005;

public class Registro0005 extends ARegistro<Dados0005> {

	public Registro0005() {
		super("/br/com/opensig/fiscal/shared/modelo/sped/bloco0/Bean0005.xml");
	}

	@Override
	protected Dados0005 getDados() throws Exception {
		FiltroNumero fn = new FiltroNumero("empEmpresaId", ECompara.IGUAL, sped.getEmpEmpresa().getEmpEmpresaId());
		EmpEmpresa emp = (EmpEmpresa) service.selecionar(new EmpEmpresa(), fn, false);

		Dados0005 d = new Dados0005();
		d.setReg("0005");
		d.setFantasia(emp.getEmpEntidade().getEmpEntidadeNome2());

		EmpEndereco end = emp.getEmpEntidade().getEmpEnderecos().get(0);
		d.setCep(Integer.valueOf(end.getEmpEnderecoCep().replaceAll("\\D", "")));
		d.setEnd(end.getEmpEnderecoLogradouro());
		d.setNum(end.getEmpEnderecoNumero() + "");
		d.setCompl(end.getEmpEnderecoComplemento());
		d.setBairro(end.getEmpEnderecoBairro());

		for (EmpContato cont : emp.getEmpEntidade().getEmpContatos()) {
			if (cont.getEmpContatoTipo().getEmpContatoTipoId() == Integer.valueOf(auth.getConf().get("nfe.tipoconttel"))) {
				d.setFone(cont.getEmpContatoDescricao().replaceAll("\\D", ""));
			} else if (cont.getEmpContatoTipo().getEmpContatoTipoId() == Integer.valueOf(auth.getConf().get("nfe.tipocontemail"))) {
				d.setEmail(cont.getEmpContatoDescricao());
			}
		}

		qtdLinhas = 1;
		return d;
	}
}
