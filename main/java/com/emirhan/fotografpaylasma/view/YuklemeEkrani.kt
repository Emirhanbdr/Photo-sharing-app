package com.emirhan.fotografpaylasma.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.emirhan.fotografpaylasma.databinding.FragmentYuklemeEkraniBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.vmadalin.easypermissions.EasyPermissions
import java.util.UUID


class yuklemeEkrani : Fragment(),EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentYuklemeEkraniBinding? = null
    private val binding get() = _binding!!
    private var requestMultiplePermissions:Int=12
    private var permission= arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES
    )
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var secilenGorsel:Uri?=null
    var secilenBitmap:Bitmap?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        auth= Firebase.auth
        storage=Firebase.storage
        db=Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentYuklemeEkraniBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.gorselKoy.setOnClickListener {
            if (EasyPermissions.hasPermissions(requireContext(),*permission)){
                val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)

            }else{
                EasyPermissions.requestPermissions(requireActivity(),"İzin verir misin?",requestMultiplePermissions,*permission)
                val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            }
        }

        binding.uploadBtn.setOnClickListener {
            val uuid=UUID.randomUUID()
            val gorsel="${uuid}.jpg"
            val referance=storage.reference
            val gorselDosyasi=referance.child("images").child(gorsel)
            if (secilenGorsel!= null){
                gorselDosyasi.putFile(secilenGorsel!!).addOnSuccessListener {
                    UploadTask->
                    //url alma işlemi burda olacak
                    gorselDosyasi.downloadUrl.addOnSuccessListener {
                        uri->
                        val dowloanduri=uri.toString()
                        val postmap= hashMapOf<String,Any>()
                        postmap.put("url",dowloanduri)
                        postmap.put("email",auth.currentUser?.email.toString())
                        postmap.put("comment",binding.yorumEkle.text.toString())
                        postmap.put("date",Timestamp.now())
                        db.collection("posts").add(postmap).addOnSuccessListener { DocumentReference->
                            val action=yuklemeEkraniDirections.actionYuklemeEkraniToFeedArayuz()
                            Navigation.findNavController(view).navigate(action)

                        }.addOnFailureListener {
                                exception->
                            Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                        }
                    }

                }.addOnFailureListener {
                    exception->
                    Toast.makeText(requireContext(),exception.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode==requestMultiplePermissions){
            //İzin verilmezse
            Toast.makeText(requireContext(),"İşlem gerçekleşmedi",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode==requestMultiplePermissions){
            //İzin verirse
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
            Toast.makeText(requireContext(),"Teşekkürler",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
    private fun registerLauncher(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode==RESULT_OK){
                val intentFromResul=result.data
                if (intentFromResul !=null){
                    secilenGorsel= intentFromResul.data
                    try {
                        if (Build.VERSION.SDK_INT>=28){
                            val source=ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            binding.gorselKoy.setImageBitmap(secilenBitmap)

                        }else{
                            secilenBitmap=MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.gorselKoy.setImageBitmap(secilenBitmap)
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

        }


    }


}