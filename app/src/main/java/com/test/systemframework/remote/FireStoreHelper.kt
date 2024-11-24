package com.test.systemframework.remote

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.test.systemframework.notification.NotificationEntity
import com.test.systemframework.notification.timestampToDateTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FireStoreHelper {

    private val TAG = this::class.java.simpleName
    suspend fun uploadData(data: NotificationEntity): Boolean{
        Log.d(TAG, " Uploading data $data")
        try {
            return withContext(Dispatchers.IO){
                val db = Firebase.firestore
                db.collection("coll").document(timestampToDateTimeString(data.timestamp))
                    .set(data)
                true
            }
        }catch (e:Exception){
            e.printStackTrace()
            Log.d(TAG, " Exception while uploading data $e")
            return false
        }
    }
}