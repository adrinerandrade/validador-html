package lista.estatica.generica;


public class ListaEstaticaGenerica<T> {

	@SuppressWarnings("unchecked")
	private T[] info = (T[]) new Object[10];
	private int tamanho;

	public void inserir(T valor) {
		if (tamanho == info.length) {
			redimensionar();
		}
		info[tamanho++] = valor;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void exibir() {
		for (int i = 0; i < tamanho; ++i) {
			System.out.println(info[i]);
		}
	}

	public int buscar(T elemento) {
		for (int i = 0; i < tamanho; ++i) {
			if (comparar(elemento, info[i])) {
				return i;
			}
		}
		return -1;
	}

	public void retirar(T elemento) {
		int index = buscar(elemento);
		if (index >= 0) {
			for (int i = index; i < tamanho; ++i) {
				int indexSubstituto = i + 1;
				if (indexSubstituto == info.length) {
					info[i] = null;
				} else {
					info[i] = info[indexSubstituto];
				}
			}
			tamanho--;
		}
	}

	public void liberar() {
		for (int i = 0; i < tamanho; ++i) {
			info[i] = null; 
		}
		tamanho = 0;
	}
	
	public T obterElemento(int index) {
		if (index >= tamanho || index < 0) {
			throw new IndexOutOfBoundsException("Index fora do alcance da lista");
		}
		return info[index];
	}
	
	public boolean estaVazia() {
		return tamanho == 0;
	}
	
	public void inverter() {
		int j = tamanho - 1;
		for (int i = 0; i < tamanho / 2; i++, j--) {
			T temp = info[i];
			info[i] = info[j];
			info[j] = temp;
		}
	}
	
	private void redimensionar() {
		@SuppressWarnings("unchecked")
		T[] newInfo = (T[]) new Object[info.length + 10];
		for (int i = 0; i < info.length; i++) {
			newInfo[i] = info[i];
		}
		info = newInfo;
	}

	private boolean comparar(T o, T a) {
		if (o == null) {
			return a == null;
		}
		return o.equals(a);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean primeiro = true;
		for (int i = 0; i < tamanho; ++i) {
			if (primeiro) {
				primeiro = false;
			} else {
				sb.append(",");
			}
			sb.append(info[i]);
		}
		return String.format("[%s]", sb.toString());
	}
	
}
