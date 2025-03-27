package net.moviemate.app.v.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import net.moviemate.app.R
import net.moviemate.app.adapaters.MoviesAdapter
import net.moviemate.app.databinding.FragmentMoviesBinding
import net.moviemate.app.m.Movie
import net.moviemate.app.vmf.MoviesViewModelFactory
import net.moviemate.app.room.MovieDatabase
import net.moviemate.app.utils.WrapContentLinearLayoutManager
import net.moviemate.app.vm.MoviesViewModel
import java.io.File


class MoviesFragment : Fragment(),MoviesAdapter.OnItemClickListener {
    private lateinit var binding:FragmentMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var viewModel: MoviesViewModel
    private var moviesList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        viewModel = ViewModelProvider(this, MoviesViewModelFactory(movieDao))[MoviesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoviesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        binding.moviesRecyclerview.layoutManager = WrapContentLinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
        binding.moviesRecyclerview.hasFixedSize()
        moviesAdapter = MoviesAdapter(moviesList)
        binding.moviesRecyclerview.adapter = moviesAdapter
        moviesAdapter.setOnItemClickListener(this)

        viewModel.moviesLiveData.observe(requireActivity()){movies ->
            binding.progressBar.visibility = View.GONE
           if (movies.isNotEmpty()){
               moviesList.clear()
               moviesList.addAll(movies)
               moviesAdapter.notifyItemChanged(0,moviesList.size)
               binding.moviesRecyclerview.visibility = View.VISIBLE
               binding.emptyListMessageView.visibility = View.GONE
           }
            else{
               binding.moviesRecyclerview.visibility = View.GONE
               binding.emptyListMessageView.visibility = View.VISIBLE
           }
        }


        binding.addFabBtn.setOnClickListener {
            findNavController().navigate(R.id.action_moviesFragment_to_addMovieFragment)
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllMovies()
    }

    override fun onItemClick(position: Int, movie: Movie) {
        val action = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailFragment(
            movie)
        findNavController().navigate(action)
    }

    override fun onItemEditClick(position: Int, movie: Movie) {
        val action = MoviesFragmentDirections.actionMoviesFragmentToEditMovieFragment(
            movie)
        findNavController().navigate(action)
    }

    override fun onItemDelete(position: Int, movie: Movie) {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle("Delete")
        builder.setMessage("Are you sure you want to delete movie?")
        builder.setNegativeButton("Cancel"){dialog,which->
            dialog.dismiss()
        }
        builder.setPositiveButton("Delete"){dialog,which->
            dialog.dismiss()
            viewModel.deleteMovie(movie)
            moviesList.removeAt(position)
            moviesAdapter.notifyItemChanged(position)

            if (moviesList.isEmpty()){
                binding.moviesRecyclerview.visibility = View.GONE
                binding.emptyListMessageView.visibility = View.VISIBLE
            }

        }

        val alert = builder.create()
        alert.show()
    }

    override fun onItemShare(position: Int, movie: Movie) {
         shareMovieDetails(movie.title,movie.review,movie.image)
    }

    private fun shareMovieDetails(title: String, review: String, imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            // If no image, just share text
            shareTextOnly(title, review)
        } else {
            // Download image first
            downloadAndShareImage(title, review, imageUrl)
        }
    }

    private fun downloadAndShareImage(title: String, review: String, imageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

        val localFile = File(requireActivity().externalCacheDir, "shared_movie_image.jpg")

        storageRef.getFile(localFile)
            .addOnSuccessListener {
                val imageUri = FileProvider.getUriForFile(
                    requireActivity().applicationContext,
                    "${requireActivity().applicationContext.packageName}.fileprovider",
                    localFile
                )
                shareTextWithImage(title, review, imageUri)
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), "Failed to download image", Toast.LENGTH_SHORT).show()
                shareTextOnly(title, review)  // Share only text if image fails
            }
    }

    private fun shareTextOnly(title: String, review: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "🎬 Movie: $title\n⭐ Review: $review\n\nCheck it out! 🍿")
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun shareTextWithImage(title: String, review: String, imageUri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, "🎬 Movie: $title\n⭐ Review: $review\n\nCheck it out! 🍿")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

}