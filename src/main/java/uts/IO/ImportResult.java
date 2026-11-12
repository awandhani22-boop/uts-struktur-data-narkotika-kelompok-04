package uts.io;

public class ImportResult {
    public int diterima;
    public int ditolak;
    public final StringBuilder laporan = new StringBuilder();
    public String catatan = "";

    public void tolak(int n, String why) {
        ditolak++;
        laporan.append("Baris ").append(n).append(": ").append(why).append('\n');
    }
}
