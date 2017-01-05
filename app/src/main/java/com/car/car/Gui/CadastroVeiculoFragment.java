package com.car.car.Gui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.car.car.Modelo.Cliente;
import com.car.car.Modelo.ClienteDao;
import com.car.car.Modelo.Veiculo;
import com.car.car.Modelo.VeiculoBo;
import com.car.car.Modelo.VeiculoDao;
import com.car.car.R;
import com.car.car.Exception.TreatException;

import java.util.List;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;

/**
 * Created by asus on 05/08/2016.
 */
public class CadastroVeiculoFragment extends Fragment {

    private AutoCompleteTextView actMarca;
    private AutoCompleteTextView actModelo;
    private EditText edtPlaca;
    private EditText edtKm;
    private AutoCompleteTextView actNome;
    private EditText edtCpf;
    private EditText edtTelefone;

    private Long idVeiculo;
    private Veiculo veiculo;
    private boolean newClient;
    private boolean isNewInsert;

    public CadastroVeiculoFragment() {
    }

    public static CadastroVeiculoFragment newInstance() {
        return new CadastroVeiculoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_cadastro_veiculo, container, false);

        actMarca = (AutoCompleteTextView) rootView.findViewById(R.id.actMarca);
        actModelo = (AutoCompleteTextView) rootView.findViewById(R.id.actModelo);
        edtPlaca = (EditText) rootView.findViewById(R.id.edtPlaca);
        edtKm = (EditText) rootView.findViewById(R.id.edtKilometragem);;
        actNome = (AutoCompleteTextView) rootView.findViewById(R.id.actNome);;
        edtCpf = (EditText) rootView.findViewById(R.id.edtCpf);;
        edtTelefone = (EditText) rootView.findViewById(R.id.edtTelefone);

        MaskEditTextChangedListener maskCpf = new MaskEditTextChangedListener("###.###.###-##", edtCpf);
        edtCpf.addTextChangedListener(maskCpf);

        MaskEditTextChangedListener maskTelephone = new MaskEditTextChangedListener("(##)#####-####", edtTelefone);
        edtTelefone.addTextChangedListener(maskTelephone);

        edtPlaca.addTextChangedListener(new TextWatcher() {

            boolean digitando = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                digitando = (after > count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (digitando && edtPlaca.getText().toString().length() == 3)
                    edtPlaca.setText(edtPlaca.getText().toString().concat("-"));
                edtPlaca.setSelection(edtPlaca.getText().length());
            }
        });

        actNome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cliente cliente = ((Cliente) parent.getItemAtPosition(position));

                edtCpf.setText(cliente.getCpf());
                edtTelefone.setText(cliente.getTelefone());

                if (veiculo == null)
                    veiculo = new Veiculo();

                veiculo.setCliente(cliente);
                newClient = false;
            }
        });

        rootView.findViewById(R.id.btnServicos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CadastroVeiculoActivity) getActivity()).getViewPager().setCurrentItem(1);
            }
        });

        idVeiculo = ((CadastroVeiculoActivity) getActivity()).getIdVeiculo();
        newClient = true;

        new BuscaClientesAsyncTask().execute();

        if (idVeiculo != -1)
            new BuscaVeiculoAsyncTask().execute();

        isNewInsert = (idVeiculo == -1);
        newClient = (idVeiculo == -1);

        ArrayAdapter<String> adapterMarca = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray( R.array.array_marcas) );

        /*ArrayAdapter<String> adapterModelo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray( R.array.array_chevrolet) );

        actModelo.setAdapter(adapterModelo);*/

        actModelo.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1));

        actMarca.setAdapter(adapterMarca);
        actMarca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapterModelo = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, getResources().getStringArray( getIdArrayMarca((String) parent.getAdapter().getItem(position))));

                actModelo.setAdapter(adapterModelo);
                adapterModelo.notifyDataSetChanged();
            }
        });

        actMarca.setThreshold(1);
        actModelo.setThreshold(1);
        actNome.setThreshold(1);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cadastro de veículos");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cadastro_veiculo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_salvar) {
            if (validFields()) {

                if (veiculo == null) {

                    veiculo = new Veiculo(actMarca.getText().toString(),
                            actModelo.getText().toString(),
                            edtPlaca.getText().toString(),
                            Long.valueOf(edtKm.getText().toString()),
                            new Cliente(actNome.getText().toString(), edtCpf.getText().toString(),
                                    edtTelefone.getText().toString()),
                            ServicosFragment.getServicos());

                } else {

                    veiculo.setMarca(actMarca.getText().toString());
                    veiculo.setModelo(actModelo.getText().toString());
                    veiculo.setPlaca(edtPlaca.getText().toString());
                    veiculo.setKm(Long.valueOf(edtKm.getText().toString()));

                    if (newClient)
                        veiculo.setCliente(new Cliente(actNome.getText().toString(), edtCpf.getText().toString(), edtTelefone.getText().toString()));
                    else {
                        veiculo.getCliente().setNome(actNome.getText().toString());
                        veiculo.getCliente().setCpf(edtCpf.getText().toString());
                        veiculo.getCliente().setTelefone(edtTelefone.getText().toString());
                    }

                }
                veiculo.setServicos(ServicosFragment.getServicos());
                new InsertAsyncTask().execute(veiculo);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validFields() {
        if (actMarca.getText().toString().isEmpty()) {
            actMarca.setError("Este campo não pode ficar vazio.");
            return false;
        }

        if (actModelo.getText().toString().isEmpty()) {
            actModelo.setError("Este campo não pode ficar vazio.");
            return false;
        }

        if (edtPlaca.getText().toString().length() < 8) {
            edtPlaca.setError("Placa inválida.");
            return false;
        }

        if (edtKm.getText().toString().isEmpty()) {
            edtKm.setError("Preencha a quilometragem");
            return false;
        }

        if (actNome.getText().toString().isEmpty()) {
            actNome.setError("O proprietário deve ser preenchido.");
            return false;
        }

        if (edtCpf.getText().toString().length() < 14) {
            edtCpf.setError("Cpf inválido.");
            return false;
        }

        if (edtTelefone.getText().toString().length() < 13) {
            edtTelefone.setError("Telefone inválido.");
            return false;
        }

        return true;
    }

    private class InsertAsyncTask extends AsyncTask<Veiculo, Void, Void> {
        private ProgressDialog progressDialog;
        private Exception exception;

        public InsertAsyncTask() {
            progressDialog = new ProgressDialog(CadastroVeiculoFragment.this.getContext());

            progressDialog.setMessage("Salvando...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Veiculo... params) {
            try {

                new VeiculoBo().insertOrUpdate(params[0], isNewInsert, newClient);

            } catch (Exception e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            if (exception != null)
                TreatException.treat(CadastroVeiculoFragment.this.getContext(), exception);
            else
                CadastroVeiculoFragment.this.getActivity().finish();
        }
    }

    private class BuscaClientesAsyncTask extends AsyncTask< Void, Void, List<Cliente> > {
        private ProgressDialog progressDialog;
        private Exception exception;

        public BuscaClientesAsyncTask() {
            progressDialog = new ProgressDialog(CadastroVeiculoFragment.this.getContext());

            progressDialog.setMessage("Buscando...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<Cliente> doInBackground(Void... params) {
            try {

                return new ClienteDao().getAll();

            } catch (Exception e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Cliente> list) {
            super.onPostExecute(list);

            progressDialog.dismiss();

            if (exception != null) {

                TreatException.treat(CadastroVeiculoFragment.this.getActivity(), exception);

            } else {

                ArrayAdapter<Cliente> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_list_item_1, list);

                actNome.setAdapter(adapter);

            }
        }
    }

    private class BuscaVeiculoAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        private Exception exception;

        public BuscaVeiculoAsyncTask() {
            progressDialog = new ProgressDialog(CadastroVeiculoFragment.this.getContext());

            progressDialog.setMessage("Buscando...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                veiculo = new VeiculoDao().getById(idVeiculo);

            } catch (Exception e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            if (exception != null) {

                TreatException.treat(CadastroVeiculoFragment.this.getActivity(), exception);
                CadastroVeiculoFragment.this.getActivity().finish();

            } else {

                actMarca.setText(veiculo.getMarca());
                actModelo.setText(veiculo.getModelo());
                edtPlaca.setText(veiculo.getPlaca());
                edtKm.setText(String.valueOf(veiculo.getKm()));
                actNome.setText(veiculo.getCliente().getNome());
                edtCpf.setText(veiculo.getCliente().getCpf());
                edtTelefone.setText(veiculo.getCliente().getTelefone());

            }
        }
    }

    private int getIdArrayMarca(String s) {
        switch (s) {
            case "Chery": return R.array.array_cherry;
            case "Chevrolet": return R.array.array_chevrolet;
            case "Citroën": return R.array.array_citroen;
            case "Fiat": return R.array.array_fiat;
            case "Ford": return R.array.array_ford;
            case "Honda": return R.array.array_honda;
            case "Hyundai": return R.array.array_hyundai;
            case "Jac Motors": return R.array.array_jac_motors;
            case "Jeep": return R.array.array_jeep;
            case "Kia": return R.array.array_kia;
            case "Land Rover": return R.array.array_land_rover;
            case "Mitsubishi": return R.array.array_mitsubishi;
            case "Nissan": return R.array.array_nissan;
            case "Peugeot": return R.array.array_peugeot;
            case "Renault": return R.array.array_renault;
            case "Ssangyong": return R.array.array_ssangyong;
            case "Toyota": return R.array.array_toyota;
            case "Volkswagen": return R.array.array_volkswagen;
        }
        return 0;
    }
}