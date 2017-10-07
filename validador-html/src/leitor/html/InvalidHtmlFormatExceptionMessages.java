package leitor.html;

public enum InvalidHtmlFormatExceptionMessages {

	ATTRIBUTE_VALUE_ATTRIBUTION_NOT_FINALIZED("Atribui��o de valor a um atributo n�o finalizada."),
	CLOSING_SINGLETON_TAG("'%s' � uma singleton tag, e n�o precisa ser fechada."),
	EXTRA_FINAL_TAG("Tag final extra encontrada."),
	INESPECTED_CHAR_ON_ATTRIBUTE_VALUE_ATTRIBUTION("Caracter insperado na atribui��o de valor de uma tag."),
	UNCLOSED_TAGS("Algumas tags finais n�o foram fechadas: %s"),
	INVALID_FINAL_TAG("Tag final inv�lida. Esperada: %s; Encontrada: %s."),
	EMPTY_TAG("Para compor uma tag, � necess�rio informar um valor."),
	NO_PROPERTY_FOR_VALUE("N�o existe uma propriedade para ser atribu�do um valor."),
	INVALID_CARACTER_ON_TAG_CREATION("Caracter inv�lido na cria��o de uma tag (%s)."),
	INCOMPLETE_TAG("Tag encontrada incompleta."),
	INCOMPLETE_ATTRIBUTE("Atributo n�o completado."),
	FILE_NOT_FOUND("N�o foi poss�vel encontrar o arquivo no caminho %s.");
	
	private String message;
	
	private InvalidHtmlFormatExceptionMessages(String message) {
		this.message = message;
	}
	
	public String message(Object... params) {
		return String.format(this.message, params);
	}
	
}
