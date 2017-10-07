package leitor.html;

import java.util.ArrayList;
import java.util.List;

import lista.duplamente.encadeada.ListaDuplamenteEncadeada;

public class HtmlTag {

	private String type;
	private ListaDuplamenteEncadeada<HtmlAttribute> attributes = new ListaDuplamenteEncadeada<>();
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setAttributes(ListaDuplamenteEncadeada<HtmlAttribute> attributes) {
		this.attributes = attributes;
	}

	public ListaDuplamenteEncadeada<HtmlAttribute> getAttributes() {
		return attributes;
	}
	
}
