package net.moviemate.app.v.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.moviemate.app.R
import net.moviemate.app.adapaters.MoviesAdapter
import net.moviemate.app.databinding.FragmentMoviesBinding
import net.moviemate.app.m.Movie
import net.moviemate.app.mvf.MoviesViewModelFactory
import net.moviemate.app.room.MovieDatabase
import net.moviemate.app.utils.WrapContentLinearLayoutManager
import net.moviemate.app.vm.MoviesViewModel


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

    override fun onItemClick(position: Int, movie: Movie) {

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

}