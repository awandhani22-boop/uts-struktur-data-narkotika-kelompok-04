package uts.logika;

import uts.model.Putusan;
import uts.struktur.SinglyLinkedList;

public final class ValidatorPutusan {

    public static final String PENGADILAN_WAJIB = "Pengadilan Negeri Jakarta Pusat";

    private ValidatorPutusan() { }

    public static void periksa(Putusan p) {
        if (p == null) throw new IllegalArgumentException("record kosong");
        if (!PENGADILAN_WAJIB.equals(p.pengadilan()))
            throw new IllegalArgumentException("pengadilan bukan " + PENGADILAN_WAJIB);
        if (!kodeValid(p.caseId(), 'P'))
            throw new IllegalArgumentException("caseId harus P001 sampai P050");
        if (!kodeValid(p.terdakwaKode(), 'T'))
            throw new IllegalArgumentException("terdakwaKode harus T001 sampai T050");
        if (kosong(p.nomorPerkara()))
            throw new IllegalArgumentException("nomorPerkara tidak boleh kosong");
        if (p.tanggalPutusan() == null)
            throw new IllegalArgumentException("tanggalPutusan tidak boleh kosong");
        if (kosong(p.jenisNarkotika()))
            throw new IllegalArgumentException("jenisNarkotika tidak boleh kosong");
        if (p.beratBarangBuktiGram() < 0)
            throw new IllegalArgumentException("beratBarangBuktiGram tidak boleh negatif");
        if (p.tuntutanBulan() < 0)
            throw new IllegalArgumentException("tuntutanBulan tidak boleh negatif");
        if (p.vonisBulan() <= 0)
            throw new IllegalArgumentException("vonisBulan harus lebih dari 0 (hanya pidana penjara waktu tertentu)");
        if (p.dendaRupiah() < 0)
            throw new IllegalArgumentException("dendaRupiah tidak boleh negatif");
    }

    public static void periksaDuplikat(SinglyLinkedList<Putusan> list, Putusan p) {
        if (list.indexOf(x -> x.caseId().equals(p.caseId())) >= 0)
            throw new IllegalArgumentException("caseId " + p.caseId() + " sudah ada");
        if (list.indexOf(x -> x.nomorPerkara().equals(p.nomorPerkara())
                && x.tanggalPutusan().equals(p.tanggalPutusan())) >= 0)
            throw new IllegalArgumentException("nomorPerkara + tanggal sudah ada");
    }

    private static boolean kosong(String s) { return s == null || s.isBlank(); }

    private static boolean kodeValid(String s, char c0) {
        if (s == null || s.length() != 4 || s.charAt(0) != c0) return false;
        int v = 0;
        for (int i = 1; i < 4; i++) {
            char ch = s.charAt(i);
            if (ch < '0' || ch > '9') return false;
            v = v * 10 + (ch - '0');
        }
        return v >= 1 && v <= 50;
    }
}
