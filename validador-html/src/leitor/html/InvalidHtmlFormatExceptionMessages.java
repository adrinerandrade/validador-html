package leitor.html;

public enum InvalidHtmlFormatExceptionMessages {

	ATTRIBUTE_VALUE_ATTRIBUTION_NOT_FINALIZED("Atribuição de valor a um atributo não finalizada."),
	CLOSING_SINGLETON_TAG("'%s' é uma singleton tag, e não precisa ser fechada."),
	EXTRA_FINAL_TAG("Tag final extra encontrada."),
	INESPECTED_CHAR_ON_ATTRIBUTE_VALUE_ATTRIBUTION("Caracter insperado na atribuição de valor de uma tag."),
	UNCLOSED_TAGS("Algumas tags finais não foram fechadas: %s"),
	INVALID_FINAL_TAG("Tag final inválida. Esperada: %s; Encontrada: %s."),
	EMPTY_TAG("Para compor uma tag, é necessário informar um valor."),
	NO_PROPERTY_FOR_VALUE("Não existe uma propriedade para ser atribuído um valor."),
	INVALID_CARACTER_ON_TAG_CREATION("Caracter inválido na criação de uma tag (%s)."),
	INCOMPLETE_TAG("Tag encontrada incompleta."),
	INCOMPLETE_ATTRIBUTE("Atributo não completado."),
	FILE_NOT_FOUND("Não foi possível encontrar o arquivo no caminho %s.");
	
	private String message;
	
	private InvalidHtmlFormatExceptionMessages(String message) {
		this.message = message;
	}
	
	public String message(Object... params) {
		return String.format(this.message, params);
	}
	
}
