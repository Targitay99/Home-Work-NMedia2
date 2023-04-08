package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.urlArg
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentPhotoViewBinding
import ru.netology.nmedia.view.load
import ru.netology.nmedia.viewmodel.PostViewModel


class PhotoView : Fragment() {

    //private val viewModel: PostViewModel by activityViewModels()

    private var fragmentBinding: FragmentPhotoViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoViewBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding
        val photo = arguments?.urlArg
        binding.attachment.load("${BuildConfig.BASE_URL}/media/${photo}")
        return binding.root
    }
}
