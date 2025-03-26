package net.moviemate.app.adapaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.moviemate.app.R
import net.moviemate.app.databinding.ItemMovieDesignBinding
import net.moviemate.app.m.Movie
import net.moviemate.app.utils.UserSession

class MoviesAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, movie: Movie)
        fun onItemEditClick(position: Int,movie: Movie)
        fun onItemDelete(position: Int,movie: Movie)
    }

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class MovieViewHolder(private val binding: ItemMovieDesignBinding,private val mListener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieReview.text = movie.review
            binding.username.text = "Posted by ${movie.username}"

           if (movie.image.isNotEmpty()){
               Picasso.get()
                   .load(movie.image)
                   .placeholder(R.drawable.loader)
                   .resize(600,400)
                   .centerCrop()
                   .error(R.drawable.placeholder)
                   .into(binding.movieImage)
           }

            if (movie.userImage.isNotEmpty()){
                Picasso.get()
                    .load(movie.userImage)
                    .placeholder(R.drawable.loader)
                    .resize(200,200)
                    .centerCrop()
                    .error(R.drawable.placeholder)
                    .into(binding.userImage)
            }

            if (UserSession.getUser()?.userId == movie.userId){
                binding.postActionWrapper.visibility = View.VISIBLE
            }
            else{
                binding.postActionWrapper.visibility = View.GONE
            }

            binding.postDeleteView.setOnClickListener {
                mListener.onItemDelete(layoutPosition,movie)
            }

            binding.postEditView.setOnClickListener {
                mListener.onItemEditClick(layoutPosition,movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding,mListener!!)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size
}
