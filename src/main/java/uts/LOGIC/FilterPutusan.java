package uts.logika;

import uts.model.Putusan;
import uts.Struktur.SinglyLinkedList;

public final class FilterPutusan {

    private FilterPutusan() { }

    public static SinglyLinkedList<Putusan> terapkan(SinglyLinkedList<Putusan> src, KriteriaFilter k) {
        SinglyLinkedList<Putusan> res = new SinglyLinkedList<>();
        for (int i = 0; i < src.size(); i++) {
            Putusan p = src.get(i);
            if (k.cocok(p)) res.addLast(p);
        }
        return res;
    }
}
