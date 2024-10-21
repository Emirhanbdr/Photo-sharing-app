package com.emirhan.fotografpaylasma.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.emirhan.fotografpaylasma.databinding.FragmentKullaniciGirisBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class kullaniciGiris : Fragment() {
    private var _binding: FragmentKullaniciGirisBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentKullaniciGirisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user=auth.currentUser
        if (user!=null){
            //Kullanıcı daha önceden giriş yapmış
            val action=kullaniciGirisDirections.actionKullaniciGirisToFeedArayuz()
            Navigation.findNavController(requireView()).navigate(action)
        }
        binding.kayit.setOnClickListener {
            val eMail=binding.eMail.text.toString()
            val password=binding.sifre.text.toString()
            if (eMail.isNotEmpty()&&password.isNotEmpty()){
                auth.createUserWithEmailAndPassword(eMail,password).addOnCompleteListener {
                    task->
                    if (task.isSuccessful){
                        //Kullanıcı oluşturuldu
                        val action=kullaniciGirisDirections.actionKullaniciGirisToFeedArayuz()
                        Navigation.findNavController(requireView()).navigate(action)
                    }
                }.addOnFailureListener {
                    exception->
                    Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),"Boş bırakma",Toast.LENGTH_SHORT).show()
            }
        }
        binding.giris.setOnClickListener {
            val eMail=binding.eMail.text.toString()
            val password=binding.sifre.text.toString()
            if (eMail.isNotEmpty()&&password.isNotEmpty()){
                auth.signInWithEmailAndPassword(eMail,password).addOnSuccessListener {
                    //Giriş yapıldı
                    val action=kullaniciGirisDirections.actionKullaniciGirisToFeedArayuz()
                    Navigation.findNavController(requireView()).navigate(action)
                }.addOnFailureListener {
                    exception->
                    Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(requireContext(),"Lütfen boş bırakma",Toast.LENGTH_SHORT).show()
            }
        }
    }


}