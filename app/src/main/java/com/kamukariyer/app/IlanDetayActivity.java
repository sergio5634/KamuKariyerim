package com.kamukariyer.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class IlanDetayActivity extends AppCompatActivity {

    private TextView tvKurum, tvPozisyon, tvBolum, tvAciklama;
    private TextView tvEgitimSeviyesi, tvKpssSarti, tvArananBolumler;
    private TextView tvGorevYeri, tvYasSarti, tvCinsiyet, tvEhliyet, tvEkSartlar;
    private TextView tvBasvuruBaslangic, tvBasvuruBitis, tvKalanGun;
    private LinearLayout layoutYayinlanmaAdresi, layoutBasvuruLinki;
    private MaterialButton btnBasvuruyaGit, btnBasvurdum;
    
    private Ilan ilan;
    private SimpleDateFormat tarihFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilan_detay);

        ilan = (Ilan) getIntent().getSerializableExtra("ilan");
        if (ilan == null) {
            Toast.makeText(this, "İlan bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tarihFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("tr"));
        
        initViews();
        setupToolbar();
        bilgileriGoster();
    }

    private void initViews() {
        tvKurum = findViewById(R.id.tvKurum);
        tvPozisyon = findViewById(R.id.tvPozisyon);
        tvBolum = findViewById(R.id.tvBolum);
        tvAciklama = findViewById(R.id.tvAciklama);
        
        tvEgitimSeviyesi = findViewById(R.id.tvEgitimSeviyesi);
        tvKpssSarti = findViewById(R.id.tvKpssSarti);
        tvArananBolumler = findViewById(R.id.tvArananBolumler);
        tvGorevYeri = findViewById(R.id.tvGorevYeri);
        tvYasSarti = findViewById(R.id.tvYasSarti);
        tvCinsiyet = findViewById(R.id.tvCinsiyet);
        tvEhliyet = findViewById(R.id.tvEhliyet);
        tvEkSartlar = findViewById(R.id.tvEkSartlar);
        
        tvBasvuruBaslangic = findViewById(R.id.tvBasvuruBaslangic);
        tvBasvuruBitis = findViewById(R.id.tvBasvuruBitis);
        tvKalanGun = findViewById(R.id.tvKalanGunDetay);
        
        layoutYayinlanmaAdresi = findViewById(R.id.layoutYayinlanmaAdresi);
        layoutBasvuruLinki = findViewById(R.id.layoutBasvuruLinki);
        
        btnBasvuruyaGit = findViewById(R.id.btnBasvuruyaGit);
        btnBasvurdum = findViewById(R.id.btnBasvurdum);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("İlan Detayı");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bilgileriGoster() {
        // Başlık bilgileri
        tvKurum.setText(ilan.getKurumAdi());
        tvPozisyon.setText(ilan.getPozisyon());
        tvBolum.setText(ilan.getBolum());
        tvAciklama.setText(ilan.getAciklama() != null ? ilan.getAciklama() : "Açıklama bulunmuyor.");

        // Eğitim ve KPSS
        tvEgitimSeviyesi.setText("Lisans (4 Yıllık) Mezunu");
        tvKpssSarti.setText(ilan.getKpssMinimum() > 0 ? 
            "Minimum " + ilan.getKpssMinimum() + " Puan" : "KPSS Şartı Yok");
        
        // Bölüm
        tvArananBolumler.setText(ilan.getBolum());
        
        // Görev yeri
        String gorevYeri = ilan.getIkametSarti() != null && !ilan.getIkametSarti().isEmpty() ?
            ilan.getIkametSarti() : (ilan.getSehir() + " - İl Sınırlaması Yok");
        tvGorevYeri.setText(gorevYeri);
        
        // Yaş
        tvYasSarti.setText(ilan.getYasSarti() != null ? ilan.getYasSarti() + " Yaş Arası" : "Yaş Şartı Yok");
        
        // Cinsiyet
        tvCinsiyet.setText(ilan.getCinsiyetSarti() != null ? 
            ilan.getCinsiyetSarti() + " Adaylar Başvurabilir" : "Kadın ve Erkek Adaylar Başvurabilir");
        
        // Ehliyet
        tvEhliyet.setText(!ilan.getEhliyetSartı().equals("Yok") ? 
            ilan.getEhliyetSartı() + " Ehliyet Gerekli" : "Ehliyet Gerekmiyor");
        
        // Ek şartlar
        StringBuilder ekSartlar = new StringBuilder();
        if (ilan.isEngelDurumuSarti()) ekSartlar.append("Engel Durumu Şartı, ");
        if (ilan.isGuvenlikKartiSarti()) ekSartlar.append("Güvenlik Kartı Şartı, ");
        if (ilan.getTecrubeSarti() != null) ekSartlar.append(ilan.getTecrubeSarti()).append(", ");
        if (ilan.isEldenEvrak()) ekSartlar.append("Elden Evrak Teslimi (").append(ilan.getEvrakTeslimBilgisi()).append("), ");
        
        tvEkSartlar.setText(ekSartlar.length() > 0 ? 
            ekSartlar.substring(0, ekSartlar.length() - 2) : "Ek şart bulunmuyor.");

        // Tarihler
        if (ilan.getBasvuruBaslangicTarihi() != null) {
            tvBasvuruBaslangic.setText(tarihFormat.format(ilan.getBasvuruBaslangicTarihi()));
        } else {
            tvBasvuruBaslangic.setText("Belirtilmemiş");
        }

        if (ilan.getSonBasvuruTarihi() != null) {
            tvBasvuruBitis.setText(tarihFormat.format(ilan.getSonBasvuruTarihi()));
        } else {
            tvBasvuruBitis.setText("Belirtilmemiş");
        }

        // Kalan gün
        tvKalanGun.setText(ilan.getKalanGunText());
        tvKalanGun.setTextColor(getResources().getColor(ilan.getKalanGunRengi()));

        // Linkler
        if (ilan.getYayinlanmaAdresi() != null && !ilan.getYayinlanmaAdresi().isEmpty()) {
            layoutYayinlanmaAdresi.setVisibility(View.VISIBLE);
            layoutYayinlanmaAdresi.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ilan.getYayinlanmaAdresi()));
                startActivity(intent);
            });
        } else {
            layoutYayinlanmaAdresi.setVisibility(View.GONE);
        }

        if (ilan.getBasvuruLinki() != null && !ilan.getBasvuruLinki().isEmpty()) {
            layoutBasvuruLinki.setVisibility(View.VISIBLE);
            btnBasvuruyaGit.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ilan.getBasvuruLinki()));
                startActivity(intent);
            });
        } else {
            layoutBasvuruLinki.setVisibility(View.GONE);
            btnBasvuruyaGit.setEnabled(false);
            btnBasvuruyaGit.setText("Başvuru Linki Yok");
        }

        btnBasvurdum.setOnClickListener(v -> {
            // Başvuruldu olarak işaretle
            Toast.makeText(this, "Başvurunuz kaydedildi!", Toast.LENGTH_SHORT).show();
            btnBasvurdum.setEnabled(false);
            btnBasvurdum.setText("Başvuruldu ✓");
        });
    }
}
