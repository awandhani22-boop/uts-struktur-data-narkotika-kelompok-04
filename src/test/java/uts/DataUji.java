import uts.model.Putusan;

import java.time.LocalDate;

final class DataUji {
    private DataUji() { }

    static Putusan putusan(int n, String jenis, double w, String pasal, int v, long d) {
        String id = String.format("%03d", n);
        return new Putusan("P" + id, n + "/Pid.Sus/2025/PN Jkt.Pst", "Pengadilan Negeri Jakarta Pusat",
                LocalDate.of(2025, 1, 1).plusDays(n), "T" + id, jenis, w, pasal, pasal,
                v + 12, v, d, "bb", "m", "r", "identitas:1");
    }

    static String barisCsv(int n) {
        return "P" + String.format("%03d", n) + "," + n + "/Pid.Sus/2025/PN Jkt.Pst,"
                + "Pengadilan Negeri Jakarta Pusat,2025-01-" + String.format("%02d", (n % 28) + 1)
                + ",T" + String.format("%03d", n) + ",Ganja,1.5,null,null,24,12,0,bb,m,r,identitas:1";
    }
}
