package com.example.lance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateNewPortfolioActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText editTextTitle, editTextName, editTextContact, editTextSummary;
    private LinearLayout layoutWorkSamples;
    private Button buttonAddSample, buttonGeneratePDF;

    // List to hold dynamically added sample views and their data
    private final ArrayList<View> workSampleViews = new ArrayList<>();
    private final HashMap<View, Uri> imageUris = new HashMap<>();

    private View currentImageRequestView; // tracks which view requested an image

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewportfolio);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextName = findViewById(R.id.editTextName);
        editTextContact = findViewById(R.id.editTextContact);
        editTextSummary = findViewById(R.id.editTextSummary);
        layoutWorkSamples = findViewById(R.id.layoutWorkSamples);
        buttonAddSample = findViewById(R.id.buttonAddSample);
        buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF);

        // Add first sample on load (optional)
        addWorkSample();

        buttonAddSample.setOnClickListener(v -> addWorkSample());

        buttonGeneratePDF.setOnClickListener(v -> generatePDFPortfolio());
    }

    private void addWorkSample() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View sampleView = inflater.inflate(R.layout.item_work_sample, layoutWorkSamples, false);

        ImageView imageViewSample = sampleView.findViewById(R.id.imageViewSample);
        Button buttonSelectImage = sampleView.findViewById(R.id.buttonSelectImage);

        buttonSelectImage.setOnClickListener(v -> {
            currentImageRequestView = sampleView;
            pickImageFromGallery();
        });

        layoutWorkSamples.addView(sampleView);
        workSampleViews.add(sampleView);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if (currentImageRequestView != null) {
                ImageView imageView = currentImageRequestView.findViewById(R.id.imageViewSample);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                    imageUris.put(currentImageRequestView, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void generatePDFPortfolio() {
        String title = editTextTitle.getText().toString();
        String name = editTextName.getText().toString();
        String contact = editTextContact.getText().toString();
        String summary = editTextSummary.getText().toString();

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595, pageHeight = 842; // A4 in points
        int y = 50;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        paint.setTextSize(16);
        canvas.drawText("Portfolio Title: " + title, 30, y, paint); y += 25;
        canvas.drawText("Name: " + name, 30, y, paint); y += 25;
        canvas.drawText("Contact: " + contact, 30, y, paint); y += 25;

        paint.setTextSize(14);
        canvas.drawText("Summary:", 30, y, paint); y += 20;

        for (String line : summary.split("\n")) {
            canvas.drawText(line, 40, y, paint);
            y += 20;
        }

        y += 20;
        canvas.drawText("Work Samples:", 30, y, paint); y += 25;

        for (View sample : workSampleViews) {
            EditText sampleTitle = sample.findViewById(R.id.editTextSampleTitle);
            EditText sampleDesc = sample.findViewById(R.id.editTextSampleDescription);
            Uri imageUri = imageUris.get(sample);

            canvas.drawText("Title: " + sampleTitle.getText().toString(), 40, y, paint); y += 20;
            canvas.drawText("Description: " + sampleDesc.getText().toString(), 40, y, paint); y += 20;

            // Draw image if available
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                    canvas.drawBitmap(scaled, 40, y, paint);
                    y += 210;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                y += 20;
            }

            y += 20;

            // New page if needed
            if (y > pageHeight - 100) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        pdfDocument.finishPage(page);

        // Save PDF to Downloads or Documents folder
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(this, "Failed to create Documents directory", Toast.LENGTH_SHORT).show();
            return;
        }

        File pdfFile = new File(getExternalFilesDir(null), "Portfolio.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF saved to: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (pdfFile.exists()) {
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    pdfFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No PDF viewer found. Please install one.", Toast.LENGTH_LONG).show();
            }
        }

        pdfDocument.close();
    }

            // Here you could pass titleText, descText, and imageUri into your PDF builder
        }