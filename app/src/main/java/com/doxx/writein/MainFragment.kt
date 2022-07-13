package com.doxx.writein

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.doxx.writein.adapters.NoteAdapter
import com.doxx.writein.databinding.FragmentMainBinding
import com.doxx.writein.models.NoteResponse
import com.doxx.writein.utils.NetworkResult
import com.doxx.writein.viewmodel.NoteViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding:FragmentMainBinding?=null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel> ()
   private lateinit var  adapter:NoteAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding =FragmentMainBinding.inflate(inflater,container,false)
        adapter=NoteAdapter (::onNoteClicked)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel.getNotes()

        binding.noteList.layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.noteList.adapter=adapter
        binding.addNote.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
        }
        bindObservers()

    }

    private fun bindObservers() {
        noteViewModel.notesLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Success ->{
                    binding.progressBar.visibility=View.GONE
                    Log.d("Mytag",it.data.toString())
                    adapter.submitList(it.data)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {

                    binding.progressBar.isVisible=true
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private fun onNoteClicked(noteResponse: NoteResponse){
        val bundle =Bundle().apply {
            putString("note",Gson().toJson(noteResponse))
        }
        findNavController().navigate(R.id.action_mainFragment_to_noteFragment,bundle)
        //Toast.makeText(requireContext(), noteResponse.title, Toast.LENGTH_SHORT).show()
    }


}