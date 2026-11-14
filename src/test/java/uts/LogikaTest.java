import org.junit.jupiter.api.Test;
import uts.logika.Agregasi;
import uts.logika.FilterPutusan;
import uts.logika.KriteriaFilter;
import uts.logika.MergeSort;
import uts.logika.Pencarian;
import uts.model.Putusan;
import uts.struktur.SinglyLinkedList;

import static org.junit.jupiter.api.Assertions.*;

class LogikaTest {

    private SinglyLinkedList<Putusan> sampel() {
        SinglyLinkedList<Putusan> l = new SinglyLinkedList<>();
        l.addLast(DataUji.putusan(1, "Metamfetamin", 1.5, "Pasal 114 ayat (1)", 48, 1000));
        l.addLast(DataUji.putusan(2, "Ganja", 12.0, "Pasal 111 ayat (1)", 30, 500));
        l.addLast(DataUji.putusan(3, "Metamfetamin; Ganja", 5.2, "Pasal 112 ayat (1)", 60, 0));
        l.addLast(DataUji.putusan(4, "Heroin", 0.8, null, 12, 0));
        l.addLast(DataUji.putusan(5, "Ganja", 250.0, "Pasal 114 ayat (2)", 108, 2000));
        return l;
    }

    @Test
    void cariAdaTidakAdaDanBatas() {
        SinglyLinkedList<Putusan> l = sampel();
        Pencarian.Hasil head = Pencarian.cariCaseId(l, "P001");
        assertEquals(0, head.indeks);
        assertEquals(1, head.langkah);
        Pencarian.Hasil tail = Pencarian.cariNomorPerkara(l, "5/Pid.Sus/2025/PN Jkt.Pst");
        assertEquals(4, tail.indeks);
        assertEquals(5, tail.langkah);
        Pencarian.Hasil nf = Pencarian.cariCaseId(l, "P999");
        assertFalse(nf.ditemukan());
        assertEquals(5, nf.langkah);
        assertNull(nf.putusan);
    }

    @Test
    void cariTidakMembedakanHurufBesarKecil() {
        assertEquals(1, Pencarian.cariCaseId(sampel(), " p002 ").indeks);
    }

    @Test
    void filterGabungan() {
        KriteriaFilter k = new KriteriaFilter();
        k.jenisNarkotika = "ganja";
        k.vonisMin = 30;
        k.beratMax = 20.0;
        SinglyLinkedList<Putusan> h = FilterPutusan.terapkan(sampel(), k);
        assertEquals(2, h.size());
        assertEquals("P002", h.get(0).caseId());
        assertEquals("P003", h.get(1).caseId());
        h.validateInvariant();
    }

    @Test
    void filterPasalMengabaikanYangNull() {
        KriteriaFilter k = new KriteriaFilter();
        k.pasalTerbukti = "114";
        assertEquals(2, FilterPutusan.terapkan(sampel(), k).size());
    }

    @Test
    void filterKosongMengembalikanSemua() {
        assertEquals(5, FilterPutusan.terapkan(sampel(), new KriteriaFilter()).size());
    }

    @Test
    void mergeSortNaikTurunDanListUtamaUtuh() {
        SinglyLinkedList<Putusan> l = sampel();
        MergeSort<Putusan> ms = new MergeSort<>();
        SinglyLinkedList<Putusan> naik = ms.urut(l, (a, b) -> Integer.compare(a.vonisBulan(), b.vonisBulan()));
        int[] exp = {12, 30, 48, 60, 108};
        for (int i = 0; i < 5; i++) assertEquals(exp[i], naik.get(i).vonisBulan());
        assertTrue(ms.getPerbandingan() > 0);
        SinglyLinkedList<Putusan> turun = ms.urut(l, (a, b) -> Integer.compare(b.vonisBulan(), a.vonisBulan()));
        assertEquals(108, turun.get(0).vonisBulan());
        assertEquals("P001", l.get(0).caseId());
        assertEquals("P005", l.get(4).caseId());
        l.validateInvariant();
        naik.validateInvariant();
    }

    @Test
    void mergeSortStabilDanListKosong() {
        SinglyLinkedList<Putusan> l = new SinglyLinkedList<>();
        l.addLast(DataUji.putusan(1, "A", 1, null, 10, 0));
        l.addLast(DataUji.putusan(2, "A", 1, null, 10, 0));
        l.addLast(DataUji.putusan(3, "A", 1, null, 5, 0));
        SinglyLinkedList<Putusan> h = new MergeSort<Putusan>().urut(l,
                (a, b) -> Integer.compare(a.vonisBulan(), b.vonisBulan()));
        assertEquals("P003", h.get(0).caseId());
        assertEquals("P001", h.get(1).caseId());
        assertEquals("P002", h.get(2).caseId());
        assertEquals(0, new MergeSort<Putusan>().urut(new SinglyLinkedList<>(), (a, b) -> 0).size());
    }

    @Test
    void agregasi() {
        Agregasi a = Agregasi.hitung(sampel());
        assertEquals(5, a.jumlahPerkara);
        assertEquals(51.6, a.rataRataVonisBulan, 0.0001);
        assertEquals(3500L, a.totalDenda);
        assertEquals(3, a.banyakJenis);
        int ganja = -1;
        for (int i = 0; i < a.banyakJenis; i++) if (a.namaJenis[i].equals("Ganja")) ganja = a.jumlahJenis[i];
        assertEquals(3, ganja);
    }

    @Test
    void agregasiListKosongTidakError() {
        Agregasi a = Agregasi.hitung(new SinglyLinkedList<>());
        assertEquals(0, a.jumlahPerkara);
        assertEquals(0.0, a.rataRataVonisBulan, 0.0);
        assertTrue(a.teks().contains("belum ada data"));
    }
}
