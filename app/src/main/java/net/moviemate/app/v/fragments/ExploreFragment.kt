package net.moviemate.app.v.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import net.moviemate.app.adapaters.OmDbMoviesAdapter
import net.moviemate.app.databinding.FragmentExploreBinding
import net.moviemate.app.m.MovieEntity
import net.moviemate.app.retrofit.RetrofitInstance
import net.moviemate.app.room.MovieDatabase
import net.moviemate.app.vm.OmDbViewModel
import net.moviemate.app.vmf.OmDbMoviesViewModelFactory


class ExploreFragment : Fragment() {
    private lateinit var binding:FragmentExploreBinding
    private lateinit var oMDbViewModel: OmDbViewModel
    private lateinit var adapter:OmDbMoviesAdapter
    private val movies = mutableListOf<MovieEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val omDbDao = MovieDatabase.getDatabase(requireActivity()).omDbDao()
        val movieApiService = RetrofitInstance.api

        val factory = OmDbMoviesViewModelFactory(omDbDao, movieApiService)
        oMDbViewModel = ViewModelProvider(this, factory)[OmDbViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OmDbMoviesAdapter(movies,
            onClick = { movie ->
            Toast.makeText(requireActivity(), "Clicked on: ${movie.Title}", Toast.LENGTH_SHORT).show()
        },
        onAddClick = { movie ->
            val action = ExploreFragmentDirections.actionExploreFragmentToAddMovieFragment(movie)
            findNavController().navigate(action)
        }
        )

        binding.omdbMoviesRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.omdbMoviesRecyclerView.adapter = adapter

        oMDbViewModel.movies.observe(requireActivity()) { recipes ->
            binding.searchButton.isEnabled = true
            binding.searchEditText.isEnabled = true
            binding.searchButton.text = "Search"
            if (recipes.isNotEmpty()) {
                movies.clear()
                movies.addAll(recipes)
            }
            adapter.notifyItemChanged(0,movies.size) // Update RecyclerView
        }
        oMDbViewModel.fetchMovies("b437f739","")
        binding.searchButton.setOnClickListener {
            binding.searchButton.isEnabled = false
            binding.searchEditText.isEnabled = false
            binding.searchButton.text = "Searching..."
            val query = binding.searchEditText.text.toString().trim()
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        oMDbViewModel.fetchMovies("b437f739",query)
    }
}