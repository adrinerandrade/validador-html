package pilha.general;

public class PilhaVetor<T> implements Pilha<T> {

	private T[] info;
	private int limite;
	private int tamanho;

	public PilhaVetor(int limite) {
		this.limite = limite;
		info = (T[]) new Object[limite];
	}

	@Override
	public void push(T info) {
		if (tamanho == limite) {
			throw new PilhaEstaCheiaException("A pilha está cheia.");
		}
		this.info[tamanho] = info;
		tamanho++;
	}

	@Override
	public T pop() {
		if (tamanho == 0) {
			throw new PilhaEstaVaziaException("A pilha está vazia");
		}
		T ret = this.info[--tamanho];
		this.info[tamanho] = null;
		return ret;
	}

	@Override
	public T peek() {
		if (tamanho == 0) {
			throw new PilhaEstaVaziaException("A pilha está vazia");
		}
		return this.info[tamanho];
	}

	@Override
	public boolean estaVazia() {
		return tamanho == 0;
	}

	@Override
	public void liberar() {
		info = (T[]) new Object[limite];
		tamanho = 0;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		if (tamanho == 0) {
			for (T info : this.info) {
				str.append(info);
				str.append(",");
			}
			str.deleteCharAt(str.length() - 1);
		}

		return str.toString();
	}

	public void concatenar(T[] array) {
		for (T obj : array) {
			push(obj);
		}
	}

}
