package com.emirhan.fotografpaylasma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emirhan.fotografpaylasma.databinding.RecyclerRowBinding
import com.emirhan.fotografpaylasma.model.post
import com.squareup.picasso.Picasso

class postAdapter(val liste:ArrayList<post>):RecyclerView.Adapter<postAdapter.postViewHolder>() {
    class postViewHolder(val binding : RecyclerRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postViewHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return postViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return liste.size
    }

    override fun onBindViewHolder(holder: postViewHolder, position: Int) {
        holder.binding.isimRec.text=liste[position].email
        holder.binding.aciklama.text=liste[position].comment
        Picasso.get().load(liste[position].url).into(holder.binding.fotorec)
    }
}