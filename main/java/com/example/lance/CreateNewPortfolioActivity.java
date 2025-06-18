package com.example.lance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CreateNewPortfolioActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText editTextTitle, editTextSummary;
    private TextView textViewName, textViewEmail, textViewContact;
    private CheckBox checkBoxName, checkBoxEmail, checkBoxContact, checkBoxUseTemplate;
    private Spinner spinnerTemplates;
    private ImageView imageViewTemplatePreview;
    private LinearLayout layoutWorkSamples;
    private int selectedTemplateResId = R.drawable.template2;

    private ArrayList<View> workSampleViews = new ArrayList<>();
    private HashMap<View, Uri> imageUris = new HashMap<>();
    private View currentImageRequestView;
    private MyDataBaseHelper dbHelper;

    private String savedName, savedEmail, savedContact;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewportfolio);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSummary = findViewById(R.id.editTextSummary);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewContact = findViewById(R.id.textViewContact);
        checkBoxName = findViewById(R.id.checkBoxIncludeName);
        checkBoxEmail = findViewById(R.id.checkBoxIncludeEmail);
        checkBoxContact = findViewById(R.id.checkBoxIncludeContact);

        layoutWorkSamples = findViewById(R.id.layoutWorkSamples);
        spinnerTemplates = findViewById(R.id.spinnerTemplates);
        imageViewTemplatePreview = findViewById(R.id.imageViewTemplatePreview);

        Button buttonAddSample = findViewById(R.id.buttonAddSample);
        Button buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF);

        dbHelper = new MyDataBaseHelper(this);
        String currentUsername = CurrentUser.getUsername();

        if (currentUsername == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Cursor cursor = dbHelper.getUserByUsername(currentUsername);
        if (cursor.moveToFirst()) {
            savedName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            savedEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            savedContact = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

            textViewName.setText("Name: " + savedName);
            textViewEmail.setText("Email: " + savedEmail);
            textViewContact.setText("Contact: " + savedContact);
        }

        cursor.close();

        String[] templateNames = {"Template 1", "Template 2", "Template 3"};
        final int[] templateResIds = {R.drawable.template1, R.drawable.template2, R.drawable.template3};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templateNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplates.setAdapter(adapter);
        spinnerTemplates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTemplateResId = templateResIds[position];
                imageViewTemplatePreview.setImageResource(selectedTemplateResId);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        imageViewTemplatePreview.setImageResource(selectedTemplateResId);
        addWorkSample();

        buttonAddSample.setOnClickListener(v -> addWorkSample());
        buttonGeneratePDF.setOnClickListener(v -> generatePDFPortfolio());
    }

    private void addWorkSample() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View sampleView = inflater.inflate(R.layout.item_work_sample, layoutWorkSamples, false);

        ImageView imageViewSample = sampleView.findViewById(R.id.imageViewSample);
        Button buttonSelectImage = sampleView.findViewById(R.id.buttonSelectImage);
        CheckBox checkBoxInclude = new CheckBox(this);
        checkBoxInclude.setText("Include in PDF");

        ((LinearLayout) sampleView).addView(checkBoxInclude, 0);

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
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        int pageWidth = 595, pageHeight = 842;
        int y = 50;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        if (checkBoxUseTemplate.isChecked()) {
            Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(), selectedTemplateResId);
            Bitmap scaled = Bitmap.createScaledBitmap(templateBitmap, pageWidth, pageHeight, true);
            canvas.drawBitmap(scaled, 0, 0, null);
        }

        paint.setTextSize(16);
        paint.setColor(Color.BLACK);

        canvas.drawText("Title: " + editTextTitle.getText().toString(), 30, y += 30, paint);
        if (checkBoxName.isChecked()) canvas.drawText("Name: " + savedName, 30, y += 25, paint);
        if (checkBoxEmail.isChecked()) canvas.drawText("Email: " + savedEmail, 30, y += 25, paint);
        if (checkBoxContact.isChecked()) canvas.drawText("Contact: " + savedContact, 30, y += 25, paint);

        canvas.drawText("Summary:", 30, y += 30, paint);
        for (String line : editTextSummary.getText().toString().split("\n")) {
            canvas.drawText(line, 40, y += 20, paint);
        }

        canvas.drawText("Work Samples:", 30, y += 40, paint);

        for (View sample : workSampleViews) {
            CheckBox includeCheck = (CheckBox) ((LinearLayout) sample).getChildAt(0);
            if (!includeCheck.isChecked()) continue;

            EditText sampleTitle = sample.findViewById(R.id.editTextSampleTitle);
            EditText sampleDesc = sample.findViewById(R.id.editTextSampleDescription);
            Uri imageUri = imageUris.get(sample);

            canvas.drawText("Title: " + sampleTitle.getText().toString(), 60, y += 30, paint);
            canvas.drawText("Description: " + sampleDesc.getText().toString(), 60, y += 30, paint);

            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                    canvas.drawBitmap(scaled, 60, y += 10, paint);
                    y += 260;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (y > pageHeight - 100) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
        }

        pdfDocument.finishPage(page);

        File pdfFile = new File(getExternalFilesDir(null), "Portfolio.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No PDF viewer found.", Toast.LENGTH_SHORT).show();
        }
    }
}