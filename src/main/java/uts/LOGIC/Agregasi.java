package uts.logika;

import uts.model.Putusan;
import uts.Struktur.SinglyLinkedList;

import java.util.Locale;

public class Agregasi {

    public int jumlahPerkara;
    public double rataRataVonisBulan;
    public long totalDenda;
    public String[] namaJenis = new String[8];
    public int[] jumlahJenis = new int[8];
    public int banyakJenis;

    public static Agregasi hitung(SinglyLinkedList<Putusan> list) {
        Agregasi a = new Agregasi();
        a.jumlahPerkara = list.size();
        long tv = 0;
        for (int i = 0; i < list.size(); i++) {
            Putusan p = list.get(i);
            tv += p.vonisBulan();
            a.totalDenda += p.dendaRupiah();
            String[] zs = p.jenisNarkotika().split(";");
            for (int j = 0; j < zs.length; j++) {
                String s = zs[j].trim();
                if (!s.isEmpty()) a.tambahJenis(s);
            }
        }
        a.rataRataVonisBulan = a.jumlahPerkara == 0 ? 0 : (double) tv / a.jumlahPerkara;
        return a;
    }

    private void tambahJenis(String s) {
        for (int i = 0; i < banyakJenis; i++) {
            if (namaJenis[i].equalsIgnoreCase(s)) { jumlahJenis[i]++; return; }
        }
        if (banyakJenis == namaJenis.length) {
            String[] n2 = new String[banyakJenis * 2];
            int[] j2 = new int[banyakJenis * 2];
            for (int i = 0; i < banyakJenis; i++) { n2[i] = namaJenis[i]; j2[i] = jumlahJenis[i]; }
            namaJenis = n2;
            jumlahJenis = j2;
        }
        namaJenis[banyakJenis] = s;
        jumlahJenis[banyakJenis] = 1;
        banyakJenis++;
    }

    public String teks() {
        Locale id = Locale.of("id", "ID");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(id, "Jumlah perkara   : %d%n", jumlahPerkara));
        sb.append(String.format(id, "Rata-rata vonis  : %.1f bulan%n", rataRataVonisBulan));
        sb.append(String.format(id, "Total denda      : Rp %,d%n", totalDenda));
        sb.append("Distribusi jenis narkotika (satu perkara bisa memuat lebih dari satu zat):\n");
        if (banyakJenis == 0) sb.append("  (belum ada data)\n");
        for (int i = 0; i < banyakJenis; i++)
            sb.append(String.format(id, "  %-20s %d perkara%n", namaJenis[i], jumlahJenis[i]));
        return sb.toString();
    }
}
