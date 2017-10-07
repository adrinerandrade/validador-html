package leitor.html;

import java.io.File;

public class ValidadorHtmlFacade {

	public void processarArquivo(File arquivo) {
		new ValidadorHtml(arquivo).validade();
	}
	
	public static void main(String[] args) {
		new ValidadorHtmlFacade().processarArquivo(new File("C:/teste html/teste.html"));
	}
	
}
