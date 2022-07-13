package com.doxx.writein

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.doxx.writein.databinding.FragmentNoteBinding
import com.doxx.writein.models.NoteRequest
import com.doxx.writein.models.NoteResponse
import com.doxx.writein.utils.NetworkResult
import com.doxx.writein.viewmodel.NoteViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {


    private var _binding:FragmentNoteBinding?=null
    private val binding get() = _binding!!
    var note: NoteResponse?=null
    private val noteViewModel by viewModels<NoteViewModel> ()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentNoteBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpInitialData()
        bindHandlers()
        noteViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Success->{
                    findNavController().popBackStack()
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        })

    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener {
            note?.let {
                noteViewModel.deleteNote(it._id)
            }
        }
        binding.btnSubmit.setOnClickListener {
            val title = binding.txtTitle.text.toString()
            val desc = binding.txtDescription.text.toString()
            val noteRequest = NoteRequest(title,desc)
            if(note == null) noteViewModel.createNote(noteRequest)
            else noteViewModel.updateNote(note!!._id,noteRequest)
        }
    }

    private fun setUpInitialData() {
        val jsonNote = arguments?.getString("note")
        if(jsonNote!= null){
            note  = Gson().fromJson(jsonNote,NoteResponse::class.java)
            note?.let {
                binding.txtTitle.setText(it.title.toString())
                binding.txtDescription.setText(it.description.toString())
            }

        }
        else {
            binding.addEditText.text="Add Note"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}