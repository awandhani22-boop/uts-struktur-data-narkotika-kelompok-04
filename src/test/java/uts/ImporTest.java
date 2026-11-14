package uts;

import org.junit.jupiter.api.Test;
import uts.io.CsvImporter;
import uts.io.ImportResult;
import uts.model.Putusan;
import uts.struktur.SinglyLinkedList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ImporTest {

    private File tulis(String... rows) throws IOException {
        File f = File.createTempFile("uji", ".csv");
        f.deleteOnExit();
        StringBuilder sb = new StringBuilder(CsvImporter.HEADER).append('\n');
        for (String r : rows) sb.append(r).append('\n');
        Files.writeString(f.toPath(), sb.toString());
        return f;
    }

    @Test
    void muatLimaPuluhRecord() throws IOException {
        String[] all = new String[50];
        for (int i = 1; i <= 50; i++) all[i - 1] = DataUji.barisCsv(i);
        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();
        ImportResult r = new CsvImporter().impor(tulis(all), list);
        assertEquals(50, list.size());
        assertEquals(50, r.diterima);
        assertEquals(0, r.ditolak);
        assertEquals("", r.catatan);
        list.validateInvariant();
    }

    @Test
    void recordTidakValidDitolakTanpaMerusakStruktur() throws IOException {
        String lain = DataUji.barisCsv(2).replace("Pengadilan Negeri Jakarta Pusat", "Pengadilan Negeri Surabaya");
        String tglBad = DataUji.barisCsv(3).replace("2025-01-", "31/01/");
        String kolom = "P004,hanya,tiga";
        String v0 = DataUji.barisCsv(5).replace(",24,12,0,", ",24,0,0,");
        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();
        ImportResult r = new CsvImporter().impor(
                tulis(DataUji.barisCsv(1), lain, tglBad, kolom, v0), list);
        assertEquals(1, list.size());
        assertEquals(1, r.diterima);
        assertEquals(4, r.ditolak);
        assertTrue(r.laporan.toString().contains("Baris 3: pengadilan bukan Pengadilan Negeri Jakarta Pusat"));
        assertTrue(r.laporan.toString().contains("tanggalPutusan harus yyyy-MM-dd"));
        assertTrue(r.laporan.toString().contains("jumlah kolom 3"));
        assertTrue(r.laporan.toString().contains("vonisBulan harus lebih dari 0"));
        list.validateInvariant();
    }

    @Test
    void duplikatCaseIdDitolak() throws IOException {
        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();
        ImportResult r = new CsvImporter().impor(tulis(DataUji.barisCsv(1), DataUji.barisCsv(1)), list);
        assertEquals(1, list.size());
        assertTrue(r.laporan.toString().contains("sudah ada"));
    }

    @Test
    void kodeDiLuarRentangDitolak() throws IOException {
        String p051 = DataUji.barisCsv(1).replaceFirst("^P001", "P051");
        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();
        ImportResult r = new CsvImporter().impor(tulis(p051), list);
        assertEquals(0, list.size());
        assertTrue(r.laporan.toString().contains("caseId harus P001 sampai P050"));
    }

    @Test
    void jumlahBukanLimaPuluhDiberiCatatan() throws IOException {
        SinglyLinkedList<Putusan> list = new SinglyLinkedList<>();
        ImportResult r = new CsvImporter().impor(tulis(DataUji.barisCsv(1)), list);
        assertTrue(r.catatan.contains("50"));
    }

    @Test
    void teksNullDisimpanSebagaiNull() {
        Putusan p = new CsvImporter().parseBaris(DataUji.barisCsv(7));
        assertNull(p.pasalDakwaan());
        assertNull(p.pasalTerbukti());
    }
}
