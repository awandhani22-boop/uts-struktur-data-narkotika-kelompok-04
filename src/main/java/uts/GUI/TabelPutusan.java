package uts.gui;

import uts.model.Putusan;
import uts.struktur.SinglyLinkedList;

import javax.swing.table.AbstractTableModel;
import java.util.Locale;

class TabelPutusan extends AbstractTableModel {

    private static final String[] KOLOM = {"Case ID", "Nomor perkara", "Tanggal", "Jenis narkotika",
            "Berat (g)", "Pasal terbukti", "Tuntutan (bln)", "Vonis (bln)", "Denda (Rp)"};

    private static final Locale ID = Locale.of("id", "ID");

    private SinglyLinkedList<Putusan> sumber;

    TabelPutusan(SinglyLinkedList<Putusan> s) { this.sumber = s; }

    void setSumber(SinglyLinkedList<Putusan> s) {
        sumber = s;
        fireTableDataChanged();
    }

    static boolean kolomAngka(int c) { return c == 4 || c == 6 || c == 7 || c == 8; }

    Putusan baris(int r) { return sumber.get(r); }

    @Override public int getRowCount() { return sumber.size(); }
    @Override public int getColumnCount() { return KOLOM.length; }
    @Override public String getColumnName(int c) { return KOLOM[c]; }

    @Override
    public Object getValueAt(int r, int c) {
        Putusan p = sumber.get(r);
        return switch (c) {
            case 0 -> p.caseId();
            case 1 -> p.nomorPerkara();
            case 2 -> p.tanggalPutusan();
            case 3 -> p.jenisNarkotika();
            case 4 -> String.format(Locale.ROOT, "%.2f", p.beratBarangBuktiGram());
            case 5 -> p.pasalTerbukti() == null ? "-" : p.pasalTerbukti();
            case 6 -> p.tuntutanBulan();
            case 7 -> p.vonisBulan();
            default -> String.format(ID, "%,d", p.dendaRupiah());
        };
    }
}
