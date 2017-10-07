package leitor.html;

import java.io.File;

/**
 * @author Adriner Maranho de Andrade
 * @author Luan Carlos Purin
 */
public class ValidadorHtmlFacade {

	public void processarArquivo(File arquivo) {
		HtmlValidator validadorHtml = new HtmlValidator(arquivo);
		validadorHtml.validade();
		validadorHtml.exibir();
	}
	
	public static void main(String[] args) {
		new ValidadorHtmlFacade().processarArquivo(new File("C:/teste html/teste.html"));
	}
	
}
