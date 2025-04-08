package net.moviemate.app.v.fragments

import android.content.Intent
import android.net.Uri
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

    private lateinit var binding: FragmentMovieDetailBinding
    private var movie: Movie? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        val args: MovieDetailFragmentArgs by navArgs()
        movie = args.movie
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Title and review
        binding.movieTitle.text = movie?.title
        binding.movieReview.text = movie?.review

        // Show "Posted by username"
        binding.username.text = "Posted by ${movie?.username}"

        // Load the user profile image
        if (!movie?.userImage.isNullOrEmpty()) {
            Picasso.get()
                .load(movie?.userImage)
                .placeholder(R.drawable.loader)
                .error(R.drawable.placeholder)
                .into(binding.userImage)
        }

        // Load the movie poster
        if (!movie?.image.isNullOrEmpty()) {
            Picasso.get()
                .load(movie?.image)
                .placeholder(R.drawable.loader)
                .resize(600, 400)
                .centerCrop()
                .error(R.drawable.placeholder)
                .into(binding.movieImage)
        }

        // Show the movie link
        binding.movieLink.text = movie?.link

        // If you want the link to open in a browser when clicked, set an onClickListener:
        binding.movieLink.setOnClickListener {
            movie?.link?.let { link ->
                if (link.isNotEmpty()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(browserIntent)
                }
            }
        }
    }
}
