package com.wahida.pelaporankendaraan.core.data.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wahida.pelaporankendaraan.DetailLaporan;
import com.wahida.pelaporankendaraan.R;
import com.wahida.pelaporankendaraan.core.data.model.DataLaporan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.HolderView> {

    private Context context;
    private ArrayList<DataLaporan> listLaporan;

    public LaporanAdapter(Context context, ArrayList<DataLaporan> listLaporan) {
        this.context = context;
        this.listLaporan = listLaporan;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_data_laporan, parent, false);
        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
        DataLaporan laporan = listLaporan.get(position);

        holder.txt_id.setText(String.valueOf(laporan.getId()));
        holder.txt_keterangan.setText(laporan.getKeterangan());
        holder.txt_tanggal.setText(laporan.getUpdated_at());
        holder.txt_nama_pelapor.setText(laporan.getNama());
        holder.txt_status.setText(laporan.getStatus());

        int color = context.getColor(R.color.menunggu);
        if (Objects.equals(laporan.getStatus(), "DIPROSES")) {
            color = context.getColor(R.color.diproses);
        } else if (Objects.equals(laporan.getStatus(), "SELESAI")) {
            color = context.getColor(R.color.selesai);
        }
        holder.txt_status.setTextColor(color);

        holder.btn_detail.setOnClickListener(v -> {
            Intent inte = new Intent(context, DetailLaporan.class);
            inte.putExtra("id", String.valueOf(laporan.getId()));
            context.startActivity(inte);
        });
    }

    @Override
    public int getItemCount() {
        return listLaporan.size();
    }

    //    fungsi pencarian data
    Filter searchData = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<DataLaporan> searchList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                searchList.addAll(listLaporan);
            } else {
                for (DataLaporan getDataLaporan : listLaporan) {
                    if (getDataLaporan
                            .getNama()
                            .toLowerCase()
                            .contains(constraint
                                    .toString()
                                    .toLowerCase())
                            || getDataLaporan
                            .getUpdated_at()
                            .toLowerCase()
                            .contains(constraint
                                    .toString()
                                    .toLowerCase())
                            || getDataLaporan
                            .getKeterangan()
                            .toLowerCase()
                            .contains(constraint
                                    .toString()
                                    .toLowerCase())
                            || getDataLaporan
                            .getStatus()
                            .toLowerCase()
                            .contains(constraint
                                    .toString()
                                    .toLowerCase())) {
                        searchList.add(getDataLaporan);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = searchList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listLaporan.clear();
            listLaporan.addAll((Collection<? extends DataLaporan>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getSearchData() {
        return searchData;
    }

    public class HolderView extends RecyclerView.ViewHolder {

        private TextView txt_nama_pelapor, txt_keterangan, txt_tanggal, txt_id, txt_status;
        private CardView btn_detail;

        public HolderView(@NonNull View itemView) {
            super(itemView);

            txt_nama_pelapor = itemView.findViewById(R.id.nama_pelapor);
            txt_keterangan = itemView.findViewById(R.id.keterangan);
            txt_tanggal = itemView.findViewById(R.id.tanggal);
            btn_detail = itemView.findViewById(R.id.btn_detail);
            txt_id = itemView.findViewById(R.id.id);
            txt_status = itemView.findViewById(R.id.status);
        }
    }
}
