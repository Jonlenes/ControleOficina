package com.car.car.Gui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.car.car.DataBase.DbHelper;
import com.car.car.Modelo.Cliente;

import com.car.car.Modelo.Veiculo;
import com.car.car.Modelo.VeiculoBo;
import com.car.car.R;
import com.car.car.Exception.TreatException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Toolbar) findViewById(R.id.toolbar)).setTitle("CIT CAR");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CadastroVeiculoActivity.class));
                }
            });
        }
        expandableListView.setOnChildClickListener(childClickListenerVeiculo);
        expandableListView.setOnItemLongClickListener(childLongClickListenerVeiculo);

        new InitBdAsyncTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new SearchAsyncTask().execute();
    }

    private final ExpandableListView.OnChildClickListener childClickListenerVeiculo = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            Intent intent = new Intent(MainActivity.this, CadastroVeiculoActivity.class);
            intent.putExtra("idVeiculo", ((Veiculo) expandableListView.getExpandableListAdapter().getChild(groupPosition, childPosition)).getId());
            startActivity(intent);
            return false;
        }
    };

    private ExpandableListView.OnItemLongClickListener childLongClickListenerVeiculo = new ExpandableListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                final Veiculo veiculo = (Veiculo) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setTitle("Opções")
                        .setItems(R.array.dialog_options_veiculo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        deleteVeiculo(veiculo.getId());
                                        break;

                                }
                            }
                        })
                        .create().show();
            } catch (Exception e) {
                return false;
            }

            return false;
        }
    };

    private void deleteVeiculo(final Long idVeiculo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmação");
        builder.setMessage("Deseja realmente excluir?");
        builder.setNegativeButton("Não", null);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteVeiculoAsyncTask().execute(idVeiculo);
            }
        });
        builder.create().show();
    }

    private class DeleteVeiculoAsyncTask extends AsyncTask<Long, Void, Void> {
        private ProgressDialog progressDialog;
        private Exception exception;

        public DeleteVeiculoAsyncTask() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Deletando...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Long... params) {
            try {

                new VeiculoBo().delete(params[0]);

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
                TreatException.treat(MainActivity.this, exception);
            else
                new SearchAsyncTask().execute();
        }
    }

    private class InitBdAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Inicializando...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DbHelper.newInstance(MainActivity.this.getBaseContext());
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
                TreatException.treat(MainActivity.this, exception);
            else
                new SearchAsyncTask().execute();
        }
    }

    private class SearchAsyncTask extends AsyncTask<Void, Void, Map<Cliente, List<Veiculo> > > {
        private ProgressDialog progressDialog;
        private Exception exception;


        public SearchAsyncTask() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Buscando...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            MainActivity.this.findViewById(R.id.tvEmpty).setVisibility(View.INVISIBLE);
        }

        @Override
        protected Map<Cliente, List<Veiculo> > doInBackground(Void... params) {
            try {

                return new VeiculoBo().getMapForView();

            } catch (Exception e) {
                exception = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<Cliente, List<Veiculo> > map) {
            super.onPostExecute(map);

            progressDialog.dismiss();

            if (exception == null) {
                if (map.isEmpty())
                    MainActivity.this.findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
                else {
                    ExpandableListAdapter adapter = new ExpandableListAdapter(MainActivity.this, new ArrayList<>(map.keySet()), map);
                    expandableListView.setAdapter(adapter);
                }
            } else
                TreatException.treat(MainActivity.this, exception);
        }
    }
}
