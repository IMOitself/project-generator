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
	private String gradleProperties = "";
	private String localProperties = "";
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

		if (new File(baseDir).exists()) {
			showToast("Already exists");
			return;
		}

        List<FileContent> fileContents = new ArrayList<>();
		buildGradle_top_level += "buildscript {\n";
		buildGradle_top_level += "    ext.build_gradle_version = '3.6.1'\n";
		buildGradle_top_level += "\n";
		buildGradle_top_level += "    repositories {\n";
		buildGradle_top_level += "        maven { url 'https://maven.aliyun.com/repository/public/' }\n";
		buildGradle_top_level += "        maven { url 'https://maven.aliyun.com/repository/google/' }\n";
		buildGradle_top_level += "        maven { url 'https://maven.aliyun.com/repository/gradle-plugin/' }\n";
		buildGradle_top_level += "        maven { url 'https://dl.bintray.com/ppartisan/maven/' }\n";
		buildGradle_top_level += "        maven { url \"https://clojars.org/repo/\" }\n";
		buildGradle_top_level += "        maven { url \"https://jitpack.io\" }\n";
		buildGradle_top_level += "        google()\n";
		buildGradle_top_level += "        mavenLocal()\n";
		buildGradle_top_level += "        mavenCentral()\n";
		buildGradle_top_level += "    }\n";
		buildGradle_top_level += "    dependencies {\n";
		buildGradle_top_level += "        classpath \"com.android.tools.build:gradle:$build_gradle_version\"\n";
		buildGradle_top_level += "        \n";
		buildGradle_top_level += "    }\n";
		buildGradle_top_level += "    \n";
		buildGradle_top_level += "}\n";
		buildGradle_top_level += "allprojects {\n";
		buildGradle_top_level += "    repositories {\n";
		buildGradle_top_level += "        maven { url 'https://maven.aliyun.com/repository/public/' } \n";
		buildGradle_top_level += "        maven { url 'https://maven.aliyun.com/repository/google/' }\n";
		buildGradle_top_level += "        maven { url 'https://maven.aliyun.com/repository/gradle-plugin/' }\n";
		buildGradle_top_level += "        maven { url 'https://dl.bintray.com/ppartisan/maven/' }\n";
		buildGradle_top_level += "        maven { url \"https://clojars.org/repo/\" }\n";
		buildGradle_top_level += "        maven { url \"https://jitpack.io\" }\n";
		buildGradle_top_level += "        mavenLocal()\n";
		buildGradle_top_level += "        mavenCentral()\n";
		buildGradle_top_level += "        google()\n";
		buildGradle_top_level += "    }\n";
		buildGradle_top_level += "}\n";
		buildGradle_top_level += "\n";
		buildGradle_top_level += "task clean(type: Delete) {\n";
		buildGradle_top_level += "    delete rootProject.buildDir\n";
		buildGradle_top_level += "}";
		gradleProperties += "# Project-wide Gradle settings.\n";
		gradleProperties += "# IDE (e.g. Android Studio) users:\n";
		gradleProperties += "# Gradle settings configured through the IDE *will override*\n";
		gradleProperties += "# any settings specified in this file.\n";
		gradleProperties += "# For more details on how to configure your build environment visit\n";
		gradleProperties += "# http://www.gradle.org/docs/current/userguide/build_environment.html\n";
		gradleProperties += "# Specifies the JVM arguments used for the daemon process.\n";
		gradleProperties += "# The setting is particularly useful for tweaking memory settings.\n";
		gradleProperties += "org.gradle.jvmargs=-Xmx2048m\n";
		gradleProperties += "# When configured, Gradle will run in incubating parallel mode.\n";
		gradleProperties += "# This option should only be used with decoupled projects. More details, visit\n";
		gradleProperties += "# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects\n";
		gradleProperties += "# org.gradle.parallel=true\n";
		gradleProperties += "# AndroidX package structure to make it clearer which packages are bundled with the\n";
		gradleProperties += "# Android operating system, and which are packaged with your app\"s APK\n";
		gradleProperties += "# https://developer.android.com/topic/libraries/support-library/androidx-rn\n";
		gradleProperties += "android.useAndroidX=false\n";
		gradleProperties += "# Automatically convert third-party libraries to use AndroidX\n";
		gradleProperties += "android.enableJetifier=false";
		localProperties += "## This file is automatically generated by Android Studio.\n";
		localProperties += "# Do not modify this file -- YOUR CHANGES WILL BE ERASED!\n";
		localProperties += "#\n";
		localProperties += "# This file should *NOT* be checked into Version Control Systems,\n";
		localProperties += "# as it contains information specific to your local configuration.\n";
		localProperties += "#\n";
		localProperties += "# Location of the SDK. This is only used by Gradle.\n";
		localProperties += "# For customization when using a Version Control System, please read the\n";
		localProperties += "# header note.\n";
		localProperties += "#sdk.dir=";

		buildGradle_app_level += "apply plugin: 'com.android.application'\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "android {\n";
		buildGradle_app_level += "    compileSdkVersion 29\n";
		buildGradle_app_level += "    buildToolsVersion \"29.0.3\"\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "    defaultConfig {\n";
		buildGradle_app_level += "        applicationId \"" + packageName + "\"\n";
		buildGradle_app_level += "        minSdkVersion 21\n";
		buildGradle_app_level += "        targetSdkVersion 29\n";
		buildGradle_app_level += "        versionCode 1\n";
		buildGradle_app_level += "        versionName \"1.0\"\n";
		buildGradle_app_level += "    }\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "    buildTypes {\n";
		buildGradle_app_level += "        release {\n";
		buildGradle_app_level += "            minifyEnabled false\n";
		buildGradle_app_level += "            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'\n";
		buildGradle_app_level += "        }\n";
		buildGradle_app_level += "    }\n";
		buildGradle_app_level += "}\n";
		buildGradle_app_level += "\n";
		buildGradle_app_level += "dependencies {\n";
		buildGradle_app_level += "    implementation fileTree(dir: \"libs\", include: [\"*.jar\"])\n";
		buildGradle_app_level += "}";
		proguardRules += "# Add project specific ProGuard rules here.\n";
		proguardRules += "# You can control the set of applied configuration files using the\n";
		proguardRules += "# proguardFiles setting in build.gradle.\n";
		proguardRules += "#\n";
		proguardRules += "# For more details, see\n";
		proguardRules += "#   http://developer.android.com/guide/developing/tools/proguard.html\n";
		proguardRules += "\n";
		proguardRules += "# If your project uses WebView with JS, uncomment the following\n";
		proguardRules += "# and specify the fully qualified class name to the JavaScript interface\n";
		proguardRules += "# class:\n";
		proguardRules += "#-keepclassmembers class fqcn.of.javascript.interface.for.webview {\n";
		proguardRules += "#   public *;\n";
		proguardRules += "#}\n";
		proguardRules += "\n";
		proguardRules += "# Uncomment this to preserve the line number information for\n";
		proguardRules += "# debugging stack traces.\n";
		proguardRules += "#-keepattributes SourceFile,LineNumberTable\n";
		proguardRules += "\n";
		proguardRules += "# If you keep the line number information, uncomment this to\n";
		proguardRules += "# hide the original source file name.\n";
		proguardRules += "#-renamesourcefileattribute SourceFile";

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
		java += "package " + packageName + ";\n";
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
            showToast("Files created successfully at:\n" + baseDir);
			finishAffinity();
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
