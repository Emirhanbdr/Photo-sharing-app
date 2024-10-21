package com.emirhan.fotografpaylasma.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.emirhan.fotografpaylasma.R
import com.emirhan.fotografpaylasma.adapter.postAdapter
import com.emirhan.fotografpaylasma.databinding.FragmentFeedArayuzBinding
import com.emirhan.fotografpaylasma.model.post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore


class feedArayuz : Fragment(),PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentFeedArayuzBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore
    val postList:ArrayList<post> = arrayListOf()
    private var adapter:postAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        db=Firebase.firestore


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedArayuzBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            val popup=PopupMenu(requireContext(),binding.floatingActionButton)
            val infilater=popup.menuInflater
            infilater.inflate(R.menu.my_popup_menu,popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener(this)
            adapter=postAdapter(postList)
            binding.feedRecText.layoutManager=LinearLayoutManager(requireContext())
            binding.feedRecText.adapter=adapter
            firebaseVerileriAl()
        }

    }
    private fun firebaseVerileriAl(){
        db.collection("posts").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if (value!=null){
                    if (!value.isEmpty){

                        //boş değilse
                        val documents=value.documents
                        for(document in documents){
                            val comment=document.get("comment") as String
                            val email=document.get("email") as String
                            val url=document.get("url") as String

                            val post= post(email,comment,url)
                            postList.add(post)
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
        }

    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        if (p0?.itemId== R.id.yuklemeİtem){
            val action=feedArayuzDirections.actionFeedArayuzToYuklemeEkrani()
            Navigation.findNavController(requireView()).navigate(action)
        }else if (p0?.itemId== R.id.cikisYap){
            auth.signOut()
            val action=feedArayuzDirections.actionFeedArayuzToKullaniciGiris()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }


}