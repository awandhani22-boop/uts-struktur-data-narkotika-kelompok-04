package uts.logika;

import uts.model.Putusan;

public class KriteriaFilter {
    public String jenisNarkotika;
    public Double beratMin, beratMax;
    public String pasalTerbukti;
    public Integer vonisMin, vonisMax;

    public boolean cocok(Putusan p) {
        if (ada(jenisNarkotika) && !mengandung(p.jenisNarkotika(), jenisNarkotika)) return false;
        if (beratMin != null && p.beratBarangBuktiGram() < beratMin) return false;
        if (beratMax != null && p.beratBarangBuktiGram() > beratMax) return false;
        if (ada(pasalTerbukti) && !mengandung(p.pasalTerbukti(), pasalTerbukti)) return false;
        if (vonisMin != null && p.vonisBulan() < vonisMin) return false;
        if (vonisMax != null && p.vonisBulan() > vonisMax) return false;
        return true;
    }

    private static boolean ada(String s) { return s != null && !s.isBlank(); }

    private static boolean mengandung(String t, String key) {
        return t != null && t.toLowerCase().contains(key.trim().toLowerCase());
    }
}
