package uts.gui;

import uts.io.CsvImporter;
import uts.io.Ekspor;
import uts.io.ImportResult;
import uts.logika.Agregasi;
import uts.logika.FilterPutusan;
import uts.logika.KriteriaFilter;
import uts.logika.MergeSort;
import uts.logika.Pencarian;
import uts.logika.ValidatorPutusan;
import uts.model.Putusan;
import uts.Struktur.SinglyLinkedList;
import uts.Struktur.StackBuatan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    private static final Color AKSEN = new Color(0x1F4E79);
    private static final Color AKSEN_GELAP = new Color(0x163A5A);
    private static final Color LATAR = new Color(0xF5F6F8);
    private static final Color GARIS = new Color(0xD9DDE3);
    private static final Color TEKS = new Color(0x1E2430);
    private static final Color REDUP = new Color(0x5F6B7A);
    private static final Color ZEBRA = new Color(0xF0F4F9);
    private static final Color SUKSES = new Color(0x1E7B4F);
    private static final Color PERINGATAN = new Color(0xB45309);
    private static final Font SERIF = new Font("Georgia", Font.BOLD, 20);
    private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SinglyLinkedList<Putusan> data = new SinglyLinkedList<>();
    private final StackBuatan<String> audit = new StackBuatan<>();

    private SinglyLinkedList<Putusan> hasilCari = new SinglyLinkedList<>();
    private SinglyLinkedList<Putusan> hasilFilter = new SinglyLinkedList<>();
    private SinglyLinkedList<Putusan> hasilUrut = new SinglyLinkedList<>();

    private final TabelPutusan modelData = new TabelPutusan(data);
    private final TabelPutusan modelFilter = new TabelPutusan(hasilFilter);
    private final TabelPutusan modelUrut = new TabelPutusan(hasilUrut);
    private final JTable tabelData = tabel(modelData);
    private final JTabbedPane tab = new JTabbedPane();
    private final CardLayout kartuData = new CardLayout();
    private final JPanel panelData = new JPanel(kartuData);
    private final JLabel hitungHeader = new JLabel();
    private final JLabel statusPesan = new JLabel();
    private final JLabel statusHitung = new JLabel();
    private final JTextArea areaDetail = area();
    private final JTextArea areaAgregasi = area();
    private final JTextArea areaAudit = area();
    private final JTextArea areaCari = area();
    private final JLabel jumlahFilter = new JLabel();
    private final JLabel infoUrut = new JLabel();

    private JButton tombolHapus, tombolPeriksa, tombolCari, tombolFilter, tombolUrut, tombolEkspor;

    public MainFrame() {
        super("Putusan Narkotika PN Jakarta Pusat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(LATAR);
        setLayout(new BorderLayout());

        add(buatHeader(), BorderLayout.NORTH);

        tab.addTab("Data", buatTabData());
        tab.addTab("Cari", buatTabCari());
        tab.addTab("Filter", buatTabFilter());
        tab.addTab("Urutan", buatTabUrutan());
        tab.addTab("Agregasi", buatTabAgregasi());
        tab.addTab("Audit", buatTabAudit());
        tab.addTab("Ekspor", buatTabEkspor());
        tab.addChangeListener(e -> {
            String t = tab.getTitleAt(tab.getSelectedIndex());
            if (t.equals("Agregasi")) hitungAgregasi();
            if (t.equals("Audit")) segarkanAudit();
        });
        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.setBorder(new EmptyBorder(10, 12, 0, 12));
        mid.add(tab, BorderLayout.CENTER);
        add(mid, BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);

        pasangPintasan();
        tabelData.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetail();
                perbaruiKeadaan();
            }
        });
        dataBerubah("Belum ada data. Impor file CSV untuk memulai.", REDUP);
    }

    private JComponent buatHeader() {
        JLabel t = new JLabel("Putusan Narkotika");
        t.setFont(SERIF);
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel("Pengadilan Negeri Jakarta Pusat");
        s.setForeground(new Color(0xC9D8E8));
        JPanel l = new JPanel();
        l.setOpaque(false);
        l.setLayout(new BoxLayout(l, BoxLayout.Y_AXIS));
        l.add(t);
        l.add(Box.createVerticalStrut(2));
        l.add(s);

        hitungHeader.setForeground(Color.WHITE);
        hitungHeader.setFont(hitungHeader.getFont().deriveFont(Font.BOLD, 15f));

        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(AKSEN);
        h.setBorder(new EmptyBorder(14, 18, 14, 18));
        h.add(l, BorderLayout.WEST);
        h.add(hitungHeader, BorderLayout.EAST);
        return h;
    }

    private JComponent buatStatusBar() {
        statusHitung.setForeground(REDUP);
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new MatteBorder(1, 0, 0, 0, GARIS));
        JPanel c = new JPanel(new BorderLayout());
        c.setOpaque(false);
        c.setBorder(new EmptyBorder(7, 16, 7, 16));
        c.add(statusPesan, BorderLayout.WEST);
        c.add(statusHitung, BorderLayout.EAST);
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private void pasangPintasan() {
        int mod = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_O, mod), "impor");
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, mod), "tambah");
        root.getActionMap().put("impor", aksi(this::aksiImpor));
        root.getActionMap().put("tambah", aksi(this::aksiTambah));
        tabelData.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "hapus");
        tabelData.getActionMap().put("hapus", aksi(this::aksiHapus));
    }

    private static Action aksi(Runnable r) {
        return new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { r.run(); }
        };
    }

    private JComponent buatTabData() {
        TombolUtama imp = new TombolUtama("Impor CSV");
        imp.setToolTipText("Muat file CSV putusan (Ctrl+O)");
        imp.addActionListener(e -> aksiImpor());
        JButton tmb = new JButton("Tambah putusan");
        tmb.setToolTipText("Isi satu putusan lewat formulir (Ctrl+N)");
        tmb.addActionListener(e -> aksiTambah());
        tombolHapus = new JButton("Hapus baris terpilih");
        tombolHapus.addActionListener(e -> aksiHapus());
        tombolPeriksa = new JButton("Periksa struktur");
        tombolPeriksa.setToolTipText("Jalankan validateInvariant() pada list dan stack");
        tombolPeriksa.addActionListener(e -> aksiPeriksaStruktur());

        JScrollPane sd = new JScrollPane(areaDetail);
        sd.setBorder(new MatteBorder(1, 1, 1, 1, GARIS));
        sd.setPreferredSize(new Dimension(100, 110));
        JScrollPane st = new JScrollPane(tabelData);
        st.setBorder(new MatteBorder(1, 1, 1, 1, GARIS));
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, st, sd);
        sp.setResizeWeight(1.0);
        sp.setBorder(null);
        sp.setDividerSize(8);

        JPanel kt = new JPanel(new BorderLayout());
        kt.setOpaque(false);
        kt.add(toolbar(imp, tmb, tombolHapus, tombolPeriksa), BorderLayout.NORTH);
        kt.add(sp, BorderLayout.CENTER);

        JLabel t1 = new JLabel("Belum ada putusan");
        t1.setFont(SERIF);
        t1.setForeground(TEKS);
        JLabel t2 = new JLabel("<html><div style='width:360px;text-align:center'>Impor file CSV berisi 50 putusan PN Jakarta Pusat. "
                + "Record yang bukan dari PN Jakarta Pusat atau salah format akan ditolak dan dilaporkan, "
                + "sisanya tetap dimuat.</div></html>");
        t2.setForeground(REDUP);
        TombolUtama ib = new TombolUtama("Impor CSV");
        ib.addActionListener(e -> aksiImpor());
        JLabel t3 = new JLabel("Pintasan: Ctrl+O");
        t3.setForeground(REDUP);
        JPanel ks = new JPanel();
        ks.setOpaque(false);
        ks.setLayout(new BoxLayout(ks, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{t1, t2, ib, t3}) c.setAlignmentX(Component.CENTER_ALIGNMENT);
        ks.add(Box.createVerticalGlue());
        ks.add(t1);
        ks.add(Box.createVerticalStrut(10));
        ks.add(t2);
        ks.add(Box.createVerticalStrut(18));
        ks.add(ib);
        ks.add(Box.createVerticalStrut(8));
        ks.add(t3);
        ks.add(Box.createVerticalGlue());

        panelData.setOpaque(false);
        panelData.add(ks, "kosong");
        panelData.add(kt, "tabel");
        return panelData;
    }

    private void aksiImpor() {
        if (!data.isEmpty() && JOptionPane.showConfirmDialog(this,
                "Data saat ini (" + data.size() + " record) akan diganti isi file yang dipilih. Lanjutkan?",
                "Ganti data?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) return;

        JFileChooser fc = new JFileChooser(new File("data"));
        fc.setDialogTitle("Pilih file CSV putusan");
        fc.setFileFilter(new FileNameExtensionFilter("File CSV (*.csv)", "csv"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();

        try {
            data.clear();
            ImportResult res = new CsvImporter().impor(f, data);
            data.validateInvariant();
            catat("IMPOR " + f.getName() + " diterima=" + res.diterima + " ditolak=" + res.ditolak);

            boolean ok = res.ditolak == 0 && res.catatan.isEmpty();
            dataBerubah(f.getName() + ": " + res.diterima + " record dimuat"
                    + (res.ditolak > 0 ? ", " + res.ditolak + " ditolak." : "."), ok ? SUKSES : PERINGATAN);
            if (!ok) tampilkanLaporan(res);
        } catch (IOException ex) {
            dataBerubah("Impor gagal.", PERINGATAN);
            JOptionPane.showMessageDialog(this, "File tidak bisa dibaca: " + ex.getMessage()
                    + "\nPeriksa bahwa file tidak sedang dibuka program lain.", "Impor gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanLaporan(ImportResult r) {
        String s = (r.catatan.isEmpty() ? "" : r.catatan + "\n\n")
                + (r.ditolak > 0 ? "Record yang ditolak:\n" + r.laporan : "");
        JTextArea ta = new JTextArea(s, 14, 70);
        ta.setEditable(false);
        ta.setFont(MONO);
        ta.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Laporan impor", JOptionPane.WARNING_MESSAGE);
    }

    private void aksiTambah() {
        String[] nm = {"caseId", "nomorPerkara", "pengadilan", "tanggalPutusan", "terdakwaKode",
                "jenisNarkotika", "beratBarangBuktiGram", "pasalDakwaan", "pasalTerbukti", "tuntutanBulan",
                "vonisBulan", "dendaRupiah", "barangBukti", "keadaanMemberatkan", "keadaanMeringankan", "sourcePages"};
        String[] hint = {"P001 sampai P050", "123/Pid.Sus/2025/PN Jkt.Pst", "", "yyyy-MM-dd, mis. 2025-03-18",
                "T001 sampai T050", "pisahkan dengan \"; \"", "total netto; tidak ada = 0", "tidak ada = null",
                "tidak ada = null", "1 tahun 6 bulan = 18", "angka bulat > 0", "tanpa titik; tidak ada = 0",
                "tanpa koma", "tanpa koma", "tanpa koma", "mis. identitas:1;vonis:20"};
        JTextField[] tf = new JTextField[nm.length];
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(6, 6, 6, 6));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < nm.length; i++) {
            g.gridx = 0; g.gridy = i; g.fill = GridBagConstraints.NONE; g.weightx = 0;
            form.add(new JLabel(nm[i]), g);
            tf[i] = new JTextField(i == 2 ? ValidatorPutusan.PENGADILAN_WAJIB : "", 28);
            g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
            form.add(tf[i], g);
            JLabel lb = new JLabel(hint[i]);
            lb.setForeground(REDUP);
            g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0;
            form.add(lb, g);
        }
        JScrollPane sc = new JScrollPane(form);
        sc.setBorder(null);
        sc.setPreferredSize(new Dimension(760, 480));

        while (true) {
            if (JOptionPane.showConfirmDialog(this, sc, "Tambah putusan",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < tf.length; i++) {
                    if (i > 0) sb.append(',');
                    sb.append(tf[i].getText());
                }
                Putusan p = new CsvImporter().parseBaris(sb.toString());
                ValidatorPutusan.periksaDuplikat(data, p);
                data.addLast(p);
                data.validateInvariant();
                catat("TAMBAH " + p.caseId());
                dataBerubah("Putusan " + p.caseId() + " ditambahkan di posisi terakhir.", SUKSES);
                return;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage() + "\n\nIsian Anda tidak hilang; perbaiki lalu klik OK lagi.",
                        "Data belum valid", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aksiHapus() {
        int r = tabelData.getSelectedRow();
        if (r < 0) return;
        String id = data.get(r).caseId();
        if (JOptionPane.showConfirmDialog(this, "Hapus putusan " + id + " dari daftar?", "Hapus putusan",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        data.removeAt(r);
        data.validateInvariant();
        catat("HAPUS " + id + " (posisi " + r + ")");
        dataBerubah("Putusan " + id + " dihapus.", SUKSES);
    }

    private void aksiPeriksaStruktur() {
        try {
            data.validateInvariant();
            audit.validateInvariant();
            JOptionPane.showMessageDialog(this,
                    "Struktur konsisten.\n\nSinglyLinkedList: " + data.size() + " elemen\nStack audit: " + audit.size() + " entri",
                    "validateInvariant()", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Struktur rusak", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanDetail() {
        int r = tabelData.getSelectedRow();
        if (r < 0 || r >= data.size()) {
            areaDetail.setText("Pilih satu baris untuk melihat rinciannya.");
            return;
        }
        Putusan p = data.get(r);
        areaDetail.setText("Pasal dakwaan : " + nilai(p.pasalDakwaan()) + "\n"
                + "Barang bukti  : " + p.barangBukti() + "\n"
                + "Memberatkan   : " + p.keadaanMemberatkan() + "\n"
                + "Meringankan   : " + p.keadaanMeringankan() + "\n"
                + "Sumber        : " + p.sourcePages());
        areaDetail.setCaretPosition(0);
    }

    private JComponent buatTabCari() {
        JComboBox<String> cb = new JComboBox<>(new String[]{"Nomor perkara", "Case ID"});
        JTextField key = new JTextField(30);
        key.setToolTipText("Contoh: P012 atau 123/Pid.Sus/2025/PN Jkt.Pst");
        tombolCari = new TombolUtama("Cari");
        areaCari.setText("Ketik nomor perkara atau Case ID, lalu tekan Enter.\nHasil menampilkan posisi dan jumlah langkah pencarian.");

        Runnable run = () -> {
            if (data.isEmpty()) return;
            Pencarian.Hasil h = cb.getSelectedIndex() == 0
                    ? Pencarian.cariNomorPerkara(data, key.getText())
                    : Pencarian.cariCaseId(data, key.getText());
            hasilCari = new SinglyLinkedList<>();
            if (h.ditemukan()) {
                hasilCari.addLast(h.putusan);
                Putusan p = h.putusan;
                areaCari.setText("Ditemukan di posisi ke-" + (h.indeks + 1) + " (indeks " + h.indeks + ")\n"
                        + "Jumlah langkah   : " + h.langkah + " node diperiksa\n\n"
                        + "Case ID          : " + p.caseId() + "\n"
                        + "Nomor perkara    : " + p.nomorPerkara() + "\n"
                        + "Tanggal putusan  : " + p.tanggalPutusan() + "\n"
                        + "Jenis narkotika  : " + p.jenisNarkotika() + "\n"
                        + "Berat            : " + p.beratBarangBuktiGram() + " g\n"
                        + "Pasal terbukti   : " + nilai(p.pasalTerbukti()) + "\n"
                        + "Tuntutan / vonis : " + p.tuntutanBulan() + " / " + p.vonisBulan() + " bulan\n"
                        + "Denda            : Rp " + p.dendaRupiah() + "\n"
                        + "Barang bukti     : " + p.barangBukti() + "\n"
                        + "Memberatkan      : " + p.keadaanMemberatkan() + "\n"
                        + "Meringankan      : " + p.keadaanMeringankan() + "\n");
            } else {
                areaCari.setText("\"" + key.getText().trim() + "\" tidak ditemukan setelah memeriksa " + h.langkah
                        + " node.\nPeriksa penulisan, atau coba pencarian berdasarkan "
                        + (cb.getSelectedIndex() == 0 ? "Case ID." : "nomor perkara."));
            }
            areaCari.setCaretPosition(0);
            catat("CARI \"" + key.getText().trim() + "\" -> " + (h.ditemukan() ? "ketemu" : "tidak ada")
                    + ", langkah=" + h.langkah);
            segarkanHitung();
        };
        tombolCari.addActionListener(e -> run.run());
        key.addActionListener(e -> run.run());

        return halaman(toolbar(new JLabel("Cari berdasarkan"), cb, key, tombolCari), gulir(areaCari), null);
    }

    private JComponent buatTabFilter() {
        JTextField jn = new JTextField(12);
        JTextField ps = new JTextField(12);
        JTextField bmin = new JTextField(5), bmax = new JTextField(5);
        JTextField vmin = new JTextField(4), vmax = new JTextField(4);
        jn.setToolTipText("Sebagian nama sudah cukup, mis. \"gan\" untuk Ganja");
        ps.setToolTipText("Sebagian teks pasal, mis. \"114\"");
        jumlahFilter.setForeground(REDUP);
        jumlahFilter.setText("Isi satu atau lebih kriteria (digabung dengan DAN), lalu klik Terapkan. Kolom kosong diabaikan.");

        tombolFilter = new TombolUtama("Terapkan filter");
        tombolFilter.addActionListener(e -> {
            try {
                KriteriaFilter k = new KriteriaFilter();
                k.jenisNarkotika = jn.getText();
                k.beratMin = angkaDesimal(bmin.getText(), "Berat minimum");
                k.beratMax = angkaDesimal(bmax.getText(), "Berat maksimum");
                k.pasalTerbukti = ps.getText();
                k.vonisMin = angkaBulat(vmin.getText(), "Vonis minimum");
                k.vonisMax = angkaBulat(vmax.getText(), "Vonis maksimum");
                hasilFilter = FilterPutusan.terapkan(data, k);
                modelFilter.setSumber(hasilFilter);
                jumlahFilter.setForeground(hasilFilter.isEmpty() ? PERINGATAN : SUKSES);
                jumlahFilter.setText(hasilFilter.isEmpty()
                        ? "Tidak ada putusan yang cocok. Longgarkan kriteria lalu coba lagi."
                        : hasilFilter.size() + " dari " + data.size() + " putusan cocok.");
                catat("FILTER hasil=" + hasilFilter.size());
                segarkanHitung();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Isian filter belum benar", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton rs = new JButton("Kosongkan");
        rs.addActionListener(e -> {
            jn.setText(""); bmin.setText(""); bmax.setText("");
            ps.setText(""); vmin.setText(""); vmax.setText("");
            hasilFilter = new SinglyLinkedList<>();
            modelFilter.setSumber(hasilFilter);
            jumlahFilter.setForeground(REDUP);
            jumlahFilter.setText("Filter dikosongkan.");
        });

        JPanel kr = toolbar(
                grup("Jenis narkotika", jn), grup("Pasal terbukti", ps),
                grup("Berat (gram)", bmin, new JLabel("sampai"), bmax),
                grup("Vonis (bulan)", vmin, new JLabel("sampai"), vmax),
                tombolFilter, rs);
        return halaman(kr, gulir(tabel(modelFilter)), baris(jumlahFilter));
    }

    private JComponent buatTabUrutan() {
        JComboBox<String> ar = new JComboBox<>(new String[]{"Vonis terendah dulu", "Vonis tertinggi dulu"});
        infoUrut.setForeground(REDUP);
        infoUrut.setText("Pengurutan memakai merge sort pada salinan; urutan di tab Data tidak berubah.");
        tombolUrut = new TombolUtama("Urutkan");
        tombolUrut.addActionListener(e -> {
            MergeSort<Putusan> ms = new MergeSort<>();
            boolean asc = ar.getSelectedIndex() == 0;
            hasilUrut = ms.urut(data, (a, b) -> asc
                    ? Integer.compare(a.vonisBulan(), b.vonisBulan())
                    : Integer.compare(b.vonisBulan(), a.vonisBulan()));
            modelUrut.setSumber(hasilUrut);
            infoUrut.setForeground(SUKSES);
            infoUrut.setText(hasilUrut.size() + " putusan diurutkan dengan " + ms.getPerbandingan()
                    + " perbandingan. Urutan di tab Data tidak berubah.");
            catat("URUT vonis " + (asc ? "naik" : "turun") + ", perbandingan=" + ms.getPerbandingan());
            segarkanHitung();
        });
        return halaman(toolbar(ar, tombolUrut), gulir(tabel(modelUrut)), baris(infoUrut));
    }

    private JComponent buatTabAgregasi() {
        JButton btn = new JButton("Hitung ulang");
        btn.addActionListener(e -> hitungAgregasi());
        return halaman(toolbar(btn), gulir(areaAgregasi), null);
    }

    private void hitungAgregasi() {
        areaAgregasi.setText(data.isEmpty() ? "Belum ada data untuk dihitung. Impor CSV dulu." : Agregasi.hitung(data).teks());
        areaAgregasi.setCaretPosition(0);
    }

    private JComponent buatTabAudit() {
        JButton btn = new JButton("Segarkan");
        btn.addActionListener(e -> segarkanAudit());
        JLabel kt = new JLabel("Terbaru di atas. Stack bekerja terakhir-masuk, pertama-keluar.");
        kt.setForeground(REDUP);
        return halaman(toolbar(btn, kt), gulir(areaAudit), null);
    }

    private void segarkanAudit() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < audit.size(); i++) {
            sb.append(audit.size() - i).append(". ").append(audit.elementFromTop(i)).append('\n');
        }
        areaAudit.setText(audit.isEmpty() ? "Belum ada operasi tercatat." : sb.toString());
        areaAudit.setCaretPosition(0);
    }

    private JComponent buatTabEkspor() {
        JComboBox<String> src = new JComboBox<>(new String[]{
                "Semua data", "Hasil cari terakhir", "Hasil filter terakhir", "Hasil urutan terakhir", "Laporan agregasi"});
        JComboBox<String> fmt = new JComboBox<>(new String[]{"Teks (.txt)", "CSV (.csv)"});
        tombolEkspor = new TombolUtama("Simpan ke file...");
        tombolEkspor.addActionListener(e -> aksiEkspor(src.getSelectedIndex(), fmt.getSelectedIndex()));

        JLabel note = new JLabel("<html><div style='width:560px'>CSV memakai header yang sama dengan file impor, sehingga bisa diimpor lagi. "
                + "Laporan agregasi hanya tersedia sebagai teks. Hasil cari, filter, dan urutan "
                + "dikosongkan otomatis saat data berubah.</div></html>");
        note.setForeground(REDUP);
        JPanel pn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pn.setOpaque(false);
        pn.setBorder(new EmptyBorder(8, 4, 0, 0));
        pn.add(note);
        return halaman(toolbar(grup("Sumber", src), grup("Format", fmt), tombolEkspor), pn, null);
    }

    private void aksiEkspor(int si, int fi) {
        boolean csv = fi == 1;
        if (si == 4 && csv) {
            JOptionPane.showMessageDialog(this, "Laporan agregasi hanya bisa disimpan sebagai teks. Ubah format ke Teks (.txt).",
                    "Ekspor", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        SinglyLinkedList<Putusan> list = switch (si) {
            case 0 -> data;
            case 1 -> hasilCari;
            case 2 -> hasilFilter;
            case 3 -> hasilUrut;
            default -> null;
        };
        String nm = switch (si) {
            case 0 -> "Semua data";
            case 1 -> "Hasil cari";
            case 2 -> "Hasil filter";
            case 3 -> "Hasil urutan vonis";
            default -> "Laporan agregasi";
        };
        if (list != null && list.isEmpty()) {
            JOptionPane.showMessageDialog(this, nm + " masih kosong. Jalankan fiturnya dulu di tab terkait.",
                    "Ekspor", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser(new File("."));
        String ext = csv ? ".csv" : ".txt";
        fc.setSelectedFile(new File("ekspor" + ext));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(ext)) f = new File(f.getPath() + ext);

        try {
            if (list == null) {
                Ekspor.keTeks("Laporan agregasi putusan narkotika PN Jakarta Pusat\n\n"
                        + Agregasi.hitung(data).teks(), f);
            } else if (csv) {
                Ekspor.keCsv(list, f);
            } else {
                Ekspor.keTeks(list, nm, f);
            }
            catat("EKSPOR " + nm + " -> " + f.getName());
            pesan("Tersimpan: " + f.getAbsolutePath(), SUKSES);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage()
                    + "\nCoba folder lain atau tutup file jika sedang terbuka.", "Ekspor gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dataBerubah(String msg, Color col) {
        hasilCari = new SinglyLinkedList<>();
        hasilFilter = new SinglyLinkedList<>();
        hasilUrut = new SinglyLinkedList<>();
        modelFilter.setSumber(hasilFilter);
        modelUrut.setSumber(hasilUrut);
        modelData.fireTableDataChanged();
        jumlahFilter.setForeground(REDUP);
        jumlahFilter.setText("Isi satu atau lebih kriteria (digabung dengan DAN), lalu klik Terapkan. Kolom kosong diabaikan.");
        infoUrut.setForeground(REDUP);
        infoUrut.setText("Pengurutan memakai merge sort pada salinan; urutan di tab Data tidak berubah.");
        kartuData.show(panelData, data.isEmpty() ? "kosong" : "tabel");
        tampilkanDetail();
        pesan(msg, col);
        perbaruiKeadaan();
    }

    private void perbaruiKeadaan() {
        boolean ok = !data.isEmpty();
        String tip = ok ? null : "Impor data dulu";
        for (JButton b : new JButton[]{tombolPeriksa, tombolCari, tombolFilter, tombolUrut, tombolEkspor}) {
            if (b == null) continue;
            b.setEnabled(ok || b == tombolPeriksa);
            b.setToolTipText(b == tombolPeriksa && !ok ? null : tip);
        }
        if (tombolHapus != null) {
            boolean sel = tabelData.getSelectedRow() >= 0;
            tombolHapus.setEnabled(sel);
            tombolHapus.setToolTipText(sel ? "Hapus baris terpilih (Delete)" : "Pilih satu baris di tabel dulu");
        }
        segarkanHitung();
    }

    private void segarkanHitung() {
        String n = data.size() + " putusan";
        hitungHeader.setText(n);
        statusHitung.setText(n + "  |  audit " + audit.size() + " entri");
        tab.setTitleAt(0, data.isEmpty() ? "Data" : "Data (" + data.size() + ")");
    }

    private void pesan(String s, Color c) {
        statusPesan.setText(s);
        statusPesan.setForeground(c);
    }

    private void catat(String s) {
        audit.push(LocalDateTime.now().format(JAM) + "  " + s);
    }

    private static JTable tabel(TabelPutusan m) {
        JTable t = new JTable(m);
        t.setRowHeight(28);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(new Color(0xCFE0F1));
        t.setSelectionForeground(TEKS);
        t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(Font.BOLD));
        t.getTableHeader().setReorderingAllowed(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean fok, int r, int c) {
                super.getTableCellRendererComponent(tb, v, sel, false, r, c);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(TabelPutusan.kolomAngka(c) ? SwingConstants.RIGHT : SwingConstants.LEFT);
                if (!sel) setBackground(r % 2 == 0 ? Color.WHITE : ZEBRA);
                return this;
            }
        });
        int[] w = {70, 210, 95, 170, 80, 300, 100, 90, 130};
        for (int i = 0; i < w.length; i++) t.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        return t;
    }

    private static JTextArea area() {
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setFont(MONO);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setMargin(new Insets(10, 12, 10, 12));
        return a;
    }

    private static JScrollPane gulir(Component c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(new MatteBorder(1, 1, 1, 1, GARIS));
        return s;
    }

    private static JPanel toolbar(Component... cs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setOpaque(false);
        for (int i = 0; i < cs.length; i++) p.add(cs[i]);
        return p;
    }

    private static JPanel baris(Component... cs) {
        return toolbar(cs);
    }

    private static JPanel grup(String txt, Component... cs) {
        JLabel l = new JLabel(txt);
        l.setForeground(REDUP);
        l.setFont(l.getFont().deriveFont(12f));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        for (int i = 0; i < cs.length; i++) row.add(cs[i]);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(3));
        p.add(row);
        return p;
    }

    private static JPanel halaman(Component n, Component c, Component s) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 0, 4, 0));
        if (n != null) p.add(n, BorderLayout.NORTH);
        if (c != null) p.add(c, BorderLayout.CENTER);
        if (s != null) p.add(s, BorderLayout.SOUTH);
        return p;
    }

    private static String nilai(String s) { return s == null ? "-" : s; }

    private static Double angkaDesimal(String s, String n) {
        if (s == null || s.isBlank()) return null;
        try { return Double.valueOf(s.trim().replace(',', '.')); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(n + " harus berupa angka, mis. 12.5"); }
    }

    private static Integer angkaBulat(String s, String n) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.valueOf(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(n + " harus berupa bilangan bulat, mis. 24"); }
    }

    private static class TombolUtama extends JButton {
        TombolUtama(String t) {
            super(t);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD));
            setBorder(new EmptyBorder(8, 18, 8, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color col = !isEnabled() ? new Color(0xA9B4C0)
                    : getModel().isPressed() || getModel().isRollover() ? AKSEN_GELAP : AKSEN;
            g2.setColor(col);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            if (hasFocus() && isEnabled()) {
                g2.setColor(new Color(0x8FB4DA));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
