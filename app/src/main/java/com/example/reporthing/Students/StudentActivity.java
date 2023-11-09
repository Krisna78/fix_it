package com.example.reporthing.Students;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.reporthing.Auth.AuthActivity;
import com.example.reporthing.DB_url;
import com.example.reporthing.R;
import com.example.reporthing.Students.Adapters.SemesterAdapter;
import com.example.reporthing.Students.Models.ProfileResponse;
import com.example.reporthing.Students.Models.SemesterData;
import com.example.reporthing.Students.Models.SemesterResponse;
import com.example.reporthing.databinding.ActivityStudentBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView name,kelas,nisn;
    Gson gson = new Gson();
    RecyclerView recyclerView;
    ActivityStudentBinding binding;
    SemesterAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<SemesterData> dataSemester;

    SemesterData semesterModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = findViewById(R.id.studentName);
        kelas = findViewById(R.id.studentClass);
        nisn = findViewById(R.id.studentNISN);
        recyclerView = findViewById(R.id.recyclerSemester);

        Button out = findViewById(R.id.out);

        SharedPreferences sp = getSharedPreferences("isLogin",Context.MODE_PRIVATE);
        dataSemester = new ArrayList<>();
        showProfile(sp.getString("id",null));
        getdata(sp.getString("nisn",null));

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
                        builder.setMessage(R.string.confirm_dialog_logout)
                        .setTitle(R.string.confirm_logout)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sp.edit().putBoolean("isLogin",false).apply();
                                Intent intent = new Intent(getApplicationContext(),AuthActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signOut) {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProfile(String profileID) {
        StringRequest request = new StringRequest(Request.Method.GET, DB_url.urlProfile + "?id=" + profileID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ProfileResponse profileResponse = gson.fromJson(response.toString(),ProfileResponse.class);
                name.setText("Nama: " + profileResponse.getUserProfile().get(0).getNamaSiswa());
                kelas.setText("Kelas: "+ profileResponse.getUserProfile().get(0).getNamaKelas());
                nisn.setText("NISN: "+ profileResponse.getUserProfile().get(0).getNisn());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Nullable
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("id",profileID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
    private void getdata(String idSiswa) {
        StringRequest request = new StringRequest(Request.Method.GET, DB_url.urlSemesters + "?id=" + idSiswa, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dataSemester = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("user_profile");
                    for (int i = 0; i < jsonArray.length();i++) {
                        semesterModel = new SemesterData();
                        JSONObject data = jsonArray.getJSONObject(i);
                        semesterModel.setNisn(data.getString("nisn"));
                        semesterModel.setSemesterKe(data.getString("semester_ke"));
                        semesterModel.setThnAjaran(data.getString("thn_ajaran"));
                        dataSemester.add(semesterModel);
                    }
                    linearLayoutManager = new LinearLayoutManager(StudentActivity.this,LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    adapter = new SemesterAdapter(StudentActivity.this,dataSemester);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentActivity.this, "Error" +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Nullable
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("id",idSiswa);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}