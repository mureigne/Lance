package com.example.lance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
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
    private CheckBox checkBoxIncludeName, checkBoxIncludeEmail, checkBoxIncludeContact, checkBoxUseTemplate;
    private Spinner spinnerTemplates;
    private ImageView imageViewTemplatePreview;
    private LinearLayout layoutWorkSamples;
    private int selectedTemplateResId = R.drawable.template2;

    private ArrayList<View> workSampleViews = new ArrayList<>();
    private HashMap<View, Uri> imageUris = new HashMap<>();
    private View currentImageRequestView;

    private String savedName, savedEmail, savedContact;
    private Database dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnewportfolio);

        dbHelper = new Database(this);

        // Initialize UI components
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSummary = findViewById(R.id.editTextSummary);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewContact = findViewById(R.id.textViewContact);
        checkBoxIncludeName = findViewById(R.id.checkBoxIncludeName);
        checkBoxIncludeEmail = findViewById(R.id.checkBoxIncludeEmail);
        checkBoxIncludeContact = findViewById(R.id.checkBoxIncludeContact);
        checkBoxUseTemplate = findViewById(R.id.checkBoxUseTemplate);
        spinnerTemplates = findViewById(R.id.spinnerTemplates);
        imageViewTemplatePreview = findViewById(R.id.imageViewTemplatePreview);
        layoutWorkSamples = findViewById(R.id.layoutWorkSamples);

        Button buttonAddSample = findViewById(R.id.buttonAddSample);
        Button buttonGeneratePDF = findViewById(R.id.buttonGeneratePDF);

        // Load current user info
        dbHelper = new Database(this);
        String currentUsername = CurrentUser.getUsername();
        if (currentUsername == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        loadUserInfo(currentUsername);

        // Template spinner setup
        setupTemplateSpinner();

        // Add first work sample section
        addWorkSample();

        // Button listeners
        buttonAddSample.setOnClickListener(v -> addWorkSample());
        buttonGeneratePDF.setOnClickListener(v -> generatePDFPortfolio());
    }

    private void loadUserInfo(String username) {
        Cursor cursor = dbHelper.getUserByUsername(username);
        if (cursor.moveToFirst()) {
            savedName = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_NAME));
            savedEmail = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_EMAIL));
            savedContact = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_PHONE));
            textViewName.setText("Name: " + savedName);
            textViewEmail.setText("Email: " + savedEmail);
            textViewContact.setText("Contact: " + savedContact);
        }
        cursor.close();
    }

    private void setupTemplateSpinner() {
        String[] names = {"Template 1", "Template 2", "Template 3"};
        int[] resIds = {R.drawable.template1, R.drawable.template2, R.drawable.template3};

        spinnerTemplates.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names));
        spinnerTemplates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedTemplateResId = resIds[pos];
                imageViewTemplatePreview.setImageResource(selectedTemplateResId);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        imageViewTemplatePreview.setImageResource(selectedTemplateResId);
    }

    private void addWorkSample() {
        View sample = LayoutInflater.from(this)
                .inflate(R.layout.item_work_sample, layoutWorkSamples, false);
        Button btn = sample.findViewById(R.id.buttonSelectImage);
        btn.setOnClickListener(v -> {
            currentImageRequestView = sample;
            pickImageFromGallery();
        });
        layoutWorkSamples.addView(sample);
        workSampleViews.add(sample);
    }

    private void pickImageFromGallery() {
        startActivityForResult(
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                PICK_IMAGE_REQUEST
        );
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_IMAGE_REQUEST && res == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (currentImageRequestView != null) {
                ImageView iv = currentImageRequestView.findViewById(R.id.imageViewSample);
                try {
                    iv.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                    imageUris.put(currentImageRequestView, uri);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void generatePDFPortfolio() {
        PdfDocument pdf = new PdfDocument();
        Paint paint = new Paint();
        int w = 595, h = 842, y = 50;

        PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(w, h, 1).create();
        PdfDocument.Page page = pdf.startPage(pi);
        Canvas canvas = page.getCanvas();

        if (checkBoxUseTemplate.isChecked()) {
            Bitmap bg = BitmapFactory.decodeResource(getResources(), selectedTemplateResId);
            canvas.drawBitmap(Bitmap.createScaledBitmap(bg, w, h, true), 0, 0, null);
        }

        paint.setTextSize(16);
        paint.setColor(Color.BLACK);
        canvas.drawText("Title: " + editTextTitle.getText().toString(), 30, y += 30, paint);
        if (checkBoxIncludeName.isChecked()) canvas.drawText("Name: " + savedName, 30, y += 25, paint);
        if (checkBoxIncludeEmail.isChecked()) canvas.drawText("Email: " + savedEmail, 30, y += 25, paint);
        if (checkBoxIncludeContact.isChecked()) canvas.drawText("Contact: " + savedContact, 30, y += 25, paint);

        canvas.drawText("Summary:", 30, y += 30, paint);
        for (String line : editTextSummary.getText().toString().split("\n")) {
            canvas.drawText(line, 40, y += 20, paint);
        }

        canvas.drawText("Work Samples:", 30, y += 40, paint);
        for (View s : workSampleViews) {
            CheckBox cb = s.findViewById(R.id.checkBoxInclude);
            if (!cb.isChecked()) continue;
            EditText et = s.findViewById(R.id.editTextSampleTitle);
            EditText ed = s.findViewById(R.id.editTextSampleDescription);
            Uri uri = imageUris.get(s);
            canvas.drawText("Title: " + et.getText(), 60, y += 30, paint);
            canvas.drawText("Description: " + ed.getText(), 60, y += 30, paint);
            if (uri != null) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    canvas.drawBitmap(Bitmap.createScaledBitmap(bmp, 250, 250, true), 60, y += 10, paint);
                    y += 260;
                } catch (IOException ignored) {}
            }
            if (y > h - 100) {
                pdf.finishPage(page);
                pi = new PdfDocument.PageInfo.Builder(w, h, pdf.getPages().size() + 1).create();
                page = pdf.startPage(pi);
                canvas = page.getCanvas();
                y = 50;
            }
        }
        pdf.finishPage(page);

        File out = new File(getExternalFilesDir(null), "Portfolio.pdf");
        try (FileOutputStream fos = new FileOutputStream(out)) {
            pdf.writeTo(fos);
            Toast.makeText(this, "Saved: " + out.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
        pdf.close();

        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", out);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, "application/pdf");
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "No PDF viewer found.", Toast.LENGTH_SHORT).show();
        }
    }
}
