package com.example.chatandstreamingapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chatandstreamingapp.Constants
import com.example.chatandstreamingapp.MeetingActivity
import com.example.chatandstreamingapp.R
import com.example.chatandstreamingapp.databinding.FragmentHomeBinding
import com.example.chatandstreamingapp.ui.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var db: FirebaseFirestore
    private val mainViewModel: MainViewModel by activityViewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = Firebase.firestore

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.joinButton.setOnClickListener {
            val intent = Intent(this.activity, MeetingActivity::class.java)
            startActivity(intent)
        }

        initObservers()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initObservers() {
        mainViewModel.getUser().observe(viewLifecycleOwner) { user ->
            binding.username.text = resources.getString(R.string.home_username, user.username)
            if (user.role == Constants.ROLE_CLIENT) {
                binding.role.text =
                    resources.getString(R.string.home_role, resources.getString(R.string.client))
            } else {
                binding.role.text =
                    resources.getString(R.string.home_role, resources.getString(R.string.restaurant))
            }
        }
    }
}