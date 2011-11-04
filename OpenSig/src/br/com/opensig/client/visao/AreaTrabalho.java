package br.com.opensig.client.visao;

import br.com.opensig.client.visao.layout.BarraMenu;
import br.com.opensig.client.visao.layout.Centro;
import br.com.opensig.client.visao.layout.Esquerda;
import br.com.opensig.client.visao.layout.Favoritos;
import br.com.opensig.client.visao.layout.Info;
import br.com.opensig.client.visao.layout.Modulos;
import br.com.opensig.core.client.UtilClient;
import br.com.opensig.core.client.padroes.Observable;
import br.com.opensig.core.client.padroes.Observer;
import br.com.opensig.core.client.padroes.Visitable;
import br.com.opensig.core.client.padroes.Visitor;
import br.com.opensig.core.client.visao.Ponte;
import br.com.opensig.core.shared.modelo.ILogin;
import br.com.opensig.permissao.client.controlador.comando.ComandoBloquear;
import br.com.opensig.permissao.client.servico.PermissaoProxy;
import br.com.opensig.permissao.client.visao.EntrarSistema;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.util.CSS;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.BorderLayout;

/**
 * Classe que representa a Area principal de trabalho, contendo todas as funcoes e painel de controle principal.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class AreaTrabalho extends Panel implements Observer, Visitable {

	/**
	 * Construtor padrao
	 */
	public AreaTrabalho() {
		inicializar();
	}

	// inicia a criacao dos objetos visuais.
	private void inicializar() {
		String modulos = RootPanel.get("modulos").getElement().getInnerText();
		// layout em borda
		setLayout(new BorderLayout());
		Ponte.getInstancia().addObserver(this);
		Ponte.setInfo(Info.getInstancia());
		// Norte
		if (modulos.equalsIgnoreCase("menu")) {
			setTopToolbar(BarraMenu.getInstancia());
			Ponte.setBarraMenu(BarraMenu.getInstancia());
		} else {
			Esquerda.getInstancia().add(Modulos.getInstancia());
		}
		// Oeste
		Esquerda.getInstancia().add(Favoritos.getInstancia());
		add(Esquerda.getInstancia(), Esquerda.getData());
		Ponte.setEsquerda(Esquerda.getInstancia());
		// Centro
		add(Centro.getInstancia(), Centro.getData());
		Ponte.setCentro(Centro.getInstancia());

		// seta o tema definido
		String tema = RootPanel.get("tema").getElement().getInnerText();
		CSS.swapStyleSheet("estilo", tema);
		new EntrarSistema();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof ILogin) {
			ILogin login = (ILogin) arg;

			if (login != null) {
				if (login.isBloqueado()) {
					new ComandoBloquear().execute(null);
				}

				// repete a cada X minuto
				int ping = 1000 * Integer.valueOf(UtilClient.CONF.get("sessao.ping"));
				Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
					public boolean execute() {
						ativo();
						return true;
					}
				}, ping);
			}
			doLayout();
		}
	}

	@Override
	public void accept(Visitor visitor) {
	}

	// mantenhem a sessao ativa
	private void ativo() {
		PermissaoProxy proxy = new PermissaoProxy();
		proxy.isLogado(new AsyncCallback<Boolean>() {
			public void onSuccess(Boolean result) {
				if (!result) {
					// caso a sessao tenha morrido
					Window.Location.reload();
				}
			}

			public void onFailure(Throwable caught) {
				onSuccess(false);
			}
		});
	}
}
