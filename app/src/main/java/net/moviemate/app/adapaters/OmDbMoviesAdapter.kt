package net.moviemate.app.adapaters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.moviemate.app.R
import net.moviemate.app.databinding.ItemOmdbMovieBinding
import net.moviemate.app.m.MovieEntity

class OmDbMoviesAdapter(
    private val oMDbMovies: List<MovieEntity>,
    private val onClick: (MovieEntity) -> Unit,
    private val onAddClick : (MovieEntity) -> Unit
) : RecyclerView.Adapter<OmDbMoviesAdapter.OmDbViewHolder>() {

    inner class OmDbViewHolder(private val binding: ItemOmdbMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieEntity) {
            binding.title.text = movie.Title
            binding.year.text = "Year: ${movie.Year}"
            binding.type.text = "Type: ${movie.Type}"
            Picasso.get().load(movie.Poster)
                .placeholder(R.drawable.loader)
                .error(R.drawable.placeholder)
                .resize(600,800)
                .centerCrop()
                .into(binding.imageView)
            binding.root.setOnClickListener { onClick(movie) }
            binding.addBtn.setOnClickListener { onAddClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OmDbViewHolder {
        val binding = ItemOmdbMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OmDbViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OmDbViewHolder, position: Int) {
        holder.bind(oMDbMovies[position])
    }

    override fun getItemCount(): Int = oMDbMovies.size
}
