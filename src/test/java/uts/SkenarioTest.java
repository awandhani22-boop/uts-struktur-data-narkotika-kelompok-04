import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uts.io.CsvImporter;
import uts.io.ImportResult;
import uts.logika.FilterPutusan;
import uts.logika.KriteriaFilter;
import uts.logika.Pencarian;
import uts.model.Putusan;
import uts.Struktur.SinglyLinkedList;
import uts.Struktur.StackBuatan;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkenarioTest {

    // Membuat file CSV berisi 50 data pengujian.
    private File buatCsv50(Path folder) throws IOException {
        Path file = folder.resolve("putusan-50.csv");

        List<String> baris = new ArrayList<>();
        baris.add(CsvImporter.HEADER);

        for (int i = 1; i <= 50; i++) {
            baris.add(DataUji.barisCsv(i));
        }

        Files.write(file, baris, StandardCharsets.UTF_8);
        return file.toFile();
    }

    // Membaca 50 data CSV ke dalam Singly Linked List.
    private SinglyLinkedList<Putusan> muat50(Path folder)
            throws IOException {

        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();

        CsvImporter importer = new CsvImporter();
        ImportResult hasil = importer.impor(buatCsv50(folder), list);

        assertEquals(50, hasil.diterima);
        assertEquals(0, hasil.ditolak);
        assertEquals(50, list.size());

        list.validateInvariant();

        return list;
    }

    // Skenario 1: Memuat 50 data putusan.
    @Test
    void skenario1Muat50Data(@TempDir Path folder)
            throws IOException {

        SinglyLinkedList<Putusan> list = muat50(folder);

        System.out.println("SKENARIO 1");
        System.out.println("Jumlah data: " + list.size());
        System.out.println(list.tracePointer());

        assertEquals(50, list.size());
        assertFalse(list.isEmpty());

        list.validateInvariant();
    }

    // Skenario 2: Mencari data ada, tidak ada, dan batas.
    @Test
    void skenario2PencarianData(@TempDir Path folder)
            throws IOException {

        SinglyLinkedList<Putusan> list = muat50(folder);

        Pencarian.Hasil ada =
                Pencarian.cariCaseId(list, "P001");

        Pencarian.Hasil tidakAda =
                Pencarian.cariCaseId(list, "P999");

        Pencarian.Hasil batas =
                Pencarian.cariCaseId(list, "P050");

        System.out.println("SKENARIO 2");
        System.out.println("P001 ditemukan: " + ada.ditemukan());
        System.out.println("Langkah: " + ada.langkah);

        System.out.println("P999 ditemukan: " + tidakAda.ditemukan());
        System.out.println("Langkah: " + tidakAda.langkah);

        System.out.println("P050 ditemukan: " + batas.ditemukan());
        System.out.println("Langkah: " + batas.langkah);

        assertTrue(ada.ditemukan());
        assertEquals(0, ada.indeks);
        assertEquals(1, ada.langkah);

        assertFalse(tidakAda.ditemukan());
        assertEquals(-1, tidakAda.indeks);
        assertEquals(50, tidakAda.langkah);

        assertTrue(batas.ditemukan());
        assertEquals(49, batas.indeks);
        assertEquals(50, batas.langkah);
    }

    // Skenario 3: Menghapus data awal, tengah, dan akhir.
    @Test
    void skenario3HapusData(@TempDir Path folder)
            throws IOException {

        SinglyLinkedList<Putusan> list = muat50(folder);

        System.out.println("SKENARIO 3");
        System.out.println("Sebelum dihapus:");
        System.out.println(list.tracePointer());

        Putusan pertama = list.removeAt(0);
        Putusan tengah = list.removeAt(24);
        Putusan terakhir = list.removeAt(list.size() - 1);

        System.out.println("Data awal dihapus: " + pertama.caseId());
        System.out.println("Data tengah dihapus: " + tengah.caseId());
        System.out.println("Data akhir dihapus: " + terakhir.caseId());

        System.out.println("Setelah dihapus:");
        System.out.println(list.tracePointer());

        assertEquals("P001", pertama.caseId());
        assertEquals("P026", tengah.caseId());
        assertEquals("P050", terakhir.caseId());

        assertEquals(47, list.size());
        assertEquals("P002", list.get(0).caseId());
        assertEquals("P049", list.get(46).caseId());

        list.validateInvariant();
    }

    // Skenario 4: Menambahkan kembali tiga data.
    @Test
    void skenario4TambahKembaliData(@TempDir Path folder)
            throws IOException {

        SinglyLinkedList<Putusan> list = muat50(folder);

        Putusan pertama = list.removeAt(0);
        Putusan tengah = list.removeAt(24);
        Putusan terakhir = list.removeAt(list.size() - 1);

        list.addFirst(pertama);
        list.addLast(tengah);
        list.addLast(terakhir);

        System.out.println("SKENARIO 4");
        System.out.println("Setelah tiga data ditambahkan kembali:");
        System.out.println(list.tracePointer());

        assertEquals(50, list.size());

        assertEquals("P001", list.get(0).caseId());
        assertEquals("P002", list.get(1).caseId());
        assertEquals("P026", list.get(48).caseId());
        assertEquals("P050", list.get(49).caseId());

        list.validateInvariant();
    }

    // Skenario 5: Filter gabungan dan pemeriksaan manual.
    @Test
    void skenario5FilterGabungan() {

        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();

        list.addLast(DataUji.putusan(
                1, "Metamfetamin", 1.5,
                "Pasal 114 ayat (1)", 48, 1000));

        list.addLast(DataUji.putusan(
                2, "Ganja", 12.0,
                "Pasal 111 ayat (1)", 30, 500));

        list.addLast(DataUji.putusan(
                3, "Metamfetamin; Ganja", 5.2,
                "Pasal 112 ayat (1)", 60, 0));

        list.addLast(DataUji.putusan(
                4, "Heroin", 0.8,
                null, 12, 0));

        list.addLast(DataUji.putusan(
                5, "Ganja", 250.0,
                "Pasal 114 ayat (2)", 108, 2000));

        KriteriaFilter k = new KriteriaFilter();

        k.jenisNarkotika = "ganja";
        k.vonisMin = 30;
        k.beratMax = 20.0;

        SinglyLinkedList<Putusan> hasil =
                FilterPutusan.terapkan(list, k);

        System.out.println("SKENARIO 5");
        System.out.println("Hasil filter:");

        for (int i = 0; i < hasil.size(); i++) {
            System.out.println(hasil.get(i).caseId());
        }

        // P002 dan P003 memenuhi semua kriteria.
        // P001 dan P004 tidak mengandung Ganja.
        // P005 memiliki berat barang bukti lebih dari 20 gram.

        assertEquals(2, hasil.size());
        assertEquals("P002", hasil.get(0).caseId());
        assertEquals("P003", hasil.get(1).caseId());

        hasil.validateInvariant();
        list.validateInvariant();
    }

    // Skenario 6: Pencarian Stack tidak mengubah urutan.
    @Test
    void skenario6PencarianStack() {

        StackBuatan<Putusan> stack = new StackBuatan<>();

        Putusan p1 = DataUji.putusan(
                1, "Ganja", 1.5, "Pasal 111", 12, 0);

        Putusan p2 = DataUji.putusan(
                2, "Ganja", 2.0, "Pasal 111", 24, 0);

        Putusan p3 = DataUji.putusan(
                3, "Heroin", 3.0, "Pasal 112", 36, 0);

        stack.push(p1);
        stack.push(p2);
        stack.push(p3);

        System.out.println("SKENARIO 6");
        System.out.println("Sebelum pencarian:");

        for (int i = 0; i < stack.size(); i++) {
            System.out.println(stack.elementFromTop(i).caseId());
        }

        int posisi = stack.searchPreserve(
                p -> p.caseId().equals("P002"));

        System.out.println("Posisi P002: " + posisi);
        System.out.println("Setelah pencarian:");

        for (int i = 0; i < stack.size(); i++) {
            System.out.println(stack.elementFromTop(i).caseId());
        }

        assertEquals(1, posisi);

        assertSame(p3, stack.elementFromTop(0));
        assertSame(p2, stack.elementFromTop(1));
        assertSame(p1, stack.elementFromTop(2));

        assertEquals(3, stack.size());

        stack.validateInvariant();
    }

    // Skenario 7: Data tidak valid ditolak.
    @Test
    void skenario7DataTidakValid(@TempDir Path folder)
            throws IOException {

        Path file = folder.resolve("data-tidak-valid.csv");

        List<String> baris = new ArrayList<>();
        baris.add(CsvImporter.HEADER);

        // Baris pertama berisi data valid.
        baris.add(DataUji.barisCsv(1));

        // Baris kedua memiliki berat barang bukti negatif.
        String dataTidakValid = DataUji.barisCsv(2)
                .replace(",1.5,", ",-1.5,");

        baris.add(dataTidakValid);

        Files.write(file, baris, StandardCharsets.UTF_8);

        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();

        CsvImporter importer = new CsvImporter();

        ImportResult hasil = importer.impor(file.toFile(), list);

        System.out.println("SKENARIO 7");
        System.out.println("Data diterima: " + hasil.diterima);
        System.out.println("Data ditolak: " + hasil.ditolak);
        System.out.println("Pesan kesalahan:");
        System.out.println(hasil.laporan);

        assertEquals(1, hasil.diterima);
        assertEquals(1, hasil.ditolak);

        assertTrue(hasil.laporan.toString().contains(
                "beratBarangBuktiGram tidak boleh negatif"));

        assertEquals(1, list.size());
        assertEquals("P001", list.get(0).caseId());

        list.validateInvariant();
    }
}