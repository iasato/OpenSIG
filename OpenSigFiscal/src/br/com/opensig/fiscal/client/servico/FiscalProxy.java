package br.com.opensig.fiscal.client.servico;

import java.util.Map;

import br.com.opensig.core.client.controlador.filtro.IFiltro;
import br.com.opensig.core.client.servico.CoreProxy;
import br.com.opensig.core.shared.modelo.Dados;
import br.com.opensig.empresa.shared.modelo.EmpEmpresa;
import br.com.opensig.fiscal.shared.modelo.FisCertificado;
import br.com.opensig.fiscal.shared.modelo.FisNotaSaida;
import br.com.opensig.fiscal.shared.modelo.FisNotaStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class FiscalProxy<E extends Dados> extends CoreProxy<E> implements FiscalServiceAsync<E> {

	private static final FiscalServiceAsync async = (FiscalServiceAsync) GWT.create(FiscalService.class);
	private static final ServiceDefTarget sdf = (ServiceDefTarget) async;

	public FiscalProxy() {
		sdf.setServiceEntryPoint(GWT.getHostPageBaseURL() + "FiscalService");
	}

	public void exportar(String arquivo, String nome, String tipo, AsyncCallback<String> asyncCallback) {
		async.exportar(arquivo, nome, tipo, asyncCallback);
	}

	public void backup(E classe, IFiltro filtro, AsyncCallback<String> asyncCallback) {
		async.backup(classe, filtro, asyncCallback);
	}

	public void status(int ambiente, int uf, int empresa, AsyncCallback<String> asyncallback) {
		async.status(ambiente, uf, empresa, asyncallback);
	}

	public void validar(int ambiente, IFiltro filtro, int empresa, AsyncCallback<String> asyncallback) {
		async.validar(ambiente, filtro, empresa, asyncallback);
	}
	
	public void situacao(int ambiente, String chave, int empresa, AsyncCallback<String> asyncallback) {
		async.situacao(ambiente, chave, empresa, asyncallback);
	}

	public void cadastro(int ambiente, int ibge, String uf, String tipo, String doc, int empresa, AsyncCallback<String> asyncallback) {
		async.cadastro(ambiente, ibge, uf, tipo, doc, empresa, asyncallback);
	}

	public void enviarNFe(String xml, int empresa, AsyncCallback<String> asyncallback) {
		async.enviarNFe(xml, empresa, asyncallback);
	}

	public void receberNFe(String xml, int empresa, String recibo, AsyncCallback<String> asyncallback) {
		async.receberNFe(xml, empresa, recibo, asyncallback);
	}
	
	public void receberNFe(FisNotaSaida saida, AsyncCallback<Map<String, String>> asyncCallback) {
		async.receberNFe(saida, asyncCallback);
	}

	public void cancelar(FisNotaSaida saida, String motivo, AsyncCallback<Map<String, String>> asyncCallback) {
		async.cancelar(saida, motivo, asyncCallback);
	};

	public void cancelar(String xml, int empresa, AsyncCallback<String> asyncallback) {
		async.cancelar(xml, empresa, asyncallback);
	}

	public void inutilizar(String xml, int empresa, AsyncCallback<String> asyncallback) {
		async.inutilizar(xml, empresa, asyncallback);
	}

	public void inutilizar(FisNotaSaida saida, String motivo, int ini, int fim, AsyncCallback<Map<String, String>> asyncCallback) {
		async.inutilizar(saida, motivo, ini, fim, asyncCallback);
	};

	public void salvarSaida(String xml, FisNotaStatus status, EmpEmpresa empresa, AsyncCallback<Map<String, String>> asyncCallback) {
		async.salvarSaida(xml, status, empresa, asyncCallback);
	}
	
	public void salvarCertificado(FisCertificado certificado, AsyncCallback asyncCallback) {
		async.salvarCertificado(certificado, asyncCallback);
	}
}
