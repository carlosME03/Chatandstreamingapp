package com.example.chatandstreamingapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatandstreamingapp.Constants
import com.example.chatandstreamingapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainViewModel  : ViewModel() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadUser()
        }
    }

    fun getUser(): LiveData<User> {
        return user
    }

    private fun loadUser() {
        auth.currentUser?.let { it ->
            db.collection(Constants.DB_NODE_USERS)
                .document(it.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        user.postValue(document.toObject<User>())
                    }
                }
        }
    }

    fun signOut() {
        auth.signOut()
    }
}