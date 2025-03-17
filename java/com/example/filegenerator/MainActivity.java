package com.example.filegenerator;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private EditText packageNameEditText;
    private Button generateButton;
    private String baseDir = Environment.getExternalStorageDirectory() + "/AppProjects/";

    // File contents
    private String buildGradle_top_level = "";
    private String settingsGradle = "include ':app'";
    private String gitIgnore = "/build";
    private String buildGradle_app_level = "";
    private String proguardRules = "";
    private String androidManifest = "";
    private String java = "";
    private String resMainXml = "";
    private String resStringsXml = "";
    private String resStylesXml = "";
    private String resStylesXmlv21 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageNameEditText = findViewById(R.id.packageNameEditText);
        generateButton = findViewById(R.id.generateButton);

        generateButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					generateFiles();
				}
			});
    }

    private void generateFiles() {
        String packageName = packageNameEditText.getText().toString().trim();
        if (packageName.isEmpty()) {
            showToast("Please enter a package name");
            return;
        }

        if (!hasStoragePermission()) {
            requestStoragePermission();
            return;
        }
		
		baseDir += packageName + "/";
		
		if (new File(baseDir).exists()){
			showToast("Already exists");
			return;
		}

        List<FileContent> fileContents = new ArrayList<>();
		buildGradle_top_level += "Buildscript {\n";
		buildGradle_top_level += "    repositories {\n";
		buildGradle_top_level += "        jcenter()\n";
		buildGradle_top_level += "    }\n";
		buildGradle_top_level += "    dependencies {\n";
		buildGradle_top_level += "        classpath 'com.android.tools.build:gradle:1.+'\n";
		buildGradle_top_level += "    }\n";
		buildGradle_top_level += "}\n";
		buildGradle_top_level += "\n";
		buildGradle_top_level += "allprojects {\n";
		buildGradle_top_level += "    repositories {\n";
		buildGradle_top_level += "        jcenter()\n";
		buildGradle_top_level += "    }\n";
		buildGradle_top_level += "}";

		buildGradle_app_level += "apply plugin: 'com.android.application'\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "android {\n";
		buildGradle_app_level += "    compileSdkVersion 21\n";
		buildGradle_app_level += "    buildToolsVersion \"21.1.0\"\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "    defaultConfig {\n";
		buildGradle_app_level += "        applicationId \"" + packageName + "\"\n";
		buildGradle_app_level += "        minSdkVersion 14\n";
		buildGradle_app_level += "        targetSdkVersion 21\n";
		buildGradle_app_level += "        versionCode 1\n";
		buildGradle_app_level += "        versionName \"1.0\"\n";
		buildGradle_app_level += "    }\n";
		buildGradle_app_level += "    buildTypes {\n";
		buildGradle_app_level += "        release {\n";
		buildGradle_app_level += "            minifyEnabled false\n";
		buildGradle_app_level += "            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'\n";
		buildGradle_app_level += "        }\n";
		buildGradle_app_level += "    }\n";
		buildGradle_app_level += "}\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "dependencies {\n";
		buildGradle_app_level += "    compile fileTree(dir: 'libs', include: ['*.jar'])\n";
		buildGradle_app_level += "}";
		proguardRules += "# Add project specific ProGuard rules here.\n";
		proguardRules += "# By default, the flags in this file are appended to flags specified\n";
		proguardRules += "# in C:\\tools\\adt-bundle-windows-x86_64-20131030\\sdk/tools/proguard/proguard-android.txt\n";
		proguardRules += "# You can edit the include path and order by changing the proguardFiles\n";
		proguardRules += "# directive in build.gradle.\n";
		proguardRules += "#\n";
		proguardRules += "# For more details, see\n";
		proguardRules += "#   http://developer.android.com/guide/developing/tools/proguard.html\n";
		proguardRules += "\n";
		proguardRules += "# Add any project specific keep options here:\n";
		proguardRules += "\n";
		proguardRules += "# If your project uses WebView with JS, uncomment the following\n";
		proguardRules += "# and specify the fully qualified class name to the JavaScript interface\n";
		proguardRules += "# class:\n";
		proguardRules += "#-keepclassmembers class fqcn.of.javascript.interface.for.webview {\n";
		proguardRules += "#   public *;\n";
		proguardRules += "#}";

		androidManifest += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
		androidManifest += "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n";
		androidManifest += "    package=\"" + packageName + "\" >\n";
		androidManifest += "\n";
		androidManifest += "    <application\n";
		androidManifest += "        android:allowBackup=\"true\"\n";
		androidManifest += "        android:icon=\"@drawable/ic_launcher\"\n";
		androidManifest += "        android:label=\"@string/app_name\"\n";
		androidManifest += "        android:theme=\"@style/AppTheme\"\n";
		androidManifest += "\t\tandroid:resizeableActivity = \"true\">\n";
		androidManifest += "        <activity\n";
		androidManifest += "            android:name=\".MainActivity\"\n";
		androidManifest += "            android:label=\"@string/app_name\" >\n";
		androidManifest += "            <intent-filter>\n";
		androidManifest += "                <action android:name=\"android.intent.action.MAIN\" />\n";
		androidManifest += "\n";
		androidManifest += "                <category android:name=\"android.intent.category.LAUNCHER\" />\n";
		androidManifest += "            </intent-filter>\n";
		androidManifest += "        </activity>\n";
		androidManifest += "    </application>\n";
		androidManifest += "\n";
		androidManifest += "</manifest>";
		java += "Package " + packageName + ";\n";
		java += "\n";
		java += "import android.app.Activity;\n";
		java += "import android.os.Bundle;\n";
		java += "\n";
		java += "public class MainActivity extends Activity \n";
		java += "{\n";
		java += "    @Override\n";
		java += "    protected void onCreate(Bundle savedInstanceState)\n";
		java += "    {\n";
		java += "        super.onCreate(savedInstanceState);\n";
		java += "        setContentView(R.layout.main);\n";
		java += "    }\n";
		java += "}";

		resMainXml += "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n";
		resMainXml += "    android:layout_width=\"match_parent\"\n";
		resMainXml += "    android:layout_height=\"match_parent\"\n";
		resMainXml += "    android:gravity=\"center\">\n";
		resMainXml += "\n";
		resMainXml += "    <TextView\n";
		resMainXml += "        android:text=\"@string/hello_world\"\n";
		resMainXml += "        android:layout_width=\"wrap_content\"\n";
		resMainXml += "        android:layout_height=\"wrap_content\" />\n";
		resMainXml += "\n";
		resMainXml += "</LinearLayout>";
		resStringsXml += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
		resStringsXml += "<resources>\n";
		resStringsXml += "\n";
		resStringsXml += "    <string name=\"app_name\">Change Me</string>\n";
		resStringsXml += "    <string name=\"hello_world\">Hello world!</string>\n";
		resStringsXml += "\n";
		resStringsXml += "</resources>";
		resStylesXml += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
		resStylesXml += "<resources>\n";
		resStylesXml += "    <style name=\"AppTheme\" parent=\"@android:style/Theme.Holo.Light\">\n";
		resStylesXml += "\t</style>\n";
		resStylesXml += "</resources>";
		resStylesXmlv21 += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
		resStylesXmlv21 += "<resources>\n";
		resStylesXmlv21 += "    <style name=\"AppTheme\" parent=\"@android:style/Theme.Material.Light\">\n";
		resStylesXmlv21 += "\t</style>\n";
		resStylesXmlv21 += "</resources>";
		
        // Root files
        fileContents.add(new FileContent(baseDir + ".gitignore", gitIgnore));
        fileContents.add(new FileContent(baseDir + "build.gradle", buildGradle_top_level));
        fileContents.add(new FileContent(baseDir + "settings.gradle", settingsGradle));

        // App files
        fileContents.add(new FileContent(baseDir + "app/build.gradle", buildGradle_app_level));
        fileContents.add(new FileContent(baseDir + "app/proguard-rules.pro", proguardRules));

        // Build and gen files
        String packagePath = packageName.replace('.', '/');
        fileContents.add(new FileContent(baseDir + "app/build/bin/injected/AndroidManifest.xml", androidManifest));
        fileContents.add(new FileContent(baseDir + "app/build/gen/" + packagePath + "/BuildConfig.java", ""));
        fileContents.add(new FileContent(baseDir + "app/build/gen/" + packagePath + "/R.java", ""));

        // Source files
        fileContents.add(new FileContent(baseDir + "app/src/main/AndroidManifest.xml", androidManifest));
        fileContents.add(new FileContent(baseDir + "app/src/main/java/" + packagePath + "/MainActivity.java", java));

        // Resource files
        fileContents.add(new FileContent(baseDir + "app/src/main/res/drawable/ic_launcher.png", "")); // Empty for binary files
        fileContents.add(new FileContent(baseDir + "app/src/main/res/layout/main.xml", resMainXml));
        fileContents.add(new FileContent(baseDir + "app/src/main/res/values/strings.xml", resStringsXml));
        fileContents.add(new FileContent(baseDir + "app/src/main/res/values/styles.xml", resStylesXml));
        fileContents.add(new FileContent(baseDir + "app/src/main/res/values-v21/styles.xml", resStylesXmlv21));

        try {
            createFilesAndDirs(fileContents);
            showToast("Files created successfully!");
        } catch (IOException e) {
            showToast("Error creating files: " + e.getMessage());
        }
    }

    private void createFilesAndDirs(List<FileContent> fileContents) throws IOException {
        for (FileContent fileContent : fileContents) {
            File file = new File(fileContent.path);

            // Create parent directories if they don't exist
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory: " + parent);
            }

            // Create file and write content
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent.content.getBytes());
            }
        }
    }

    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper class to hold file path and content
    private static class FileContent {
        String path;
        String content;

        FileContent(String path, String content) {
            this.path = path;
            this.content = content;
        }
    }
}
