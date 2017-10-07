package leitor.html;

import static leitor.html.InvalidHtmlFormatExceptionMessages.ATTRIBUTE_VALUE_ATTRIBUTION_NOT_FINALIZED;
import static leitor.html.InvalidHtmlFormatExceptionMessages.CLOSING_SINGLETON_TAG;
import static leitor.html.InvalidHtmlFormatExceptionMessages.EMPTY_TAG;
import static leitor.html.InvalidHtmlFormatExceptionMessages.EXTRA_FINAL_TAG;
import static leitor.html.InvalidHtmlFormatExceptionMessages.FILE_NOT_FOUND;
import static leitor.html.InvalidHtmlFormatExceptionMessages.INCOMPLETE_ATTRIBUTE;
import static leitor.html.InvalidHtmlFormatExceptionMessages.INCOMPLETE_TAG;
import static leitor.html.InvalidHtmlFormatExceptionMessages.INESPECTED_CHAR_ON_ATTRIBUTE_VALUE_ATTRIBUTION;
import static leitor.html.InvalidHtmlFormatExceptionMessages.INVALID_CARACTER_ON_TAG_CREATION;
import static leitor.html.InvalidHtmlFormatExceptionMessages.INVALID_FINAL_TAG;
import static leitor.html.InvalidHtmlFormatExceptionMessages.NO_PROPERTY_FOR_VALUE;
import static leitor.html.InvalidHtmlFormatExceptionMessages.UNCLOSED_TAGS;

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

	private static final String VALID_CHAR = "[a-zA-Z!-]";
	
	private static final Predicate<String> IS_CONTENT = Pattern.compile(VALID_CHAR).asPredicate();

	private final BufferedReader reader;
	private Pilha<String> pilha = new PilhaVetor<>(255);
	private ListaEstaticaGenerica<HtmlCounter> counters = new ListaEstaticaGenerica<>();

	public HtmlValidator(File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(FILE_NOT_FOUND.message(file.getAbsolutePath()));
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
				throw new InvalidHtmlFormatException(UNCLOSED_TAGS.message(unclosedTags), count);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void exibir() {
		for (int i = 0; i < counters.getTamanho(); i++) {
			HtmlCounter counter = counters.obterElemento(i);
			System.out.println(String.format("Tag '%s' encontrada %d vezes.", counter.getType(), counter.getCount()));
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
					throw new InvalidHtmlFormatException(INCOMPLETE_TAG.message(), count);
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
				throw new InvalidHtmlFormatException(INVALID_CARACTER_ON_TAG_CREATION.message(c), count);
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
				throw new InvalidHtmlFormatException(INVALID_CARACTER_ON_TAG_CREATION.message(c), count);
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
					throw new InvalidHtmlFormatException(EMPTY_TAG.message(), count);
				}
				String lastElement;
				try {
					lastElement = pilha.pop();
				} catch(PilhaEstaVaziaException e) {
					throw new InvalidHtmlFormatException(EXTRA_FINAL_TAG.message(), count);
				}
				if (SingletonTag.isSingletonTag(tagType.toString())) {
					throw new InvalidHtmlFormatException(CLOSING_SINGLETON_TAG.message(tagType), count);
				}
				if (!tagType.toString().equals(lastElement)) {
					throw new InvalidHtmlFormatException(INVALID_FINAL_TAG.message(lastElement, tagType), count);
				}
				return true;
			} else if (!IS_CONTENT.test(String.valueOf(c))) {
			}
			tagType.append(c);
		}
		return false;
	}

	private void createTag(String type, int line) {
		if (StringUtils.isBlank(type)) {
			throw new InvalidHtmlFormatException(EMPTY_TAG.message(), line);
		}

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
		HtmlAttribute lastAttribute;
		
		int read;
		while ((read = stream.read()) != -1) {
			char c = (char) read;
			if (Character.isWhitespace(c)) {
				if (StringUtils.isNotBlank(reading)) {
					lastAttribute = new HtmlAttribute(reading);
					reading = "";
				}
				continue;
			} else if (c == '=') {
				if (StringUtils.isNotBlank(reading)) {
					lastAttribute = new HtmlAttribute(reading);
					reading = "";
				} else {
					throw new InvalidHtmlFormatException(NO_PROPERTY_FOR_VALUE.message(), count); 
				}
				if (lastAttribute != null && lastAttribute.getValue() == null) {
					lastAttribute.setValue(findPropertyValue(stream, count));
					continue;
				} else {
					throw new InvalidHtmlFormatException(NO_PROPERTY_FOR_VALUE.message(), count);
				}
			} else if (c == '>') {
				return true;
			} else if (!IS_CONTENT.test(String.valueOf(c))) {
				throw new InvalidHtmlFormatException(INVALID_CARACTER_ON_TAG_CREATION.message(c), count);
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
			throw new InvalidHtmlFormatException(INESPECTED_CHAR_ON_ATTRIBUTE_VALUE_ATTRIBUTION.message(), count);
		}
		throw new InvalidHtmlFormatException(INCOMPLETE_ATTRIBUTE.message(), count);
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
		throw new InvalidHtmlFormatException(ATTRIBUTE_VALUE_ATTRIBUTION_NOT_FINALIZED.message(), count);
	}

}
