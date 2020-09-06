package com.example.barrierfree;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;

public class CloudStorageFile {
    private FirebaseFirestore mFirestore;
    private DocumentReference documentReference;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String imageUrl;
    private UploadTask uploadTask;

    public void uploadURIToFirebase(String url) throws MalformedURLException, URISyntaxException {
        storage = FirebaseStorage.getInstance();
        Uri file = Uri.fromFile(new File(url));
        int idx = url.indexOf("/");
        String filename = url.substring(idx+1);
        UUID uuid = UUID.randomUUID();
        storageRef = storage.getReferenceFromUrl("gs://barrierfree-b7959.appspot.com").child("images/"+filename+"_"+uuid);
        //uploadTask = storageRef.putStream(fileInputStream);
        uploadTask = storageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("메시지", exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("메시지", "업로드 성공");
                Task<Uri> downloadUri = storageRef.getDownloadUrl();
                downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) { //4.Url 받는곳
                        imageUrl = uri.toString();
                        Log.d("이미지", imageUrl);
                        //얘는 여기서 5. Firestore에 저장하는 메서드 필요
                    }
                });
            }
        });
    }
}
