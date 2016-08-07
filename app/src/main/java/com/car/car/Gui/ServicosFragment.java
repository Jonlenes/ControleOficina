package com.car.car.Gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.car.car.Modelo.Servico;
import com.car.car.Modelo.ServicoDao;
import com.car.car.Modelo.ServicosPrestadoDao;
import com.car.car.R;
import com.car.car.Exception.TreatException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 05/08/2016.
 */
public class ServicosFragment extends Fragment {

    private ListView lvServicos;
    private TextView tvValorTotal;
    private AdapterListServico adapterListServico;
    private static List<Servico> servicos = null;

    private Long idVeiculo;
    private boolean buscarTodos;
    private Double valorTotal;

    public ServicosFragment() {
    }

    public static ServicosFragment newInstance() {
        return new ServicosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_servicos, container, false);

        lvServicos = (ListView) rootView.findViewById(R.id.lvServicos);
        tvValorTotal = (TextView) rootView.findViewById(R.id.tvValorTotal);
        idVeiculo = ((CadastroVeiculoActivity) getActivity()).getIdVeiculo();
        buscarTodos = false;
        valorTotal = 0.0;

        lvServicos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvServicos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (adapterListServico.toggleSelection(position))
                    valorTotal += ((Servico) parent.getItemAtPosition(position)).getValor();
                else
                    valorTotal -= ((Servico) parent.getItemAtPosition(position)).getValor();

                atualizaViewTotalizadora();
            }
        });

        atualizaViewTotalizadora();
        new BuscaServicosAsyncTask().execute();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        servicos = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
            ((CadastroVeiculoActivity) getActivity()).getToolbar().setTitle("Serviços");
        else {
            if (lvServicos != null)
                fillListServicos();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_servicos, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (idVeiculo == -1)
            menu.findItem(R.id.action_mais).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_ok: {
                ((CadastroVeiculoActivity) getActivity()).getViewPager().setCurrentItem(0);
                return true;
            }
            case R.id.action_mais: {
                buscarTodos = true;
                new BuscaServicosAsyncTask().execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void fillListServicos() {
        if (servicos == null)
            servicos = new ArrayList<>();

        if (adapterListServico == null)
            return;

        SparseBooleanArray array = adapterListServico.getSelectedIds();

        servicos.clear();
        for (int i = 0; i < array.size(); ++i)
            servicos.add((Servico) lvServicos.getItemAtPosition(array.keyAt(i)));
    }

    public static List<Servico> getServicos() {
        if (servicos == null)
            return new ArrayList<>();
        return servicos;
    }

    private void atualizaViewTotalizadora() {
        tvValorTotal.setText(NumberFormat.getCurrencyInstance().format(valorTotal));
    }

    private class AdapterListServico extends ArrayAdapter<Servico> {

        private SparseBooleanArray mSelectedItemsIds;
        private List<Servico> list;
        private LayoutInflater inflater;

        public AdapterListServico(Context context, List<Servico> list) {
            super(context, R.layout.lv_servico_item, list);
            this.list = list;
            mSelectedItemsIds = new SparseBooleanArray();
            inflater = LayoutInflater.from(context);
        }

        private class ViewHolder {
            TextView tvDescrisaoServico;
            TextView tvValorServico;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder holder;

            if (view == null) {

                holder = new ViewHolder();
                view = inflater.inflate(R.layout.lv_servico_item, null);

                holder.tvDescrisaoServico = (TextView) view.findViewById(R.id.tvDescrisaoServico);
                holder.tvValorServico = (TextView) view.findViewById(R.id.tvValorServico);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.tvDescrisaoServico.setText(list.get(position).getDescrisao());
            holder.tvValorServico.setText(NumberFormat.getCurrencyInstance().format(list.get(position).getValor()));

            if (mSelectedItemsIds.get(position))
                view.setBackgroundColor(Color.GRAY);
            else
                view.setBackgroundColor(Color.TRANSPARENT);

            return view;
        }

        public boolean toggleSelection(int position) {
            selectView(position, !mSelectedItemsIds.get(position));
            return mSelectedItemsIds.get(position);
        }

        public void selectView(int position, boolean value) {
            if (value)
                mSelectedItemsIds.put(position, value);
            else
                mSelectedItemsIds.delete(position);
            notifyDataSetChanged();
        }

        public SparseBooleanArray getSelectedIds() {
            return mSelectedItemsIds;

        }
    }

    private class BuscaServicosAsyncTask extends AsyncTask<Void, Void, List<Servico>> {
        private ProgressDialog progressDialog;
        private Exception exception;

        List<Servico> list = new ArrayList<>();
        int sizeSelected = 0;

        public BuscaServicosAsyncTask() {
            progressDialog = new ProgressDialog(ServicosFragment.this.getContext());

            progressDialog.setMessage("Buscando os serviços...");
            progressDialog.setCancelable(false);

            list.add(new Servico("Lataria, Mecânica, Eletricidade", 100.0));
            list.add(new Servico("Hora Serviço Injeção Eletrônica", 120.0));
            list.add(new Servico("Estofador, Vidraceiro", 90.0));
            list.add(new Servico("Carga Bateria Rápida ou Lenta", 20.0));
            list.add(new Servico("Pintura mão obra sem material", 65.0));
            list.add(new Servico("Pintura Perolizada", 80.0));
            list.add(new Servico("Pneu sem câmara automóvel", 15.0));
            list.add(new Servico("Pneu com câmara", 18.0));
            list.add(new Servico("Pneu de Utilitário", 20.0));
            list.add(new Servico("Geometria Carreta.", 150.0));
            list.add(new Servico("Geometria ônibus 2 eixos", 150.0));
            list.add(new Servico("Geometria ônibus 3 eixos", 180.0));
            list.add(new Servico("Roda Ferro", 15.0));
            list.add(new Servico("Roda Utilitário", 25.0));
            list.add(new Servico("Alinhamento", 40.0));
            list.add(new Servico("Balanceamento", 40.0));
            list.add(new Servico("Higienização", 300.0));
            list.add(new Servico("Lavagem com cera", 40.0));
            list.add(new Servico("Lavagem sem cera", 50.0));
            list.add(new Servico("Revisão para viagem", 250.0));

            valorTotal = 0.0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<Servico> doInBackground(Void... params) {
            try {

                if (new ServicoDao().getAll(null).size() == 0) {
                    ServicoDao servicoDao = new ServicoDao();
                    for (Servico servico : list) {
                        servicoDao.insert(servico);
                    }
                }

                if (idVeiculo == -1)
                    return new ServicoDao().getAll(null);
                else {
                    if (buscarTodos) {
                        List<Servico> servicosSelected = new ServicosPrestadoDao().getAllByCar(idVeiculo);
                        List<Servico> servicosOutros = new ServicoDao().getAll(idVeiculo);
                        List<Servico> servicosResult = new ArrayList<>();

                        sizeSelected = servicosSelected.size();
                        servicosResult.addAll(servicosSelected);
                        servicosResult.addAll(servicosOutros);

                        return servicosResult;
                    }
                    else {
                        List<Servico> servicosSelected = new ServicosPrestadoDao().getAllByCar(idVeiculo);
                        sizeSelected = servicosSelected.size();
                        return servicosSelected;
                    }
                }

            } catch (Exception e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Servico> list) {
            super.onPostExecute(list);

            progressDialog.dismiss();

            if (exception != null)
                TreatException.treat(ServicosFragment.this.getContext(), exception);
            else {
                adapterListServico = new AdapterListServico(getContext(), list);
                lvServicos.setAdapter(adapterListServico);
                if (sizeSelected > 0) {
                    for (int i = 0; i < sizeSelected; ++i) {
                        adapterListServico.selectView(i, true);
                        lvServicos.getAdapter().getView(i, null, lvServicos).setBackgroundColor(Color.LTGRAY);
                        valorTotal += list.get(i).getValor();
                    }
                    atualizaViewTotalizadora();
                }
            }
        }
    }
}
