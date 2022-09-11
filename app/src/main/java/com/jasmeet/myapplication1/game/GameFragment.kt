

package com.jasmeet.myapplication1.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jasmeet.myapplication1.R
import com.jasmeet.myapplication1.databinding.GameFragmentBinding


class GameFragment : Fragment() {

    private val viewModel : GameViewModel by viewModels()


    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        Log.d("GameFragment", "GameFragment created/re-created")
        Log.d("Game Fragment", "Word : ${viewModel.currentScrambledWord}" +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS


        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates

        binding.lifecycleOwner = viewLifecycleOwner

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }


    //      Checks the user's word, and updates the score accordingly.
    //      Displays the next scrambled word.

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if(viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if(!viewModel.nextWord())
                showFinalScoreDialog()
        }
        else
            setErrorTextField(true)
    }


//     Skips the current word without changing the score.
//     Increases the word count.

    private fun onSkipWord() {
        if(viewModel.nextWord()) {
            setErrorTextField(false)

        }
        else
            showFinalScoreDialog()
    }


//    Creates and shows an AlertDialog with the final score.

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored,viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ -> exitGame() }
            .setPositiveButton(getString(R.string.play_again)) { _, _ -> restartGame() }
            .show()
    }


//       Re-initializes the data in the ViewModel and updates the views with the new data, to
//       restart the game.

    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)

    }


//       Exits the game.

    private fun exitGame() {
        activity?.finish()
    }


//      Sets and resets the text field error status.

    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}
