package net.moviemate.app.v.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import net.moviemate.app.R
import net.moviemate.app.databinding.FragmentUpdateAccountDetailBinding
import net.moviemate.app.m.User
import net.moviemate.app.vm.AuthViewModel


class UpdateAccountDetailFragment : Fragment() {

    private lateinit var binding:FragmentUpdateAccountDetailBinding
    private var user: User?=null
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.profileImageView.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateAccountDetailBinding.inflate(inflater, container, false)

        val args: UpdateAccountDetailFragmentArgs by navArgs()
        user = args.user

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            if (user!!.image.isNotEmpty()){
                Picasso.get()
                    .load(user!!.image)
                    .placeholder(R.drawable.loader)
                    .resize(200,200)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(profileImageView)
            }

            usernameEditBox.setText(user!!.username)
        }

        authViewModel.profileUpdateStatus.observe(viewLifecycleOwner) { message ->

            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                if (it.contains("Failed to update profile")){
                    binding.progressBar.visibility = View.GONE
                    binding.btnEditImage.isEnabled = true
                    binding.btnUpdate.isEnabled = true
                    binding.btnCancel.isEnabled  = true
                    binding.usernameEditBox.isEnabled = true
                }
                else{
                    findNavController().navigateUp()
                }

            }
        }

        binding.btnEditImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnUpdate.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnEditImage.isEnabled = false
            binding.btnUpdate.isEnabled = false
            binding.btnCancel.isEnabled  = false
            binding.usernameEditBox.isEnabled = false
            authViewModel.updateProfile(binding.usernameEditBox.text.toString(),imageUri)
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}