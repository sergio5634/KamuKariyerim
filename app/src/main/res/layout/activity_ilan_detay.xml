package com.kamukariyer.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class IlanDetayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilan_detay);

        Ilan ilan = (Ilan) getIntent().getSerializableExtra("ilan");
        if (ilan == null) {
            Toast.makeText(this, "İlan bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // View'ları bağla
        TextView tvKurum = findViewById(R.id.tvDetayKurum);
        TextView tvPozisyon = findViewById(R.id.tvDetayPozisyon);
        TextView tvBolum = findViewById(R.id.tvDetayBolum);
        TextView tvKadro = findViewById(R.id.tvDetayKadro);
        TextView tvSehir = findViewById(R.id.tvDetaySehir);
        TextView tvKpss = findViewById(R.id.tvDetayKpss);
        TextView tvYas = findViewById(R.id.tvDetayYas);
        TextView tvCinsiyet = findViewById(R.id.tvDetayCinsiyet);
        TextView tvEhliyet = findViewById(R.id.tvDetayEhliyet);
        TextView tvTecrube = findViewById(R.id.tvDetayTecrube);
        TextView tvIkamet = findViewById(R.id.tvDetayIkamet);
        TextView tvEngel = findViewById(R.id.tvDetayEngel);
        TextView tvGuvenlik = findViewById(R.id.tvDetayGuvenlik);
        TextView tvEldenEvrak = findViewById(R.id.tvDetayEldenEvrak);
        TextView tvAciklama = findViewById(R.id.tvDetayAciklama);
        TextView tvSonBasvuru = findViewById(R.id.tvDetaySonBasvuru);
        Button btnBasvur = findViewById(R.id.btnBasvur);
        Button btnGeri = findViewById(R.id.btnGeri);

        // Verileri doldur
        tvKurum.setText(ilan.getKurumAdi());
        tvPozisyon.setText(ilan.getPozisyon());
        tvBolum.setText("Bölüm: " + ilan.getBolum());
        tvKadro.setText("Kadro: " + ilan.getKadroTipi());
        tvSehir.setText("Şehir: " + ilan.getSehir());
        tvKpss.setText("KPSS Şartı: " + (ilan.getKpssMinimum() > 0 ? ilan.getKpssMinimum() + "+" : "Yok"));
        
        // Yaş şartı
        if (ilan.getYasSarti() != null && !ilan.getYasSarti().isEmpty()) {
            tvYas.setText("Yaş Şartı: " + ilan.getYasSarti());
            tvYas.setVisibility(View.VISIBLE);
        } else {
            tvYas.setVisibility(View.GONE);
        }
        
        // Cinsiyet şartı
        if (ilan.getCinsiyetSarti() != null && !ilan.getCinsiyetSarti().isEmpty() && 
            !ilan.getCinsiyetSarti().equalsIgnoreCase("Farketmez")) {
            tvCinsiyet.setText("Cinsiyet: " + ilan.getCinsiyetSarti());
            tvCinsiyet.setVisibility(View.VISIBLE);
        } else {
            tvCinsiyet.setVisibility(View.GONE);
        }
        
        tvEhliyet.setText("Ehliyet: " + ilan.getEhliyetSartı());
        tvTecrube.setText("Tecrübe: " + (ilan.getTecrubeSarti() != null ? ilan.getTecrubeSarti() : "Yok"));
        
        // İkamet şartı
        if (ilan.getIkametSarti() != null && !ilan.getIkametSarti().isEmpty()) {
            tvIkamet.setText("İkamet Şartı: " + ilan.getIkametSarti());
            tvIkamet.setVisibility(View.VISIBLE);
        } else {
            tvIkamet.setVisibility(View.GONE);
        }
        
        // Engel durumu şartı
        if (ilan.isEngelDurumuSarti()) {
            tvEngel.setText("Engel Durumu: Engelli adaylar öncelikli");
            tvEngel.setVisibility(View.VISIBLE);
        } else {
            tvEngel.setVisibility(View.GONE);
        }
        
        // Güvenlik kartı şartı
        if (ilan.isGuvenlikKartiSarti()) {
            tvGuvenlik.setText("Güvenlik Kartı: Gerekli");
            tvGuvenlik.setVisibility(View.VISIBLE);
        } else {
            tvGuvenlik.setVisibility(View.GONE);
        }
        
        // Elden evrak teslimi
        if (ilan.isEldenEvrak()) {
            tvEldenEvrak.setText("Elden Evrak Teslimi: Gerekli");
            tvEldenEvrak.setTextColor(0xFFD32F2F); // Kırmızı
            tvEldenEvrak.setVisibility(View.VISIBLE);
        } else {
            tvEldenEvrak.setVisibility(View.GONE);
        }
        
        tvAciklama.setText(ilan.getAciklama());
        tvSonBasvuru.setText("Son Başvuru: " + ilan.getSonBasvuruTarihi());

        // Butonlar
        btnBasvur.setOnClickListener(v -> {
            if (ilan.getBasvuruLinki() != null && !ilan.getBasvuruLinki().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ilan.getBasvuruLinki()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Başvuru linki bulunamadı", Toast.LENGTH_SHORT).show();
            }
        });

        btnGeri.setOnClickListener(v -> finish());
    }
}
