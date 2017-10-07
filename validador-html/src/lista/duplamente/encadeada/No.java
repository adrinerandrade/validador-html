package lista.duplamente.encadeada;

public class No<T> {

	private T info;
	private No<T> anterior;
	private No<T> proximo;
	
	public No(T elemento) {
		this.info = elemento;
	}
	
	public T getInfo() {
		return info;
	}
	public void setInfo(T elemento) {
		this.info = elemento;
	}
	public No<T> getAnterior() {
		return anterior;
	}
	public void setAnterior(No<T> anterior) {
		this.anterior = anterior;
	}
	public No<T> getProximo() {
		return proximo;
	}
	public void setProximo(No<T> proximo) {
		this.proximo = proximo;
	}
	
	@Override
	public String toString() {
		return String.format("{element=%s,next=%s,previous=%s}", info, extrair(proximo), extrair(anterior));
	}
	
	private T extrair(No<T> element) {
		if (element == null) {
			return null;
		}
		return element.getInfo();
	}
	
}
