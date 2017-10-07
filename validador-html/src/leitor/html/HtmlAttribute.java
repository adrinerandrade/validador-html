package leitor.html;

public class HtmlAttribute {

	private String name;
	private String value;
	
	public HtmlAttribute(String name) {
		this.name = name;
	}
	
	public HtmlAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
}
