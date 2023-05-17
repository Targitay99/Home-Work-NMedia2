package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val mBinding get() = _binding!!
    private var user = User("", "")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        mBinding.log.setOnClickListener {
            user = User(
                mBinding.username.text.toString(),
                mBinding.password.text.toString()
            )

            if (user.login.isNotEmpty() && user.password.isNotEmpty()) {
                viewModel.singIn(user)
               // findNavController().navigateUp()
            } else Toast.makeText(context, getString(R.string.Enter_name), Toast.LENGTH_LONG)
                .show()
        }

        viewModel.dataState.observe(viewLifecycleOwner) {
            when (it) {
                0 -> {
                    findNavController().navigateUp()
                    viewModel._dataState.value = -1
                }
                -1 -> {
                    //      Toast.makeText(context, "1", Toast.LENGTH_LONG)
                    //          .show()
                }
                1 -> {
                    Toast.makeText(context, R.string.Invalid_username_or_password, Toast.LENGTH_LONG)
                        .show()
                    viewModel._dataState.value = -1
                }
                2 -> {
                    Toast.makeText(context, R.string.Error, Toast.LENGTH_LONG)
                        .show()
                    viewModel._dataState.value = -1
                }
                3 -> {
                    Toast.makeText(context, R.string.Error, Toast.LENGTH_LONG)
                        .show()
                    viewModel._dataState.value = -1
                }
                else -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_LONG)
                        .show()
                    viewModel._dataState.value = -1
                }
            }
        }
        return mBinding.root
    }
}