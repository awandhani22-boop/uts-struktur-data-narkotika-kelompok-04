# ADR-002: Strategi Pencarian dan Pengurutan Data

## 1. Konteks

Di aplikasi Sistem Data Putusan yang menggunakan **Singly Linked List** sebagai struktur data utama, ada dua operasi inti yang paling sering dipakai pengguna:
1. **Mengurutkan data putusan** berdasarkan lama vonis (`vonisBulan`) dari yang paling ringan ke paling berat (atau sebaliknya).
2. **Mencari data putusan** tertentu berdasarkan `nomorPerkara` atau `caseId`, sekaligus menghitung berapa langkah yang dibutuhkan selama pencarian.

Karena Singly Linked List tidak punya akses indeks langsung seperti array, kami perlu memilih algoritma pengurutan dan pencarian yang paling cocok dengan sifat pointer berantai (*node* ke *node*).

## 2. Keputusan

Kami sepakat menggunakan:
- **Merge Sort** untuk mengurutkan data putusan berdasarkan `vonisBulan`.
- **Linear Search** untuk mencari data berdasarkan `nomorPerkara` atau `caseId`.

---

## 3. Alasan Memilih Merge Sort untuk Pengurutan

1. **Paling cocok untuk Linked List:** Merge Sort bekerja dengan cara membagi daftar menjadi dua bagian (*divide*) lalu menggabungkannya kembali secara terurut (*merge*). Di Singly Linked List, proses ini cukup memindahkan pointer `next` tanpa perlu akses indeks acak seperti *Quick Sort* atau *Heap Sort*.
2. **Kompleksitas waktu stabil:** Baik pada kondisi terbaik, rata-rata, maupun terburuk, kompleksitas waktunya tetap **`O(n log n)`**. Jadi performanya konsisten meskipun data awal sudah terurut atau acak.
3. **Bersifat stabil (*stable sort*):** Jika ada dua putusan dengan lama vonis yang sama (misalnya sama-sama 60 bulan), urutan relatif keduanya tetap terjaga sesuai urutan aslinya.
4. **Tidak merusak *invariant* struktur:** Proses pengurutan bisa dilakukan dengan menghasilkan tampilan terurut atau menyusun ulang pointer `next` secara terkontrol, sehingga `head`, `tail`, dan `size` tetap valid.

---

## 4. Alasan Memilih Linear Search untuk Pencarian

1. **Sesuai dengan akses sekuensial Linked List:** Karena Singly Linked List hanya bisa ditelusuri dari `head` ke `tail` lewat pointer `next`, *Linear Search* adalah cara yang paling alami (`O(n)`).
2. **Tidak mewajibkan data dalam kondisi terurut:** Pengguna bisa langsung mencari `nomorPerkara` kapan saja setelah data dimuat atau ditambah, tanpa harus mengurutkan daftar terlebih dahulu (berbeda dengan *Binary Search* yang butuh data terurut dan akses indeks `O(1)`).
3. **Mudah menghitung jumlah langkah:** Setiap perpindahan ke `node.next` langsung dihitung sebagai 1 langkah, sehingga kebutuhan fitur untuk melaporkan jumlah langkah pencarian bisa langsung terpenuhi secara akurat.

---

## 5. Konsekuensi

- **Pengurutan (`Merge Sort`):** Membutuhkan fungsi bantu untuk mencari titik tengah list (metode *slow & fast pointer*) dan menggabungkan dua sub-list terurut, namun hasilnya jauh lebih cepat dan rapi dibanding algoritma `O(n^2)` seperti *Bubble Sort*.
- **Pencarian (`Linear Search`):** Pada skenario terburuk (data berada di ujung `tail` atau tidak ditemukan), program harus menelusuri seluruh `n` node (`50` langkah untuk 50 data). Untuk skala 50 data putusan pada proyek ini, waktu eksekusinya tetap instan dan sangat memadai.
