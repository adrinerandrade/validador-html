package leitor.html;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import lista.estatica.generica.ListaEstaticaGenerica;

import org.apache.commons.lang3.StringUtils;

import pilha.general.Pilha;
import pilha.general.PilhaEstaVaziaException;
import pilha.general.PilhaVetor;

public class HtmlValidator {

	private static final String EXTRA_FINAL_TAG = "Tag final extra encontrada.";
	private static final String INESPECTED_CHAR_ON_ATTRIBUTE_VALUE_ATTRIBUTION = "Caracter insperado na atribuição de valor de uma tag.";
	private static final String UNCLOSED_TAGS = "Algumas tags finais não foram fechadas: %s";
	private static final String INVALID_FINAL_TAG = "Tag final inválida. Esperada: %s; Encontrada: %s.";
	private static final String EMPTY_TAG = "Para compor uma tag, é necessário informar um valor";
	private static final String NO_PROPERTY_FOR_VALUE = "Não existe uma propriedade para ser atribuído um valor.";
	private static final String VALID_CHAR = "[a-zA-Z!]";
	private static final String INVALID_CARACTER_ON_TAG_CREATION = "Caracter inválido na criação de uma tag (%s).";
	private static final String INCOMPLETE_TAG = "Tag encontrada incompleta.";
	private static final String INCOMPLETE_ATTRIBUTE = "Atributo não completado.";
	private static final String FILE_NOT_FOUND = "Não foi possível encontrar o arquivo no caminho %s.";
	
	private static final Predicate<String> IS_CONTENT = Pattern.compile(VALID_CHAR).asPredicate();

	private final BufferedReader reader;
	private Pilha<String> pilha = new PilhaVetor<>(255);
	private ListaEstaticaGenerica<HtmlCounter> counters = new ListaEstaticaGenerica<>();

	public HtmlValidator(File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(String.format(FILE_NOT_FOUND, file.getAbsolutePath()));
		}
	}

	public void validade() {
		try {
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					read(line, ++count);
				}
			}
			if (!pilha.estaVazia()) {
				ListaEstaticaGenerica<String> unclosedTags = new ListaEstaticaGenerica<>();
				while (!pilha.estaVazia()) {
					unclosedTags.inserir(pilha.pop());
				}
				throw new InvalidHtmlFormatException(String.format(UNCLOSED_TAGS, unclosedTags), count);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void exibir() {
		for (int i = 0; i < counters.getTamanho(); i++) {
			HtmlCounter counter = counters.obterElemento(i);
			System.out.println(String.format("Elemento '%s' encontrado %d vezes.", counter.getType(), counter.getCount()));
		}
 	}

	private void read(String line, int count) {
		ByteArrayInputStream stream = new ByteArrayInputStream(line.getBytes());
		int read;
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (c == '<') {
				boolean success = readTag(stream, count);
				if (!success) {
					throw new InvalidHtmlFormatException(INCOMPLETE_TAG, count);
				}
			}
		}
	}

	private boolean readTag(ByteArrayInputStream stream, int count) {
		int read;
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (c == '/') {
				return readClosingTag(stream, count);
			} else if (!IS_CONTENT.test(String.valueOf(c))) {
				throw new InvalidHtmlFormatException(String.format(INVALID_CARACTER_ON_TAG_CREATION, c), count);
			}
			readOpeningTag(c, stream, count);
			return true;
		}
		return false;
	}

	private boolean readOpeningTag(char firstRead, ByteArrayInputStream stream, int count) {
		StringBuilder tagType = new StringBuilder();

		int read = firstRead;
		do {
			char c = (char) read;
			if (Character.isWhitespace(c)) {
				createTag(tagType.toString(), count);
				validateAttributes(stream, count);
				return true;
			} else if (c == '>') {
				createTag(tagType.toString(), count);
				return true;
			} else if (!IS_CONTENT.test(String.valueOf(c))) {
				throw new InvalidHtmlFormatException(String.format(INVALID_CARACTER_ON_TAG_CREATION, c), count);
			}
			tagType.append(c);
		} while ((read = stream.read()) != -1);

		return false;
	}

	private boolean readClosingTag(ByteArrayInputStream stream, int count) {
		StringBuilder tagType = new StringBuilder();
		int read;
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (c == '>') {
				if (StringUtils.isBlank(tagType)) {
					throw new InvalidHtmlFormatException(EMPTY_TAG, count);
				}
				String lastElement;
				try {
					lastElement = pilha.pop();
				} catch(PilhaEstaVaziaException e) {
					throw new InvalidHtmlFormatException(EXTRA_FINAL_TAG, count);
				}
				if (!tagType.toString().equals(lastElement)) {
					throw new InvalidHtmlFormatException(String.format(INVALID_FINAL_TAG, lastElement, tagType), count);
				}
				return true;
			} else if (!IS_CONTENT.test(String.valueOf(c))) {
				throw new InvalidHtmlFormatException(String.format(INVALID_CARACTER_ON_TAG_CREATION, c), count);
			}
			tagType.append(c);
		}
		return false;
	}

	private void createTag(String type, int line) {
		if (StringUtils.isBlank(type)) {
			throw new InvalidHtmlFormatException(EMPTY_TAG, line);
		}
		HtmlTag tag = new HtmlTag();
		tag.setType(type);

		if (!SingletonTag.isSingletonTag(type)) {
			pilha.push(type);
		}

		
		int buscar = counters.buscar(new HtmlCounter(type));
		HtmlCounter counter;
		if (buscar == -1) {
			counter = new HtmlCounter();
			counter.setType(type);
			counters.inserir(counter);
		} else {
			counter = counters.obterElemento(buscar);
		}
		counter.increment();
	}

	private boolean validateAttributes(ByteArrayInputStream stream, int count) {
		String reading = "";
		Pilha<HtmlAttribute> attributes = new PilhaVetor<>(100);
		
		int read;
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (Character.isWhitespace(c)) {
				if (StringUtils.isNotBlank(reading)) {
					attributes.push(new HtmlAttribute(reading));
					reading = "";
				}
				continue;
			} else if (c == '=') {
				if (StringUtils.isNotBlank(reading)) {
					attributes.push(new HtmlAttribute(reading));
					reading = "";
				} else {
					throw new InvalidHtmlFormatException(NO_PROPERTY_FOR_VALUE, count); 
				}
				HtmlAttribute attribute = attributes.pop();
				if (attribute != null && attribute.getValue() == null) {
					attribute.setValue(findPropertyValue(stream, count));
					continue;
				} else {
					throw new InvalidHtmlFormatException(NO_PROPERTY_FOR_VALUE, count);
				}
			} else if (c == '>') {
				if (StringUtils.isNotBlank(reading)) {
					attributes.push(new HtmlAttribute(reading));
					reading = "";
				}
				return true;
			} else if (!IS_CONTENT.test(String.valueOf(c))) {
				throw new InvalidHtmlFormatException(String.format(INVALID_CARACTER_ON_TAG_CREATION, c), count);
			}
			reading += c;
		}
		return false;
	}

	private String findPropertyValue(ByteArrayInputStream stream, int count) {
		int read;
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (Character.isWhitespace(c)) {
				continue;
			} else if (c == '"') {
				return extractAttributeValue(stream, count);
			}
			throw new InvalidHtmlFormatException(INESPECTED_CHAR_ON_ATTRIBUTE_VALUE_ATTRIBUTION, count);
		}
		throw new InvalidHtmlFormatException(INCOMPLETE_ATTRIBUTE, count);
	}

	private String extractAttributeValue(ByteArrayInputStream stream, int count) {
		int read;
		String value = "";
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (c == '"') {
				return value;
			}
			value += c;
		}
		throw new InvalidHtmlFormatException("Valor não fechado", count);
	}

}
