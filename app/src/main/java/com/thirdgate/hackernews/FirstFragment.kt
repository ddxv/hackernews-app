package com.thirdgate.hackernews

import ApiService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.thirdgate.hackernews.databinding.FragmentFirstBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val uiScope = CoroutineScope(Dispatchers.Main)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val apiService = ApiService()
                val articleData =
                    apiService.getArticle("37208083") // Replace with desired article number

                val kids = anyToArrayOfString(articleData["kids"])
                //val kidsLength = kids.size.toString()
                val myArticle =
                    """${articleData["title"]}
                        |score: ${
                        articleData["score"].toString().replace(".0", "")
                    } comments: ${kids.size.toString()}  """.trimMargin()
                Log.i("Write Article", myArticle)
                binding.textviewFirst.text = myArticle
            } catch (e: Exception) {
                Log.e("FirstFragment", "ApiService $e")
                // Handle the error (e.g., show a Toast or a Snackbar)
            }
        }
    }

    fun anyToArrayOfString(value: Any?): Array<String> {
        return (value as? List<*>)?.map { it.toString() }?.toTypedArray() ?: emptyArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
