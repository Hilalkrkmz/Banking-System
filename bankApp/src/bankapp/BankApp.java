package bankapp;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//hata yönetim kısmı
class BankaIstisnasi extends Exception {
    public BankaIstisnasi(String mesaj) { super(mesaj); }
}

class YetersizBakiyeException extends BankaIstisnasi {
    public YetersizBakiyeException(String m) { 
        super("İŞLEM REDDEDİLDİ: Yetersiz Bakiye! (" + m + ")"); }
}

//interface ile sadece ilgili sınıflara
interface Faizli { void faizIslet(); }

class Islem {
    String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    String tarih = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    String aciklama; double miktar; double bakiyeSonrasi;

    public Islem(String aciklama, double miktar, double bakiyeSonrasi) {
        this.aciklama = aciklama; this.miktar = miktar; this.bakiyeSonrasi = bakiyeSonrasi;
    }

    @Override//object sınıfından geliyor(tostrıng equlas)
    public String toString() {
        return String.format("[%s] %s | %-20s | %10.2f TL | Bakiye: %10.2f TL", id, tarih, aciklama, miktar, bakiyeSonrasi);
    }
}



class KartYonetimi {
    public static void kartMenu(Musteri m) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- KARTLARIM ---");
        System.out.println("1. Banka Kartı Detayları\n2. Kredi Kartı Detayları\n0. Geri");
        int s = sc.nextInt();
        if (s == 1) {
            System.out.println("\n[ BANKA KARTI ]\nBağlı Hesap: " + m.vadesiz.hesapNo + "\nBakiye: " + m.vadesiz.bakiye + " TL");
        } else if (s == 2) {
            System.out.println("\n[ KREDİ KARTI ]\nNo: " + m.kk.kartNo + "\nLimit: " + m.kk.limit + " TL\nBorç: " + m.kk.borc + " TL\nKullanılabilir: " + (m.kk.limit - m.kk.borc) + " TL");
        }
    }
}

class DovizOfisi {
    static double USD_ALIS = 32.50, USD_SATIS = 33.10, EUR_ALIS = 35.20, EUR_SATIS = 35.90;
    public static void dovizMenu(Vadesiz v) throws YetersizBakiyeException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- DÖVİZ İŞLEMLERİ ---");
        System.out.println("1. Döviz Al (TL -> Döviz)\n2. Döviz Sat (Döviz -> TL)\n3. Kur Hesapla");
        int s = sc.nextInt();
        if (s == 1 || s == 2) {
            System.out.print("Tür (USD/EUR): "); String tur = sc.next().toUpperCase();
            System.out.print("Miktar: "); double miktar = sc.nextDouble();
            double kur = tur.equals("USD") ? (s == 1 ? USD_SATIS : USD_ALIS) : (s == 1 ? EUR_SATIS : EUR_ALIS);
            v.hareketEkle(tur + (s == 1 ? " Alımı" : " Satışı"), (s == 1 ? -1 : 1) * (miktar * kur));
            System.out.println("İşlem Başarılı.");
        } else if (s == 3) {
            System.out.print("Miktar (TL): "); double tl = sc.nextDouble();
            System.out.println("USD: " + String.format("%.2f", tl/USD_SATIS) + " | EUR: " + String.format("%.2f", tl/EUR_SATIS));
        }
    }
}

class BorsaYonetimi {
    static Map<String, Double> hisseler = Map.of("THYAO", 285.0, "ASELS", 62.0, "SISE", 48.0);
    public static void borsaMenu(Yatirim y) throws YetersizBakiyeException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- BORSA PORTFÖY ---");
        hisseler.forEach((k, v) -> System.out.println(k + ": " + v + " TL"));
        System.out.print("Hisse Kodu: "); String kod = sc.next().toUpperCase();
        System.out.print("Adet: "); int adet = sc.nextInt();
        if (hisseler.containsKey(kod)) {
            y.hareketEkle(kod + " Alımı", -(hisseler.get(kod) * adet));
            System.out.println("Alım Gerçekleşti.");
        }
    }
}

class OdemeMerkezi {
    public static void odemeYap(Musteri m) throws YetersizBakiyeException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- ÖDEME KAYNAĞI ---");
        System.out.println("1. Vadesiz Hesap\n2. Kredi Kartı");
        int kaynak = sc.nextInt();

        System.out.println("\n--- ÖDEMELER ---");
        System.out.println("1. Fatura Öde\n2. Kart Borç Öde\n3. Bağış Yap");
        int s = sc.nextInt();
        
        System.out.print("Tutar: "); double tutar = sc.nextDouble();
        String aciklama = (s == 1) ? "Fatura" : (s == 2) ? "Kart Borç" : "Bağış";

        if (kaynak == 1) {
            m.vadesiz.hareketEkle(aciklama, -tutar);
            if(s == 2) m.kk.borc -= tutar;
        } else {
            if (m.kk.borc + tutar > m.kk.limit) throw new YetersizBakiyeException("KK Limit");
            m.kk.borc += tutar;
            System.out.println("İşlem Kredi Kartı borcuna yansıtıldı.");
        }
    }
}

class BasvuruSistemi {
    public static void basvuruAl() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- BAŞVURU MERKEZİ ---");
        System.out.println("1. Kredi Kartı\n2. Banka Kartı\n3. Sigorta");
        int s = sc.nextInt();
        System.out.println("Başvurunuz onay sürecine alınmıştır.");
    }
}

// hesap ve musteri kısmı
abstract class Hesap {//abstract ile temel kurallar
    String hesapNo; double bakiye; 
    List<Islem> hareketler = new ArrayList<>();
    public Hesap(String no, double b) { this.hesapNo = no; this.bakiye = b; }
    public void hareketEkle(String desc, double miktar) throws YetersizBakiyeException {
        if (this.bakiye + miktar < 0) throw new YetersizBakiyeException(hesapNo);
        this.bakiye += miktar; hareketler.add(new Islem(desc, miktar, this.bakiye));
    }
}

class Vadesiz extends Hesap { 
    public Vadesiz(String no, double b) { super(no, b); } }

class Vadeli extends Hesap implements Faizli { 
    public Vadeli(String no, double b) { 
        super(no, b); }
    public void faizIslet() { 
        bakiye += bakiye * 0.45 / 12; hareketler.add(new Islem("Faiz", bakiye*0.45/12, bakiye)); }
}

class Yatirim extends Hesap {
    public Yatirim(String no, double b) { 
        super(no, b); } }

class AltinHesabi extends Hesap { 
    public AltinHesabi(String no, double g) { 
        super(no, g); } 
    public double getTL() { 
        return bakiye * 3150; } }

class KrediKarti { 
    String kartNo = "5555-6666-7777-8888";
    double limit = 20000, borc = 0; 
    int krediPuani = 1200;
    List<Islem> harcamalar = new ArrayList<>(); 

    public void harcamaYap(String aciklama, double miktar) throws YetersizBakiyeException {
        if (borc + miktar > limit) throw new YetersizBakiyeException("KK Limit Yetersiz");
        borc += miktar;
        
        harcamalar.add(new Islem(aciklama, miktar, limit - borc));
    }
}

//musteri bilgileri
class Musteri {//encapsulation ->veriler sınıfın içinde
    String ad = "Hilal ", sifre = "1234";
    Vadesiz vadesiz = new Vadesiz("TR-101", 5000); 
    Vadeli vadeli = new Vadeli("TR-201", 10000);
    Yatirim yatirim = new Yatirim("TR-102", 2000);
    AltinHesabi altin = new AltinHesabi("TR-GLD-401", 10);
    KrediKarti kk = new KrediKarti();
}

// main kısmı
public class BankApp {
    static Scanner sc = new Scanner(System.in); 
    static Musteri m = new Musteri();

    public static void main(String[] args) {
        System.out.println("------ BİHTER HANIM DİJİTAL BANKACILIK GİRİŞ ------- ");
        System.out.print("Kullanıcı Adı: ");
        String girilenAd = sc.nextLine();
        System.out.print("Şifre: ");
        String girilenSifre = sc.nextLine();
        
        if (girilenAd.trim().equalsIgnoreCase(m.ad.trim()) && girilenSifre.equals(m.sifre)) {
        System.out.println("\nGiriş Başarılı! Hoş geldiniz, " + m.ad);
    } else {
        System.out.println("HATA: Kullanıcı adı veya şifre yanlış. Uygulama kapatılıyor.");
        return; // Programı bitirir
    }
        
        while (true) {
            try {
                System.out.println("\n------ BİHTER HANIM DİJİTAL BANKACILIK ------- ");
                System.out.println("1. Hesaplarım\n2. Para Transferi\n3. Kartlarım\n4. Ödemeler\n5. Profil\n6. Borsa\n7. Döviz\n8. Başvuru\n9. Tüm İşlem Geçmişi\n0. Çıkış");
                System.out.print("Seçim: "); 
                int secim = sc.nextInt();
                if (secim == 0) break;

                switch (secim) {
                    case 1 -> hesapMenusu();
                    case 2 -> transferMenusu();
                    case 3 -> KartYonetimi.kartMenu(m);
                    case 4 -> OdemeMerkezi.odemeYap(m);
                    case 5 -> profilMenusu();
                    case 6 -> BorsaYonetimi.borsaMenu(m.yatirim);
                    case 7 -> DovizOfisi.dovizMenu(m.vadesiz);
                    case 8 -> BasvuruSistemi.basvuruAl();
                    case 9 -> tumIslemleriGoster();
                }
            } catch (Exception e) { System.out.println("Hata: " + e.getMessage()); sc.nextLine(); }
        }
    }

    static void tumIslemleriGoster() {
        System.out.println("\n--- TÜM HESAPLARIN HAREKETLERİ ---");
        System.out.println("[VADESİZ HESAP]"); 
        m.vadesiz.hareketler.forEach(System.out::println);//foreach listenin basından sonuna kdr geziyor
        System.out.println("[VADELİ HESAP]");
        m.vadeli.hareketler.forEach(System.out::println);
        System.out.println("[YATIRIM HESABI]"); 
        m.yatirim.hareketler.forEach(System.out::println);
        System.out.println("[ALTIN HESABI]");
        m.altin.hareketler.forEach(System.out::println);
    }

    static void yeniHesapAcmaMenusu() {
    System.out.println("\n--- YENİ HESAP AÇILIŞI ---");
    System.out.println("Hangi tür hesap açmak istiyorsunuz?");
    System.out.println("1. Vadesiz\n2. Vadeli\n3. Yatırım\n4. Altın");
    int secim = sc.nextInt();
    
    System.out.print("Başlangıç Bakiyesi/Miktarı Giriniz: ");
    double baslangic = sc.nextDouble();
    
    String yeniNo = "TR-" + (int)(Math.random() * 900 + 100);

    switch (secim) {
        case 1 -> {
            m.vadesiz = new Vadesiz(yeniNo, baslangic);
            System.out.println("Yeni Vadesiz Hesabınız Açıldı: " + yeniNo);
        }
        case 2 -> {
            m.vadeli = new Vadeli(yeniNo, baslangic);
            System.out.println("Yeni Vadeli Hesabınız Açıldı: " + yeniNo);
        }
        case 3 -> {
            m.yatirim = new Yatirim(yeniNo, baslangic);
            System.out.println("Yeni Yatırım Hesabınız Açıldı: " + yeniNo);
        }
        case 4 -> {
            m.altin = new AltinHesabi(yeniNo, baslangic);
            System.out.println("Yeni Altın Hesabınız Açıldı: " + yeniNo);
        }
        default -> System.out.println("Geçersiz seçim.");
    }
}
    
    static void hesapMenusu() {
    System.out.println("\n--- HESAPLARIM ---");
    System.out.println("1. Vadesiz\n2. Vadeli\n3. Yatırım\n4. Altın\n5. Toplam Varlık\n6. Yeni Hesap Aç\n0. Geri");
    int s = sc.nextInt();
    
    if (s == 0) return;
    
    if (s == 5) {
        System.out.println("Toplam: " + (m.vadesiz.bakiye + m.vadeli.bakiye + m.yatirim.bakiye + m.altin.getTL()) + " TL");
    } else if (s == 6) {
        yeniHesapAcmaMenusu(); 
    } else {
        // Mevcut polymorphism mantığı
        Hesap h = (s==1) ? m.vadesiz : (s==2) ? m.vadeli : (s==3) ? m.yatirim : m.altin;
        System.out.println("Bakiye: " + h.bakiye + (s == 4 ? " Gram" : " TL")); 
        h.hareketler.forEach(System.out::println);

        if (s == 2) {
            System.out.print("\nBu hesaba faiz işletilsin mi? (1-Evet / 0-Hayır): ");
            if(sc.nextInt() == 1) {
                ((Vadeli)h).faizIslet();
                System.out.println("İşlem Başarılı. Yeni Bakiye: " + h.bakiye);
            }
        }
    }
}


    static void transferMenusu() throws YetersizBakiyeException {
        System.out.println("\n1. Virman\n2. EFT");
        int s = sc.nextInt();
        System.out.print("Tutar: "); 
        double t = sc.nextDouble();
        if (s == 1) { 
            m.vadesiz.hareketEkle("Virman", -t); 
            m.yatirim.hareketEkle("Vadesizden Gelen", t); }
        else { 
            System.out.print("IBAN: "); sc.next();
            m.vadesiz.hareketEkle("EFT Giden", -t); }
    }

    static void profilMenusu() {
        System.out.println("\n--- PROFİL ---\nAd: " + m.ad + "\nPuan: " + m.kk.krediPuani);
        System.out.print("Şifre Değiştir? (1-Evet)&(0-Hayır): ");
        if(sc.nextInt()==1){
            m.sifre = sc.next();}
    }
}