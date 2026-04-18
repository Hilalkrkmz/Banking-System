package bankapp;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BankaSwing extends JFrame {
    private Musteri m = new Musteri();
    private DefaultListModel<String> hareketModel = new DefaultListModel<>();
    private JLabel bakiyeEtiketi;

    public BankaSwing() {
        setTitle("Bihter Hanım Dijital Bankacılık");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //üst kısım
        JPanel ustPanel = new JPanel(new BorderLayout());
        ustPanel.setBackground(new Color(41, 128, 185));
        ustPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblBaslik = new JLabel("Hoş Geldin, " + m.ad);
        lblBaslik.setForeground(Color.WHITE);
        lblBaslik.setFont(new Font("Arial", Font.BOLD, 18));
        
        bakiyeEtiketi = new JLabel("Vadesiz Bakiye: " + m.vadesiz.bakiye + " TL");
        bakiyeEtiketi.setForeground(Color.WHITE);
        bakiyeEtiketi.setFont(new Font("Arial", Font.BOLD, 18));
        
        ustPanel.add(lblBaslik, BorderLayout.WEST);
        ustPanel.add(bakiyeEtiketi, BorderLayout.EAST);
        add(ustPanel, BorderLayout.NORTH);

        // menü kısmı
        JPanel solPanel = new JPanel(new GridLayout(14, 1, 5, 5)); // Satır sayısı 12
        solPanel.setBorder(BorderFactory.createTitledBorder("Ana Menü"));
        solPanel.setPreferredSize(new Dimension(220, 0));

        JButton btnVadesiz = new JButton("Vadesiz Hesap");
        JButton btnVadeli = new JButton("Vadeli Hesap");
        JButton btnFaizIslet = new JButton("Vadeyi İşlet (Faiz)"); 
        JButton btnKartlarim = new JButton("Kartlarım");
        JButton btnBasvuru = new JButton("Başvuru Merkezi");
        JButton btnYatirim = new JButton("Yatırım Hesabı");
        JButton btnAltin = new JButton("Altın Hesabı");
        JButton btnDoviz = new JButton("Döviz İşlemleri");
        JButton btnBorsa = new JButton("Borsa İşlemleri");
        JButton btnOdeme = new JButton("Ödemeler");
        JButton btnTransfer = new JButton("Para Transferi");
        JButton btnTumGecmis = new JButton("Tüm İşlem Geçmişi");
        JButton btnProfil = new JButton("Profil Ayarları");

        // Panale ekleme 
        solPanel.add(btnVadesiz); 
        solPanel.add(btnVadeli);
        solPanel.add(btnFaizIslet); 
        solPanel.add(btnKartlarim);
        solPanel.add(btnBasvuru);
        solPanel.add(new JSeparator());
        solPanel.add(btnYatirim); 
        solPanel.add(btnAltin);
        solPanel.add(btnDoviz); 
        solPanel.add(btnBorsa);
        solPanel.add(btnOdeme); 
        solPanel.add(btnTransfer);
        solPanel.add(btnTumGecmis);
        solPanel.add(btnProfil);
        add(solPanel, BorderLayout.WEST);

        //orta panel
        JList<String> liste = new JList<>(hareketModel);
        liste.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(liste);
        scroll.setBorder(BorderFactory.createTitledBorder("Hesap Özeti ve Hareketler"));
        add(scroll, BorderLayout.CENTER);

        //butonlar
        // Hesap Görüntüleme
        btnVadesiz.addActionListener(e -> listeGuncelle(m.vadesiz.hareketler, "Vadesiz"));
        btnVadeli.addActionListener(e -> listeGuncelle(m.vadeli.hareketler, "Vadeli"));
        btnYatirim.addActionListener(e -> listeGuncelle(m.yatirim.hareketler, "Yatırım"));
        btnAltin.addActionListener(e -> listeGuncelle(m.altin.hareketler, "Altın"));

        
        //kartların durumu
        btnKartlarim.addActionListener(e -> {
        String mesaj = "--- BANKA KARTIM ---\n" +
                       "Bağlı Hesap: " + m.vadesiz.hesapNo + "\n" +
                       "Bakiye: " + m.vadesiz.bakiye + " TL\n\n" +
                       "--- KREDİ KARTIM ---\n" +
                       "Kart No: " + m.kk.kartNo + "\n" +
                       "Limit: " + m.kk.limit + " TL\n" +
                       "Borç: " + m.kk.borc + " TL\n" +
                       "Kullanılabilir: " + (m.kk.limit - m.kk.borc) + " TL";
        
        JOptionPane.showMessageDialog(this, mesaj, "Kartlarım", JOptionPane.INFORMATION_MESSAGE);
    });
        
    // Başvuru İşlemleri
    btnBasvuru.addActionListener(e -> {
    String[] secenekler = {"Kredi Kartı", "Banka Kartı", "Sigorta", "Kredi"};
    int secim = JOptionPane.showOptionDialog(this, 
            "Lütfen başvuru yapmak istediğiniz ürünü seçiniz:", 
            "Başvuru Merkezi",
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.INFORMATION_MESSAGE, 
            null, secenekler, secenekler[0]);

    if (secim != -1) {
        String urun = secenekler[secim];
        JOptionPane.showMessageDialog(this, 
            urun + " başvurunuz alınmıştır.\nDeğerlendirme sonucu SMS ile iletilecektir.", 
            "Başvuru Başarılı", 
            JOptionPane.INFORMATION_MESSAGE);
    }
});
        
        // Faiz İşletme
        btnFaizIslet.addActionListener(e -> {
            m.vadeli.faizIslet(); // Senin sınıfındaki faizIslet() metodu çalışır
            JOptionPane.showMessageDialog(this, "Aylık faiz getirisi başarıyla eklendi!");
            listeGuncelle(m.vadeli.hareketler, "Vadeli");
        });

        // Döviz İşlemleri 
        btnDoviz.addActionListener(e -> {
            String[] secenekler = {"USD Al", "EUR Al", "Kur Hesapla"};
            int secim = JOptionPane.showOptionDialog(this, "Döviz Ofisi", "İşlem Seçin",
                    0, JOptionPane.QUESTION_MESSAGE, null, secenekler, secenekler[0]);
            
            if (secim == 2) {
                String tl = JOptionPane.showInputDialog("TL Miktarı:");
                if(tl != null && !tl.isEmpty()) {
                    double miktar = Double.parseDouble(tl);
                    JOptionPane.showMessageDialog(this, String.format("USD: %.2f | EUR: %.2f", miktar/33.10, miktar/35.90));
                }
            } else if (secim != -1) {
                String miktarStr = JOptionPane.showInputDialog("Almak istediğiniz döviz miktarı:");
                if(miktarStr != null && !miktarStr.isEmpty()) {
                    try {
                        double miktar = Double.parseDouble(miktarStr);
                        String tur = (secim == 0) ? "USD" : "EUR";
                        double kur = (secim == 0) ? 33.10 : 35.90;
                        m.vadesiz.hareketEkle(tur + " Alımı", -(miktar * kur));
                        JOptionPane.showMessageDialog(this, "İşlem Başarılı!");
                        listeGuncelle(m.vadesiz.hareketler, "Vadesiz");
                    } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
                }
            }
        });

        // Borsa İşlemleri 
        btnBorsa.addActionListener(e -> {
            String kod = JOptionPane.showInputDialog("Hisse Kodu (THYAO/ASELS/SISE):").toUpperCase();
            String adetStr = JOptionPane.showInputDialog("Adet:");
            if(kod != null && adetStr != null && !kod.isEmpty() && !adetStr.isEmpty()) {
                try {
                    int adet = Integer.parseInt(adetStr);
                    if (BorsaYonetimi.hisseler.containsKey(kod)) {
                        m.yatirim.hareketEkle(kod + " Alımı", -(BorsaYonetimi.hisseler.get(kod) * adet));
                        JOptionPane.showMessageDialog(this, "Hisse alımı gerçekleşti.");
                        listeGuncelle(m.yatirim.hareketler, "Yatırım");
                    } else { JOptionPane.showMessageDialog(this, "Hisse bulunamadı."); }
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

      
        // Ödemeler Butonu
        btnOdeme.addActionListener(e -> {
         String[] kaynaklar = {"Vadesiz Hesap", "Kredi Kartı"};
         int kaynak = JOptionPane.showOptionDialog(this, "Ödeme Kaynağı Seçin", "Kaynak",
            0, JOptionPane.QUESTION_MESSAGE, null, kaynaklar, kaynaklar[0]);
    
         String[] turler = {"Fatura", "KK Borç", "Bağış"};
         int tur = JOptionPane.showOptionDialog(this, "İşlem Türü", "Ödeme",
            0, JOptionPane.QUESTION_MESSAGE, null, turler, turler[0]);

         String tutarStr = JOptionPane.showInputDialog("Tutar:");
         if(tutarStr != null && !tutarStr.isEmpty()) {
         try {
            double tutar = Double.parseDouble(tutarStr);
            String aciklama = turler[tur];

            if (kaynak == 0) { // Vadesiz Hesap Seçildiyse
                m.vadesiz.hareketEkle(aciklama, -tutar);
                if(tur == 1) m.kk.borc -= tutar; // Eğer KK Borcu ödeniyorsa borcu düşür
                listeGuncelle(m.vadesiz.hareketler, "Vadesiz");
            } 
            else { // BURASI SENİN SORDUĞUN KISIM: Kredi Kartı Seçildiyse
                m.kk.harcamaYap(aciklama, tutar); // Harcamayı listeye kaydeden metod
                JOptionPane.showMessageDialog(this, "İşlem Kredi Kartı borcuna yansıtıldı.");
                // Görünümü güncellemek için KK hareketlerini listele
                listeGuncelle(m.kk.harcamalar, "Kredi Kartı");
            }
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE); 
        }
    }
});

        // Tüm İşlem Geçmişi 
        btnTumGecmis.addActionListener(e -> {
            hareketModel.clear();
            hareketModel.addElement("--- TÜM HESAP HAREKETLERİ ---");
            hareketModel.addElement("--- VADESİZ ---"); m.vadesiz.hareketler.forEach(i -> hareketModel.addElement(i.toString()));
            hareketModel.addElement("--- VADELİ ---"); m.vadeli.hareketler.forEach(i -> hareketModel.addElement(i.toString()));
            hareketModel.addElement("--- YATIRIM ---"); m.yatirim.hareketler.forEach(i -> hareketModel.addElement(i.toString()));
            hareketModel.addElement("--- ALTIN ---"); m.altin.hareketler.forEach(i -> hareketModel.addElement(i.toString()));
            hareketModel.addElement("--- KREDİ KARTI DURUMU ---");
            hareketModel.addElement("Kart No: " + m.kk.kartNo);
            hareketModel.addElement("Toplam Borç: " + m.kk.borc + " TL");
            hareketModel.addElement("Kullanılabilir Limit: " + (m.kk.limit - m.kk.borc) + " TL");
        });

        // Transfer 
        btnTransfer.addActionListener(e -> {
        String[] secenekler = {"Virman (Kendi Hesaplarım)", "Havale/EFT (Dışarıya)"};
        int secim = JOptionPane.showOptionDialog(this, 
            "İşlem türünü seçin:", "Para Transferi", 
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
            null, secenekler, secenekler[0]);

    if (secim == -1) return;

    try {
        String tStr = JOptionPane.showInputDialog(this, "Tutar Giriniz (TL):");
        if (tStr == null || tStr.isEmpty()) return;
        double tutar = Double.parseDouble(tStr);

        if (tutar > m.vadesiz.bakiye) {
            JOptionPane.showMessageDialog(this, "Yetersiz Bakiye! Mevcut: " + m.vadesiz.bakiye + " TL", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (secim == 0) { // VİRMAN
            String[] hedefHesaplar = {"Vadeli", "Yatırım", "Altın"};
            int hedef = JOptionPane.showOptionDialog(this, "Hedef hesabı seçin:", "Kendi Hesaplarım",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, hedefHesaplar, hedefHesaplar[0]);

            if (hedef != -1) {
                m.vadesiz.hareketEkle("Virman: " + hedefHesaplar[hedef], -tutar);
                if (hedef == 0) m.vadeli.hareketEkle("Vadesizden Gelen", tutar);
                else if (hedef == 1) m.yatirim.hareketEkle("Vadesizden Gelen", tutar);
                else if (hedef == 2) m.altin.hareketEkle("Vadesizden Gelen", tutar / 3150); 
                
                JOptionPane.showMessageDialog(this, "Virman başarıyla gerçekleşti.");
            }

        } else { // HAVALE/EFT
            String aliciBilgi = JOptionPane.showInputDialog(this, "Alıcı Bilgisi (IBAN veya İsim girin):");
            if (aliciBilgi != null && !aliciBilgi.trim().isEmpty()) {
                
                m.vadesiz.hareketEkle("EFT: " + aliciBilgi, -tutar);
                JOptionPane.showMessageDialog(this, "Transfer başarıyla yapıldı.");
            }
        }
      
        listeGuncelle(m.vadesiz.hareketler, "Vadesiz");

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Hata: Lütfen sadece sayısal bir tutar giriniz!");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "İşlem Reddedildi: " + ex.getMessage());
    }
});
        
       //profil
       btnProfil.addActionListener(e -> {
       String mesaj = "Ad: " + m.ad + 
                   "\nKredi Puanı: " + m.kk.krediPuani + 
                   "\nKart No: " + m.kk.kartNo +
                   "\n\nŞifrenizi güncellemek ister misiniz?";
    
    int secim = JOptionPane.showConfirmDialog(this, mesaj, "Profil ve Güvenlik", JOptionPane.YES_NO_OPTION);
    
    if (secim == JOptionPane.YES_OPTION) {
        String yeniSifre = JOptionPane.showInputDialog(this, "Yeni Şifrenizi Girin:");
        
        if (yeniSifre != null && !yeniSifre.trim().isEmpty()) {
            m.sifre = yeniSifre; //şifre değiştirme 
            JOptionPane.showMessageDialog(this, "Şifreniz başarıyla güncellendi!");
        } else {
            JOptionPane.showMessageDialog(this, "İşlem iptal edildi veya geçersiz şifre girdiniz.");
        }
    }
});
        
        // Başlangıçta vadesiz geçmişi
        listeGuncelle(m.vadesiz.hareketler, "Vadesiz");
    }

    private void listeGuncelle(List<Islem> list, String baslik) {
        hareketModel.clear();
        hareketModel.addElement(">>> " + baslik.toUpperCase() + " HESABI GÜNCEL DURUM <<<");
        
        // Bakiye+geçmiş kısmı
        double bakiye = 0;
        if(baslik.equals("Vadesiz")) bakiye = m.vadesiz.bakiye;
        else if(baslik.equals("Vadeli")) bakiye = m.vadeli.bakiye;
        else if(baslik.equals("Yatırım")) bakiye = m.yatirim.bakiye;
        else if(baslik.equals("Altın")) bakiye = m.altin.bakiye;

        hareketModel.addElement("GÜNCEL BAKİYE: " + bakiye + (baslik.equals("Altın") ? " Gram" : " TL"));
        hareketModel.addElement("---------------------------------------------");

        if (list.isEmpty()) {
            hareketModel.addElement("Henüz bir işlem hareketi bulunmuyor.");
        } else {
            for (int i = list.size() - 1; i >= 0; i--) {
                hareketModel.addElement(list.get(i).toString());
            }
        }
        
        bakiyeEtiketi.setText("Vadesiz Bakiye: " + m.vadesiz.bakiye + " TL");
    }

    public static void main(String[] args) {
    try { 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
    } catch (Exception e) {}

    SwingUtilities.invokeLater(() -> {
        // 1. Giriş Bilgilerini Al
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
            "Kullanıcı Adı:", usernameField,
            "Şifre:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Banka Giriş Paneli", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            
            // Musteri sınıfındaki varsayılan değerlerle (Hilal / 1234) kontrol et
            Musteri geciciMusteri = new Musteri(); 
            
            if (user.trim().equalsIgnoreCase(geciciMusteri.ad.trim()) && pass.equals(geciciMusteri.sifre)) {
                new BankaSwing().setVisible(true); // Giriş başarılıysa ana ekranı aç
            } else {
                JOptionPane.showMessageDialog(null, "Hatalı kullanıcı adı veya şifre!", "Giriş Reddedildi", JOptionPane.ERROR_MESSAGE);
                System.exit(0); // Programı kapat
            }
        } else {
            System.exit(0); // İptal edilirse kapat
        }
    });
}
}