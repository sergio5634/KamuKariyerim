private void profiliKaydet() {
        String bolum = autoCompleteBolum.getText().toString().trim();
        String kpssPuani = etKpssPuani.getText().toString().trim();
        
        // Ehliyet seçimi
        int selectedEhliyetId = rgEhliyet.getCheckedRadioButtonId();
        String ehliyet = "Yok";
        if (selectedEhliyetId == R.id.rbEhliyetB) ehliyet = "B";
        else if (selectedEhliyetId == R.id.rbEhliyetC) ehliyet = "C ve Üzeri";

        // Validasyon
        if (bolum.isEmpty()) {
            autoCompleteBolum.setError("Bölüm seçiniz");
            return;
        }
        if (kpssPuani.isEmpty()) {
            etKpssPuani.setError("KPSS puanınızı giriniz");
            return;
        }

        // Şehir ve ilçeleri kaydet
        List<String> seciliSehirler = new ArrayList<>();
        List<String> seciliIlceler = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            String sehir = spinnerSehirler[i].getSelectedItem().toString();
            if (!sehir.equals("Seçiniz")) {
                seciliSehirler.add(sehir);
                String ilce = spinnerIlceler[i].getSelectedItem().toString();
                seciliIlceler.add(ilce);
            }
        }

        // SharedPreferences'a kaydet
        SharedPreferences prefs = getSharedPreferences("KullaniciProfili", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("bolum", bolum);
        editor.putString("kpssPuani", kpssPuani);
        editor.putString("ehliyet", ehliyet);
        
        // Şehir ve ilçeleri kaydet
        editor.putString("sehir1", seciliSehirler.size() > 0 ? seciliSehirler.get(0) : "");
        editor.putString("ilce1", seciliIlceler.size() > 0 ? seciliIlceler.get(0) : "");
        editor.putString("sehir2", seciliSehirler.size() > 1 ? seciliSehirler.get(1) : "");
        editor.putString("ilce2", seciliIlceler.size() > 1 ? seciliIlceler.get(1) : "");
        editor.putString("sehir3", seciliSehirler.size() > 2 ? seciliSehirler.get(2) : "");
        editor.putString("ilce3", seciliIlceler.size() > 2 ? seciliIlceler.get(2) : "");
        
        editor.apply();

        // Bildirimlere abone ol
        if (!bolum.isEmpty()) {
            BildirimYoneticisi.bildirimlereAboneOl(this, bolum);
        }

        Toast.makeText(this, "Profil kaydedildi!", Toast.LENGTH_SHORT).show();
        
        // Ana ekrana geç
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadSavedData() {
        SharedPreferences prefs = getSharedPreferences("KullaniciProfili", MODE_PRIVATE);
        
        // Önceki verileri yükle (bölüm, KPSS, ehliyet)
        String kayitliBolum = prefs.getString("bolum", "");
        String kayitliKpss = prefs.getString("kpssPuani", "");
        String kayitliEhliyet = prefs.getString("ehliyet", "Yok");

        if (!kayitliBolum.isEmpty()) {
            autoCompleteBolum.setText(kayitliBolum);
        }
        if (!kayitliKpss.isEmpty()) {
            etKpssPuani.setText(kayitliKpss);
        }
        
        // Ehliyet radio button
        if (kayitliEhliyet.equals("B")) {
            rgEhliyet.check(R.id.rbEhliyetB);
        } else if (kayitliEhliyet.equals("C ve Üzeri")) {
            rgEhliyet.check(R.id.rbEhliyetC);
        }
        
        // Kayıtlı şehir ve ilçeleri yükle (opsiyonel)
        // Bu kısım karmaşık olduğu için şimdilik atlıyoruz
    }
}
