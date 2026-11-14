package uts.io;

import uts.model.Putusan;
import uts.Struktur.SinglyLinkedList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class Ekspor {

    private Ekspor() { }

    public static void keCsv(SinglyLinkedList<Putusan> list, File f) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8)) {
            out.write(CsvImporter.HEADER);
            out.newLine();
            for (int i = 0; i < list.size(); i++) {
                Putusan p = list.get(i);
                out.write(String.join(",",
                        p.caseId(), p.nomorPerkara(), p.pengadilan(), p.tanggalPutusan().toString(),
                        p.terdakwaKode(), p.jenisNarkotika(), String.valueOf(p.beratBarangBuktiGram()),
                        n(p.pasalDakwaan()), n(p.pasalTerbukti()),
                        String.valueOf(p.tuntutanBulan()), String.valueOf(p.vonisBulan()),
                        String.valueOf(p.dendaRupiah()), p.barangBukti(),
                        p.keadaanMemberatkan(), p.keadaanMeringankan(), p.sourcePages()));
                out.newLine();
            }
        }
    }

    public static void keTeks(SinglyLinkedList<Putusan> list, String judul, File f) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8)) {
            out.write(judul);
            out.newLine();
            out.write("Jumlah record: " + list.size());
            out.newLine();
            out.newLine();
            for (int i = 0; i < list.size(); i++) {
                Putusan p = list.get(i);
                out.write((i + 1) + ". " + p.caseId() + " | " + p.nomorPerkara() + " | " + p.tanggalPutusan());
                out.newLine();
                out.write("   Jenis: " + p.jenisNarkotika() + " | Berat: " + p.beratBarangBuktiGram() + " g");
                out.newLine();
                out.write("   Pasal terbukti: " + n(p.pasalTerbukti()));
                out.newLine();
                out.write("   Tuntutan: " + p.tuntutanBulan() + " bln | Vonis: " + p.vonisBulan()
                        + " bln | Denda: Rp " + p.dendaRupiah());
                out.newLine();
            }
        }
    }

    public static void keTeks(String s, File f) throws IOException {
        Files.writeString(f.toPath(), s, StandardCharsets.UTF_8);
    }

    private static String n(String s) { return s == null ? "null" : s; }
}
