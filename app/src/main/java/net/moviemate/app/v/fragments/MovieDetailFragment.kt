package net.moviemate.app.v.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import net.moviemate.app.R
import net.moviemate.app.databinding.FragmentMovieDetailBinding
import net.moviemate.app.m.Movie


class MovieDetailFragment : Fragment() {

    private lateinit var binding:FragmentMovieDetailBinding
    private var movie:Movie?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        val args: MovieDetailFragmentArgs by navArgs()
        movie = args.movie
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.movieTitle.text = movie!!.title
        binding.movieReview.text = movie!!.review
        binding.username.text = "Posted by ${movie!!.username}"

        if (movie!!.image.isNotEmpty()){
            Picasso.get()
                .load(movie!!.image)
                .placeholder(R.drawable.loader)
                .resize(600,400)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(binding.movieImage)
        }
    }

}