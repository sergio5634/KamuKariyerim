package com.kamukariyer.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity 
    implements IlanAdapter.OnIlanClickListener, FirebaseFirestoreHelper.IlanListener {

    private RecyclerView recyclerIlanlar;
    private IlanAdapter ilanAdapter;
    private TextView tvKullaniciBolum, tvKpssBilgi, tvEhliyetBilgi, tvBosDurum, tvIstatistik;
    private Spinner spinnerSiralama;
    private MaterialButton btnFiltrele;
    private BottomNavigationView bottomNavigation;
    private SwipeRefreshLayout swipeRefresh;

    private String kullaniciBolum;
    private double kullaniciKpss;
    private int kullaniciYas;
    private String kullaniciEhliyet;
    private String kullaniciCinsiyet;
    private boolean kullaniciEngel;
    private boolean kullaniciGuvenlikKarti;
    private List<String> kullaniciSehirler;
    private List<Ilan> tumIlanlar;
    private List<Ilan> filtrelenmisIlanlar;
    
    private FirebaseFirestoreHelper firestoreHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        profilBilgileriniYukle();
        setupRecyclerView();
        setupListeners();
        
        firestoreHelper = new FirebaseFirestoreHelper(this);
        executorService = Executors.newSingleThreadExecutor();
        
        ilanlariYukle();
        
        if (!kullaniciBolum.isEmpty()) {
            firestoreHelper.yeniIlanlariDinle(kullaniciBolum);
        }
    }

    private void initViews() {
        recyclerIlanlar = findViewById(R.id.recyclerIlanlar);
        tvKullaniciBolum = findViewById(R.id.tvKullaniciBolum);
        tvKpssBilgi = findViewById(R.id.tvKpssBilgi);
        tvEhliyetBilgi = findViewById(R.id.tvEhliyetBilgi);
        tvBosDurum = findViewById(R.id.tvBosDurum);
        tvIstatistik = findViewById(R.id.tvIstatistik);
        spinnerSiralama = findViewById(R.id.spinnerSiralama);
        btnFiltrele = findViewById(R.id.btnFiltrele);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    private void setupRecyclerView() {
        recyclerIlanlar.setLayoutManager(new LinearLayoutManager(this));
        ilanAdapter = new IlanAdapter(new ArrayList<>(), this);
        recyclerIlanlar.setAdapter(ilanAdapter);
    }

    private void setupListeners() {
        String[] siralamaSecenekleri = {"Uyum Yüzdesi", "Yeni İlanlar", "Son Başvuru Tarihi", "KPSS Puanı"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, siralamaSecenekleri);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSiralama.setAdapter(spinnerAdapter);
        spinnerSiralama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ilanlariSirala(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        swipeRefresh.setColorSchemeResources(R.color.mavi);
        swipeRefresh.setOnRefreshListener(this::ilanlariYukle);

        btnFiltrele.setOnClickListener(v -> ilanlariFiltrele());

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_anasayfa) {
                return true;
            } else if (itemId == R.id.nav_favoriler) {
                favorileriGoster();
                return true;
            } else if (itemId == R.id.nav_profil) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void ilanlariYukle() {
        swipeRefresh.setRefreshing(true);
        executorService.execute(() -> {
            firestoreHelper.aktifIlanlariGetir();
        });
    }

    @Override
    public void onIlanlarGeldi(List<Ilan> ilanlar) {
        new Handler(Looper.getMainLooper()).post(() -> {
            swipeRefresh.setRefreshing(false);
            tumIlanlar = ilanlar;
            ilanlariFiltrele();
        });
    }

    @Override
    public void onYeniIlan(Ilan ilan) {
        new Handler(Looper.getMainLooper()).post(() -> {
            bildirimGoster(ilan);
            if (tumIlanlar != null) {
                tumIlanlar.add(0, ilan);
                ilanlariFiltrele();
            }
        });
    }

    @Override
    public void onHata(String hata) {
        new Handler(Looper.getMainLooper()).post(() -> {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(this, "Veri çekme hatası: " + hata, Toast.LENGTH_LONG).show();
        });
    }

    private void bildirimGoster(Ilan ilan) {
        Toast.makeText(this, "🎯 Yeni ilan: " + ilan.getKurumAdi(), Toast.LENGTH_LONG).show();
    }

    private void profilBilgileriniYukle() {
        SharedPreferences prefs = getSharedPreferences("KullaniciProfili", MODE_PRIVATE);
        kullaniciBolum = prefs.getString("bolum", "");
        kullaniciKpss = Double.parseDouble(prefs.getString("kpssPuani", "0"));
        kullaniciYas = Integer.parseInt(prefs.getString("yas", "0"));
        kullaniciEhliyet = prefs.getString("ehliyet", "Yok");
        kullaniciCinsiyet = prefs.getString("cinsiyet", "Farketmez");
        kullaniciEngel = prefs.getBoolean("engelDurumu", false);
        kullaniciGuvenlikKarti = prefs.getBoolean("guvenlikKarti", false);
        
        // Şehirleri yükle
        kullaniciSehirler = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String sehir = prefs.getString("sehir" + i, "");
            if (!sehir.isEmpty()) kullaniciSehirler.add(sehir);
        }

        tvKullaniciBolum.setText("Bölüm: " + (kullaniciBolum.isEmpty() ? "Belirtilmemiş" : kullaniciBolum));
        tvKpssBilgi.setText("KPSS: " + kullaniciKpss);
        tvEhliyetBilgi.setText("Ehliyet: " + kullaniciEhliyet);

        if (kullaniciBolum.isEmpty()) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
    }

    private void ilanlariFiltrele() {
        if (tumIlanlar == null) return;
        
        filtrelenmisIlanlar = new ArrayList<>();
        
        for (Ilan ilan : tumIlanlar) {
            int uyumPuani = 0;
            int toplamKriter = 0;
            
            // Bölüm eşleşmesi (%30)
            boolean bolumEslesiyor = ilan.getBolum().equalsIgnoreCase(kullaniciBolum) ||
                    ilan.getBolum().equalsIgnoreCase("Herhangi bir lisans bölümü");
            if (bolumEslesiyor) uyumPuani += 30;
            toplamKriter += 30;
            
            // KPSS (%20)
            boolean kpssUygun = ilan.getKpssMinimum() <= kullaniciKpss;
            if (kpssUygun) uyumPuani += 20;
            toplamKriter += 20;
            
            // Yaş şartı (%15)
            boolean yasUygun = true;
            if (ilan.getYasSarti() != null && !ilan.getYasSarti().isEmpty()) {
                toplamKriter += 15;
                yasUygun = yasKontrolu(ilan.getYasSarti(), kullaniciYas);
                if (yasUygun) uyumPuani += 15;
            }
            
            // Cinsiyet şartı (%10)
            boolean cinsiyetUygun = true;
            if (ilan.getCinsiyetSarti() != null && !ilan.getCinsiyetSarti().equalsIgnoreCase("Farketmez")) {
                toplamKriter += 10;
                cinsiyetUygun = ilan.getCinsiyetSarti().equalsIgnoreCase(kullaniciCinsiyet);
                if (cinsiyetUygun) uyumPuani += 10;
            }
            
            // Ehliyet (%10)
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
            
            // İkamet şartı (%10)
            boolean ikametUygun = true;
            if (ilan.getIkametSarti() != null && !ilan.getIkametSarti().isEmpty()) {
                toplamKriter += 10;
                ikametUygun = ikametKontrolu(ilan.getIkametSarti());
                if (ikametUygun) uyumPuani += 10;
            }
            
            // Engel durumu şartı (bonus)
            if (ilan.isEngelDurumuSarti() && kullaniciEngel) {
                uyumPuani += 5; // Bonus puan
            }
            
            // Güvenlik kartı şartı
            boolean guvenlikUygun = true;
            if (ilan.isGuvenlikKartiSarti() && !kullaniciGuvenlikKarti) {
                guvenlikUygun = false;
            }

            int uyumYuzdesi = toplamKriter > 0 ? (uyumPuani * 100) / toplamKriter : 0;
            ilan.setUyumYuzdesi(uyumYuzdesi);

            // Tüm şartlar uyuyorsa listeye ekle
            if (bolumEslesiyor && kpssUygun && yasUygun && cinsiyetUygun && 
                ehliyetUygun && ikametUygun && guvenlikUygun) {
                filtrelenmisIlanlar.add(ilan);
            }
        }

        // İstatistik güncelle
        guncelleIstatistik();

        Collections.sort(filtrelenmisIlanlar, (a, b) -> 
            Integer.compare(b.getUyumYuzdesi(), a.getUyumYuzdesi()));

        guncelleUI();
    }

    private boolean yasKontrolu(String yasSarti, int kullaniciYas) {
        // "18-30", "35-40" formatında
        try {
            String[] parcalar = yasSarti.split("-");
            int minYas = Integer.parseInt(parcalar[0].trim());
            int maxYas = Integer.parseInt(parcalar[1].trim());
            return kullaniciYas >= minYas && kullaniciYas <= maxYas;
        } catch (Exception e) {
            return true; // Parse hatası varsa şartsız kabul et
        }
    }

    private boolean ikametKontrolu(String ikametSarti) {
        for (String sehir : kullaniciSehirler) {
            if (ikametSarti.toLowerCase().contains(sehir.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void guncelleIstatistik() {
        int toplamIlan = tumIlanlar != null ? tumIlanlar.size() : 0;
        int uygunIlan = filtrelenmisIlanlar.size();
        
        String istatistik = "Toplam: " + toplamIlan + " | Size Uygun: " + uygunIlan;
        tvIstatistik.setText(istatistik);
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
        if (firestoreHelper != null) {
            firestoreHelper.dinlemeyiDurdur();
        }
    }
}
