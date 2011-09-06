package br.com.opensig.core.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import sun.misc.BASE64Decoder;
import br.com.opensig.core.client.controlador.filtro.ECompara;
import br.com.opensig.core.client.controlador.filtro.FiltroCampo;
import br.com.opensig.core.client.controlador.filtro.FiltroNumero;
import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.controlador.parametro.IParametro;
import br.com.opensig.core.client.controlador.parametro.ParametroException;
import br.com.opensig.core.client.servico.CoreException;
import br.com.opensig.core.client.servico.CoreService;
import br.com.opensig.core.client.servico.ExportacaoException;
import br.com.opensig.core.server.exportar.FabricaExportacao;
import br.com.opensig.core.server.exportar.IExportacao;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.Colecao;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.EArquivo;
import br.com.opensig.core.shared.modelo.EBusca;
import br.com.opensig.core.shared.modelo.EComando;
import br.com.opensig.core.shared.modelo.EData;
import br.com.opensig.core.shared.modelo.EDirecao;
import br.com.opensig.core.shared.modelo.ELetra;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;
import br.com.opensig.core.shared.modelo.Lista;
import br.com.opensig.core.shared.modelo.Sql;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Classe que implementa na parte do servidor a resposta a chamada de
 * procedimento do cliente, executando os comandos de persistencia no banco de
 * dados.
 * 
 * @param <E>
 * @author Pedro H. Lira
 * @version 1.0
 */
public class CoreServiceImpl<E extends Dados> extends RemoteServiceServlet implements CoreService<E> {

	private static final long serialVersionUID = 1L;

	@Override
	public Lista<E> selecionar(Dados classe, int inicio, int limite, IFiltro filtro, boolean removeDependencia) throws CoreException, ParametroException {

		// mosta a instrução padrão
		String sql = String.format("SELECT DISTINCT t FROM %s t ", classe.getTabela());
		sql += getColecao(classe.getColecao());
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			// recupera uma instância do gerenciador de entidades
			emf = Conexao.getInstancia(classe.getPu());
			em = emf.createEntityManager();

			// caso tenha filtros, recupera no padrão sql e adiciona a instrução
			if (filtro != null) {
				sql += String.format(" WHERE %s", filtro.getSql());
			}

			// caso seja passado um campo para ordenar, adiciona o comando a
			// instrução
			if (classe.getCampoOrdem() != null && !classe.getCampoOrdem().isEmpty()) {
				String ordem = classe.getCampoOrdem();
				Pattern pat = Pattern.compile("^t\\d*\\.");
				Matcher mat = pat.matcher(ordem);
				if (!mat.find()) {
					ordem = "t." + ordem;
				}
				EDirecao direcao = classe.getOrdemDirecao() == null ? EDirecao.ASC : classe.getOrdemDirecao();
				sql += String.format(" ORDER BY %s %s", ordem, direcao.toString());
			}

			// pega a transação padrão e inicia
			em.getTransaction().begin();
			// gera um query
			UtilServer.LOG.debug("Sql gerado: " + sql);
			Query rs = em.createQuery(sql);

			// se foi definido um limete de resgistros, caso contrario recupera
			// todos.
			if (limite > 0) {
				inicio = inicio < 0 ? 0 : inicio;
				// seta a posição inicial de recuperação dos registros
				// (paginação)
				rs.setFirstResult(inicio);
				// seta a posição a quantidade total de registros (paginação)
				rs.setMaxResults(limite);
			}

			// se foi passados filtros coloca agora os valores nos devidos
			// campos
			if (filtro != null) {
				Collection<IFiltro> params = filtro.getParametro();
				for (IFiltro fil : params) {
					if (!(fil instanceof FiltroCampo) && fil.getCompara() != ECompara.NULO && fil.getCompara() != ECompara.VAZIO) {
						rs.setParameter(fil.getCampoId(), fil.getValor());
					}
				}
			}

			// realiza toda a operação caso tudo tenha sucesso
			em.getTransaction().commit();
			// recupera a lista de dados
			List<E> lista = (List<E>) rs.getResultList();
			// chama o método que retorna o total de registros sem paginação
			int total = buscar(classe, classe.getCampoId(), EBusca.CONTAGEM, filtro).intValue();
			UtilServer.LOG.debug("Total de registros: " + total);

			// insere os dados num modelo de objeto e retorna
			Lista<E> listagem = new Lista<E>();
			listagem.setTotal(total);
			limite = limite <= 0 || total < limite ? total : limite;

			if (removeDependencia) {
				// transformando o resultado numa matrix de Strings
				String[][] dados = new String[total][];
				for (int i = 0; i < lista.size(); i++) {
					if (UtilServer.CONF.get("empresa") != null) {
						lista.get(i).setEmpresa(Integer.valueOf(UtilServer.CONF.get("empresa")));
					}
					dados[i] = lista.get(i).toArray();
				}

				listagem.setDados(dados);
				UtilServer.LOG.debug("Retornando textos.");
			} else {
				listagem.setLista(lista);
				UtilServer.LOG.debug("Retornando objetos.");
			}

			return listagem;
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao selecionar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public E selecionar(Dados classe, IFiltro filtro, boolean removeDependencia) throws CoreException, ParametroException {
		// formata o sql
		String sql = String.format("SELECT DISTINCT t FROM %s t ", classe.getTabela());
		sql += getColecao(classe.getColecao());

		// pega o resultado
		E obj = (E) getResultado(classe.getPu(), sql, filtro);
		if (removeDependencia && obj != null) {
			obj.anularDependencia();
		}

		return obj;
	}

	@Override
	public Number buscar(Dados classe, String campo, EBusca busca, IFiltro filtro) throws CoreException, ParametroException {
		Pattern pat = Pattern.compile("^t\\d*\\.");
		Matcher mat = pat.matcher(campo);
		if (!mat.find()) {
			campo = "t." + campo;
		}

		// verifica se é contagem
		String conta = EBusca.CONTAGEM == busca ? "DISTINCT " : "";

		// formata o sql
		String sql = String.format("SELECT %s(%s%s) FROM %s t ", busca.toString(), conta, campo, classe.getTabela());
		sql += getColecao(classe.getColecao());

		// pega o resultado
		Number obj = (Number) getResultado(classe.getPu(), sql, filtro);
		return obj;
	}

	@Override
	public Collection<String[]> buscar(Dados classe, String campoX, String campoSubX, String grupoX, String campoY, EBusca busca, EDirecao direcao, IFiltro filtro) throws CoreException,
			ParametroException {

		if (campoSubX == null || campoSubX.equals("")) {
			campoSubX = campoX;
		}

		// sql principal
		String sql = String.format("SELECT %s, %s, COUNT(%s), SUM(%s), AVG(%s) FROM %s t ", campoX, campoSubX, campoY, campoY, campoY, classe.getTabela());
		sql += getColecao(classe.getColecao());
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			// recupera uma instância do gerenciador de entidades
			emf = Conexao.getInstancia(classe.getPu());
			em = emf.createEntityManager();

			// caso tenha filtros, recupera no padrão sql e adiciona a instrução
			if (filtro != null) {
				sql += String.format(" WHERE %s", filtro.getSql());
			}

			// agrupamento
			sql += String.format(" GROUP BY %s, %s", campoX, campoSubX);

			// ordem
			sql += String.format(" ORDER BY %s, %s", campoX, campoSubX);

			// pega a transação padrão e inicia
			em.getTransaction().begin();
			// gera um query
			UtilServer.LOG.debug("Sql gerado: " + sql);
			Query rs = em.createQuery(sql);

			// se foi passados filtros coloca os valores nos campos
			if (filtro != null) {
				Collection<IFiltro> params = filtro.getParametro();
				for (IFiltro fil : params) {
					if (!(fil instanceof FiltroCampo) && fil.getCompara() != ECompara.NULO && fil.getCompara() != ECompara.VAZIO) {
						rs.setParameter(fil.getCampoId(), fil.getValor());
					}
				}
			}

			// realiza toda a operação caso tudo tenha sucesso
			em.getTransaction().commit();
			// recupera a lista de dados
			List<Vector> resultado = rs.getResultList();
			Collection<String[]> lista = new ArrayList<String[]>();

			boolean campoData = false;
			for (Object v : resultado) {
				String[] linha = new String[5];
				Object[] val = (Object[]) v;
				try {
					linha[0] = getSubData((Date) val[0], grupoX);
					linha[1] = linha[0];
					campoData = true;
				} catch (Exception ex) {
					linha[0] = val[0] == null ? "" : val[0].toString();
					try {
						linha[1] = getSubData((Date) val[1], grupoX);
					} catch (Exception ex1) {
						linha[1] = val[1] == null ? "" : val[1].toString();
					}
				}
				linha[2] = val[2] == null ? "" : val[2].toString();
				linha[3] = val[3] == null ? "" : val[3].toString();
				linha[4] = val[4] == null ? "" : val[4].toString();
				lista.add(linha);
			}

			// agrupando pelo subcampo se necessario
			if (!campoX.equals(campoSubX) || campoData) {
				lista = agrupar(lista, grupoX);
			} else {
				// ordenando pelo campo e direcao corretos
				Collections.sort((List<String[]>) lista, ordenar(busca, direcao));
			}

			return lista;
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao buscar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

	/**
	 * Metodo padrão para recuperar um registro.
	 * 
	 * @param pu
	 *            o nome da unidade de persistencia.
	 * @param sql
	 *            a instrução em EQL formatada.
	 * @param filtro
	 *            o filtro a ser usado.
	 * @return um unidade de classe de acordo com a generic.
	 * @throws CoreException
	 *             ocorre em erros no acesso aos dados.
	 * @throws ParametroException
	 *             ocorre em caso de filtro incorreto.
	 */
	public Object getResultado(String pu, String sql, IFiltro filtro) throws CoreException, ParametroException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			// recupera uma instância do gerenciador de entidades
			emf = Conexao.getInstancia(pu);
			em = emf.createEntityManager();

			// caso tenha filtros, recupera no padrão sql e adiciona a instrução
			if (filtro != null) {
				sql += " WHERE " + filtro.getSql();
			}

			// pega a transação padrão e inicia
			em.getTransaction().begin();
			// gera um query
			UtilServer.LOG.debug("Sql gerado: " + sql);
			Query rs = em.createQuery(sql);

			// se foi passados filtros coloca agora os valores nos devidos
			// campos
			if (filtro != null) {
				Collection<IFiltro> params = filtro.getParametro();

				for (IFiltro fil : params) {
					if (!(fil instanceof FiltroCampo) && fil.getCompara() != ECompara.NULO && fil.getCompara() != ECompara.VAZIO) {
						rs.setParameter(fil.getCampoId(), fil.getValor());
					}
				}
			}

			// realiza toda a operação caso tudo tenha sucesso
			em.getTransaction().commit();
			try {
				return rs.getSingleResult();
			} catch (Exception e) {
				return null;
			}
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao pegar resultado", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public Collection<E> salvar(Collection<E> unidades) throws CoreException {
		return salvar(unidades, true);
	}

	/**
	 * @see CoreService#salvar(Collection)
	 */
	public Collection<E> salvar(Collection<E> unidades, boolean removeDependencia) throws CoreException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			if (unidades != null && !unidades.isEmpty()) {
				Dados[] d = unidades.toArray(new Dados[] {});
				emf = Conexao.getInstancia(d[0].getPu());
				em = emf.createEntityManager();
				em.getTransaction().begin();
				salvar(em, unidades);
				em.getTransaction().commit();
			}
			return unidades;
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao salvar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			if (removeDependencia) {
				for (E unidade : unidades) {
					unidade.anularDependencia();
				}
			}
			em.close();
			emf.close();
		}
	}

	/**
	 * Metodo que salva uma colecao de entidades usando a mesma transacao.
	 * 
	 * @param em
	 *            o gerenciado de entidade.
	 * @param unidades
	 *            a colecao de entidades.
	 * @return a colecao de entidades com valores salvos.
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	public Collection<E> salvar(EntityManager em, Collection<E> unidades) throws CoreException {
		for (E unidade : unidades) {
			salvar(em, unidade);
		}
		return unidades;
	}

	@Override
	public E salvar(E unidade) throws CoreException {
		return salvar(unidade, true);
	}

	/**
	 * @see CoreService#salvar(Dados)
	 */
	public E salvar(E unidade, boolean removeDependencia) throws CoreException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			emf = Conexao.getInstancia(unidade.getPu());
			em = emf.createEntityManager();
			em.getTransaction().begin();
			salvar(em, unidade);
			em.getTransaction().commit();
			return unidade;
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao salvar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			if (removeDependencia) {
				unidade.anularDependencia();
			}
			em.close();
			emf.close();
		}
	}

	/**
	 * Metodo que salva a entidade usando a mesma transacao passada.
	 * 
	 * @param em
	 *            o gerenciado de entidade.
	 * @param unidade
	 *            a entidade
	 * @return a entidade com valores salvos.
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	public E salvar(EntityManager em, E unidade) throws CoreException {
		if (unidade.getId().intValue() == 0) {
			// verifica se tem limite para esta funcao
			int limite;
			try {
				limite = Integer.valueOf(UtilServer.CONF.get(unidade.getClass().getName().toLowerCase()));
			} catch (Exception e) {
				limite = 0;
			}

			// verifica se o limite é infinito=0 ou restrito
			if (limite > 0) {
				Number total = buscar(unidade, unidade.getCampoId(), EBusca.CONTAGEM, null);
				if (total.intValue() >= limite) {
					throw new CoreException("O limite maximo de registros desta funcao foi alcancado!");
				}
			}
			padronizaLetras(unidade, unidade.getTipoLetra(), unidade.isLimpaBranco());
			em.persist(unidade);
		} else {
			padronizaLetras(unidade, unidade.getTipoLetra(), unidade.isLimpaBranco());
			em.merge(unidade);
		}

		return unidade;
	}

	@Override
	public void deletar(Collection<E> unidades) throws CoreException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			if (unidades != null && !unidades.isEmpty()) {
				Dados[] d = unidades.toArray(new Dados[] {});
				emf = Conexao.getInstancia(d[0].getPu());
				em = emf.createEntityManager();
				em.getTransaction().begin();

				deletar(em, unidades);
				em.getTransaction().commit();
			}
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao deletar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

	/**
	 * Metodo que deleta uma colecao de entidades com a mesma transacao.
	 * 
	 * @param em
	 *            o gerenciador de entidades.
	 * @param unidades
	 *            a colecao de entidades.
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	public void deletar(EntityManager em, Collection<E> unidades) throws CoreException {
		for (E unidade : unidades) {
			deletar(em, unidade);
		}
	}

	@Override
	public void deletar(E unidade) throws CoreException {
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try {
			emf = Conexao.getInstancia(unidade.getPu());
			em = emf.createEntityManager();
			em.getTransaction().begin();

			deletar(em, unidade);
			em.getTransaction().commit();
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao deletar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}
	}

	/**
	 * Metodo que deleta a entidade com a mesma transacao passada.
	 * 
	 * @param em
	 *            o gerenciador de entidades.
	 * @param unidade
	 *            a entidade
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	public void deletar(EntityManager em, E unidade) throws CoreException {
		unidade = (E) em.find(unidade.getClass(), unidade.getId());
		em.remove(unidade);
	}

	@Override
	public Integer[] executar(Sql[] sqls) throws CoreException {
		EntityManagerFactory emf = null;
		EntityManager em = null;
		Integer[] resultado = null;
		int pos = 0;

		try {
			if (sqls != null && sqls.length > 0) {
				resultado = new Integer[sqls.length];
				emf = Conexao.getInstancia(sqls[0].getClasse().getPu());
				em = emf.createEntityManager();
				em.getTransaction().begin();

				for (Sql sql : sqls) {
					resultado[pos++] = executar(em, sql);
				}

				em.getTransaction().commit();
			}
		} catch (Exception ex) {
			if (em != null && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}

			UtilServer.LOG.error("Erro ao executar", ex);
			throw new CoreException(ex.getMessage());
		} finally {
			em.close();
			emf.close();
		}

		return resultado;
	}

	/**
	 * Metodo para executar instruções diretas no BD com a mesma transacao.
	 * 
	 * @param em
	 *            o gerenciador de entidades.
	 * @param sql
	 *            a instrucao Sql em formato de objeto.
	 * @return um inteiro informando a quantidade de registros afetados.
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	public int executar(EntityManager em, Sql sql) throws CoreException {
		int resultado = 0;

		if (sql != null) {
			// recupera uma instância do gerenciador de entidades
			Dados dados = sql.getClasse();
			Pattern pat = Pattern.compile("t\\d+\\.");

			// gerando a acao
			String acao = "";
			if (sql.getComando() == EComando.ATUALIZAR) {
				// caso a acao seja atualizar um campo de colecao
				Matcher mat = pat.matcher(sql.getParametro().getSql());
				if (mat.find()) {
					return atualizar(em, sql);
				} else {
					acao = "UPDATE " + dados.getTabela() + " t SET " + sql.getParametro().getSql();
				}
			} else {
				acao = "DELETE FROM " + dados.getTabela() + " t ";
			}

			// caso tenha filtros, recupera no padrão sql e adiciona a
			// instrução
			if (sql.getFiltro() != null) {
				Matcher mat = pat.matcher(sql.getFiltro().getSql());
				if (mat.find()) {
					if (sql.getComando() == EComando.ATUALIZAR) {
						return atualizar(em, sql);
					} else {
						return excluir(em, sql);
					}
				} else {
					acao += String.format(" WHERE %s", sql.getFiltro().getSql());
				}
			}

			// gera um query
			UtilServer.LOG.debug("Sql gerado: " + acao);
			Query rs = em.createQuery(acao);

			// se foi passados filtros coloca agora os valores nos
			// devidos campos
			if (sql.getFiltro() != null) {
				Collection<IFiltro> params = sql.getFiltro().getParametro();
				for (IFiltro fil : params) {
					if (!(fil instanceof FiltroCampo) && fil.getCompara() != ECompara.NULO && fil.getCompara() != ECompara.VAZIO) {
						rs.setParameter(fil.getCampoId(), fil.getValor());
					}
				}
			}

			// se foi passados parametros coloca agora os valores nos
			// devidos campos
			if (sql.getParametro() != null) {
				Collection<IParametro> params = sql.getParametro().getParametro();
				for (IParametro par : params) {
					rs.setParameter(par.getCampoId(), par.getValor());
				}
			}

			// executa o comando
			resultado = rs.executeUpdate();
		}

		return resultado;
	}

	/**
	 * Metodo para executar instruções de atualizacao diretas no BD com a mesma
	 * transacao.
	 * 
	 * @param em
	 *            o gerenciador de entidades.
	 * @param sql
	 *            a instrucao Sql em formato de objeto.
	 * @return um inteiro informando a quantidade de registros afetados.
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	protected int atualizar(EntityManager em, Sql sql) throws CoreException {
		int resultado = 0;
		String nMet = sql.getParametro().getCampo().replaceAll("t\\d*\\.", "set");

		// faz a selecao dos objetos
		Dados dado = sql.getClasse();
		Lista<E> lista = selecionar(dado, 0, 0, sql.getFiltro(), false);

		try {
			// percorre cada um para atualizar o campo
			for (E obj : lista.getLista()) {
				// os metodos do objeto
				for (Method met : obj.getClass().getMethods()) {
					// verifica se é o get e retorna List
					if (isGetter(met) && met.getReturnType() == List.class) {
						List<E> vMet = (List<E>) met.invoke(obj, new Object[] {});
						// percorre as colecoes
						for (Colecao col : dado.getColecao()) {
							// verifica se tem valor e compativel com o objeto
							if (vMet != null && !vMet.isEmpty() && vMet.get(0).getTabela().equals(col.getTabela())) {
								// percorre os objetos
								for (E subObj : vMet) {
									// percorre os metodos do objeto final
									for (Method subMet : subObj.getClass().getMethods()) {
										// verifica se é set e tem o mesmo nome
										if (isSetter(subMet) && subMet.getName().equalsIgnoreCase(nMet)) {
											// seta o valor
											subMet.invoke(subObj, new Object[] { sql.getParametro().getValor() });
											break;
										}
									}
								}

								// salva os objetos altualizados
								salvar(em, vMet);
								resultado += vMet.size();
							}
						}
						// verifica se é set e tem o mesmo nome
					} else if (isSetter(met) && met.getName().equalsIgnoreCase(nMet)) {
						// seta o valor
						met.invoke(obj, new Object[] { sql.getParametro().getValor() });
						break;
					}
				}
			}
		} catch (Exception ex) {
			UtilServer.LOG.error("Erro ao atualizar", ex);
			resultado = 0;
		}

		return resultado;
	}

	/**
	 * Metodo para executar instruções de exclusoes diretas no BD com a mesma
	 * transacao.
	 * 
	 * @param em
	 *            o gerenciador de entidades.
	 * @param sql
	 *            a instrucao Sql em formato de objeto.
	 * @return um inteiro informando a quantidade de registros afetados.
	 * @throws CoreException
	 *             dispara uma excecao em caso de erro.
	 */
	protected int excluir(EntityManager em, Sql sql) throws CoreException {
		// faz a selecao dos objetos
		Dados dado = sql.getClasse();
		Lista<E> lista = selecionar(dado, 0, 0, sql.getFiltro(), false);
		deletar(em, lista.getLista());

		return lista.getTotal();
	}

	/**
	 * Metodo que gera a instrucao de colecoes em JQL.
	 * 
	 * @param colecao
	 *            um array de colecoes de tabelas.
	 * @return uma string no formato de busca.
	 */
	protected String getColecao(Colecao[] colecao) {
		String sql = "";
		if (colecao != null) {
			for (Colecao col : colecao) {
				sql += String.format("%s %s %s ", col.getJuncao(), col.getCampo(), col.getPrefixo());
			}
		}
		return sql;
	}

	/**
	 * Metodo que agrupa os resultados de buscas para o grafico.
	 * 
	 * @param lista
	 *            uma colecao de string com os dados detalhados.
	 * @param grupo
	 *            o nome do campo do eixoX a ser agrupado.
	 * @return a colecao agrupada.
	 */
	protected Collection<String[]> agrupar(Collection<String[]> lista, String grupo) {
		Collection<String[]> aux = new ArrayList<String[]>();

		String campoX = "";
		String campoSubX = "";
		double valCount = 0.00;
		double valSum = 0.00;
		double valAvg = 0.00;

		for (String[] reg : lista) {
			if (reg[0].equals(campoX) && reg[1].equals(campoSubX)) {
				valCount += Double.valueOf(reg[2]);
				valSum += Double.valueOf(reg[3]);
				valAvg = valSum / valCount;
			} else {
				if (!campoX.equals("")) {
					aux.add(new String[] { campoX, campoSubX, valCount + "", valSum + "", valAvg + "" });
				}

				campoX = reg[0];
				campoSubX = reg[1];
				valCount = Double.valueOf(reg[2]);
				valSum = Double.valueOf(reg[3]);
				valAvg = Double.valueOf(reg[4]);
			}
		}
		aux.add(new String[] { campoX, campoSubX, valCount + "", valSum + "", valAvg + "" });

		return aux;
	}

	/**
	 * Metodo que ordena a busca do grafico.
	 * 
	 * @param busca
	 *            o tipo de busca.
	 * @param direcao
	 *            a direcao da ordenacao.
	 * @return o comparador.
	 */
	protected Comparator ordenar(final EBusca busca, final EDirecao direcao) {
		Comparator comp = new Comparator() {
			public int compare(Object o1, Object o2) {
				int dir = direcao == EDirecao.ASC ? 1 : -1;
				String[] obj1 = (String[]) o1;
				String[] obj2 = (String[]) o2;
				double val1;
				double val2;

				switch (busca) {
				case CONTAGEM:
					val1 = Double.valueOf(obj1[2]);
					val2 = Double.valueOf(obj2[2]);
					break;
				case SOMA:
					val1 = Double.valueOf(obj1[3]);
					val2 = Double.valueOf(obj2[3]);
					break;
				default:
					val1 = Double.valueOf(obj1[4]);
					val2 = Double.valueOf(obj2[4]);
				}

				if (val1 < val2) {
					return -1 * dir;
				} else if (val1 > val2) {
					return 1 * dir;
				} else {
					return 0;
				}

			}
		};

		return comp;
	}

	/**
	 * Metodo que gera a string para usar sub buscas por data.
	 * 
	 * @param data
	 *            a data do registro.
	 * @param parte
	 *            a parte da data a ser usada.
	 * @return a string com a parte da instrucao.
	 */
	protected String getSubData(Date data, String parte) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);

		if (parte.equals(EData.DIA.toString())) {
			return cal.get(Calendar.DAY_OF_MONTH) + "";
		} else if (parte.equals(EData.MES.toString())) {
			return (cal.get(Calendar.MONTH) + 1) + "";
		} else {
			return cal.get(Calendar.YEAR) + "";
		}
	}

	/**
	 * Metodo que padrozina os tamanhos da letras ao salvar os dados.
	 * 
	 * @param unidade
	 *            o objeto a ser salvo.
	 * @param tipo
	 *            o tipo de letra padrao usado.
	 * @param limpar
	 *            se deve remover os espacos em branco do comeco e fim.
	 */
	protected void padronizaLetras(Object unidade, ELetra tipo, boolean limpar) {
		for (Method metodo : unidade.getClass().getMethods()) {
			try {
				if (isGetter(metodo)) {
					Object valorMetodo = metodo.invoke(unidade, new Object[] {});

					if (metodo.getReturnType() == String.class) {
						String nomeMetodo = metodo.getName().replaceFirst("get", "set");
						Method set = unidade.getClass().getMethod(nomeMetodo, new Class[] { String.class });
						String valor = valorMetodo == null ? "" : valorMetodo.toString();

						if (tipo == ELetra.GRANDE) {
							valor = valor.toUpperCase();
						} else if (tipo == ELetra.PEQUENA) {
							valor = valor.toLowerCase();
						}

						set.invoke(unidade, new Object[] { limpar ? valor.trim() : valor });
					} else if (metodo.getReturnType().getSuperclass() == Dados.class && valorMetodo != null) {
						padronizaLetras(valorMetodo, tipo, limpar);
					}
				}
			} catch (Exception ex) {
				UtilServer.LOG.debug("Erro ao padronizar. " + metodo.getName(), ex);
			}
		}
	}

	/**
	 * Metodo que informa se o metodo da classe é do tipo GET.
	 * 
	 * @param method
	 *            usando reflection para descrobrir os metodos.
	 * @return verdadeiro se o metodo for GET, falso caso contrario.
	 */
	protected boolean isGetter(Method method) {
		if (!method.getName().startsWith("get")) {
			return false;
		}
		if (method.getParameterTypes().length != 0) {
			return false;
		}
		if (void.class.equals(method.getReturnType())) {
			return false;
		}
		return true;
	}

	/**
	 * Metodo que informa se o metodo da classe é do tipo SET.
	 * 
	 * @param method
	 *            usando reflection para descrobrir os metodos.
	 * @return verdadeiro se o metodo for SET, falso caso contrario.
	 */
	protected boolean isSetter(Method method) {
		if (!method.getName().startsWith("set")) {
			return false;
		}
		if (method.getParameterTypes().length == 0) {
			return false;
		}
		if (!void.class.equals(method.getReturnType())) {
			return false;
		}
		return true;
	}

	@Override
	public String exportar(ExportacaoListagem expLista, EArquivo tipo) throws ExportacaoException {
		String retorno = "";

		try {
			Dados d = expLista.getUnidade();
			d.setCampoOrdem(expLista.getCampoOrdem());
			d.setOrdemDirecao(expLista.getDirecao());

			Lista<E> lista = selecionar(d, expLista.getInicio(), expLista.getLimite(), expLista.getFiltro(), true);
			expLista.setDados(lista.getDados());
			retorno = exportar(null, expLista, tipo, expLista.getNome());
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao exportar", e);
			throw new ExportacaoException(e.getMessage());
		}

		return retorno;
	}

	@Override
	public String exportar(ExportacaoRegistro expRegistro, EArquivo tipo) throws ExportacaoException {
		String retorno = "";

		try {
			if (expRegistro.getExpLista() != null) {
				Collection<ExportacaoListagem> aux = new ArrayList<ExportacaoListagem>();
				for (ExportacaoListagem exp : expRegistro.getExpLista()) {
					Dados d = exp.getUnidade();
					d.setCampoOrdem(exp.getCampoOrdem());
					d.setOrdemDirecao(exp.getDirecao());
					Lista<E> lista = selecionar(d, 0, 0, exp.getFiltro(), true);
					exp.setDados(lista.getDados());
					if (lista.getDados().length > 0) {
						aux.add(exp);
					}
				}
				expRegistro.setExpLista(aux);
			}
			retorno = exportar(expRegistro, null, tipo, expRegistro.getNome());
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao exportar", e);
			throw new ExportacaoException(e.getMessage());
		}

		return retorno;
	}

	@Override
	public String exportar(String arquivo, String nome, String tipo) throws ExportacaoException {
		String retorno = "";
		HttpSession sessao = getThreadLocalRequest().getSession();
		byte[] obj = null;

		// se nao enteden-se como base 64
		try {
			File arq = new File(arquivo + "." + tipo.toLowerCase());
			if (arq.exists()) {
				InputStream is = new FileInputStream(arq);
				obj = new byte[is.available()];
				is.read(obj);
				is.close();
			} else if (arquivo.startsWith(System.getProperty("file.separator")) || arquivo.substring(1, 2).equals(":")) {
				throw new Exception(UtilServer.CONF.get("errRegistro"));
			} else {
				obj = new BASE64Decoder().decodeBuffer(arquivo);
			}
		} catch (Exception ex) {
			UtilServer.LOG.error("Erro ao exportar", ex);
			throw new ExportacaoException(ex.getMessage());
		}

		retorno = sessao.getId() + UtilServer.getData().getTime();
		sessao.setAttribute(retorno, obj);
		sessao.setAttribute(retorno + "arquivo", nome);
		sessao.setAttribute(retorno + "tipo", tipo);
		return retorno;
	}

	/**
	 * Metodo que padrozina a exportacao das listagens e registros.
	 * 
	 * @param expRegistro
	 *            um objeto de exportacao de registro.
	 * @param expLista
	 *            um objeto de expotaco de listagem.
	 * @param tipo
	 *            o formato do arquivo a ser exportado.
	 * @param nome
	 *            o nome do arquivo.
	 * @return o id para ser usado no download.
	 * @throws ExportacaoException
	 *             caso ocorra um erro dispara a excecao.
	 */
	protected String exportar(ExportacaoRegistro expRegistro, ExportacaoListagem expLista, EArquivo tipo, String nome) throws ExportacaoException {
		HttpSession sessao = getThreadLocalRequest().getSession();
		Autenticacao autenticacao = (Autenticacao) sessao.getAttribute("Autenticacao");
		String retorno = sessao.getId() + UtilServer.getData().getTime();

		try {
			String[] empresa = autenticacao.getEmpresa();
			String[][] enderecos = getEnderecos(empresa[1]);
			String[][] contatos = getContatos(empresa[1]);

			UtilServer.CONF.put("usuario", autenticacao.getUsuario()[1]);
			IExportacao exporta = FabricaExportacao.getInstancia().getExpotacao(tipo);
			byte[] obj = null;

			if (expRegistro != null) {
				obj = exporta.getArquivo(expRegistro, expRegistro.getExpLista(), empresa, enderecos, contatos);
			} else {
				obj = exporta.getArquivo(expLista, empresa, enderecos, contatos);
			}

			if (UtilServer.CONF.get("nome") != null) {
				nome = UtilServer.CONF.get("nome");
			}

			sessao.setAttribute(retorno, obj);
			sessao.setAttribute(retorno + "arquivo", nome);
			sessao.setAttribute(retorno + "tipo", tipo);
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao exportar", e);
			throw new ExportacaoException(e.getMessage());
		}

		return retorno;
	}

	/**
	 * Metodo que recupera os enderecos da entidade.
	 * 
	 * @param idEntidade
	 *            o identificado.
	 * @return uma matriz com os dados dos enderecos.
	 */
	protected String[][] getEnderecos(String idEntidade) {
		try {
			@SuppressWarnings("serial")
			Dados d = new Dados("pu_empresa", "EmpEndereco", "empEnderecoId") {
				@Override
				public String[] toArray() {
					return null;
				}

				@Override
				public void setId(Number id) {
				}

				@Override
				public Number getId() {
					return null;
				}
			};

			FiltroNumero filtro = new FiltroNumero("empEntidade.empEntidadeId", ECompara.IGUAL, idEntidade);
			Lista enderecos = selecionar(d, 0, 0, filtro, true);
			return enderecos.getDados();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao pegar endereco", e);
			return null;
		}
	}

	/**
	 * Metodo que recupera os contatos da entidade.
	 * 
	 * @param idEntidade
	 *            o identificado.
	 * @return uma matriz com os dados dos contatos.
	 */
	protected String[][] getContatos(String idEntidade) {
		try {
			@SuppressWarnings("serial")
			Dados d = new Dados("pu_empresa", "EmpContato", "empContatoId") {
				@Override
				public String[] toArray() {
					return null;
				}

				@Override
				public void setId(Number id) {
				}

				@Override
				public Number getId() {
					return null;
				}
			};

			FiltroNumero filtro = new FiltroNumero("empEntidade.empEntidadeId", ECompara.IGUAL, idEntidade);
			Lista enderecos = selecionar(d, 0, 0, filtro, true);
			return enderecos.getDados();
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao pegar contatos", e);
			return null;
		}
	}

	/**
	 * Metodo que recupera que interage com o envio do navegador.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getParameter("id");
		String modo = req.getParameter("modo");
		String data = req.getParameter("data");

		try {
			if (id != null) {
				// pegando os dados e normalizando
				HttpSession sessao = req.getSession();
				byte[] obj = (byte[]) sessao.getAttribute(id);
				String arquivo = sessao.getAttribute(id + "arquivo").toString();
				arquivo = UtilServer.normaliza(arquivo).replace(" ", "_");
				String tipo;

				// definindo o tipo
				try {
					EArquivo arqTipo = (EArquivo) sessao.getAttribute(id + "tipo");
					tipo = arqTipo.toString();
				} catch (Exception ex) {
					tipo = sessao.getAttribute(id + "tipo").toString();
				}

				// setando os cabecalhos
				if (modo == null) {
					resp.addHeader("Content-Disposition", "attachment; filename=" + arquivo.toLowerCase() + "." + tipo.toLowerCase());
					resp.addHeader("Pragma", "no-cache");
					resp.addIntHeader("Expires", 0);
					resp.addHeader("Content-Type", "application/octet-stream");
				} else {
					String html = new String(obj).replace("<body>", "<body onload='this.focus(); this.print();'>");
					html = UtilServer.normaliza(html);
					obj = html.getBytes();
					resp.addHeader("Content-Type", modo);
					UtilServer.LOG.debug("Html formadado: " + html);
				}

				// codificando e enviando
				resp.setCharacterEncoding("utf-8");
				resp.getOutputStream().write(obj);
				resp.flushBuffer();
			} else if (data != null) {
				HttpSession session = req.getSession();
				Captcha captcha = new Captcha.Builder(150, 50).addText().addBackground(new GradiatedBackgroundProducer()).gimp().addNoise().addBorder().build();
				session.setAttribute(Captcha.NAME, captcha);
				CaptchaServletUtil.writeImage(resp, captcha.getImage());
			}
		} catch (Exception ex) {
			UtilServer.LOG.error("Erro ao chamada", ex);
		}
	}

}