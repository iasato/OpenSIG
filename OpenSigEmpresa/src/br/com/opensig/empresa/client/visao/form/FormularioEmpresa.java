package br.com.opensig.empresa.client.visao.form;

import java.util.ArrayList;
import java.util.List;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.empresa.client.visao.lista.ListagemPlano;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.empresa.shared.modelo.EmpPlano;

import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtextux.client.widgets.window.ToastWindow;

public class FormularioEmpresa extends FormularioEntidade<EmpEmpresa> {

	private ListagemPlano gridPlano;

	public FormularioEmpresa(SisFuncao funcao) {
		super(new EmpEmpresa(), funcao, "empEntidade");
		inicializar();
	}

	public void inicializar() {
		hdnId = new Hidden("empEmpresaId", "0");
		add(hdnId);

		super.configurar();
		// adicionando nova aba planos
		gridPlano = new ListagemPlano(true);
		tabDados.add(gridPlano);
		super.inicializar();
	}

	@Override
	public void gerarListas() {
		super.gerarListas();
		// plano
		Integer[] tamPlano = new Integer[gridPlano.getModelos().getColumnCount()];
		String[] rotPlano = new String[gridPlano.getModelos().getColumnCount()];

		for (int i = 0; i < gridPlano.getModelos().getColumnCount(); i++) {
			if (!gridPlano.getModelos().isHidden(i)) {
				tamPlano[i] = gridPlano.getModelos().getColumnWidth(i);
				rotPlano[i] = gridPlano.getModelos().getColumnHeader(i);
			}
		}

		Record rec = lista.getPanel().getSelectionModel().getSelected();
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(rec.getAsInteger("empEmpresaId")));

		ExportacaoListagem<EmpPlano> planos = new ExportacaoListagem<EmpPlano>();
		planos.setUnidade(new EmpPlano());
		planos.setFiltro(fo);
		planos.setTamanhos(tamPlano);
		planos.setRotulos(rotPlano);
		planos.setNome(gridPlano.getTitle());
		expLista.add(planos);
	}

	@Override
	public void limparDados() {
		super.limparDados();
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(0));
		gridPlano.getProxy().setFiltroPadrao(fo);
		gridPlano.getStore().removeAll();
	}

	@Override
	public boolean setDados() {
		classe.setEmpEmpresaId(Integer.valueOf(hdnId.getValueAsString()));
		classe.setEmpEntidade(entidade);

		boolean retorno = super.setDados();
		List<EmpPlano> planos = new ArrayList<EmpPlano>();
		if (!gridPlano.validar(planos)) {
			retorno = false;
			tabDados.setActiveItem(2);
			new ToastWindow(OpenSigCore.i18n.txtListagem(), OpenSigCore.i18n.errLista()).show();
		}
		classe.setEmPlano(planos);
		return retorno;
	}

	@Override
	public void mostrarDados() {
		super.mostrarDados();
		Record rec = lista.getPanel().getSelectionModel().getSelected();

		if (rec != null) {
			FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(rec.getAsInteger("empEmpresaId")));
			gridPlano.getProxy().setFiltroPadrao(fo);
			gridPlano.getStore().reload();
		}
	}

	public ListagemPlano getGridPlano() {
		return gridPlano;
	}

	public void setGridPlano(ListagemPlano gridPlano) {
		this.gridPlano = gridPlano;
	}

}
