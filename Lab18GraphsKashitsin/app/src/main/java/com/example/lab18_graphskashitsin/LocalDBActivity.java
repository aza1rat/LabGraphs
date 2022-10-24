package com.example.lab18_graphskashitsin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import model.Graph;

public class LocalDBActivity extends AppCompatActivity {
    RadioButton rbUndirect;
    RadioButton rbDirect;
    EditText inputedName;
    TextView tvId;
    TextView tvNow;
    ListView lvGraphs;
    ArrayAdapter<Graph> adapterGraph;
    ArrayList<Graph> listGraphs = new ArrayList<Graph>();
    Graph selectedGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_db);
        Intent intent = getIntent();
        rbUndirect = findViewById(R.id.rb_undirect);
        rbDirect = findViewById(R.id.rb_direct);
        inputedName = findViewById(R.id.input_nameGraph);
        tvId = findViewById(R.id.tv_name);
        tvNow = findViewById(R.id.tv_now);
        lvGraphs = findViewById(R.id.lv_graphsDBl);
        adapterGraph = new ArrayAdapter<Graph>(this, android.R.layout.simple_list_item_1,listGraphs);
        lvGraphs.setAdapter(adapterGraph);

        lvGraphs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGraph = adapterGraph.getItem(position);
                updateSelectedView(selectedGraph.name, selectedGraph.isDirectable,selectedGraph.id);
            }
        });
        updateList();
        updateNowView();
    }

    void updateList()
    {
        listGraphs.clear();
        DB.helper.getAllGraph(listGraphs);
        adapterGraph.notifyDataSetChanged();
    }

    void updateSelectedView(String name, boolean isDirectable, int id)
    {
        inputedName.setText(name);
        if (isDirectable)
            rbDirect.setChecked(true);
        else
            rbUndirect.setChecked(true);
        tvId.setText("Выбрано (" + id + "): ");
    }

    void updateNowView()
    {
        String direct;
        if (GraphHelper.isDirectable)
            direct = "Ориентированный";
        else
             direct = "Неориентированный";
        tvNow.setText("Текущий граф: " + GraphHelper.idGraph + ") " + GraphHelper.name + " " + direct);
    }

    void addGraph(int id, String name)
    {
        if (rbUndirect.isChecked())
            DB.helper.addNotDirectlyGraph(id, name);
        if (rbDirect.isChecked())
            DB.helper.addDirectlyGraph(id, name);
    }

    public void onCreateGraph(View v)
    {
        int idLastGraph = DB.helper.getMaxId("Graph");
        addGraph(idLastGraph + 1, inputedName.getText().toString());
        updateList();
    }

    public void onLoadGraph(View v)
    {
        if (selectedGraph.id == -1)
            return;
        GraphHelper.idGraph = selectedGraph.id;
        GraphHelper.name = selectedGraph.name;
        GraphHelper.isDirectable = selectedGraph.isDirectable;
        GraphHelper.link.clear();
        GraphHelper.node.clear();
        DB.helper.load(selectedGraph.id);
        updateNowView();
    }

    public void onRenameGraph(View v)
    {
        if (selectedGraph.id == -1)
            return;
        DB.helper.renameGraph(selectedGraph.id, inputedName.getText().toString());
        updateList();
    }

    public void onDeleteGraph(View v)
    {
        DB.helper.deleteGraph(selectedGraph.id);
        if (GraphHelper.idGraph == selectedGraph.id)
        {
            GraphHelper.idGraph = 0;
            GraphHelper.name = "Name";
            GraphHelper.isDirectable = true;
            GraphHelper.link.clear();
            GraphHelper.node.clear();
        }
        updateList();
        updateNowView();
        selectedGraph.id = -1;
    }

    public void onCopyGraph(View v)
    {
        if (selectedGraph.id == -1)
            return;
        DB.helper.copy(selectedGraph.id,selectedGraph.name,selectedGraph.isDirectable);
        updateList();
    }

    public void onSaveGraph(View v)
    {
        int idNode = DB.helper.getMaxId("Node");
        int idLink = DB.helper.getMaxId("Link");
        if (GraphHelper.idGraph != 0)
        {
            DB.helper.delete();
        }
        else
        {
            GraphHelper.idGraph = DB.helper.getMaxId("Graph") + 1;
            GraphHelper.name = inputedName.getText().toString();
            GraphHelper.isDirectable = rbDirect.isChecked();
            updateNowView();
            addGraph(GraphHelper.idGraph, GraphHelper.name);
        }
        DB.helper.save(idNode,idLink,GraphHelper.node,GraphHelper.link, GraphHelper.idGraph);
        updateList();
    }
}