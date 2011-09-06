package br.com.opensig.core.server.exportar;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

import br.com.opensig.core.server.UtilServer;
import br.com.opensig.core.shared.modelo.ExportacaoListagem;
import br.com.opensig.core.shared.modelo.ExportacaoRegistro;

/**
 * Classe que define a exportacao de arquivo no formato de XLS.
 * 
 * @author Pedro H. Lira
 * @version 1.0
 */
public class ExportacaoXls extends AExportacao {

	private HSSFWorkbook wb;
	private CreationHelper ch;
	private CellStyle cssCabecalho;
	private CellStyle cssRodape;
	private CellStyle cssTexto;
	private CellStyle cssNumero;
	private CellStyle cssInteiro;
	private CellStyle cssData;

	/**
	 * Construtor padrao.
	 */
	public ExportacaoXls() {
		wb = new HSSFWorkbook();
		ch = wb.getCreationHelper();

		// estilo
		Font font1 = wb.createFont();
		font1.setFontName("Arial");
		font1.setBoldweight(Font.BOLDWEIGHT_BOLD);

		cssCabecalho = wb.createCellStyle();
		cssCabecalho.setAlignment(CellStyle.ALIGN_CENTER);
		cssCabecalho.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cssCabecalho.setFont(font1);

		cssRodape = wb.createCellStyle();
		cssRodape.setAlignment(CellStyle.ALIGN_CENTER);
		cssRodape.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cssRodape.setFont(font1);
		cssRodape.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));

		Font font2 = wb.createFont();
		font2.setFontName("Arial");
		cssTexto = wb.createCellStyle();
		cssTexto.setFont(font2);

		cssNumero = wb.createCellStyle();
		cssNumero.setDataFormat(ch.createDataFormat().getFormat("#,##0.00"));

		cssInteiro = wb.createCellStyle();
		cssInteiro.setDataFormat(ch.createDataFormat().getFormat("0"));

		cssData = wb.createCellStyle();
		cssData.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yyyy"));
	}

	@Override
	public byte[] getArquivo(ExportacaoListagem lista, String[] empresa, String[][] enderecos, String[][] contatos) {
		// inicio do arquivo
		HSSFSheet sheet = wb.createSheet(lista.getNome());
		// cabecalho
		getCabecalhoListagem(sheet, lista);
		// corpo
		getCorpoListagem(sheet, lista);
		// rodape
		getRodapeListagem(sheet, lista);
		// retorno
		return wb.getBytes();
	}

	@Override
	public byte[] getArquivo(ExportacaoRegistro registro, Collection<ExportacaoListagem> listas, String[] empresa, String[][] enderecos, String[][] contatos) {
		// inicio do arquivo
		HSSFSheet sheet = wb.createSheet(registro.getNome());
		// registro
		getCorpoRegistro(sheet, registro);
		// listas do registro
		if (listas != null) {
			for (ExportacaoListagem lista : listas) {
				// inicio do planilha
				HSSFSheet folha = wb.createSheet(lista.getNome());
				// cabecalho
				getCabecalhoListagem(folha, lista);
				// corpo
				getCorpoListagem(folha, lista);
				// rodape
				getRodapeListagem(folha, lista);
			}
		}
		// retorno
		return wb.getBytes();
	}

	/**
	 * Metodo que gera o cabecalho da listagem.
	 * 
	 * @param sheet
	 *            o objeto de planilha.
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 */
	public void getCabecalhoListagem(HSSFSheet sheet, ExportacaoListagem lista) {
		Row lin = sheet.createRow(0);
		lin.setHeightInPoints(30);

		int pos = 0;
		for (int i = 0; i < lista.getRotulos().length; i++) {
			if (lista.getRotulos()[i] != null) {
				Cell col = lin.createCell(pos);
				col.setCellStyle(cssCabecalho);
				col.setCellValue(lista.getRotulos()[i]);
				pos++;
			}
		}
	}

	/**
	 * Metodo que gera o corpo da listagem.
	 * 
	 * @param sheet
	 *            o objeto de planilha.
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 */
	public void getCorpoListagem(HSSFSheet sheet, ExportacaoListagem lista) {
		int fim = lista.getDados().length - lista.getInicio();
		if (lista.getLimite() > 0 && lista.getLimite() < fim) {
			fim = lista.getLimite();
		}

		for (int j = 0; j < fim; j++) {
			Row lin = sheet.createRow(j + 1);
			int pos = 0;

			for (int i = 0; i < lista.getRotulos().length; i++) {
				if (lista.getRotulos()[i] != null) {
					Cell col = lin.createCell(pos);
					setValor(lista.getDados()[j][i], col);
					sheet.autoSizeColumn(pos);
					pos++;
				}
			}
		}
	}

	/**
	 * Metodo que gera o rodape da listagem.
	 * 
	 * @param sheet
	 *            o objeto de planilha.
	 * @param lista
	 *            o objeto de exportacao de listagem.
	 */
	public void getRodapeListagem(HSSFSheet sheet, ExportacaoListagem lista) {
		int reg = lista.getLimite() > 0 ? lista.getLimite() : lista.getDados().length;
		Row lin = sheet.createRow(reg + 1);
		lin.setHeightInPoints(30);
		int pos = 0;

		for (int i = 0; i < lista.getRotulos().length; i++) {
			if (lista.getRotulos()[i] != null) {
				if (lista.getAgrupamentos()[i] != null) {
					Cell col = lin.createCell(pos);
					col.setCellStyle(cssRodape);
					char letra = (char) (65 + pos);
					col.setCellFormula(lista.getAgrupamentos()[i].toString() + "(" + letra + "2:" + letra + (reg + 1) + ")");
				}
				pos++;
			}
		}
	}

	/**
	 * Metodo que gera o corpo do registro.
	 * 
	 * @param sheet
	 *            o objeto de planilha.
	 * @param reg
	 *            o objeto de exportacao do registro.
	 */
	public void getCorpoRegistro(HSSFSheet sheet, ExportacaoRegistro reg) {
		int col = 4;
		int linhas = 0;
		Row lin = sheet.createRow(linhas);

		for (int i = 0; i < reg.getRotulos().length; i++) {
			if (i != 0 && i % col == 0) {
				linhas += 2;
				lin = sheet.createRow(linhas);
			}

			Cell rotulo = lin.createCell((i % 4) * 2);
			rotulo.setCellStyle(cssCabecalho);
			rotulo.setCellValue(reg.getRotulos()[i]);
			sheet.autoSizeColumn(rotulo.getColumnIndex());

			Cell coluna = lin.createCell(rotulo.getColumnIndex() + 1);
			setValor(reg.getDados()[i], coluna);
			sheet.autoSizeColumn(rotulo.getColumnIndex() + 1);
		}
	}

	/**
	 * @see AExportacao#getValor(String)
	 */
	public void setValor(String valor, Cell col) {
		valor = super.getValor(valor);
		// valida se e data
		Pattern data = Pattern.compile("^[0-9]{2}/[0-9]{2}/[0-9]{4}$");
		Matcher mat = data.matcher(valor);
		if (mat.find()) {
			try {
				col.setCellStyle(cssData);
				col.setCellValue(new SimpleDateFormat("dd/MM/yyyy", UtilServer.LOCAL).parse(valor));
			} catch (Exception e) {
				col.setCellValue(valor);
			}
		} else {
			// valida se é decimal
			Pattern decimal = Pattern.compile("^[0-9]+(\\.[0-9]{3})*\\,[0-9]{2}$");
			mat = decimal.matcher(valor);
			if (mat.find()) {
				valor = valor.replace(".", "").replace(",", ".");
				col.setCellStyle(cssNumero);
				col.setCellValue(Double.parseDouble(valor));
			} else {
				// valida se é numero
				Pattern numero = Pattern.compile("^[0-9]+$");
				mat = numero.matcher(valor);
				if (mat.find()) {
					col.setCellStyle(cssInteiro);
					col.setCellValue(Double.parseDouble(valor));
				} else { // texto e outros
					col.setCellStyle(cssTexto);
					col.setCellValue(valor);
				}
			}
		}
	}
}
