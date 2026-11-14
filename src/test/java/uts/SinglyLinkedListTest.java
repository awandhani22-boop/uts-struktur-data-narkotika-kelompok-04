import org.junit.jupiter.api.Test;
import uts.model.Putusan;
import uts.Struktur.SinglyLinkedList;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTest {

    private SinglyLinkedList<Integer> isi(int n) {
        SinglyLinkedList<Integer> l = new SinglyLinkedList<>();
        for (int i = 1; i <= n; i++) l.addLast(i);
        return l;
    }

    @Test
    void listKosongValid() {
        SinglyLinkedList<Integer> l = new SinglyLinkedList<>();
        l.validateInvariant();
        assertTrue(l.isEmpty());
    }

    @Test
    void addFirstDanAddLastMenjagaUrutan() {
        SinglyLinkedList<Integer> l = isi(3);
        l.addFirst(0);
        l.addLast(4);
        l.validateInvariant();
        assertEquals(5, l.size());
        assertEquals("HEAD -> [0] -> [1] -> [2] -> [3] -> [4] -> null | TAIL=[4] | size=5", l.tracePointer());
    }

    @Test
    void hapusPertamaTengahTerakhir() {
        SinglyLinkedList<Integer> l = isi(5);
        assertEquals(1, l.removeAt(0));
        l.validateInvariant();
        assertEquals(3, l.removeAt(1));
        l.validateInvariant();
        assertEquals(5, l.removeAt(l.size() - 1));
        l.validateInvariant();
        assertEquals("HEAD -> [2] -> [4] -> null | TAIL=[4] | size=2", l.tracePointer());
    }

    @Test
    void hapusSatuSatunyaMengosongkanHeadDanTail() {
        SinglyLinkedList<Integer> l = isi(1);
        l.removeAt(0);
        l.validateInvariant();
        assertTrue(l.isEmpty());
        l.addLast(9);
        l.validateInvariant();
        assertEquals(9, l.get(0));
    }

    @Test
    void tambahKembaliTigaSetelahHapus() {
        SinglyLinkedList<Integer> l = isi(5);
        l.removeAt(0); l.removeAt(2); l.removeAt(l.size() - 1);
        l.addLast(10); l.addLast(11); l.addFirst(12);
        l.validateInvariant();
        assertEquals(5, l.size());
        assertEquals(12, l.get(0));
        assertEquals(11, l.get(4));
    }

    @Test
    void indeksDiLuarBatasDitolak() {
        SinglyLinkedList<Integer> l = isi(2);
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> l.removeAt(-1));
    }

    @Test
    void salinanReferensiTidakMengubahAsal() {
        SinglyLinkedList<Integer> l = isi(3);
        SinglyLinkedList<Integer> s = l.salinanReferensi();
        s.removeAt(0);
        assertEquals(3, l.size());
        assertEquals(2, s.size());
        l.validateInvariant();
        s.validateInvariant();
    }

    @Test
    void indexOfMenghitungLangkah() {
        SinglyLinkedList<Integer> l = isi(10);
        assertEquals(6, l.indexOf(x -> x == 7));
        assertEquals(7, l.getLangkahTerakhir());
        assertEquals(-1, l.indexOf(x -> x == 99));
        assertEquals(10, l.getLangkahTerakhir());
    }

    @Test
    void clearMengosongkan() {
        SinglyLinkedList<Putusan> l = new SinglyLinkedList<>();
        l.addLast(DataUji.putusan(1, "Ganja", 1, null, 10, 0));
        l.clear();
        l.validateInvariant();
        assertEquals(0, l.size());
    }
}
