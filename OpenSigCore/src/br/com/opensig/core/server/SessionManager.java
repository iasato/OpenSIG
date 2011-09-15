package br.com.opensig.core.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Classe que controla as sessoes logadas no sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class SessionManager implements HttpSessionListener {

	/**
	 * Variavel a nivel de aplicacao que armazena as sessoes logadas.
	 */
	public static final Map<String, String> APP = new HashMap<String, String>();

	/**
	 * Ao cria a sessao adiciona ao controle.
	 */
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		if (!APP.containsKey(arg0.getSession().getId())) {
			APP.put(arg0.getSession().getId(), "");
		}
	}

	/**
	 * Ao destruir a sessao remove do controle.
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		APP.remove(arg0.getSession().getId());
	}
}
