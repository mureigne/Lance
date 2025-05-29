package com.example.lance;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.lance.TemplateSpinnerAdapter;

public class CreateNewPortfolioActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText editTextTitle, editTextSummary;
    private String savedName, savedEmail;
    private TextView textViewName, textViewContact;
    private LinearLayout layoutWorkSamples;
    private Spinner spinnerTemplates;
    private ImageView imageViewTemplatePreview;
    private int selectedTemplateResId = R.drawable.template2;
    private Button buttonAddSample, buttonGeneratePDF;

    private final ArrayList<View> workSampleViews = new ArrayList<>();
    private final HashMap<View, Uri> imageUris = new HashMap<>();

    private View currentImageRequestView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewportfolio);


        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        textViewName = findViewById(R.id.textViewName);
        textViewContact = findViewById(R.id.textViewContact);
        editTextSummary = findViewById(R.id.editTextSummary);
        layoutWorkSamples = findViewById(R.id.layoutWorkSamples);
        buttonAddSample = findViewById(R.id.buttonAddSample);
        buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF);
        spinnerTemplates = findViewById(R.id.spinnerTemplates);
        imageViewTemplatePreview = findViewById(R.id.imageViewTemplatePreview);
        String[] templateNames = {"Template 1", "Template 2", "Template 3"};
        final int[] templateResIds = {R.drawable.template1, R.drawable.template2, R.drawable.template3};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templateNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplates.setAdapter(adapter);
        spinnerTemplates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTemplateResId = templateResIds[position];
                imageViewTemplatePreview.setImageResource(selectedTemplateResId); // Optional: update preview too
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

// Default preview
        imageViewTemplatePreview.setImageResource(templateResIds[1]);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        savedName = prefs.getString("name", "");
        savedEmail = prefs.getString("email", "");

        textViewName.setText("Name: " + savedName);
        textViewContact.setText("Contact: " + savedEmail);

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
        String name = savedName;
        String contact = savedEmail;
        String summary = editTextSummary.getText().toString();

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595, pageHeight = 842;
        int y = 50;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(), selectedTemplateResId);
        Bitmap scaledTemplate = Bitmap.createScaledBitmap(templateBitmap, pageWidth, pageHeight, true);

        canvas.drawBitmap(scaledTemplate, 0, 0, null);
        paint.setTextSize(16);
        paint.setColor(android.graphics.Color.BLACK);

        y = 80;
        canvas.drawText("Portfolio Title: " + title, 30, y, paint);
        y += 25;
        canvas.drawText("Name: " + name, 30, y, paint);
        y += 25;
        canvas.drawText("Contact: " + contact, 30, y, paint);
        y += 25;

        paint.setTextSize(14);
        canvas.drawText("Summary:", 30, y, paint);
        y += 20;
        for (String line : summary.split("\n")) {
            canvas.drawText(line, 40, y, paint);
            y += 20;
        }

        y += 20;
        canvas.drawText("Work Samples:", 30, y, paint);
        y += 25;

        for (View sample : workSampleViews) {
            EditText sampleTitle = sample.findViewById(R.id.editTextSampleTitle);
            EditText sampleDesc = sample.findViewById(R.id.editTextSampleDescription);
            Uri imageUri = imageUris.get(sample);

            canvas.drawText("Title: " + sampleTitle.getText().toString(), 60, y, paint);
            y += 40;
            canvas.drawText("Description: " + sampleDesc.getText().toString(), 60, y, paint);
            y += 40;

            if (selectedTemplateResId == 0) {
                Toast.makeText(this, "Please select a template.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                    canvas.drawBitmap(scaled, 40, y, paint);
                    y += 210;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                y += 20;
            }

            y += 20;

            if (y > pageHeight - 100) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        pdfDocument.finishPage(page);

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
}
