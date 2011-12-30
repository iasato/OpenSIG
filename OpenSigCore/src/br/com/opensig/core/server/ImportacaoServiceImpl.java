package br.com.opensig.core.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import br.com.opensig.core.client.servico.ImportacaoException;
import br.com.opensig.core.client.servico.ImportacaoService;
import br.com.opensig.core.server.importar.IImportacao;
import br.com.opensig.core.shared.modelo.Autenticacao;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.core.shared.modelo.sistema.SisExpImp;

/**
 * Classe que implementa as chamadas no servidor para as importacoes do sistema.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ImportacaoServiceImpl<E extends Dados> extends CoreServiceImpl<E> implements ImportacaoService<E> {

	public ImportacaoServiceImpl() {
	}

	public ImportacaoServiceImpl(Autenticacao auth) {
		super(auth);
	}

	@Override
	public Map<String, List<E>> importar(SisExpImp modo, List<String> arquivos) throws ImportacaoException {
		HttpSession sessao = getThreadLocalRequest().getSession();
		Autenticacao auth = SessionManager.LOGIN.get(sessao);

		try {
			Map<String, byte[]> dados = new HashMap<String, byte[]>();
			// valida o tipo do arquivo
			for (String nomeArquivo : arquivos) {
				try {
					dados.put(nomeArquivo, (byte[]) sessao.getAttribute(nomeArquivo));
				} finally {
					sessao.setAttribute(nomeArquivo, null);
				}
			}

			// identifica a classe de importacao
			Class exp = Class.forName(modo.getSisExpImpClasse());
			IImportacao<E> importa = (IImportacao<E>) exp.newInstance();
			return importa.setArquivo(auth, dados, modo);
		} catch (Exception e) {
			UtilServer.LOG.error("Erro ao importar", e);
			throw new ImportacaoException(e.getMessage());
		}
	}

}
