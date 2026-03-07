    private void ilanlariFiltrele() {
        if (tumIlanlar == null) return;
        
        filtrelenmisIlanlar = new ArrayList<>();
        
        for (Ilan ilan : tumIlanlar) {
            int uyumPuani = 0;
            int toplamKriter = 0;
            
            // BÖLÜM EŞLEŞMESİ - BASİTLEŞTİRİLMİŞ
            String ilanBolum = ilan.getBolum().toLowerCase();
            String kullaniciBolumLower = kullaniciBolum.toLowerCase();
            boolean bolumEslesiyor = false;

            // 1. Tam eşleşme (Hemşirelik = Hemşirelik)
            if (ilanBolum.equals(kullaniciBolumLower)) {
                bolumEslesiyor = true;
            }
            // 2. "Herhangi lisans sağlık bölümü" gibi grup şartları
            else if (ilanBolum.contains("herhangi") && ilanBolum.contains("lisans")) {
                // Kullanıcı bölümü ilan şartına uyuyor mu kontrol et
                if (bolumGrubuEslesiyor(ilanBolum, kullaniciBolumLower)) {
                    bolumEslesiyor = true;
                }
            }

            if (bolumEslesiyor) uyumPuani += 30;
            toplamKriter += 30;
            
            // Diğer şartlar (KPSS, yaş vb.) aynı kalıyor...
            boolean kpssUygun = ilan.getKpssMinimum() <= kullaniciKpss;
            if (kpssUygun) uyumPuani += 20;
            toplamKriter += 20;
            
            boolean yasUygun = true;
            if (ilan.getYasSarti() != null && !ilan.getYasSarti().isEmpty()) {
                toplamKriter += 15;
                yasUygun = yasKontrolu(ilan.getYasSarti(), kullaniciYas);
                if (yasUygun) uyumPuani += 15;
            }
            
            boolean cinsiyetUygun = true;
            if (ilan.getCinsiyetSarti() != null && !ilan.getCinsiyetSarti().equalsIgnoreCase("Farketmez")) {
                toplamKriter += 10;
                cinsiyetUygun = ilan.getCinsiyetSarti().equalsIgnoreCase(kullaniciCinsiyet);
                if (cinsiyetUygun) uyumPuani += 10;
            }
            
            boolean ehliyetUygun = true;
            if (!ilan.getEhliyetSartı().equals("Yok")) {
                toplamKriter += 10;
                if (kullaniciEhliyet.equals("Yok")) {
                    ehliyetUygun = false;
                } else if (ilan.getEhliyetSartı().equals("C ve Üzeri") && 
                          !kullaniciEhliyet.equals("C ve Üzeri")) {
                    ehliyetUygun = false;
                } else {
                    uyumPuani += 10;
                }
            }
            
            boolean ikametUygun = true;
            if (ilan.getIkametSarti() != null && !ilan.getIkametSarti().isEmpty()) {
                toplamKriter += 10;
                ikametUygun = ikametKontrolu(ilan.getIkametSarti());
                if (ikametUygun) uyumPuani += 10;
            }
            
            if (ilan.isEngelDurumuSarti() && kullaniciEngel) {
                uyumPuani += 5;
            }
            
            boolean guvenlikUygun = true;
            if (ilan.isGuvenlikKartiSarti() && !kullaniciGuvenlikKarti) {
                guvenlikUygun = false;
            }

            int uyumYuzdesi = toplamKriter > 0 ? (uyumPuani * 100) / toplamKriter : 0;
            ilan.setUyumYuzdesi(uyumYuzdesi);

            if (bolumEslesiyor && kpssUygun && yasUygun && cinsiyetUygun && 
                ehliyetUygun && ikametUygun && guvenlikUygun) {
                filtrelenmisIlanlar.add(ilan);
            }
        }

        guncelleIstatistik();
        Collections.sort(filtrelenmisIlanlar, (a, b) -> 
            Integer.compare(b.getUyumYuzdesi(), a.getUyumYuzdesi()));
        guncelleUI();
    }

    // YENİ: Bölüm grubu eşleştirme
    private boolean bolumGrubuEslesiyor(String ilanSarti, String kullaniciBolum) {
        // Sağlık bölümleri
        if (ilanSarti.contains("sağlık")) {
            String[] saglikBolumleri = {"hemşirelik", "tıp", "eczacılık", "fizyoterapi", 
                "beslenme", "diyetetik", "sosyal hizmet", "psikoloji", "biyokimya"};
            for (String bolum : saglikBolumleri) {
                if (kullaniciBolum.contains(bolum)) return true;
            }
        }
        // Mühendislik bölümleri
        else if (ilanSarti.contains("mühendislik")) {
            String[] muhendislikBolumleri = {"mühendisliği", "mühendislik"};
            for (String bolum : muhendislikBolumleri) {
                if (kullaniciBolum.contains(bolum)) return true;
            }
        }
        // Fen-Edebiyat bölümleri
        else if (ilanSarti.contains("fen") || ilanSarti.contains("edebiyat")) {
            String[] fenEdebiyatBolumleri = {"fizik", "kimya", "biyoloji", "matematik", 
                "tarih", "coğrafya", "felsefe", "sosyoloji", "psikoloji"};
            for (String bolum : fenEdebiyatBolumleri) {
                if (kullaniciBolum.contains(bolum)) return true;
            }
        }
        // İktisadi bölümler
        else if (ilanSarti.contains("iktisat") || ilanSarti.contains("işletme")) {
            String[] iktisadiBolumleri = {"işletme", "iktisat", "ekonomi", "maliye", 
                "muhasebe", "finans", "bankacılık"};
            for (String bolum : iktisadiBolumleri) {
                if (kullaniciBolum.contains(bolum)) return true;
            }
        }
        // Hukuk
        else if (ilanSarti.contains("hukuk")) {
            if (kullaniciBolum.contains("hukuk")) return true;
        }
        // Eğitim
        else if (ilanSarti.contains("eğitim")) {
            String[] egitimBolumleri = {"öğretmenliği", "eğitimi"};
            for (String bolum : egitimBolumleri) {
                if (kullaniciBolum.contains(bolum)) return true;
            }
        }
        // Genel "herhangi lisans bölümü" - tüm lisans mezunları
        else if (ilanSarti.contains("herhangi") && ilanSarti.contains("bölüm")) {
            return true;
        }
        
        return false;
    }
