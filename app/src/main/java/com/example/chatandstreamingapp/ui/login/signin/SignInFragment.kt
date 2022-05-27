package com.example.chatandstreamingapp.ui.login.signin

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatandstreamingapp.R
import com.example.chatandstreamingapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SignInFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private lateinit var auth: FirebaseAuth

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
    ): View {
        auth = Firebase.auth
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //user is logged in
        auth.currentUser?.let {
            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
        }

        binding.login.setOnClickListener {

            signIn(binding.username.text.toString(),binding.password.text.toString())
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let {
                        if (it.isEmailVerified) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                        }
                        else {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle(resources.getString(R.string.dialog_verification))
                            builder.setMessage(resources.getString(R.string.dialog_verification_message_error))

                            builder.setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            builder.setNeutralButton("Re-send verification"){ dialog, _ ->
                                auth.currentUser?.sendEmailVerification()
                                dialog.dismiss()
                            }
                            builder.setOnDismissListener {
                                auth.signOut()
                            }
                            builder.show()
                        }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}