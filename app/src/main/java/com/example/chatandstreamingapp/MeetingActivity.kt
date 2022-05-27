package com.example.chatandstreamingapp

import android.content.ContentValues
import android.os.Bundle
import com.example.chatandstreamingapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL
 class MeetingActivity : JitsiMeetActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting)
        auth = Firebase.auth
        db = Firebase.firestore

        auth.currentUser?.let {
            db.collection(Constants.DB_NODE_USERS)
                .document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject<User>()
                        user?.username?.let { username ->
                            launchMeeting(username)
                        }
                    } else {
                        Timber.tag(ContentValues.TAG).d("No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.tag(ContentValues.TAG).w(exception, "Error getting user document.")
                }
        }

    }

    private fun launchMeeting(username: String){
        val userInfo = JitsiMeetUserInfo()
        userInfo.displayName = username
        userInfo.email = auth.currentUser?.email

        val options = JitsiMeetConferenceOptions.Builder()
            .setServerURL(URL("https://meet.jit.si"))
            .setRoom("Chatandstreamingapptest")
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setAudioOnly(false)
            .setSubject("Test meeting")
            .setUserInfo(userInfo)
            .build()
        launch(this, options)
    }
}