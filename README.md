# Tugas 2 PBO API Subscriptions
API adalah seperangkat aturan dan komunikasi yang memungkinkan aplikasi untuk berinteraksi dan berbagi informasi satu sama lain. Fungsinya mirip dengan perantara yang memungkinkan aplikasi berbeda untuk saling menggunakan fungsi tanpa perlu mengetahui detail internal masing-masing aplikasi.

Secara singkat, API memungkinkan pengembang untuk menggunakan layanan atau kode yang sudah ada untuk membangun aplikasi baru dengan lebih cepat dan efisien, karena tidak perlu membuat ulang fungsi yang sudah ada.

Program Java ini bertujuan untuk membuat API sederhana yang dapat melakukan operasi dasar (CRUD) untuk sistem manajemen Customers, item, dan langganan. API ini menggunakan database SQLite untuk menyimpan data dan memperbolehkan pengguna untuk berinteraksi dengan sistem melalui permintaan HTTP, seperti GET untuk mendapatkan data, POST untuk membuat data baru, PUT untuk mengubah data, dan DELETE untuk menghapus data. API ini cocok digunakan dalam konteks aplikasi yang membutuhkan manajemen data pelanggan dan langganan, seperti layanan berlangganan produk atau konten. Untuk menguji API, kita dapat menggunakan aplikasi seperti Postman.

### Nama Kelompok: 
- Ni Luh Made Tika Kurniasari 
(2305551034)
- Ni Komang Dama Angelina 
(2305551059)

## Spesifikasi API
# Customer
- GET Customers 
-- GET/customers : Daftar semua pelanggan 
![get customers](https://github.com/tikakurnsri/Subscription/assets/147127000/0cb0df71-1102-4e48-89d3-8bfce505a15c)
-- GET/customers/id : menampilkan pelanggan dan alamatnya 
![get customers id](https://github.com/tikakurnsri/Subscription/assets/147127000/013c457e-e570-4a89-a263-db010c6c1440)
-- GET/customers/id/cards : daftar kartu kredit/debit milik pelanggan 
![get custoners id cards](https://github.com/tikakurnsri/Subscription/assets/147127000/5b6ea87b-02b2-4959-91e8-54215d58c5ed)
-- GET/customers/id/subscriptions : daftar semua subscription milik pelanggan
![get customers id subscriptions](https://github.com/tikakurnsri/Subscription/assets/147127000/bb80a5dc-c308-4df8-b92a-071670880e76)
-- GET/customers/{id}/subscriptions?subscriptions_status={active, cancelled,non-renewing} : daftar semua subscription milik pelanggan yang berstatus aktif/cancelled/non-renewing
![get customers subscription status](https://github.com/tikakurnsri/Subscription/assets/147127000/c1c45f39-cad7-4c6a-90d7-315255f50281)

- POST Customers 
--POST/customers : buat pelanggan baru
![post customers](https://github.com/tikakurnsri/Subscription/assets/147127000/ac3ddee1-93ea-4cb0-9408-a3d816fba898)

- PUT Customers
-- PUT/customers/id
![put customers id](https://github.com/tikakurnsri/Subscription/assets/147127000/0065418f-635e-4389-b4ca-71970e18676f)
-- PUT/customers/id/shipping_addresses/id
- DELETE Customers 
--DELETE/customers/id/cards/id : menghapus informasi kartu kredit pelanggan jika is_primary bernilai false
- GET Items 
-- GET/items : menampilkan daftar semua produk
![get items ](https://github.com/tikakurnsri/Subscription/assets/147127000/40a1937a-dfe7-45c5-8200-92b4928a1261)
-- GET /items?is_active=true : daftar semua produk yang memiliki status aktif
![get items status active](https://github.com/tikakurnsri/Subscription/assets/147127000/1a9610c2-67f8-4a97-9b57-4c373ca20777)
-- GET/customers/id : informasi produk
![get items id](https://github.com/tikakurnsri/Subscription/assets/147127000/55fb7b5c-76f9-4f06-8275-5d406d0b0dc2)
- POST Itens
-- POST/items : membuat item baru
![post items ](https://github.com/tikakurnsri/Subscription/assets/147127000/fc35005f-bdc4-4b2b-a4dc-2862debf2b76)
- PUT Items 
-- PUT/items/id
![put items ](https://github.com/tikakurnsri/Subscription/assets/147127000/4a2f96c4-fa9b-4d8e-b36d-49a3ad542820)
- DELETE Items
-- DELETE/items/id : mengubah status item is_active menjadi false
![delete items](https://github.com/tikakurnsri/Subscription/assets/147127000/9ee95b83-784a-4f98-8214-e9e7a5310943)

## Subscription
- GET Subscription
-- GET/subscriptions : daftar semua subscriptions
![get subscription](https://github.com/tikakurnsri/Subscription/assets/147127000/d884eea7-9642-481a-91fe-1429fb7b10ab)
-- GET /subscriptions?sort_by=current_term_end&sort_type=desc : daftar semua subscriptions diurutkan berdasarkan current_term_end secara descending
![get subscription active](https://github.com/tikakurnsri/Subscription/assets/147127000/6c41117f-5921-4ba3-a3df-18eb40c81f2c)
-- GET /subscriptions/{id} :
informasi subscription,
customer: id, first_name, last_name,
subscription_items: quantity, amount,
item: id, name, price, type
![get subscription id ](https://github.com/tikakurnsri/Subscription/assets/147127000/a8ef825e-1872-4fb1-947a-ed082f0cd5ee)


