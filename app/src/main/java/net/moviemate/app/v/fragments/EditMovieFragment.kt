package net.moviemate.app.v.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import net.moviemate.app.R
import net.moviemate.app.databinding.FragmentEditMovieBinding
import net.moviemate.app.m.Movie
import net.moviemate.app.vmf.MoviesViewModelFactory
import net.moviemate.app.room.MovieDatabase
import net.moviemate.app.vm.MoviesViewModel


class EditMovieFragment : Fragment() {
   private lateinit var binding:FragmentEditMovieBinding
    private lateinit var viewModel: MoviesViewModel
    private var imageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var movie:Movie?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.ivMovieImage.setImageURI(uri)
                binding.ivMovieImage.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        val movieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        viewModel = ViewModelProvider(this, MoviesViewModelFactory(movieDao))[MoviesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditMovieBinding.inflate(inflater, container, false)

        val args: EditMovieFragmentArgs by navArgs()
        movie = args.movie

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            movie?.let {
                if (it.image.isNotEmpty()){
                    Picasso.get()
                        .load(it.image)
                        .placeholder(R.drawable.loader)
                        .resize(600,400)
                        .centerCrop()
                        .error(R.drawable.placeholder)
                        .into(ivMovieImage)
                }
                 ivMovieImage.visibility = View.VISIBLE
                etMovieTitle.setText(movie!!.title)
                etMovieReview.setText(movie!!.review)
                etMovieLink.setText(movie!!.link)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.btnUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnUpdatePost.setOnClickListener {
            val title = binding.etMovieTitle.text.toString()
            val review = binding.etMovieReview.text.toString()
            val link = binding.etMovieLink.text.toString()
            if (validation(title,review)){

                    movie!!.title = title
                    movie!!.review = review
                    movie!!.link = link

                viewModel.updateMovie(movie!!, imageUri)
            }
        }
    }

    private fun validation(title: String, review: String): Boolean {
        if (title.isEmpty()){
            Toast.makeText(requireActivity(),"Title field is Empty!",Toast.LENGTH_SHORT).show()
            return false
        }
        else if (review.isEmpty()){
            Toast.makeText(requireActivity(),"Review field is Empty!",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}