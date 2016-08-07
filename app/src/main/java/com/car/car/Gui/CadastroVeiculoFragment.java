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

    private EditText edtPlaca;
    private EditText edtVeiculo;
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

        edtPlaca = (EditText) rootView.findViewById(R.id.edtPlaca);
        edtVeiculo = (EditText) rootView.findViewById(R.id.edtVeiculo);;
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

                    veiculo = new Veiculo(edtPlaca.getText().toString(),
                            edtVeiculo.getText().toString(),
                            new Cliente(actNome.getText().toString(), edtCpf.getText().toString(),
                                    edtTelefone.getText().toString()),
                            ServicosFragment.getServicos());

                } else {

                    veiculo.setPlaca(edtPlaca.getText().toString());
                    veiculo.setDescrisao(edtVeiculo.getText().toString());

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
        if (edtPlaca.getText().toString().length() < 8) {
            edtPlaca.setError("Placa inválida.");
            return false;
        }

        if (edtVeiculo.getText().toString().isEmpty()) {
            edtVeiculo.setError("O veículo deve ser preenchido.");
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

                edtPlaca.setText(veiculo.getPlaca());
                edtVeiculo.setText(veiculo.getDescrisao());
                actNome.setText(veiculo.getCliente().getNome());
                edtCpf.setText(veiculo.getCliente().getCpf());
                edtTelefone.setText(veiculo.getCliente().getTelefone());

            }
        }
    }
}