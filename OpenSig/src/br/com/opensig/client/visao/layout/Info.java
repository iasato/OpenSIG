package br.com.opensig.client.visao.layout;

import java.util.LinkedList;

import br.com.opensig.core.client.OpenSigCore;

import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;

/**
 * Classe do componente visual que compoen a esquerda da area de trabalho.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class Info extends Panel {

	private static Info info;
	private LinkedList<Component> itens;

	/**
	 * Construtor padrao
	 */
	private Info() {
		this.itens = new LinkedList<Component>();
		inicializar();
	}

	/**
	 * Metodo que devolve a referencia a unica instancia.
	 * 
	 * @return referencia a unica instancia do objeto.
	 */
	public static Info getInstancia() {
		if (info == null) {
			info = new Info();
		}
		return info;
	}

	// inicializa os componentes visuais
	private void inicializar() {
		setTitle(OpenSigCore.i18n.txtInformacao());
		setIconCls("icon-informacao");
		setWidth(200);
		setPaddings(5);
		setStyle("font-size: 12px");
		setAutoScroll(true);
		setButtonAlign(Position.CENTER);
	}

	/**
	 * Metodo que limpa o conteudo visual.
	 */
	public void clear() {
		itens.clear();
		super.clear();
	}

	/**
	 * Metodo que adiciona um elemento ao componente visual.
	 */
	public void add(Component component) {
		itens.addFirst(component);
		atualizar();
	}

	// atualiza os componentes visuais.
	private void atualizar() {
		for (Component com : itens) {
			super.add(com);
		}
		doLayout();
	}
}
