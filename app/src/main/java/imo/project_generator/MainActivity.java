package imo.project_generator;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class MainActivity extends Activity {
    private EditText packageNameEditText;
    private Button generateButton;
    private static final int OPEN_DIRECTORY_REQUEST_CODE = 123;
    private static String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageNameEditText = findViewById(R.id.packageNameEditText);
        generateButton = findViewById(R.id.generateButton);

        generateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    start();
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_DIRECTORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri directoryUri = data.getData();
                if (directoryUri != null) {
                    getContentResolver().takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                    resume(directoryUri);
                }
            }
        }
    }

    private void start() {
        packageName = packageNameEditText.getText().toString().trim();
        if (packageName.isEmpty()) {
            showToast("Please enter a package name");
            return;
        }
        if(packageName.matches(".*[^a-zA-Z._].*")){
            showToast("package name only allows alphabet, period (.) and underscore (_)");
            return;
        }
        if(! packageName.contains(".")){
            showToast("package name must have a period (.)");
            return;
        }
        if(! packageName.toLowerCase().equals(packageName)){
            showToast("package name is suggested to be lowercase");
            return;
        }
        requestDirectoryPermission();
    }

    private void resume(Uri directoryUri) {
        generateFiles(directoryUri);
    }

    private void generateFiles(Uri directoryUri) {
        List<FileContent> fileContents = new ArrayList<>();
        fileContents.add(new FileContent("build.gradle", readFileFromAssets("to_generate/build.gradle")));
        fileContents.add(new FileContent("settings.gradle", readFileFromAssets("to_generate/settings.gradle")));
        fileContents.add(new FileContent("gradle.properties", readFileFromAssets("to_generate/gradle.properties")));

        fileContents.add(new FileContent("app/build.gradle", readFileFromAssets("to_generate/app/build.gradle")));
        fileContents.add(new FileContent("app/proguard-rules.pro", readFileFromAssets("to_generate/app/proguard-rules.pro")));

        fileContents.add(new FileContent("app/src/main/AndroidManifest.xml", readFileFromAssets("to_generate/app/src/main/AndroidManifest.xml")));
        fileContents.add(new FileContent("app/src/main/res/layout/activity_main.xml", readFileFromAssets("to_generate/app/src/main/res/layout/activity_main.xml")));
        fileContents.add(new FileContent("app/src/main/res/values/strings.xml", readFileFromAssets("to_generate/app/src/main/res/values/strings.xml")));
        fileContents.add(new FileContent("app/src/main/res/values/styles.xml", readFileFromAssets("to_generate/app/src/main/res/values/styles.xml")));
        fileContents.add(new FileContent("app/src/main/res/drawable/ic_launcher.png", readFileFromAssets("to_generate/app/src/main/res/drawable/ic_launcher.png")));
        
        String packagePath = packageName.replace('.', '/');
        fileContents.add(new FileContent("app/src/main/java/" + packagePath + "/MainActivity.java", readFileFromAssets("to_generate/app/src/main/java/MainActivity.java")));
        fileContents.add(new FileContent(".gitignore", readFileFromAssets("to_generate/gitignore")));
        
        try {
            createFilesAndDirs(MainActivity.this, directoryUri, fileContents);
            showToast("Files created successfully");
            finishAffinity();
            
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            packageNameEditText.setText(sw.toString());
        }
    }

    private void createFilesAndDirs(Context context, Uri treeUri, List<FileContent> fileContents) throws IOException {
        ContentResolver resolver = context.getContentResolver();

        for (FileContent fileContent : fileContents) {
            String[] pathSegments = fileContent.path.split("/");
            Uri currentDirUri = null;

            String parentDocumentId = DocumentsContract.getTreeDocumentId(treeUri);

            for (int i = 0; i < pathSegments.length - 1; i++) {
                String segment = pathSegments[i];
                Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocumentId);
                Uri nextDirUri = findFileInDirectory(resolver, childrenUri, segment);

                if (nextDirUri == null) {
                    currentDirUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, parentDocumentId);
                    nextDirUri = DocumentsContract.createDocument(resolver, currentDirUri, DocumentsContract.Document.MIME_TYPE_DIR, segment);
                }
                if (nextDirUri == null) throw new IOException("Cant create folder: " + segment);
                
                parentDocumentId = DocumentsContract.getDocumentId(nextDirUri);
            }

            currentDirUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, parentDocumentId);
            String fileName = pathSegments[pathSegments.length - 1];

            String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
            String mimeType = null;

            if (extension != null) mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
            if (mimeType == null) mimeType = "application/octet-stream";
            
            Uri newFileUri = DocumentsContract.createDocument(resolver, currentDirUri, mimeType, fileName);
            if (newFileUri == null) throw new IOException("Failed to create file: " + fileName);
            
            OutputStream outputStream = null;
            OutputStreamWriter writer = null;
            try {
                outputStream = resolver.openOutputStream(newFileUri);
                writer = new OutputStreamWriter(outputStream);
                if (fileContent.content != null && !fileContent.content.isEmpty()) {
                    writer.write(fileContent.content);
                    writer.flush();
                }
            } finally {
                if (writer != null) writer.close(); 
            }
        }
    }

    private Uri findFileInDirectory(ContentResolver resolver, Uri childrenUri, String displayName) {
        try{
            Cursor cursor = resolver.query(
                childrenUri,
                new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                null, null, null);
                
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(1);
                    if (displayName.equals(name)) {
                        String docId = cursor.getString(0);
                        return DocumentsContract.buildDocumentUriUsingTree(childrenUri, docId);
                    }
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            packageNameEditText.setText(sw.toString());
        }
        return null;
    }

    private void requestDirectoryPermission() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        );
        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private String readFileFromAssets(String filePath){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = this.getAssets().open(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }

        return stringBuilder.toString();
    }

    private static class FileContent {
        String path;
        String content;

        FileContent(String path, String content) {
            this.path = path;
            this.content = content;
        }
    }
}
