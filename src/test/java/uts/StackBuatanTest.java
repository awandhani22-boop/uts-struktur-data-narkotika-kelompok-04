package uts;

import org.junit.jupiter.api.Test;
import uts.struktur.StackBuatan;

import static org.junit.jupiter.api.Assertions.*;

class StackBuatanTest {

    @Test
    void pushPopLifo() {
        StackBuatan<String> s = new StackBuatan<>();
        s.push("a"); s.push("b"); s.push("c");
        assertEquals("c", s.pop());
        assertEquals("b", s.peek());
        assertEquals(2, s.size());
        s.validateInvariant();
    }

    @Test
    void kapasitasTumbuhOtomatis() {
        StackBuatan<Integer> s = new StackBuatan<>();
        for (int i = 0; i < 100; i++) s.push(i);
        assertEquals(100, s.size());
        assertEquals(99, s.peek());
        s.validateInvariant();
    }

    @Test
    void popStackKosongMelempar() {
        assertThrows(IllegalStateException.class, () -> new StackBuatan<String>().pop());
    }

    @Test
    void searchPreserveTidakMengubahUrutanAkhir() {
        StackBuatan<Integer> s = new StackBuatan<>();
        for (int i = 1; i <= 6; i++) s.push(i);
        assertEquals(3, s.searchPreserve(x -> x == 3));
        assertEquals(-1, s.searchPreserve(x -> x == 99));
        assertEquals(6, s.size());
        for (int i = 0; i < 6; i++) assertEquals(6 - i, s.elementFromTop(i));
        s.validateInvariant();
    }
}
