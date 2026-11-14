# Trace Manual: Alur Kerja Struktur Data dan Algoritma

Catatan ini dibuat untuk menelusuri secara manual bagaimana pointer di **Singly Linked List** berubah saat data dihapus atau ditambah, serta bagaimana **Linear Search** dan **Merge Sort** bekerja langkah demi langkah pada sampel data dari `data/putusan-50.csv`.

---

## 1. Penelusuran Pointer di `SinglyLinkedList` (Hapus & Tambah)

Misalkan di awal kita punya 4 data putusan yang saling terhubung:
`P001 -> P002 -> P003 -> P004 -> null`
- `head` menunjuk ke `P001`
- `tail` menunjuk ke `P004`
- `size = 4`

### A. Hapus data paling depan (`remove(0)`)
1. Ambil isi `head` (`P001`) untuk dikembalikan sebagai hasil.
2. Geser `head` ke node berikutnya (`head = head.next`), jadi sekarang `head` menunjuk ke `P002`.
3. Kurangi `size` jadi `3`.
- **Hasilnya:** `P002 -> P003 -> P004 -> null` (`head = P002`, `tail = P004`, `size = 3`).

### B. Hapus data di tengah (`remove(1)` — menghapus `P003`)
1. Mulai jalan dari `head` (`P002`) dan berhenti tepat sebelum posisi yang mau dihapus (di `P002`).
2. Sambungkan langsung `P002.next` melewati `P003` ke `P004`.
3. Kurangi `size` jadi `2`.
- **Hasilnya:** `P002 -> P004 -> null` (`head = P002`, `tail = P004`, `size = 2`).

### C. Hapus data paling belakang (`remove(1)` — menghapus `P004`)
1. Jalan dari `head` (`P002`) sampai ketemu node tepat sebelum `tail` (yaitu `P002`).
2. Putus sambungan ke `P004` dengan mengubah `P002.next = null`.
3. Pindahkan `tail` ke `P002`, lalu kurangi `size` jadi `1`.
- **Hasilnya:** `P002 -> null` (`head = P002`, `tail = P002`, `size = 1`).

### D. Tambah 3 data baru (`P048`, `P049`, `P050`)
1. Tambah `P048`: `tail.next` disambungkan ke `P048`, lalu `tail` pindah ke `P048` (`size = 2`).
2. Tambah `P049`: `tail.next` disambungkan ke `P049`, lalu `tail` pindah ke `P049` (`size = 3`).
3. Tambah `P050`: `tail.next` disambungkan ke `P050`, lalu `tail` pindah ke `P050` (`size = 4`).
- **Hasil akhir:** `P002 -> P048 -> P049 -> P050 -> null` (`head = P002`, `tail = P050`, `size = 4`).
- Semua *invariant* tetap aman: `head` tidak `null`, `tail.next` menunjuk `null`, dan `size` pas `4`.

---

## 2. Penelusuran Pencarian (`Linear Search` di `Pencarian.java`)

Misalnya kita mau mencari `caseId = "P004"` di dalam list `P001 -> P002 -> P003 -> P004 -> P005`:

| Langkah | Node yang Dicek | `caseId` | Ketemu? | Catatan |
| :---: | :--- | :--- | :---: | :--- |
| 1 | `head` | `P001` | Belum | Lanjut ke `next` |
| 2 | node ke-2 | `P002` | Belum | Lanjut ke `next` |
| 3 | node ke-3 | `P003` | Belum | Lanjut ke `next` |
| 4 | node ke-4 | `P004` | **Ya** | Ketemu, pencacah berhenti di **4 langkah** |

Kalau data yang dicari tidak ada (misal `"P999"` dari 50 data), program bakal menelusuri dari `P001` sampai `P050`, lalu berhenti setelah **50 langkah** dengan hasil tidak ditemukan (`null`).

---

## 3. Penelusuran Pengurutan (`Merge Sort` di `MergeSort.java`)

Misalnya kita urutkan 4 data berdasarkan lama vonis (`vonisBulan`) dari yang paling ringan:
Data awal: `[P001 (120 bln), P002 (60 bln), P003 (60 bln), P004 (84 bln)]`

1. **Belah dua (*divide*) pakai *slow & fast pointer*:**
   - Bagian kiri: `[P001 (120), P002 (60)]`
   - Bagian kanan: `[P003 (60), P004 (84)]`
2. **Belah lagi sampai masing-masing tinggal 1 node:**
   - `[P001 (120)]` dan `[P002 (60)]`
   - `[P003 (60)]` dan `[P004 (84)]`
3. **Gabungkan sambil diurutkan (*merge*):**
   - Bandingkan `120` dan `60` -> hasilnya `[P002 (60), P001 (120)]`
   - Bandingkan `60` dan `84` -> hasilnya `[P003 (60), P004 (84)]`
4. **Gabungkan dua bagian terakhir:**
   - `P002 (60)` vs `P003 (60)` -> ambil `P002 (60)` dulu supaya urutan aslinya tetap terjaga (*stable*)
   - `P001 (120)` vs `P003 (60)` -> ambil `P003 (60)`
   - `P001 (120)` vs `P004 (84)` -> ambil `P004 (84)`
   - Terakhir masukkan sisa `P001 (120)`
- **Hasil akhir:** `[P002 (60), P003 (60), P004 (84), P001 (120)]`
