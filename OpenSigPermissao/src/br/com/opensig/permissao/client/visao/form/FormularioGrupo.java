package br.com.opensig.permissao.client.visao.form;

import java.util.ArrayList;
import java.util.List;

import br.com.opensig.core.client.OpenSigCore;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.client.visao.abstrato.AFormulario;
import br.com.opensig.core.shared.modelo.sistema.SisFuncao;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.permissao.shared.modelo.SisGrupo;
import br.com.opensig.permissao.shared.modelo.SisPermissao;
import br.com.opensig.permissao.shared.modelo.SisUsuario;

import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.Checkbox;
import com.gwtext.client.widgets.form.Hidden;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FormLayout;

public class FormularioGrupo extends AFormulario<SisGrupo> {

	private Hidden hdnCod;
	private Hidden hdnEmpresa;
	private TextField txtNome;
	private TextField txtDescricao;
	private NumberField txtDesconto;
	private Checkbox chkAtivo;
	private Checkbox chkSistema;

	public FormularioGrupo(SisFuncao funcao) {
		super(new SisGrupo(), funcao);
		inicializar();
	}

	public void inicializar() {
		super.inicializar();

		Panel coluna1 = new Panel();
		coluna1.setBorder(false);
		coluna1.setLayout(new FormLayout());

		hdnCod = new Hidden("sisGrupoId", "0");
		coluna1.add(hdnCod);
		hdnEmpresa = new Hidden("empEmpresa.empEmpresaId", "0");
		add(hdnEmpresa);

		txtNome = new TextField(OpenSigCore.i18n.txtNome(), "sisGrupoNome", 200);
		txtNome.setAllowBlank(false);
		txtNome.setMaxLength(50);
		coluna1.add(txtNome);

		txtDesconto = new NumberField(OpenSigCore.i18n.txtDesconto() + "_%", "sisGrupoDesconto", 100);
		txtDesconto.setAllowBlank(false);
		txtDesconto.setAllowDecimals(false);
		txtDesconto.setAllowNegative(false);
		txtDesconto.setMinValue(0);
		txtDesconto.setMaxValue(100);
		coluna1.add(txtDesconto);

		Panel coluna2 = new Panel();
		coluna2.setWidth(230);
		coluna2.setBorder(false);
		coluna2.setLayout(new FormLayout());

		txtDescricao = new TextField(OpenSigCore.i18n.txtDescricao(), "sisGrupoDescricao", 200);
		txtDescricao.setAllowBlank(false);
		txtDescricao.setMaxLength(255);
		coluna2.add(txtDescricao);

		chkAtivo = new Checkbox(OpenSigCore.i18n.txtAtivo(), "sisGrupoAtivo");
		chkAtivo.setValue(true);

		chkSistema = new Checkbox(OpenSigCore.i18n.txtSistema(), "sisGrupoSistema");
		chkSistema.setValue(false);

		MultiFieldPanel linha1 = new MultiFieldPanel();
		linha1.setBorder(false);
		linha1.addToRow(chkAtivo, 60);
		if (Ponte.getLogin().getId() == 1) {
			linha1.addToRow(chkSistema, 70);
		}
		coluna2.add(linha1);

		Panel formColuna = new Panel();
		formColuna.setBorder(false);
		formColuna.setLayout(new ColumnLayout());
		formColuna.add(coluna1, new ColumnLayoutData(.5));
		formColuna.add(coluna2, new ColumnLayoutData(.5));
		add(formColuna);
	}

	public boolean setDados() {
		classe.setSisGrupoId(Integer.valueOf(hdnCod.getValueAsString()));
		classe.setSisGrupoNome(txtNome.getValueAsString());
		classe.setSisGrupoDescricao(txtDescricao.getValueAsString());
		if (txtDesconto.getValue() != null) {
			classe.setSisGrupoDesconto(txtDesconto.getValue().intValue());
		}
		classe.setSisGrupoAtivo(chkAtivo.getValue());
		classe.setSisGrupoSistema(chkSistema.getValue());

		if (hdnEmpresa.getValueAsString().equals("0")) {
			classe.setEmpEmpresa(new EmpEmpresa(Ponte.getLogin().getEmpresaId()));
		} else {
			classe.setEmpEmpresa(new EmpEmpresa(Integer.valueOf(hdnEmpresa.getValueAsString())));
		}

		return true;
	}

	public void limparDados() {
		getForm().reset();
	}

	public void mostrarDados() {
		Record rec = lista.getPanel().getSelectionModel().getSelected();
		if (rec != null) {
			getForm().loadRecord(rec);

			// usuarios
			String users = rec.getAsString("sisUsuario");
			if (users != null && !users.equals("")) {
				List<SisUsuario> usuarios = new ArrayList<SisUsuario>();
				for (String usuario : users.split("::")) {
					usuarios.add(new SisUsuario(Integer.valueOf(usuario)));
				}
				classe.setSisUsuarios(usuarios);
			}
			// permissoes
			String per = rec.getAsString("sisPermissao");
			classe.setSisPermissoes(SisPermissao.getPermissoes(per));
		}
		txtNome.focus(true);

		if (duplicar) {
			hdnCod.setValue("0");
			hdnEmpresa.setValue("0");
			duplicar = false;
		}
	}

	public void gerarListas() {
	}

	public Hidden getHdnCod() {
		return hdnCod;
	}

	public void setHdnCod(Hidden hdnCod) {
		this.hdnCod = hdnCod;
	}

	public TextField getTxtNome() {
		return txtNome;
	}

	public Hidden getHdnEmpresa() {
		return hdnEmpresa;
	}

	public void setHdnEmpresa(Hidden hdnEmpresa) {
		this.hdnEmpresa = hdnEmpresa;
	}

	public void setTxtNome(TextField txtNome) {
		this.txtNome = txtNome;
	}

	public TextField getTxtDescricao() {
		return txtDescricao;
	}

	public void setTxtDescricao(TextField txtDescricao) {
		this.txtDescricao = txtDescricao;
	}

	public NumberField getTxtDesconto() {
		return txtDesconto;
	}

	public void setTxtDesconto(NumberField txtDesconto) {
		this.txtDesconto = txtDesconto;
	}

	public Checkbox getChkAtivo() {
		return chkAtivo;
	}

	public void setChkAtivo(Checkbox chkAtivo) {
		this.chkAtivo = chkAtivo;
	}

	public Checkbox getChkSistema() {
		return chkSistema;
	}

	public void setChkSistema(Checkbox chkSistema) {
		this.chkSistema = chkSistema;
	}

}
