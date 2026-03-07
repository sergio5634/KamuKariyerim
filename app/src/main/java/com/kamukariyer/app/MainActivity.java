package com.kamukariyer.app;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirebaseFirestoreHelper {
    
    private static final String TAG = "FirestoreHelper";
    private static final String COLLECTION_ILANLAR = "kamuilani";
    
    private FirebaseFirestore db;
    private IlanListener listener;
    private ListenerRegistration ilanListener;
    
    public interface IlanListener {
        void onIlanlarGeldi(List<Ilan> ilanlar);
        void onYeniIlan(Ilan ilan);
        void onHata(String hata);
    }
    
    public FirebaseFirestoreHelper(IlanListener listener) {
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
    }
    
    // Tüm aktif ilanları getir
    public void aktifIlanlariGetir() {
        db.collection(COLLECTION_ILANLAR)
            .whereEqualTo("aktif", true)
            .orderBy("yayinlanmaTarihi", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Ilan> ilanlar = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Ilan ilan = dokumaniIlanYap(doc);
                    if (ilan != null) {
                        ilanlar.add(ilan);
                    }
                }
                if (listener != null) {
                    listener.onIlanlarGeldi(ilanlar);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "İlanlar çekilirken hata: " + e.getMessage());
                if (listener != null) {
                    listener.onHata(e.getMessage());
                }
            });
    }
    
    // Yeni ilanları dinle (real-time)
    public void yeniIlanlariDinle(String kullaniciBolum) {
        // Önceki listener'ı temizle
        if (ilanListener != null) {
            ilanListener.remove();
        }
        
        Date simdi = new Date();
        
        ilanListener = db.collection(COLLECTION_ILANLAR)
            .whereEqualTo("aktif", true)
            .whereGreaterThan("yayinlanmaTarihi", simdi)
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Log.e(TAG, "Dinleme hatası: " + e.getMessage());
                    return;
                }
                
                if (snapshots != null && !snapshots.isEmpty()) {
                    for (QueryDocumentSnapshot doc : snapshots.getDocumentChanges()) {
                        if (doc.getType().equals(com.google.firebase.firestore.DocumentChange.Type.ADDED)) {
                            Ilan yeniIlan = dokumaniIlanYap(doc);
                            if (yeniIlan != null && listener != null) {
                                // Bölüm kontrolü
                                if (yeniIlan.getBolum().equalsIgnoreCase(kullaniciBolum) ||
                                    yeniIlan.getBolum().equalsIgnoreCase("Herhangi bir lisans bölümü")) {
                                    listener.onYeniIlan(yeniIlan);
                                }
                            }
                        }
                    }
                }
            });
    }
    
    // Firestore dokümanını Ilan objesine çevir
    private Ilan dokumaniIlanYap(QueryDocumentSnapshot doc) {
        try {
            Ilan ilan = new Ilan();
            ilan.setId(doc.getId());
            ilan.setKurumAdi(doc.getString("kurumAdi"));
            ilan.setPozisyon(doc.getString("pozisyon"));
            ilan.setBolum(doc.getString("bolum"));
            ilan.setBolumGrubu(doc.getString("bolumGrubu"));
            ilan.setKadroTipi(doc.getString("kadroTipi"));
            ilan.setSehir(doc.getString("sehir"));
            ilan.setAciklama(doc.getString("aciklama"));
            ilan.setBasvuruLinki(doc.getString("basvuruLinki"));
            
            // Sayısal değerler
            Double kpss = doc.getDouble("kpssMinimum");
            ilan.setKpssMinimum(kpss != null ? kpss : 0);
            
            // Tarihler
            if (doc.getTimestamp("sonBasvuruTarihi") != null) {
                ilan.setSonBasvuruTarihi(doc.getTimestamp("sonBasvuruTarihi").toDate());
            }
            if (doc.getTimestamp("yayinlanmaTarihi") != null) {
                ilan.setYayinlanmaTarihi(doc.getTimestamp("yayinlanmaTarihi").toDate());
            }
            
            // Şartlar
            ilan.setEhliyetSartı(doc.getString("ehliyetSartı") != null ? 
                doc.getString("ehliyetSartı") : "Yok");
            ilan.setYasSarti(doc.getString("yasSarti"));
            ilan.setCinsiyetSarti(doc.getString("cinsiyetSarti"));
            ilan.setTecrubeSarti(doc.getString("tecrubeSarti"));
            ilan.setIkametSarti(doc.getString("ikametSarti"));
            
            Boolean engel = doc.getBoolean("engelDurumuSarti");
            ilan.setEngelDurumuSarti(engel != null ? engel : false);
            
            Boolean guvenlik = doc.getBoolean("guvenlikKartiSarti");
            ilan.setGuvenlikKartiSarti(guvenlik != null ? guvenlik : false);
            
            Boolean aktif = doc.getBoolean("aktif");
            ilan.setAktif(aktif != null ? aktif : true);
            
            // Yeni ilan kontrolü (son 24 saat)
            if (ilan.getYayinlanmaTarihi() != null) {
                long fark = System.currentTimeMillis() - ilan.getYayinlanmaTarihi().getTime();
                ilan.setYeniIlan(fark < 24 * 60 * 60 * 1000);
            }
            
            return ilan;
        } catch (Exception e) {
            Log.e(TAG, "Doküman dönüştürme hatası: " + e.getMessage());
            return null;
        }
    }
    
    public void dinlemeyiDurdur() {
        if (ilanListener != null) {
            ilanListener.remove();
        }
    }
}
