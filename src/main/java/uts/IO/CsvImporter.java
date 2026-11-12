package uts.io;

import uts.logika.ValidatorPutusan;
import uts.model.Putusan;
import uts.struktur.SinglyLinkedList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CsvImporter {

    public static final String HEADER =
            "caseId,nomorPerkara,pengadilan,tanggalPutusan,terdakwaKode,jenisNarkotika,beratBarangBuktiGram,"
                    + "pasalDakwaan,pasalTerbukti,tuntutanBulan,vonisBulan,dendaRupiah,barangBukti,"
                    + "keadaanMemberatkan,keadaanMeringankan,sourcePages";
    public static final int TARGET_RECORD = 50;
    private static final int JUMLAH_KOLOM = 16;

    public ImportResult impor(File f, SinglyLinkedList<Putusan> dst) throws IOException {
        ImportResult res = new ImportResult();
        try (BufferedReader in = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8)) {
            String line = in.readLine();
            if (line == null) {
                res.catatan = "File kosong.";
                return res;
            }
            int n = 1;
            while ((line = in.readLine()) != null) {
                n++;
                if (line.isBlank()) continue;
                try {
                    Putusan p = parseBaris(line);
                    ValidatorPutusan.periksaDuplikat(dst, p);
                    dst.addLast(p);
                    res.diterima++;
                } catch (IllegalArgumentException e) {
                    res.tolak(n, e.getMessage());
                }
            }
        }
        if (dst.size() != TARGET_RECORD) {
            res.catatan = "Perhatian: tugas mensyaratkan tepat " + TARGET_RECORD
                    + " record valid, sekarang ada " + dst.size() + ".";
        }
        return res;
    }

    public Putusan parseBaris(String s) {
        String[] c = s.split(",", -1);
        if (c.length != JUMLAH_KOLOM) {
            throw new IllegalArgumentException("jumlah kolom " + c.length + ", seharusnya " + JUMLAH_KOLOM
                    + " (pastikan tidak ada koma di dalam isi kolom)");
        }
        LocalDate tgl;
        try {
            tgl = LocalDate.parse(c[3].trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("tanggalPutusan harus yyyy-MM-dd");
        }
        Putusan p = new Putusan(
                c[0].trim(), c[1].trim(), c[2].trim(), tgl, c[4].trim(), c[5].trim(),
                angkaDouble(c[6], "beratBarangBuktiGram"),
                teksAtauNull(c[7]), teksAtauNull(c[8]),
                angkaInt(c[9], "tuntutanBulan"), angkaInt(c[10], "vonisBulan"),
                angkaLong(c[11], "dendaRupiah"),
                c[12].trim(), c[13].trim(), c[14].trim(), c[15].trim());
        ValidatorPutusan.periksa(p);
        return p;
    }

    private static String teksAtauNull(String s) {
        String t = s.trim();
        return t.isEmpty() || t.equalsIgnoreCase("null") ? null : t;
    }

    private static double angkaDouble(String s, String n) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(n + " bukan angka"); }
    }

    private static int angkaInt(String s, String n) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(n + " bukan bilangan bulat"); }
    }

    private static long angkaLong(String s, String n) {
        try { return Long.parseLong(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(n + " bukan bilangan bulat"); }
    }
}
