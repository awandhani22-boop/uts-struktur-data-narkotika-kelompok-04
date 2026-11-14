# ADR-001: Pemilihan Struktur Data Utama

## 1. Konteks

Di proyek Sistem Data Putusan ini, kami butuh tempat penyimpanan untuk menampung dan mengolah data putusan pengadilan narkotika. Masing-masing data putusan punya cukup banyak atribut—mulai dari nomor perkara, jenis narkotika, berat barang bukti, pasal yang terbukti, lama vonis, sampai tanggal putusan.

Karena datanya nanti bakal sering ditambah, dihapus, dicari, dan difilter lewat aplikasi, kami perlu memilih struktur data utama yang paling pas dan tidak menyulitkan saat proses pengelolaan data berjalan.

## 2. Keputusan

Kami sepakat untuk menggunakan **Singly Linked List** sebagai struktur data utama dalam menyimpan seluruh objek putusan.

Pada struktur ini, setiap *node* membungkus satu objek `Putusan` dan menyimpan referensi (`next`) yang menunjuk ke *node* berikutnya. Dengan cara ini, data tersimpan secara berantai dan bisa bertambah atau berkurang kapan saja tanpa perlu mematok kapasitas maksimal dari awal.

## 3. Alasan Pemilihan

Ada beberapa pertimbangan utama kenapa kami menjatuhkan pilihan ke Singly Linked List:

1. **Ukurannya dinamis:** Kami tidak perlu menebak atau mengunci ukuran penyimpanan sejak awal; jumlah *node* bisa langsung menyesuaikan banyaknya data yang masuk.
2. **Tambah data lebih praktis:** Memasukkan data baru cukup dengan menyambungkan *node* baru ke ujung atau posisi tertentu lewat referensi `next`.
3. **Hapus data tanpa geser elemen:** Kalau ada data yang dihapus, kami cukup memindahkan arah pointer `next` dari *node* sebelumnya, tanpa harus menggeser seluruh elemen seperti di array biasa.
4. **Rapi untuk objek kompleks:** Setiap *node* langsung menampung satu objek `Putusan` secara utuh, jadi struktur kodenya lebih bersih dan mudah dirawat.

## 4. Kelebihan dan Kekurangan

### Kelebihan
- Kapasitasnya fleksibel dan bisa tumbuh atau menyusut sesuai kebutuhan.
- Operasi tambah dan hapus *node* tidak memerlukan penggeseran elemen.
- Alokasi memorinya tidak harus bersebelahan secara fisik.
- Tidak perlu menentukan ukuran maksimal di awal program.

### Kekurangan
- Butuh sedikit memori tambahan di setiap *node* untuk menyimpan pointer `next`.
- Tidak bisa langsung lompat ke data tertentu pakai indeks (tidak mendukung *random access*).
- Pencarian data harus menelusuri *node* satu per satu dari awal (*head*).
- Implementasi pointer-nya sedikit lebih rawan bug (misalnya salah sambung `head`/`tail`) dibanding array sederhana.

## 5. Perbandingan dengan Struktur Data Lain

| Struktur Data | Kelebihan | Kekurangan |
| :--- | :--- | :--- |
| **Array Dinamis** | Akses elemen lewat indeks sangat cepat (`O(1)`). | Saat kapasitas penuh harus *resize* dan menyalin ulang seluruh isi array; hapus di tengah butuh geser elemen. |
| **Singly Linked List** | Ukuran fleksibel, tambah dan hapus *node* cukup mengubah pointer. | Pencarian dan akses posisi tertentu harus menelusuri dari *head* (`O(n)`). |
| **Stack** | Pas untuk alur *Last In First Out* (LIFO) seperti riwayat aksi. | Terlalu kaku untuk mengelola seluruh data putusan secara bebas karena hanya bisa diakses dari puncak (*top*). |
| **Queue** | Cocok untuk antrean *First In First Out* (FIFO). | Akses data terbatas dari ujung depan/belakang, kurang leluasa untuk operasi filter atau hapus di tengah. |

## 6. Konsekuensi

Dengan memilih Singly Linked List, aplikasi kami jadi punya penyimpanan yang fleksibel dan bebas dari batasan ukuran statis.

Konsekuensinya, karena tidak ada akses indeks langsung, setiap proses pencarian atau akses posisi tertentu harus menelusuri *node* secara berurutan menggunakan algoritma seperti *Linear Search*. Selain itu, kami perlu menjaga *invariant* agar pointer `head`, `tail`, dan `size` selalu sinkron setiap kali ada penambahan atau penghapusan data.

## 7. Kesimpulan

Untuk kebutuhan Sistem Data Putusan, **Singly Linked List** dirasa paling seimbang antara fleksibilitas dan kemudahan pengelolaan objek `Putusan`. Meskipun pencarian datanya berjalan secara sekuensial, hal tersebut masih sangat wajar untuk skala 50 data putusan pada proyek ini.
