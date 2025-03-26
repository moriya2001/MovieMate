package net.moviemate.app.v.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import net.moviemate.app.R
import net.moviemate.app.databinding.FragmentAccountBinding
import net.moviemate.app.utils.UserSession
import net.moviemate.app.v.activities.LoginSignUpActivity
import net.moviemate.app.vm.AuthViewModel


class AccountFragment : Fragment() {

    private lateinit var binding:FragmentAccountBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (UserSession.getUser() != null){
            val user = UserSession.getUser()
            binding.apply {
                if (user!!.image.isNotEmpty()){
                    Picasso.get()
                        .load(user.image)
                        .placeholder(R.drawable.loader)
                        .resize(200,200)
                        .centerCrop()
                        .error(R.drawable.placeholder)
                        .into(profileImageView)
                }

                tvUsername.text = user.username
                tvUserEmail.text = user.email
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnLogout.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireActivity())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setNegativeButton("Cancel"){dialog,which->
                dialog.dismiss()
            }
            builder.setPositiveButton("Logout"){dialog,which->
                dialog.dismiss()
                UserSession.clearUser()
                authViewModel.signOut()
                startActivity(Intent(requireActivity(),LoginSignUpActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }).apply {
                    requireActivity().finish()
                }
            }

            val alert = builder.create()
            alert.show()
        }

        binding.btnEditProfile.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToUpdateAccountDetailFragment(UserSession.getUser()!!)
            findNavController().navigate(action)
        }
    }

}