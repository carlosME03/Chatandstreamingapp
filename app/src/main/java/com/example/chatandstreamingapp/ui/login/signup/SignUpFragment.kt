package com.example.chatandstreamingapp.ui.login.signup

import android.app.AlertDialog
import android.content.ContentValues.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatandstreamingapp.Constants
import com.example.chatandstreamingapp.Constants.ROLE_CLIENT
import com.example.chatandstreamingapp.R
import com.example.chatandstreamingapp.databinding.FragmentRegisterBinding
import com.example.chatandstreamingapp.models.User
import com.example.chatandstreamingapp.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class SignUpFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var _binding: FragmentRegisterBinding? = null
    private var userRole = ROLE_CLIENT

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        db = Firebase.firestore

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.register.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()
            if (email.isValidEmail()){
                if (password == confirmPassword){
                    signUp(binding.email.text.toString(), binding.password.text.toString(), username)
                }
                else {
                    Toast.makeText(requireActivity(), "Passwords don't match", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(requireActivity(), "Invalid Email", Toast.LENGTH_LONG).show()
            }
        }

        val spinner: Spinner = binding.roleSpinner
        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.role_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
            spinner.setSelection(0)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signUp(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signUpWithEmail:success")
                    addUserToFirestore(username)
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signUpWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToFirestore(username: String) {
        auth.currentUser?.let {
            val user = User(username, it.email!!, it.uid, userRole)
            db.collection(Constants.DB_NODE_USERS)
                .document(it.uid)
                .set(user)
                .addOnSuccessListener { _ ->
                    Timber.d("DocumentSnapshot added with ID: ${it.uid}")
                    auth.currentUser?.sendEmailVerification()
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(resources.getString(R.string.dialog_verification))
                    builder.setMessage(resources.getString(R.string.dialog_verification_message_instructions))
                    builder.setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.setOnDismissListener {
                        auth.signOut()
                        findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
                    }
                    builder.show()

                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        userRole = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        //not used
    }

}