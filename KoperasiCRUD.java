import java.sql.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

// Database Connection class
// Class ini digunakan untuk menyambungkan aplikasi dengan database MySQL
class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/koperasi"; // URL database
            String user = "root"; // Username database
            String password = ""; // Password database
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // Exception handling untuk kesalahan koneksi database
            System.out.println("Error connecting to the database: " + e.getMessage());
            return null;
        }
    }
}

// Interface untuk transaksi
interface Transaksi {
    void tampilkanTransaksi(); // Method abstrak untuk menampilkan transaksi
}

// Superclass untuk transaksi (Inheritance - Superclass)
abstract class TransaksiUmum implements Transaksi {
    int id; // ID transaksi
    String nama; // Nama anggota
    String jenisTransaksi; // Jenis transaksi: "Simpanan" atau "Peminjaman"
    double jumlah; // Jumlah transaksi
    String tanggalTransaksi; // Tanggal transaksi

    // Constructor untuk menginisialisasi transaksi umum
    public TransaksiUmum(int id, String nama, String jenisTransaksi, double jumlah) {
        this.id = id; // ID transaksi
        this.nama = formatNama(nama); // Format nama (manipulasi string)
        this.jenisTransaksi = jenisTransaksi;
        this.jumlah = jumlah;
        this.tanggalTransaksi = getCurrentDate(); // Menyimpan tanggal transaksi saat ini
    }

    @Override
    public abstract void tampilkanTransaksi(); // Implementasi method abstrak dari interface

    // Method untuk memformat nama menjadi huruf kapital dan menghapus spasi berlebih
    public String formatNama(String nama) {
        return nama.trim().toUpperCase(); // Manipulasi String
    }

    // Method untuk mendapatkan tanggal saat ini dalam format tertentu
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format tanggal
        return sdf.format(new java.util.Date()); // Menggunakan java.util.Date untuk tanggal sekarang
    }
}

// Subclass Anggota (Inheritance - Subclass)
class Anggota extends TransaksiUmum {
    int cicilanPerBulan; // Jumlah cicilan per bulan
    double bungaPerPersen; // Bunga tahunan dalam persen

    // Constructor untuk menginisialisasi anggota dengan parameter tambahan
    public Anggota(int id, String nama, String jenisTransaksi, double jumlah, int cicilanPerBulan, double bungaPerPersen) {
        super(id, nama, jenisTransaksi, jumlah); // Memanggil constructor superclass
        this.cicilanPerBulan = cicilanPerBulan;
        this.bungaPerPersen = bungaPerPersen;
    }

    // Method untuk menghitung bunga per bulan
    public double hitungBunga() {
        return jumlah * (bungaPerPersen / 100) / 12; // Perhitungan matematika bunga bulanan
    }

    // Method untuk menghitung total pinjaman
    public double hitungTotalPinjaman() {
        return Math.round(jumlah + (hitungBunga() * cicilanPerBulan)); // Total pinjaman termasuk bunga (dibulatkan)
    }

    // Method untuk menghitung angsuran per bulan
    public double hitungAngsuranPerBulan() {
        return Math.round(hitungTotalPinjaman() / cicilanPerBulan); // Angsuran bulanan (dibulatkan)
    }

    @Override
    public void tampilkanTransaksi() {
        DecimalFormat decimalFormat = new DecimalFormat("#0"); // Format angka tanpa koma

        // Percabangan untuk membedakan jenis transaksi
        if (jenisTransaksi.equals("Peminjaman")) {
            System.out.println("ID Transaksi: " + id + ", Nama: " + nama + ", Jenis Transaksi: " + jenisTransaksi +
                    ", Jumlah: Rp " + decimalFormat.format(jumlah) + ", Cicilan per Bulan: " + cicilanPerBulan + " bulan, Bunga per Persen: " + bungaPerPersen + "%" +
                    ", Total Pinjaman: Rp " + decimalFormat.format(hitungTotalPinjaman()) +
                    ", Angsuran/Bulan: Rp " + decimalFormat.format(hitungAngsuranPerBulan()) +
                    ", Tanggal Transaksi: " + tanggalTransaksi);
        } else {
            System.out.println("ID Transaksi: " + id + ", Nama: " + nama + ", Jenis Transaksi: " + jenisTransaksi + 
                    ", Jumlah: Rp " + decimalFormat.format(jumlah) + ", Tanggal Transaksi: " + tanggalTransaksi);
        }
    }
}

// Main class untuk aplikasi Koperasi
public class KoperasiCRUD {
    static Scanner scanner = new Scanner(System.in); // Input dari user
    static List<TransaksiUmum> transaksiList = new ArrayList<>(); // Collection Framework (List)

    public static void main(String[] args) {
        int pilihan;
        do {
            System.out.println("\n=== KOPERASI SIMPAN PINJAM ===");
            System.out.println("1. Tambah Transaksi");
            System.out.println("2. Lihat Semua Transaksi");
            System.out.println("3. Ubah Transaksi");
            System.out.println("4. Hapus Transaksi");
            System.out.println("5. Keluar");
            System.out.print("Pilih menu (1-5): ");
            pilihan = scanner.nextInt();
            scanner.nextLine(); // Membersihkan buffer input

            // Percabangan untuk memilih menu
            switch (pilihan) {
                case 1 -> tambahTransaksi();
                case 2 -> lihatTransaksi();
                case 3 -> ubahTransaksi();
                case 4 -> hapusTransaksi();
                case 5 -> System.out.println("Program selesai.");
                default -> System.out.println("Pilihan tidak valid! Silakan pilih antara 1-5.");
            }
        } while (pilihan != 5); // Perulangan sampai user memilih keluar
    }

    static void tambahTransaksi() {
        int id;
        String nama;
        String jenisTransaksi;
        double jumlah;
        int cicilanPerBulan = 0;
        double bungaPerPersen = 0;
    
        // Input ID transaksi dengan validasi
        while (true) {
            System.out.print("\nMasukkan ID transaksi: ");
            id = scanner.nextInt();
            if (id > 0) {
                break;
            } else {
                System.out.println("ID transaksi harus lebih besar dari 0!");
            }
        }
    
        scanner.nextLine(); // Membersihkan buffer input
    
        // Input nama anggota dengan validasi
        while (true) {
            System.out.print("Masukkan nama anggota: ");
            nama = scanner.nextLine();
            if (!nama.isEmpty()) {
                break;
            } else {
                System.out.println("Nama tidak boleh kosong!");
            }
        }
    
        // Input jenis transaksi dengan validasi
        while (true) {
            System.out.print("Jenis transaksi (Simpanan/Peminjaman): ");
            jenisTransaksi = scanner.nextLine();
            if (jenisTransaksi.equalsIgnoreCase("Simpanan") || jenisTransaksi.equalsIgnoreCase("Peminjaman")) {
                break;
            } else {
                System.out.println("Jenis transaksi tidak valid! Harus 'Simpanan' atau 'Peminjaman'.");
            }
        }
    
        // Input jumlah transaksi dengan validasi
        while (true) {
            System.out.print("Masukkan jumlah: ");
            try {
                jumlah = scanner.nextDouble();
                if (jumlah <= 0) {
                    throw new IllegalArgumentException("Jumlah harus lebih besar dari 0!");
                } else if (jenisTransaksi.equalsIgnoreCase("Peminjaman") && jumlah < 100000) {
                    throw new IllegalArgumentException("Jumlah pinjaman tidak boleh kurang dari 100.000.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Kesalahan: " + e.getMessage()); // Exception Handling
                scanner.nextLine(); // Clear buffer setelah input salah
            }
        }
    
        // Input cicilan per bulan dan bunga jika jenis transaksi adalah Peminjaman
        if (jenisTransaksi.equalsIgnoreCase("Peminjaman")) {
            while (true) {
                try {
                    System.out.print("Masukkan cicilan per bulan: ");
                    cicilanPerBulan = scanner.nextInt();
                    if (cicilanPerBulan <= 0) {
                        throw new IllegalArgumentException("Cicilan per bulan harus lebih dari 0 bulan.");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Kesalahan: " + e.getMessage());
                }
            }
    
            while (true) {
                try {
                    System.out.print("Masukkan bunga per persen (%): ");
                    bungaPerPersen = scanner.nextDouble();
                    if (bungaPerPersen <= 0) {
                        throw new IllegalArgumentException("Bunga per persen harus diisi dan lebih besar dari 0.");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Kesalahan: " + e.getMessage());
                }
            }
    
            Anggota anggota = new Anggota(id, nama, jenisTransaksi, jumlah, cicilanPerBulan, bungaPerPersen);
            System.out.println("\nDetail Perhitungan Peminjaman:");
            System.out.println("Total Pinjaman (termasuk bunga): Rp " + anggota.hitungTotalPinjaman());
            System.out.println("Angsuran per Bulan: Rp " + anggota.hitungAngsuranPerBulan());
        }
    
        // Jika jenis transaksi adalah Simpanan, angsuran per bulan dan total pinjaman diset menjadi null atau tidak dihitung
        if (jenisTransaksi.equalsIgnoreCase("Simpanan")) {
            cicilanPerBulan = 0; // Set cicilan per bulan menjadi 0
            bungaPerPersen = 0; // Set bunga menjadi 0
        }
    
        // Membuat objek transaksi
        TransaksiUmum transaksi = new Anggota(id, nama, jenisTransaksi, jumlah, cicilanPerBulan, bungaPerPersen);
        transaksiList.add(transaksi); // Menambahkan transaksi ke dalam list
    
        // Menyimpan transaksi ke dalam database
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO transaksi (id, nama, jenis_transaksi, jumlah, cicilan_per_bulan, bunga_per_persen, tanggal_transaksi, total_pinjaman, angsuran_per_bulan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, nama);
            statement.setString(3, jenisTransaksi);
            statement.setDouble(4, jumlah);
            statement.setInt(5, cicilanPerBulan);
            statement.setDouble(6, bungaPerPersen);
            statement.setString(7, transaksi.tanggalTransaksi);
    
            // Jika jenis transaksi adalah Simpanan, total pinjaman dan angsuran per bulan diset NULL
            if (jenisTransaksi.equalsIgnoreCase("Simpanan")) {
                statement.setNull(8, Types.DOUBLE);
                statement.setNull(9, Types.DOUBLE);
            } else {
                statement.setDouble(8, ((Anggota) transaksi).hitungTotalPinjaman());
                statement.setDouble(9, ((Anggota) transaksi).hitungAngsuranPerBulan());
            }
            
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction to database: " + e.getMessage());
        }
    
        System.out.println("Transaksi berhasil ditambahkan!");
    }

    static void lihatTransaksi() {
        System.out.println("\n--- Daftar Transaksi ---");
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM transaksi";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) { // Perulangan untuk menampilkan semua data transaksi
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                String jenisTransaksi = rs.getString("jenis_transaksi");
                double jumlah = rs.getDouble("jumlah");
                int cicilanPerBulan = rs.getInt("cicilan_per_bulan");
                double bungaPerPersen = rs.getDouble("bunga_per_persen");
                String tanggalTransaksi = rs.getString("tanggal_transaksi");
                double totalPinjaman = rs.getDouble("total_pinjaman");
                double angsuranPerBulan = rs.getDouble("angsuran_per_bulan");

                System.out.println("ID Transaksi: " + id + ", Nama: " + nama + ", Jenis Transaksi: " + jenisTransaksi + ", Jumlah: Rp " + jumlah + ", Cicilan per Bulan: " + cicilanPerBulan + ", Bunga: " + bungaPerPersen + "%" + ", Total Pinjaman: Rp " + totalPinjaman + ", Angsuran/Bulan: Rp " + angsuranPerBulan + ", Tanggal Transaksi: " + tanggalTransaksi);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transactions: " + e.getMessage());
        }
    }

    static void ubahTransaksi() {
        System.out.println("\n--- Ubah Transaksi ---");
        System.out.print("Masukkan ID transaksi yang akan diubah: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membersihkan buffer input
    
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM transaksi WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
    
            if (rs.next()) {
                System.out.println("Transaksi ditemukan!");
                System.out.print("Masukkan nama baru: ");
                String namaBaru = scanner.nextLine();
    
                System.out.print("Masukkan jenis transaksi baru (Simpanan/Peminjaman): ");
                String jenisTransaksiBaru = scanner.nextLine();
    
                System.out.print("Masukkan jumlah baru: ");
                double jumlahBaru = scanner.nextDouble();
                scanner.nextLine(); // Membersihkan buffer input

                int cicilanPerBulanBaru = 0;
                double bungaPerPersenBaru = 0;
    
                if (jenisTransaksiBaru.equalsIgnoreCase("Peminjaman")) {
                    System.out.print("Masukkan cicilan per bulan baru: ");
                    cicilanPerBulanBaru = scanner.nextInt();
                    scanner.nextLine();
    
                    System.out.print("Masukkan bunga per persen baru (%): ");
                    bungaPerPersenBaru = scanner.nextDouble();
                    scanner.nextLine();
                }
    
                String updateSQL;
                if (jenisTransaksiBaru.equalsIgnoreCase("Simpanan")) {
                    updateSQL = "UPDATE transaksi SET nama = ?, jenis_transaksi = ?, jumlah = ?, cicilan_per_bulan = NULL, bunga_per_persen = NULL, total_pinjaman = NULL, angsuran_per_bulan = NULL WHERE id = ?";
                } else {
                    updateSQL = "UPDATE transaksi SET nama = ?, jenis_transaksi = ?, jumlah = ?, cicilan_per_bulan = ?, bunga_per_persen = ?, total_pinjaman = ?, angsuran_per_bulan = ? WHERE id = ?";
                }
    
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
                    updateStatement.setString(1, namaBaru);
                    updateStatement.setString(2, jenisTransaksiBaru);
                    updateStatement.setDouble(3, jumlahBaru);
    
                    if (jenisTransaksiBaru.equalsIgnoreCase("Simpanan")) {
                        updateStatement.setInt(4, id); // ID parameter
                    } else {
                        double totalPinjamanBaru = jumlahBaru + (jumlahBaru * (bungaPerPersenBaru / 100) / 12) * cicilanPerBulanBaru;
                        double angsuranPerBulanBaru = totalPinjamanBaru / cicilanPerBulanBaru;
    
                        updateStatement.setInt(4, cicilanPerBulanBaru);
                        updateStatement.setDouble(5, bungaPerPersenBaru);
                        updateStatement.setDouble(6, totalPinjamanBaru);
                        updateStatement.setDouble(7, angsuranPerBulanBaru);
                        updateStatement.setInt(8, id); // ID parameter
                    }
    
                    updateStatement.executeUpdate();
                    System.out.println("Transaksi berhasil diperbarui!");
                }
            } else {
                System.out.println("Transaksi dengan ID " + id + " tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating transaction: " + e.getMessage());
        }
    }
    

    static void hapusTransaksi() {
        System.out.println("\n--- Hapus Transaksi ---");
        System.out.print("Masukkan ID transaksi yang akan dihapus: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membersihkan buffer input

        try (Connection connection = DatabaseConnection.getConnection()) {
            String deleteSQL = "DELETE FROM transaksi WHERE id = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL)) {
                deleteStatement.setInt(1, id);
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaksi berhasil dihapus!");
                } else {
                    System.out.println("Transaksi dengan ID " + id + " tidak ditemukan.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
        }
    }
}
