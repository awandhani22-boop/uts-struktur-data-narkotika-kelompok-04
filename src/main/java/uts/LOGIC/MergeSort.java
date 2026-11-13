package uts.logika;

import uts.struktur.Pembanding;
import uts.struktur.SinglyLinkedList;

public class MergeSort<T> {

    private int perbandingan;

    public int getPerbandingan() { return perbandingan; }

    public SinglyLinkedList<T> urut(SinglyLinkedList<T> src, Pembanding<T> c) {
        perbandingan = 0;
        SinglyLinkedList<T> cp = src.salinanReferensi();
        int n = cp.size();
        Object[] a = new Object[n];
        for (int i = 0; i < n; i++) a[i] = cp.get(i);
        Object[] tmp = new Object[n];
        bagi(a, tmp, 0, n - 1, c);
        SinglyLinkedList<T> res = new SinglyLinkedList<>();
        for (int i = 0; i < n; i++) res.addLast(cast(a[i]));
        return res;
    }

    private void bagi(Object[] a, Object[] tmp, int l, int r, Pembanding<T> c) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        bagi(a, tmp, l, m, c);
        bagi(a, tmp, m + 1, r, c);
        gabung(a, tmp, l, m, r, c);
    }

    private void gabung(Object[] a, Object[] tmp, int l, int m, int r, Pembanding<T> c) {
        for (int i = l; i <= r; i++) tmp[i] = a[i];
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) {
            perbandingan++;
            if (c.bandingkan(cast(tmp[i]), cast(tmp[j])) <= 0) a[k++] = tmp[i++];
            else a[k++] = tmp[j++];
        }
        while (i <= m) a[k++] = tmp[i++];
        while (j <= r) a[k++] = tmp[j++];
    }

    @SuppressWarnings("unchecked")
    private T cast(Object o) { return (T) o; }
}
