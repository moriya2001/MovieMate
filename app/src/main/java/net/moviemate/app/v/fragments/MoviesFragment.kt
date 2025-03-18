package net.moviemate.app.v.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import net.moviemate.app.R
import net.moviemate.app.databinding.FragmentMoviesBinding


class MoviesFragment : Fragment() {
    private lateinit var binding:FragmentMoviesBinding

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

        binding.addFabBtn.setOnClickListener {
            findNavController().navigate(R.id.action_moviesFragment_to_addMovieFragment)
        }
    }

}