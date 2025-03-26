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
import net.moviemate.app.databinding.FragmentMyPostsBinding
import net.moviemate.app.m.Movie
import net.moviemate.app.mvf.MoviesViewModelFactory
import net.moviemate.app.room.MovieDatabase
import net.moviemate.app.utils.UserSession
import net.moviemate.app.utils.WrapContentLinearLayoutManager
import net.moviemate.app.vm.MoviesViewModel


class MyPostsFragment : Fragment(),MoviesAdapter.OnItemClickListener {
     private lateinit var binding:FragmentMyPostsBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var viewModel: MoviesViewModel
    private var myMoviesList = mutableListOf<Movie>()

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
        binding = FragmentMyPostsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        binding.myPostMoviesRecyclerview.layoutManager = WrapContentLinearLayoutManager(requireActivity(),
            RecyclerView.VERTICAL,false)
        binding.myPostMoviesRecyclerview.hasFixedSize()
        moviesAdapter = MoviesAdapter(myMoviesList)
        binding.myPostMoviesRecyclerview.adapter = moviesAdapter
        moviesAdapter.setOnItemClickListener(this)

        viewModel.userMoviesLiveData.observe(requireActivity()){movies ->
            binding.progressBar.visibility = View.GONE
            if (movies.isNotEmpty()){
                myMoviesList.clear()
                myMoviesList.addAll(movies)
                moviesAdapter.notifyItemChanged(0,myMoviesList.size)
                binding.myPostMoviesRecyclerview.visibility = View.VISIBLE
                binding.emptyListMessageView.visibility = View.GONE
            }
            else{
                binding.myPostMoviesRecyclerview.visibility = View.GONE
                binding.emptyListMessageView.visibility = View.VISIBLE
            }
        }
        viewModel.getAllMoviesByUserId("${UserSession.getUser()?.userId}")
    }

    override fun onItemClick(position: Int, movie: Movie) {

    }

    override fun onItemEditClick(position: Int, movie: Movie) {
        val action = MyPostsFragmentDirections.actionMyPostsFragmentToEditMovieFragment(
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
            myMoviesList.removeAt(position)
            moviesAdapter.notifyItemChanged(position)

            if (myMoviesList.isEmpty()){
                binding.myPostMoviesRecyclerview.visibility = View.GONE
                binding.emptyListMessageView.visibility = View.VISIBLE
            }

        }

        val alert = builder.create()
        alert.show()
    }

}