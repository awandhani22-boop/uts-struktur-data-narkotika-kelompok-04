package uts.logika;

import uts.model.Putusan;
import uts.struktur.SinglyLinkedList;

public final class Pencarian {

    private Pencarian() { }

    public static final class Hasil {
        public final int indeks;
        public final int langkah;
        public final Putusan putusan;
        Hasil(int i, int n, Putusan p) {
            this.indeks = i; this.langkah = n; this.putusan = p;
        }
        public boolean ditemukan() { return indeks >= 0; }
    }

    public static Hasil cariNomorPerkara(SinglyLinkedList<Putusan> list, String no) {
        String k = no == null ? "" : no.trim();
        return cari(list, p -> p.nomorPerkara().equalsIgnoreCase(k));
    }

    public static Hasil cariCaseId(SinglyLinkedList<Putusan> list, String id) {
        String k = id == null ? "" : id.trim();
        return cari(list, p -> p.caseId().equalsIgnoreCase(k));
    }

    private static Hasil cari(SinglyLinkedList<Putusan> list, uts.struktur.Kondisi<Putusan> k) {
        int i = list.indexOf(k);
        int n = list.getLangkahTerakhir();
        return new Hasil(i, n, i >= 0 ? list.get(i) : null);
    }
}
