package com.kamukariyer.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class IlanAdapter extends RecyclerView.Adapter<IlanAdapter.IlanViewHolder> {

    private List<Ilan> ilanlar;
    private OnIlanClickListener listener;
    private SimpleDateFormat tarihFormat;

    public interface OnIlanClickListener {
        void onDetayClick(Ilan ilan);
        void onFavoriClick(Ilan ilan);
    }

    public IlanAdapter(List<Ilan> ilanlar, OnIlanClickListener listener) {
        this.ilanlar = ilanlar;
        this.listener = listener;
        this.tarihFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("tr"));
    }

    public void guncelleListe(List<Ilan> yeniIlanlar) {
        this.ilanlar = yeniIlanlar;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ilan, parent, false);
        return new IlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IlanViewHolder holder, int position) {
        Ilan ilan = ilanlar.get(position);
        holder.bind(ilan);
    }

    @Override
    public int getItemCount() {
        return ilanlar != null ? ilanlar.size() : 0;
    }

    class IlanViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvKurum, tvPozisyon, tvBolum, tvSehir, tvKadro, tvTarih, tvUyum;
        TextView tvKpss, tvEhliyet, tvYas, tvIkamet, tvEvrakTeslim;
        MaterialButton btnDetay;
        ImageButton btnFavori;
        View viewUyumRengi;

        IlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardIlan);
            tvKurum = itemView.findViewById(R.id.tvKurum);
            tvPozisyon = itemView.findViewById(R.id.tvPozisyon);
            tvBolum = itemView.findViewById(R.id.tvBolum);
            tvSehir = itemView.findViewById(R.id.tvSehir);
            tvKadro = itemView.findViewById(R.id.tvKadro);
            tvTarih = itemView.findViewById(R.id.tvTarih);
            tvUyum = itemView.findViewById(R.id.tvUyum);
            tvKpss = itemView.findViewById(R.id.tvKpss);
            tvEhliyet = itemView.findViewById(R.id.tvEhliyet);
            tvYas = itemView.findViewById(R.id.tvYas);
            tvIkamet = itemView.findViewById(R.id.tvIkamet);
            tvEvrakTeslim = itemView.findViewById(R.id.tvEvrakTeslim);
            btnDetay = itemView.findViewById(R.id.btnDetay);
            btnFavori = itemView.findViewById(R.id.btnFavori);
            viewUyumRengi = itemView.findViewById(R.id.viewUyumRengi);
        }

        void bind(Ilan ilan) {
            tvKurum.setText(ilan.getKurumAdi());
            tvPozisyon.setText(ilan.getPozisyon());
            tvBolum.setText("Bölüm: " + ilan.getBolum());
            tvSehir.setText("📍 " + ilan.getSehir());
            tvKadro.setText(ilan.getKadroTipi());
            
            if (ilan.getSonBasvuruTarihi() != null) {
                tvTarih.setText("Son Tarih: " + tarihFormat.format(ilan.getSonBasvuruTarihi()));
            } else {
                tvTarih.setText("Son Tarih: Belirtilmemiş");
            }

            // Uyum yüzdesi ve rengi
            tvUyum.setText("%" + ilan.getUyumYuzdesi() + " Uyum");
            int uyumRengi = getUyumRengi(ilan.getUyumYuzdesi());
            viewUyumRengi.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), uyumRengi));
            tvUyum.setTextColor(ContextCompat.getColor(itemView.getContext(), uyumRengi));

            // Şartlar özeti
            tvKpss.setText("KPSS: " + (ilan.getKpssMinimum() > 0 ? ilan.getKpssMinimum() : "Şartsız"));
            tvEhliyet.setText("Ehliyet: " + ilan.getEhliyetSartı());
            tvYas.setText(ilan.getYasSarti() != null ? "Yaş: " + ilan.getYasSarti() : "Yaş: Şartsız");
            tvIkamet.setText(ilan.getIkametSarti() != null ? "İkamet: " + ilan.getIkametSarti() : "İkamet: Şartsız");

            // YENİ: Evrak teslim bilgisi
            tvEvrakTeslim.setText(ilan.getEvrakTeslimBilgisi());
            if (ilan.isEldenEvrak()) {
                tvEvrakTeslim.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                tvEvrakTeslim.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
            } else {
                tvEvrakTeslim.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                tvEvrakTeslim.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
            }

            // Yeni ilan badge
            if (ilan.isYeniIlan()) {
                tvKurum.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_notification_overlay, 0);
            } else {
                tvKurum.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            btnDetay.setOnClickListener(v -> {
                if (listener != null) listener.onDetayClick(ilan);
            });

            btnFavori.setOnClickListener(v -> {
                if (listener != null) listener.onFavoriClick(ilan);
            });
        }

        private int getUyumRengi(int yuzde) {
            if (yuzde >= 80) return android.R.color.holo_green_dark;
            if (yuzde >= 60) return android.R.color.holo_orange_dark;
            return android.R.color.holo_red_dark;
        }
    }
}
