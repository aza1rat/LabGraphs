package com.example.lab18_graphskashitsin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    GraphView gv;
    Button buttonOK;
    Button buttonCancel;
    EditText data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv = findViewById(R.id.gv_paintGraphs);
        DB.helper = new DBHelper(this,"graphs.db",null,1);
    }

    public void onAddClick(View v)
    {
        gv.addNode();
    }

    public void onRemoveClick(View v)
    {
        gv.removeSelectedNodes();
    }

    public void onRemoveLink(View v) {gv.removeSelectedLink();}

    public void onLinkClick(View v)
    {
        gv.linkSelectedNodes();
    }

    public void onToLDBClick(View v)
    {
        Intent intent = new Intent(this, LocalDBActivity.class);
        startActivity(intent);
    }


    public void onNodeName(View v)
    {
        AlertDialog dlg = makeDialog("Надпись узла","Имя:");
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setNameOfSelectedNode(data.getText().toString());
                dlg.cancel();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setNameOfSelectedNode(null);
                dlg.cancel();
            }
        });
        dlg.show();
    }

    public void onLinkName(View v)
    {
        AlertDialog dlg = makeDialog("Надпись соединения", "Имя:");
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setValueOfSelectedLink(data.getText().toString());
                dlg.cancel();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setValueOfSelectedLink(null);
                dlg.cancel();
            }
        });
        dlg.show();
    }

    AlertDialog makeDialog(String str, String nameBox)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dlg = builder.create();
        dlg.setTitle(str);
        LayoutInflater linflater = getLayoutInflater();
        View dialogView = linflater.inflate(R.layout.activity_edit, null);
        TextView tv = dialogView.findViewById(R.id.tv_field);
        data = dialogView.findViewById(R.id.input_stg);
        tv.setText(nameBox);
        buttonOK = dialogView.findViewById(R.id.btn_OK);
        buttonCancel = dialogView.findViewById(R.id.btn_cancel);
        dlg.setView(dialogView);
        return dlg;
    }
}