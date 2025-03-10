package com.example.crudproductos;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crudproductos.Modelo.Producto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private DatabaseHelper databaseHelper;
    private Button btnAgregarProducto, btnReporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);
        btnReporte = findViewById(R.id.btnReporte);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        Log.d("DB_DEBUG", "Cantidad de productos: " + databaseHelper.contarRegistros());
        actualizarLista();

        btnAgregarProducto.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AgregarProductoActivity.class));
        });

        btnReporte.setOnClickListener(v -> {
            generarPDF();
        });
    }

    private void actualizarLista() {
        List<Producto> listaProductos = databaseHelper.obtenerProductos();
        adapter = new ProductoAdapter(this, listaProductos);
        recyclerView.setAdapter(adapter);
    }

    private void generarPDF() {
        List<Producto> listaProductos = databaseHelper.obtenerProductos();

        if (listaProductos.isEmpty()) {
            Log.e("PDF", "No hay productos para generar el PDF.");
            return;
        }

        PdfDocument documentoPDF = new PdfDocument();
        Paint paint = new Paint();
        int startX = 50, startY = 50, lineHeight = 30;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page pagina = documentoPDF.startPage(pageInfo);
        Canvas canvas = pagina.getCanvas();
        paint.setTextSize(18);
        canvas.drawText("Listado de Productos", startX, startY, paint);

        paint.setTextSize(14);
        startY += 40;

        for (Producto producto : listaProductos) {
            canvas.drawText("ID: " + producto.getId() + " - " + producto.getNombre() +
                    " - Precio: " + producto.getPrecio(), startX, startY, paint);
            startY += lineHeight;
        }

        documentoPDF.finishPage(pagina);

        File directorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ReporteProductos.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(directorio);
            documentoPDF.writeTo(fos);
            documentoPDF.close();
            fos.close();
            Log.d("PDF", "PDF generado en: " + directorio.getAbsolutePath());
        } catch (IOException e) {
            Log.e("PDF", "Error al generar el PDF", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarLista();
    }
}
