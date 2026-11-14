package uts.Struktur;

public class SinglyLinkedList<T> implements StrukturData<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;
    private int langkahTerakhir;

    public int getLangkahTerakhir() { return langkahTerakhir; }

    public void addFirst(T val) {
        Node<T> n = new Node<>(val);
        n.next = head;
        head = n;
        if (tail == null) tail = n;
        size++;
    }

    public void addLast(T val) {
        Node<T> n = new Node<>(val);
        if (tail == null) {
            head = n;
        } else {
            tail.next = n;
        }
        tail = n;
        size++;
    }

    public T get(int idx) {
        cekIndeks(idx);
        langkahTerakhir = 0;
        Node<T> cur = head;
        langkahTerakhir++;
        for (int i = 0; i < idx; i++) {
            cur = cur.next;
            langkahTerakhir++;
        }
        return cur.data;
    }

    public T removeAt(int idx) {
        cekIndeks(idx);
        T res;
        if (idx == 0) {
            res = head.data;
            head = head.next;
            if (head == null) tail = null;
        } else {
            Node<T> prev = head;
            for (int i = 0; i < idx - 1; i++) prev = prev.next;
            Node<T> t = prev.next;
            res = t.data;
            prev.next = t.next;
            if (t == tail) tail = prev;
        }
        size--;
        return res;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public int indexOf(Kondisi<T> k) {
        langkahTerakhir = 0;
        Node<T> cur = head;
        int i = 0;
        while (cur != null) {
            langkahTerakhir++;
            if (k.cocok(cur.data)) return i;
            cur = cur.next;
            i++;
        }
        return -1;
    }

    public SinglyLinkedList<T> salinanReferensi() {
        SinglyLinkedList<T> cp = new SinglyLinkedList<>();
        Node<T> cur = head;
        while (cur != null) {
            cp.addLast(cur.data);
            cur = cur.next;
        }
        return cp;
    }

    public String tracePointer() {
        StringBuilder sb = new StringBuilder("HEAD");
        Node<T> cur = head;
        while (cur != null) {
            sb.append(" -> [").append(cur.data).append(']');
            cur = cur.next;
        }
        sb.append(" -> null | TAIL=").append(tail == null ? "null" : "[" + tail.data + "]");
        sb.append(" | size=").append(size);
        return sb.toString();
    }

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }

    @Override
    public void validateInvariant() {
        if (size < 0) throw new IllegalStateException("size negatif");
        if (size == 0) {
            if (head != null || tail != null)
                throw new IllegalStateException("size 0 tetapi head/tail tidak null");
            return;
        }
        if (head == null || tail == null)
            throw new IllegalStateException("size > 0 tetapi head/tail null");

        int n = 0;
        Node<T> cur = head;
        Node<T> last = null;
        while (cur != null) {
            n++;
            if (n > size) throw new IllegalStateException("jumlah node melebihi size (mungkin ada siklus)");
            last = cur;
            cur = cur.next;
        }
        if (n != size) throw new IllegalStateException("jumlah node " + n + " != size " + size);
        if (last != tail) throw new IllegalStateException("tail tidak menunjuk node terakhir");
        if (tail.next != null) throw new IllegalStateException("tail.next harus null");
    }

    private void cekIndeks(int idx) {
        if (idx < 0 || idx >= size)
            throw new IndexOutOfBoundsException("indeks " + idx + ", size " + size);
    }
}
