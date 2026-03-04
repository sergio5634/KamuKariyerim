static class IlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvKurumAdi, tvPozisyon, tvKadroTipi, tvSehir, tvSonBasvuru;
        TextView tvSartBolum, tvSartKpss, tvSartEhliyet, tvSartTecrube, tvSartIkamet, tvUyumYuzdesi, tvYeniRozet;
        LinearLayout layoutSartEhliyet, layoutSartTecrube, layoutSartIkamet;
        MaterialButton btnDetay, btnFavori;

        public IlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKurumAdi = itemView.findViewById(R.id.tvKurumAdi);
            tvPozisyon = itemView.findViewById(R.id.tvPozisyon);
            tvKadroTipi = itemView.findViewById(R.id.tvKadroTipi);
            tvSehir = itemView.findViewById(R.id.tvSehir);
            tvSonBasvuru = itemView.findViewById(R.id.tvSonBasvuru);
            
            // Şartlar özeti
            tvSartBolum = itemView.findViewById(R.id.tvSartBolum);
            tvSartKpss = itemView.findViewById(R.id.tvSartKpss);
            tvSartEhliyet = itemView.findViewById(R.id.tvSartEhliyet);
            tvSartTecrube = itemView.findViewById(R.id.tvSartTecrube);
            tvSartIkamet = itemView.findViewById(R.id.tvSartIkamet);
            layoutSartEhliyet = itemView.findViewById(R.id.layoutSartEhliyet);
            layoutSartTecrube = itemView.findViewById(R.id.layoutSartTecrube);
            layoutSartIkamet = itemView.findViewById(R.id.layoutSartIkamet);
            tvUyumYuzdesi = itemView.findViewById(R.id.tvUyumYuzdesi);
            
            tvYeniRozet = itemView.findViewById(R.id.tvYeniRozet);
            btnDetay = itemView.findViewById(R.id.btnDetay);
            btnFavori = itemView.findViewById(R.id.btnFavori);
        }
    }
}
