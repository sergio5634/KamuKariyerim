if (kullaniciEhliyet.equals("Yok")) {
                    ehliyetUygun = false;
                } else if (ilan.getEhliyetSartı().equals("C ve Üzeri") && 
                          !kullaniciEhliyet.equals("C ve Üzeri")) {
                    ehliyetUygun = false;
                } else {
                    uyumPuani += 20;
                }
            }
            
            // İkamet şartı kontrolü (%10)
            boolean ikametUygun = true;
            if (ilan.getIkametSarti() != null && !ilan.getIkametSarti().isEmpty()) {
                toplamKriter += 10;
                // İlanın ikamet şartı kullanıcının şehirleri arasında mı?
                boolean ikametEslesiyor = false;
                for (String sehir : kullaniciSehirler) {
                    if (ilan.getIkametSarti().contains(sehir)) {
                        ikametEslesiyor = true;
                        break;
                    }
                }
                if (ikametEslesiyor) {
                    uyumPuani += 10;
                } else {
                    ikametUygun = false; // İkamet şartı var ama uymuyor
                }
            }
            
            int uyumYuzdesi = toplamKriter > 0 ? (uyumPuani * 100) / toplamKriter : 0;
            ilan.setUyumYuzdesi(uyumYuzdesi);

            // Tüm şartlar uyuyorsa listeye ekle
            if (bolumEslesiyor && kpssUygun && ehliyetUygun && ikametUygun) {
                filtrelenmisIlanlar.add(ilan);
            }
        }

        Collections.sort(filtrelenmisIlanlar, (a, b) -> 
            Integer.compare(b.getUyumYuzdesi(), a.getUyumYuzdesi()));

        guncelleUI();
    }

    private void guncelleUI() {
        if (filtrelenmisIlanlar.isEmpty()) {
            recyclerIlanlar.setVisibility(View.GONE);
            tvBosDurum.setVisibility(View.VISIBLE);
        } else {
            recyclerIlanlar.setVisibility(View.VISIBLE);
            tvBosDurum.setVisibility(View.GONE);
            ilanAdapter.guncelleListe(filtrelenmisIlanlar);
        }
    }

    private void ilanlariSirala(int tip) {
        if (filtrelenmisIlanlar == null) return;

        switch (tip) {
            case 0: // Uyum
                Collections.sort(filtrelenmisIlanlar, 
                    (a, b) -> Integer.compare(b.getUyumYuzdesi(), a.getUyumYuzdesi()));
                break;
            case 1: // Yeni
                Collections.sort(filtrelenmisIlanlar, 
                    (a, b) -> Boolean.compare(b.isYeniIlan(), a.isYeniIlan()));
                break;
            case 2: // Tarih
                Collections.sort(filtrelenmisIlanlar, 
                    Comparator.comparing(Ilan::getSonBasvuruTarihi));
                break;
            case 3: // KPSS
                Collections.sort(filtrelenmisIlanlar, 
                    (a, b) -> Double.compare(b.getKpssMinimum(), a.getKpssMinimum()));
                break;
        }
        ilanAdapter.guncelleListe(filtrelenmisIlanlar);
    }

    @Override
    public void onDetayClick(Ilan ilan) {
        Intent intent = new Intent(this, IlanDetayActivity.class);
        intent.putExtra("ilan", ilan);
        startActivity(intent);
    }

    @Override
    public void onFavoriClick(Ilan ilan) {
        favorilereEkle(ilan);
    }

    private void favorilereEkle(Ilan ilan) {
        SharedPreferences prefs = getSharedPreferences("Favoriler", MODE_PRIVATE);
        prefs.edit().putString(ilan.getId(), ilan.getKurumAdi()).apply();
        Toast.makeText(this, "Favorilere eklendi", Toast.LENGTH_SHORT).show();
    }

    private void favorileriGoster() {
        Toast.makeText(this, "Favoriler ekranı yakında", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        profilBilgileriniYukle();
        ilanlariFiltrele();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
