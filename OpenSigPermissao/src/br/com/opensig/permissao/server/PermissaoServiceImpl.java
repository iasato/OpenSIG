package br.com.opensig.permissao.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.EJuncao;
import br.com.opensig.core.client.controlador.filtro.FiltroBinario;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.FiltroObjeto;
import br.com.opensig.core.client.controlador.filtro.FiltroTexto;
import br.com.opensig.core.client.controlador.filtro.GrupoFiltro;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.servico.MailException;
import br.com.opensig.core.client.servico.OpenSigException;
import br.com.opensig.core.server.CoreServiceImpl;
import br.com.opensig.core.server.MailServiceImpl;
import br.com.opensig.core.server.SessionManager;
import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.permissao.SisAcao;
import br.com.opensig.core.shared.modelo.permissao.SisFuncao;
import br.com.opensig.core.shared.modelo.permissao.SisModulo;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.permissao.client.servico.PermissaoException;
import br.com.opensig.permissao.client.servico.PermissaoService;
import br.com.opensig.permissao.shared.modelo.SisConfiguracao;
import br.com.opensig.permissao.shared.modelo.SisGrupo;
import br.com.opensig.permissao.shared.modelo.SisPermissao;
import br.com.opensig.permissao.shared.modelo.SisUsuario;

/**
 * Classe que implementa a chamada no servidor da função de entrar no sistema,
 * acessando os dados para autenticar o usuário junto ao servidor.
 * 
 * @author Pedro H. Lira
 * @since 14/04/2009
 * @version 1.0
 */

public class PermissaoServiceImpl extends CoreServiceImpl implements PermissaoService {

	private static final long serialVersionUID = -7949461066268853021L;
	private static List<SisAcao> acoesPadroes = null;

	public Autenticacao entrar(String usuario, String senha, String captcha, int empresa, boolean permissao) throws PermissaoException {
		// recupera a sessão atual
		HttpSession sessao = getThreadLocalRequest().getSession();
		return entrar(sessao, usuario, senha, captcha, empresa, permissao);
	}

	/**
	 * @see PermissaoService#entrar(String, String, String, int, boolean)
	 */
	public Autenticacao entrar(HttpSession sessao, String usuario, String senha, String captcha, int empresa, boolean permissao) throws PermissaoException {
		Autenticacao autenticacao;

		// verifica se o usuário já está logado na sessão
		if (sessao.getAttribute("Autenticacao") == null || permissao) {
			// valida o captcha
			if (!captcha.isEmpty()) {
				Captcha cap = (Captcha) sessao.getAttribute(Captcha.NAME);
				if (!cap.isCorrect(captcha)) {
					UtilServer.LOG.debug("cod de imagem invalido");
					throw new PermissaoException("Codigo da imagem invalido!");
				}
			}

			// cria os dois filtros contendo os valores de login
			FiltroTexto ft1 = new FiltroTexto("sisUsuarioLogin", ECompara.IGUAL, usuario);
			FiltroTexto ft2 = new FiltroTexto("sisUsuarioSenha", ECompara.IGUAL, senha);
			FiltroBinario fb = new FiltroBinario("sisUsuarioAtivo", ECompara.IGUAL, 1);
			FiltroNumero fn = new FiltroNumero("empEmpresaId", ECompara.IGUAL, empresa);
			fn.setCampoPrefixo("t1.");
			// gera o grupo com junção E com os dois filtros
			GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { ft1, ft2, fb, fn });

			try {
				// valida o usuario
				SisUsuario sisUsuario = (SisUsuario) selecionar(new SisUsuario(), gf, false);
				if (sisUsuario == null) {
					throw new PermissaoException("Usuario ou Senha ou Empresa invalidos!");
				}

				// valida se ja esta logado
				if (SessionManager.APP.containsValue(sisUsuario.getId().toString())) {
					throw new PermissaoException("Usuario ja tem uma sessao aberta!");
				} else {
					SessionManager.APP.put(sessao.getId(), sisUsuario.getId().toString());
				}

				for (SisGrupo grupo : sisUsuario.getSisGrupos()) {
					if (grupo.getEmpEmpresa().getEmpEmpresaId() == empresa && grupo.getSisGrupoDesconto() > sisUsuario.getSisUsuarioDesconto()) {
						sisUsuario.setSisUsuarioDesconto(grupo.getSisGrupoDesconto());
					}
					if (grupo.getEmpEmpresa().getEmpEmpresaId() != empresa) {
						grupo.setSisPermissoes(null);
					}
				}

				autenticacao = new Autenticacao();
				autenticacao.setUsuario(sisUsuario.toArray());
				autenticacao.setModulos(gerarPermissoes(sisUsuario));
				for (EmpEmpresa emp : sisUsuario.getEmpEmpresas()) {
					if (emp.getEmpEmpresaId() == empresa) {
						autenticacao.setEmpresa(emp.toArray());
						break;
					}
				}

				if (!permissao) {
					// seta o usuario logado na sessão
					sessao.setAttribute("Autenticacao", autenticacao);
				}
			} catch (PermissaoException pe) {
				throw pe;
			} catch (Exception ex) {
				UtilServer.LOG.error("Erro ao pegar as permissoes", ex);
				throw new PermissaoException(ex.getMessage());
			}
		} else {
			// caso já esteja logado só recupera da sessão
			autenticacao = (Autenticacao) sessao.getAttribute("Autenticacao");
			usuario = autenticacao.getUsuario()[1];
			empresa = Integer.valueOf(autenticacao.getEmpresa()[0]);
		}

		try {
			autenticacao.setConf(getConfig(empresa, usuario));
		} catch (OpenSigException e) {
			UtilServer.LOG.error("Erro no login", e);
			throw new PermissaoException(e.getMessage());
		}

		return autenticacao;
	}

	@Override
	public void sair() {
		// recupera a sessão atual
		HttpSession sessao = getThreadLocalRequest().getSession();
		sessao.setAttribute("Autenticacao", null);
		SessionManager.APP.remove(sessao.getId());
	}

	@Override
	public void bloquear(boolean bloqueio) {
		// recupera a sessão atual
		HttpSession sessao = getThreadLocalRequest().getSession();

		// verifica se o usuário já está logado na sessão
		if (sessao.getAttribute("Autenticacao") != null) {
			Autenticacao autenticacao = (Autenticacao) sessao.getAttribute("Autenticacao");
			autenticacao.setBloqueado(bloqueio);
			sessao.setAttribute("Autenticacao", autenticacao);
		}
	}

	@Override
	public void recuperarSenha(String email) throws PermissaoException, MailException {
		FiltroTexto ft = new FiltroTexto("sisUsuarioEmail", ECompara.IGUAL, email);
		try {
			// acha o usuario
			SisUsuario usuario = (SisUsuario) selecionar(new SisUsuario(), ft, false);
			if (usuario == null) {
				throw new PermissaoException("Usuário não encontrado!");
			}
			// gera a mensagem e envia o email
			String msg = getMensagem(usuario.getSisUsuarioLogin(), usuario.getSisUsuarioSenha(), email);
			MailServiceImpl mail = new MailServiceImpl();
			mail.enviarEmail(null, email, "Altera&ccedil;&atilde;o de Senha!", msg);
		} catch (Exception e) {
			throw new PermissaoException(e.getMessage());
		}
	}

	private String getMensagem(String usuario, String senha, String email) throws OpenSigException {
		// quando
		Date hoje = new Date();
		String data = UtilServer.formataData(hoje, "dd/MM/yyyy");
		String hora = UtilServer.formataHora(hoje, "HH:mm:ss");
		// onde
		String ip = getThreadLocalRequest().getRemoteAddr();
		// link
		String link = getThreadLocalRequest().getRequestURL().toString().replace("/PermissaoService", "");
		link += "/novasenha.jsp?email=" + email + "&id=" + senha;

		String msg = UtilServer.getTextoArquivo(UtilServer.getRealPath("/WEB-INF/modelos/novasenha.html"));
		msg = msg.replace("#link#", link);
		msg = msg.replace("#data#", data);
		msg = msg.replace("#hora#", hora);
		msg = msg.replace("#ip#", ip);
		return msg;
	}

	/*
	 * Metodo que recupera as ações padrões do do SigCore.
	 */
	private void pegarAcoes() throws OpenSigException {
		if (acoesPadroes == null) {
			// pegando a função padrão
			FiltroNumero fn = new FiltroNumero("sisFuncaoId", ECompara.IGUAL, 0);
			SisFuncao funcao = (SisFuncao) selecionar(new SisFuncao(), fn, false);
			acoesPadroes = funcao.getSisAcoes();
		}
	}

	/*
	 * Recupera as permissões dadas ao usuário que efetuou o login.
	 */
	private List<SisModulo> gerarPermissoes(SisUsuario sisUsuario) throws OpenSigException {
		// recupera as permissões do usuario
		pegarAcoes();
		SortedSet<SisPermissao> permissoes = new TreeSet<SisPermissao>(sisUsuario.getSisPermissoes());

		// recupera os grupos do usuario e em seguida as permissões dos grupos
		List<SisGrupo> uGrupos = sisUsuario.getSisGrupos();
		for (SisGrupo sisGrupo : uGrupos) {
			if (sisGrupo.getAtivo() && sisGrupo.getSisPermissoes() != null) {
				permissoes.addAll(sisGrupo.getSisPermissoes());
			}
		}

		// filtra as permissoes de modulos
		Lista<SisModulo> lista = selecionar(new SisModulo(), 0, 0, null, false);
		lista.setLista(filtraModulos(lista.getLista(), permissoes));

		return lista.getLista();
	}

	/*
	 * Filtra os módulos permitidos do usuário.
	 */
	private List<SisModulo> filtraModulos(List<SisModulo> modulos, Collection<SisPermissao> permissoes) {
		List<SisModulo> modulosAux = new ArrayList<SisModulo>();

		for (SisModulo modulo : modulos) {
			for (SisPermissao permissao : permissoes) {
				if (modulo.getAtivo() && (modulo.getSisModuloId() == permissao.getSisModuloId() || permissao.getSisModuloId() == -1)) {
					modulo.setSisFuncoes(filtraFuncoes(modulo.getSisModuloId(), modulo.getSisFuncoes(), permissoes));
					modulo.anularDependencia();
					modulosAux.add(modulo);
					break;
				}
			}
		}

		Collections.sort(modulosAux);
		return modulosAux;
	}

	/*
	 * Filtra as funções permitidas de cada módulo.
	 */
	private List<SisFuncao> filtraFuncoes(int moduloId, List<SisFuncao> funcoes, Collection<SisPermissao> permissoes) {
		List<SisFuncao> funcoesAux = new ArrayList<SisFuncao>();

		for (SisFuncao funcao : funcoes) {
			if (funcao.getAtivo()) {
				for (SisPermissao permissao : permissoes) {
					if (moduloId == permissao.getSisModuloId() || permissao.getSisModuloId() == -1) {
						if (funcao.getAtivo()
								&& (funcao.getSisFuncaoId() == permissao.getSisFuncaoId() || permissao.getSisFuncaoId() == -1 || funcao.getSisFuncaoClasse().equalsIgnoreCase("Separador"))) {
							funcao.setSisAcoes(filtraAcoes(moduloId, funcao.getSisFuncaoId(), funcao.getSisAcoes(), permissoes));
							funcao.anularDependencia();
							funcoesAux.add(funcao);
							break;
						}
					}
				}
			}
		}

		Collections.sort(funcoesAux);
		return funcoesAux;
	}

	/*
	 * Filtras as ações permitidas de cada função.
	 */
	private List<SisAcao> filtraAcoes(int moduloId, int funcaoId, List<SisAcao> acoes, Collection<SisPermissao> permissoes) {
		List<SisAcao> acoesAux = new ArrayList<SisAcao>();
		acoes.addAll(acoesPadroes);

		for (SisAcao acao : acoes) {
			if (acao.getAtivo()) {
				for (SisPermissao permissao : permissoes) {
					if ((moduloId == permissao.getSisModuloId() || permissao.getSisModuloId() == -1) && (funcaoId == permissao.getSisFuncaoId() || permissao.getSisFuncaoId() == -1)) {
						if (acao.getAtivo() && (acao.getSisAcaoId() == permissao.getSisAcaoId() || permissao.getSisAcaoId() == -1 || acao.getSisAcaoClasse().equalsIgnoreCase("Separador"))) {
							acao.anularDependencia();
							acao.setExecutar(permissao.getSisExecutar());
							acoesAux.add(acao);
							break;
						}
					}
				}
			}
		}

		Collections.sort(acoesAux);
		return acoesAux;
	}

	private Map<String, String> getConfig(int empresa, String usuario) throws OpenSigException {
		// adicionando os dados do idioma
		String idioma = "";
		Locale locale;

		try {
			idioma = getServletContext().getInitParameter("login.idioma");
		} catch (Exception e) {
			idioma = "pt_BR";
		} finally {
			if (idioma.indexOf("_") > 0) {
				String[] loc = idioma.split("_");
				locale = new Locale(loc[0], loc[1]);
			} else {
				locale = new Locale(idioma);
			}
			idioma = idioma.equals("pt_BR") ? "" : "_" + idioma;
		}

		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(UtilServer.getRealPath("/lang/I18N" + idioma + ".properties"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			prop.load(br);
			fis.close();
		} catch (Exception ex) {
			throw new PersistenceException("Erro ao ler a linguagem -> " + idioma, ex);
		} finally {
			// adicionando os valores
			for (Entry<Object, Object> entry : prop.entrySet()) {
				UtilServer.CONF.put(entry.getKey().toString(), entry.getValue().toString());
			}
		}

		// adicionando as configuracoes
		FiltroBinario fb = new FiltroBinario("sisConfiguracaoAtivo", ECompara.IGUAL, 1);
		FiltroObjeto fo = new FiltroObjeto("empEmpresa", ECompara.IGUAL, new EmpEmpresa(empresa));
		GrupoFiltro gf = new GrupoFiltro(EJuncao.E, new IFiltro[] { fb, fo });
		Lista<SisConfiguracao> lista = selecionar(new SisConfiguracao(), 0, 0, gf, false);

		for (SisConfiguracao conf : lista.getLista()) {
			UtilServer.CONF.put(conf.getSisConfiguracaoChave().toLowerCase(), conf.getSisConfiguracaoValor());
		}

		// adicionando extras
		UtilServer.CONF.put("usuario", usuario);
		UtilServer.CONF.put("empresa", empresa + "");
		UtilServer.LOCAL = locale;

		return UtilServer.CONF;
	}
}
