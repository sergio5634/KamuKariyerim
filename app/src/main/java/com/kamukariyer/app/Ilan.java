package com.kamukariyer.app;

import java.io.Serializable;
import java.util.Date;

public class Ilan implements Serializable {
    
    private String id;
    private String kurumAdi;
    private String pozisyon;
    private String bolum;
    private String bolumGrubu;
    private String kadroTipi;
    private String sehir;
    private String aciklama;
    private String basvuruLinki;
    
    private double kpssMinimum;
    private String ehliyetSartı;
    private String yasSarti;
    private String cinsiyetSarti;
    private String tecrubeSarti;
    private String ikametSarti;
    private boolean engelDurumuSarti;
    private boolean guvenlikKartiSarti;
    
    // YENİ: Elden evrak teslimi
    private boolean eldenEvrak;
    private String evrakTeslimYeri;
    
    private Date sonBasvuruTarihi;
    private Date yayinlanmaTarihi;
    private boolean aktif;
    
    // UI için ek alanlar
    private int uyumYuzdesi;
    private boolean yeniIlan;
    
    // Boş constructor (Firebase için gerekli)
    public Ilan() {}
    
    // Getter ve Setter'lar
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getKurumAdi() { return kurumAdi; }
    public void setKurumAdi(String kurumAdi) { this.kurumAdi = kurumAdi; }
    
    public String getPozisyon() { return pozisyon; }
    public void setPozisyon(String pozisyon) { this.pozisyon = pozisyon; }
    
    public String getBolum() { return bolum != null ? bolum : "Herhangi bir lisans bölümü"; }
    public void setBolum(String bolum) { this.bolum = bolum; }
    
    public String getBolumGrubu() { return bolumGrubu; }
    public void setBolumGrubu(String bolumGrubu) { this.bolumGrubu = bolumGrubu; }
    
    public String getKadroTipi() { return kadroTipi; }
    public void setKadroTipi(String kadroTipi) { this.kadroTipi = kadroTipi; }
    
    public String getSehir() { return sehir; }
    public void setSehir(String sehir) { this.sehir = sehir; }
    
    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
    
    public String getBasvuruLinki() { return basvuruLinki; }
    public void setBasvuruLinki(String basvuruLinki) { this.basvuruLinki = basvuruLinki; }
    
    public double getKpssMinimum() { return kpssMinimum; }
    public void setKpssMinimum(double kpssMinimum) { this.kpssMinimum = kpssMinimum; }
    
    public String getEhliyetSartı() { return ehliyetSartı != null ? ehliyetSartı : "Yok"; }
    public void setEhliyetSartı(String ehliyetSartı) { this.ehliyetSartı = ehliyetSartı; }
    
    public String getYasSarti() { return yasSarti; }
    public void setYasSarti(String yasSarti) { this.yasSarti = yasSarti; }
    
    public String getCinsiyetSarti() { return cinsiyetSarti; }
    public void setCinsiyetSarti(String cinsiyetSarti) { this.cinsiyetSarti = cinsiyetSarti; }
    
    public String getTecrubeSarti() { return tecrubeSarti; }
    public void setTecrubeSarti(String tecrubeSarti) { this.tecrubeSarti = tecrubeSarti; }
    
    public String getIkametSarti() { return ikametSarti; }
    public void setIkametSarti(String ikametSarti) { this.ikametSarti = ikametSarti; }
    
    public boolean isEngelDurumuSarti() { return engelDurumuSarti; }
    public void setEngelDurumuSarti(boolean engelDurumuSarti) { this.engelDurumuSarti = engelDurumuSarti; }
    
    public boolean isGuvenlikKartiSarti() { return guvenlikKartiSarti; }
    public void setGuvenlikKartiSarti(boolean guvenlikKartiSarti) { this.guvenlikKartiSarti = guvenlikKartiSarti; }
    
    // YENİ: Elden evrak getter/setter
    public boolean isEldenEvrak() { return eldenEvrak; }
    public void setEldenEvrak(boolean eldenEvrak) { this.eldenEvrak = eldenEvrak; }
    
    public String getEvrakTeslimYeri() { return evrakTeslimYeri; }
    public void setEvrakTeslimYeri(String evrakTeslimYeri) { this.evrakTeslimYeri = evrakTeslimYeri; }
    
    public Date getSonBasvuruTarihi() { return sonBasvuruTarihi; }
    public void setSonBasvuruTarihi(Date sonBasvuruTarihi) { this.sonBasvuruTarihi = sonBasvuruTarihi; }
    
    public Date getYayinlanmaTarihi() { return yayinlanmaTarihi; }
    public void setYayinlanmaTarihi(Date yayinlanmaTarihi) { this.yayinlanmaTarihi = yayinlanmaTarihi; }
    
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }
    
    public int getUyumYuzdesi() { return uyumYuzdesi; }
    public void setUyumYuzdesi(int uyumYuzdesi) { this.uyumYuzdesi = uyumYuzdesi; }
    
    public boolean isYeniIlan() { return yeniIlan; }
    public void setYeniIlan(boolean yeniIlan) { this.yeniIlan = yeniIlan; }
    
    // YENİ: Evrak teslim bilgisi metin olarak
    public String getEvrakTeslimBilgisi() {
        if (!eldenEvrak) {
            return "Online Başvuru";
        }
        if (evrakTeslimYeri != null && !evrakTeslimYeri.isEmpty()) {
            return "Elden: " + evrakTeslimYeri;
        }
        return "Elden Evrak Gerekli";
    }
}
