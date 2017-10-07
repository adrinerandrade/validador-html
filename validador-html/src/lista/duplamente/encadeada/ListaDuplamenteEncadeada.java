package lista.duplamente.encadeada;

import java.util.function.Consumer;

public class ListaDuplamenteEncadeada<T> {

	private No<T> primeiro;
	private No<T> ultimo;

	public void inserir(T elemento) {
		No<T> novoNo = new No<>(elemento);
		if (primeiro == null) {
			primeiro = novoNo;
			ultimo = novoNo;
		} else {
			primeiro.setAnterior(novoNo);
			novoNo.setProximo(primeiro);
			primeiro = novoNo;
		}
	}

	public No<T> getPrimeiro() {
		return primeiro;
	}
	
	public No<T> getUltimo() {
		return ultimo;
	}

	public No<T> buscar(T elemento) {
		No<T> atual = primeiro;
		while (atual != null) {
			if (compararElemento(atual, elemento)) {
				return atual;
			}
			atual = atual.getProximo();
		}
		return null;
	}

	public void retirar(T elemento) {
		No<T> paraRemover = buscar(elemento);
		if (paraRemover != null) {
			No<T> anterior = paraRemover.getAnterior();
			No<T> proximo = paraRemover.getProximo();
			if (anterior != null) {
				anterior.setProximo(proximo);
			} else {
				primeiro = proximo;
			}

			if (proximo != null) {
				proximo.setAnterior(anterior);
			} else {
				ultimo = anterior;
			}
		}
	}

	public void liberar() {
		percorrer(no -> {
			no.setProximo(null);
			no.setAnterior(null);
		});
		primeiro = null;
		ultimo = null;
	}

	public void exibir() {
		percorrer(System.out::println);
	}
	
	public void exibirOrdemInversa() {
		No<T> atual = ultimo;
		while (atual != null) {
			System.out.println(atual);
			atual = atual.getAnterior();
		}
	}

	public int obterComprimento() {
		int comprimento = 0;
		No<T> atual = primeiro;
		while (atual != null) {
			comprimento++;
			atual = atual.getProximo();
		}
		return comprimento;
	}

	public boolean estaVazia() {
		return primeiro == null;
	}

	public No<T> obterNo(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index não pode ser menor do que zero");
		}
		if (estaVazia()) {
			throw new IndexOutOfBoundsException("Lista está vazia. Não há index para ser acessado.");
		}
		No<T> atual = primeiro;
		for (int i = 0; i < index; i++) {
			if (atual == null) {
				throw new IndexOutOfBoundsException("Index não pode ser maior do que a quantidade de elementos presentes na lista.");
			}
			atual = atual.getProximo();
		}
		return atual;
	}

	private void percorrer(Consumer<No<T>> acao) {
		No<T> atual = primeiro;
		while (atual != null) {
			No<T> proximo = atual.getProximo();
			acao.accept(atual);
			atual = proximo;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		No<T> atual = primeiro;
		boolean primeiro = true;
		while (atual != null) {
			if (primeiro) {
				primeiro = false;
			} else {
				sb.append(",");
			}
			sb.append(atual.toString());
			atual = atual.getProximo();
		}
		return String.format("[%s]", sb.toString());
	}

	private boolean compararElemento(No<T> no, T elemento) {
		T elementoDoNo = no.getInfo();
		if (elemento == null) {
			return elementoDoNo == null;
		}
		return elemento.equals(elementoDoNo);
	}
	
}
