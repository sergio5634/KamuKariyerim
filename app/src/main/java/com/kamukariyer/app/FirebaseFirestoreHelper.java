package com.kamukariyer.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FirebaseFirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private static final String COLLECTION_ILANLAR = "kamuilani";
    
    private FirebaseFirestore db;
    private Context context;
    private IlanListener ilanListener;

    public interface IlanListener {
        void onIlanlarGeldi(List<Ilan> ilanlar);
        void onYeniIlan(Ilan ilan);
        void onHata(String hata);
    }

    public FirebaseFirestoreHelper(Context context, IlanListener listener) {
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
        this.ilanListener = listener;
    }

    public void aktifIlanlariGetir() {
        db.collection(COLLECTION_ILANLAR)
            .whereEqualTo("aktif", true)
            .orderBy("sonBasvuruTarihi", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Ilan> ilanlar = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Ilan ilan = doc.toObject(Ilan.class);
                    ilan.setId(doc.getId());
                    ilanlar.add(ilan);
                }
                ilanListener.onIlanlarGeldi(ilanlar);
                Log.d(TAG, ilanlar.size() + " ilan çekildi");
            })
            .addOnFailureListener(e -> {
                ilanListener.onHata(e.getMessage());
                Log.e(TAG, "Hata: " + e.getMessage());
            });
    }

    public void yeniIlanlariDinle(String kullaniciBolum) {
        db.collection(COLLECTION_ILANLAR)
            .whereEqualTo("aktif", true)
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Log.e(TAG, "Dinleme hatası: " + e.getMessage());
                    return;
                }

                if (snapshots != null) {
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Ilan yeniIlan = dc.getDocument().toObject(Ilan.class);
                            yeniIlan.setId(dc.getDocument().getId());
                            yeniIlan.setYeniIlan(true);
                            
                            // Kullanıcının bölümüne uygun mu kontrol et
                            if (bolumEslesiyor(yeniIlan.getBolum(), kullaniciBolum)) {
                                if (yeniMi(yeniIlan.getYayinlanmaTarihi())) {
                                    ilanListener.onYeniIlan(yeniIlan);
                                    sonKontrolZamaniniGuncelle();
                                }
                            }
                        }
                    }
                }
            });
    }

    private boolean bolumEslesiyor(String ilanBolum, String kullaniciBolum) {
        // Tam eşleşme veya "Herhangi bir lisans bölümü"
        return ilanBolum.equalsIgnoreCase(kullaniciBolum) ||
               ilanBolum.equalsIgnoreCase("Herhangi bir lisans bölümü");
    }

    private boolean yeniMi(long yayinlanmaTarihi) {
        SharedPreferences prefs = context.getSharedPreferences("IlanTakip", Context.MODE_PRIVATE);
        long sonKontrol = prefs.getLong("son_kontrol", 0);
        return yayinlanmaTarihi > sonKontrol;
    }

    private void sonKontrolZamaniniGuncelle() {
        SharedPreferences prefs = context.getSharedPreferences("IlanTakip", Context.MODE_PRIVATE);
        prefs.edit().putLong("son_kontrol", System.currentTimeMillis()).apply();
    }
}
