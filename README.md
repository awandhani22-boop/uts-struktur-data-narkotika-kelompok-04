# Sistem Data Putusan Narkotika — PN Jakarta Pusat

Aplikasi Java Swing berorientasi objek untuk menyimpan, mencari, menyaring, mengurutkan, dan menganalisis **50 data putusan pengadilan narkotika** dari Pengadilan Negeri Jakarta Pusat (PN Jkt.Pst) menggunakan struktur data yang dibangun dari nol.

## 1. Identitas Kelompok
- **Mata Kuliah:** Struktur Data (UTS — Semester Ganjil 2026/2027)
- **Kelompok:** 05
- **Anggota:**
    1. **Ahmad Dhani Dzakwan** — `202510370110072`
    2. **Rasyid Abdillah** — `202510370110080`

## 2. Struktur Data Pilihan
- **Struktur Data Utama:** **Singly Linked List** (`uts.struktur.SinglyLinkedList<T>`)
    - Menyimpan seluruh 50 objek `Putusan` secara dinamis dengan pointer `head`, `tail`, dan pencatatan `size`.
    - Dilengkapi `validateInvariant()` untuk memastikan konsistensi pointer.
- **Struktur Data Sekunder:** **Stack** (`uts.struktur.StackBuatan<T>`)
    - Merekam riwayat aktivitas pengguna (impor, cari, filter, urutkan, hapus/tambah) pada tab **Audit** dengan prinsip LIFO.

## 3. Cara Build
Pastikan **Java JDK** dan **Maven** sudah terpasang, lalu jalankan di root repositori:
```bash
mvn clean compile
```

## 4. Cara Test
Untuk menjalankan seluruh unit test dan 7 skenario pengujian wajib (`SkenarioTest.java`, `SinglyLinkedListTest.java`, `StackBuatanTest.java`, `ImporTest.java`, `LogikaTest.java`):
```bash
mvn test
```

## 5. Cara Menjalankan Aplikasi
- **Melalui IntelliJ IDEA:** Buka proyek, lalu jalankan `src/main/java/uts/App.java`.
- **Melalui Maven:**
  ```bash
  mvn exec:java -Dexec.mainClass="uts.App"
  ```

## 6. Contoh Penggunaan
1. **Impor Data:** Buka tab **Data**, klik **Impor CSV**, lalu pilih `data/putusan-50.csv` (memuat 50 putusan `P001`–`P050`).
2. **Cari:** Buka tab **Cari**, pilih `Case ID` atau `Nomor Perkara`, masukkan kata kunci (misal `P012`), lalu klik **Cari** untuk melihat detail putusan dan jumlah langkah *Linear Search*.
3. **Filter:** Buka tab **Filter**, isi kriteria (misal jenis `sabu`, pasal `114`, berat minimal `20`), lalu klik **Terapkan filter**.
4. **Urutan:** Buka tab **Urutan** untuk mengurutkan data berdasarkan lama vonis menggunakan **Merge Sort**.
5. **Agregasi & Audit:** Buka tab **Agregasi** untuk melihat rata-rata vonis, total denda, dan distribusi jenis narkotika; buka tab **Audit** untuk melihat riwayat operasi dari **Stack**.
6. **Ekspor:** Buka tab **Ekspor** untuk menyimpan laporan ke file `.txt` atau `.csv`.
