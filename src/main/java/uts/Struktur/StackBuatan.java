package uts.Struktur;

public class StackBuatan<T> implements StrukturData<T> {

    private Object[] data = new Object[8];
    private int top = 0;

    public void push(T x) {
        if (top == data.length) {
            Object[] n = new Object[data.length * 2];
            for (int i = 0; i < top; i++) n[i] = data[i];
            data = n;
        }
        data[top++] = x;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (top == 0) throw new IllegalStateException("stack kosong");
        T x = (T) data[--top];
        data[top] = null;
        return x;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (top == 0) throw new IllegalStateException("stack kosong");
        return (T) data[top - 1];
    }

    @SuppressWarnings("unchecked")
    public T elementFromTop(int i) {
        if (i < 0 || i >= top) throw new IndexOutOfBoundsException("indeks " + i + ", size " + top);
        return (T) data[top - 1 - i];
    }

    public int searchPreserve(Kondisi<T> k) {
        StackBuatan<T> tmp = new StackBuatan<>();
        int res = -1;
        int d = 0;
        while (!isEmpty()) {
            T x = pop();
            tmp.push(x);
            if (k.cocok(x)) { res = d; break; }
            d++;
        }
        while (!tmp.isEmpty()) push(tmp.pop());
        return res;
    }

    @Override public int size() { return top; }
    @Override public boolean isEmpty() { return top == 0; }

    @Override
    public void validateInvariant() {
        if (top < 0 || top > data.length) throw new IllegalStateException("top di luar kapasitas");
        for (int i = 0; i < top; i++)
            if (data[i] == null) throw new IllegalStateException("elemen null di bawah top (indeks " + i + ")");
        for (int i = top; i < data.length; i++)
            if (data[i] != null) throw new IllegalStateException("sisa referensi di atas top (indeks " + i + ")");
    }
}
