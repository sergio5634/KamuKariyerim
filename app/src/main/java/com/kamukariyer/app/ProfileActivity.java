package com.kamukariyer.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteBolum;
    private EditText etKpssPuani, etYas;
    private RadioGroup rgEhliyet, rgCinsiyet;
    private Switch switchEngel, switchGuvenlikKarti;
    private Button btnKaydet;
    private LinearLayout layoutSehirler;
    
    // 3 şehir ve ilçe seçimi için
    private Spinner[] spinnerSehirler = new Spinner[3];
    private Spinner[] spinnerIlceler = new Spinner[3];
    private TextView[] tvIlceLabels = new TextView[3];
    
    private String[] bolumler;
    
    // Şehir ve ilçe verileri
    private Map<String, List<String>> sehirIlceMap;
    private String[] sehirler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initData();
        initViews();
        setupBolumListesi();
        setupSehirIlceSpinners();
        loadSavedData();
    }

    private void initData() {
        // Şehir ve ilçe verilerini hazırla
        sehirIlceMap = new HashMap<>();
        
        // İstanbul
        sehirIlceMap.put("İstanbul", Arrays.asList(
            "Adalar", "Arnavutköy", "Ataşehir", "Avcılar", "Bağcılar", 
            "Bahçelievler", "Bakırköy", "Başakşehir", "Bayrampaşa", "Beşiktaş",
            "Beykoz", "Beylikdüzü", "Beyoğlu", "Büyükçekmece", "Çatalca",
            "Çekmeköy", "Esenler", "Esenyurt", "Eyüpsultan", "Fatih",
            "Gaziosmanpaşa", "Güngören", "Kadıköy", "Kağıthane", "Kartal",
            "Küçükçekmece", "Maltepe", "Pendik", "Sancaktepe", "Sarıyer",
            "Silivri", "Sultanbeyli", "Sultangazi", "Şile", "Şişli",
            "Tuzla", "Ümraniye", "Üsküdar", "Zeytinburnu"
        ));
        
        // Ankara
        sehirIlceMap.put("Ankara", Arrays.asList(
            "Altındağ", "Ayaş", "Bala", "Beypazarı", "Çamlıdere",
            "Çankaya", "Çubuk", "Elmadağ", "Etimesgut", "Evren",
            "Gölbaşı", "Güdül", "Haymana", "Kahramankazan", "Kalecik",
            "Keçiören", "Kızılcahamam", "Mamak", "Nallıhan", "Polatlı",
            "Pursaklar", "Sincan", "Şereflikoçhisar", "Yenimahalle"
        ));
        
        // İzmir
        sehirIlceMap.put("İzmir", Arrays.asList(
            "Aliağa", "Balçova", "Bayındır", "Bayraklı", "Bergama",
            "Beydağ", "Bornova", "Buca", "Çeşme", "Çiğli",
            "Dikili", "Foça", "Gaziemir", "Güzelbahçe", "Karabağlar",
            "Karaburun", "Karşıyaka", "Kemalpaşa", "Kınık", "Kiraz",
            "Konak", "Menderes", "Menemen", "Narlıdere", "Ödemiş",
            "Seferihisar", "Selçuk", "Tire", "Torbalı", "Urla"
        ));
        
        sehirler = new String[]{"Seçiniz", "İstanbul", "Ankara", "İzmir"};
        
        // Bölümler listesi
        bolumler = new String[]{
            "Bilgisayar Mühendisliği", "Elektrik Mühendisliği", "Elektronik Mühendisliği",
            "Makine Mühendisliği", "İnşaat Mühendisliği", "Endüstri Mühendisliği",
            "İşletme", "İktisat", "Maliye", "Muhasebe", "Kamu Yönetimi",
            "Hukuk", "Psikoloji", "Sosyoloji", "Tarih", "Coğrafya", 
            "Türkçe Öğretmenliği", "Matematik Öğretmenliği", "Fen Bilgisi Öğretmenliği",
            "Okul Öncesi Öğretmenliği", "Hemşirelik", "Ebelik", 
            "Sağlık Teknikeri", "Tıbbi Laboratuvar",
            "Gıda Mühendisliği", "Ziraat Mühendisliği", "Orman Mühendisliği",
            "Şehir ve Bölge Planlama", "Mimarlık", "İç Mimarlık",
            "Fizik", "Kimya", "Biyoloji", "Matematik", "İstatistik",
            "Uluslararası İlişkiler", "Siyaset Bilimi", "İletişim", 
            "Halkla İlişkiler", "İlahiyat", "Türk Dili ve Edebiyatı"
        };
    }

    private void initViews() {
        autoCompleteBolum = findViewById(R.id.autoCompleteBolum);
        etKpssPuani = findViewById(R.id.etKpssPuani);
        etYas = findViewById(R.id.etYas);
        rgEhliyet = findViewById(R.id.rgEhliyet);
        rgCinsiyet = findViewById(R.id.rgCinsiyet);
        switchEngel = findViewById(R.id.switchEngel);
        switchGuvenlikKarti = findViewById(R.id.switchGuvenlikKarti);
        btnKaydet = findViewById(R.id.btnKaydet);
        layoutSehirler = findViewById(R.id.layoutSehirler);
        
        // Şehir ve ilçe view'larını bağla
        spinnerSehirler[0] = findViewById(R.id.spinnerSehir1);
        spinnerIlceler[0] = findViewById(R.id.spinnerIlce1);
        tvIlceLabels[0] = findViewById(R.id.tvIlceLabel1);
        
        spinnerSehirler[1] = findViewById(R.id.spinnerSehir2);
        spinnerIlceler[1] = findViewById(R.id.spinnerIlce2);
        tvIlceLabels[1] = findViewById(R.id.tvIlceLabel2);
        
        spinnerSehirler[2] = findViewById(R.id.spinnerSehir3);
        spinnerIlceler[2] = findViewById(R.id.spinnerIlce3);
        tvIlceLabels[2] = findViewById(R.id.tvIlceLabel3);
        
        btnKaydet.setOnClickListener(v -> profiliKaydet());
    }

    private void setupBolumListesi() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_dropdown_item_1line, 
            bolumler
        );
        autoCompleteBolum.setAdapter(adapter);
        autoCompleteBolum.setThreshold(1);
    }

    private void setupSehirIlceSpinners() {
        ArrayAdapter<String> sehirAdapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            sehirler
        );
        sehirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        for (int i = 0; i < 3; i++) {
            spinnerSehirler[i].setAdapter(sehirAdapter);
            final int index = i;
            
            spinnerSehirler[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String seciliSehir = sehirler[position];
                    if (!seciliSehir.equals("Seçiniz")) {
                        ilceleriGuncelle(index, seciliSehir);
                        tvIlceLabels[index].setVisibility(View.VISIBLE);
                        spinnerIlceler[index].setVisibility(View.VISIBLE);
                    } else {
                        tvIlceLabels[index].setVisibility(View.GONE);
                        spinnerIlceler[index].setVisibility(View.GONE);
                    }
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void ilceleriGuncelle(int sehirIndex, String sehir) {
        List<String> ilceler = sehirIlceMap.get(sehir);
        if (ilceler != null) {
            List<String> ilceListesi = new ArrayList<>();
            ilceListesi.add("Tüm İlçeler");
            ilceListesi.addAll(ilceler);
            
            ArrayAdapter<String> ilceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ilceListesi
            );
            ilceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerIlceler[sehirIndex].setAdapter(ilceAdapter);
        }
    }

    private void profiliKaydet() {
        String bolum = autoCompleteBolum.getText().toString().trim();
        String kpssPuani = etKpssPuani.getText().toString().trim();
        String yas = etYas.getText().toString().trim();
        
        // Ehliyet seçimi
        int selectedEhliyetId = rgEhliyet.getCheckedRadioButtonId();
        String ehliyet = "Yok";
        if (selectedEhliyetId == R.id.rbEhliyetB) ehliyet = "B";
        else if (selectedEhliyetId == R.id.rbEhliyetC) ehliyet = "C ve Üzeri";
        
        // Cinsiyet seçimi
        int selectedCinsiyetId = rgCinsiyet.getCheckedRadioButtonId();
        String cinsiyet = "Farketmez";
        if (selectedCinsiyetId == R.id.rbErkek) cinsiyet = "Erkek";
        else if (selectedCinsiyetId == R.id.rbKadin) cinsiyet = "Kadın";

        // Validasyon
        if (bolum.isEmpty()) {
            autoCompleteBolum.setError("Bölüm seçiniz");
            return;
        }
        if (kpssPuani.isEmpty()) {
            etKpssPuani.setError("KPSS puanınızı giriniz");
            return;
        }
        if (yas.isEmpty()) {
            etYas.setError("Yaşınızı giriniz");
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
        editor.putString("yas", yas);
        editor.putString("ehliyet", ehliyet);
        editor.putString("cinsiyet", cinsiyet);
        editor.putBoolean("engelDurumu", switchEngel.isChecked());
        editor.putBoolean("guvenlikKarti", switchGuvenlikKarti.isChecked());
        
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
        
        // Önceki verileri yükle
        String kayitliBolum = prefs.getString("bolum", "");
        String kayitliKpss = prefs.getString("kpssPuani", "");
        String kayitliYas = prefs.getString("yas", "");
        String kayitliEhliyet = prefs.getString("ehliyet", "Yok");
        String kayitliCinsiyet = prefs.getString("cinsiyet", "Farketmez");

        if (!kayitliBolum.isEmpty()) {
            autoCompleteBolum.setText(kayitliBolum);
        }
        if (!kayitliKpss.isEmpty()) {
            etKpssPuani.setText(kayitliKpss);
        }
        if (!kayitliYas.isEmpty()) {
            etYas.setText(kayitliYas);
        }
        
        // Ehliyet radio button
        if (kayitliEhliyet.equals("B")) {
            rgEhliyet.check(R.id.rbEhliyetB);
        } else if (kayitliEhliyet.equals("C ve Üzeri")) {
            rgEhliyet.check(R.id.rbEhliyetC);
        }
        
        // Cinsiyet radio button
        if (kayitliCinsiyet.equals("Erkek")) {
            rgCinsiyet.check(R.id.rbErkek);
        } else if (kayitliCinsiyet.equals("Kadın")) {
            rgCinsiyet.check(R.id.rbKadin);
        }
        
        // Switch'ler
        switchEngel.setChecked(prefs.getBoolean("engelDurumu", false));
        switchGuvenlikKarti.setChecked(prefs.getBoolean("guvenlikKarti", false));
    }
}
