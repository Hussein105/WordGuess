package com.ho.wordguess.game

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.ho.wordguess.R
import com.ho.wordguess.databinding.FragmentGameBinding
import com.ho.wordguess.game.GameViewModel.BuzzType.*

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_game,
            container,
            false
        )
        // implement the viewModel
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // observer to update the game state
        viewModel.gameFinished.observe(viewLifecycleOwner) { hasFinished ->
            if (hasFinished) {
                gameFinished()
                viewModel.onGameFinishComplete()
            }
        }

        viewModel.eventBuzz.observe(viewLifecycleOwner) { buzzType ->
            if (buzzType != NO_BUZZ) {
                buzz(buzzType.pattern)
                viewModel.onBuzzComplete()
            }
        }

        return binding.root
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()
        buzzer?.let {
            buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
        }
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val currentScore = viewModel.score.value ?: 0
        val action =
            GameFragmentDirections.actionGameFragmentToScoreFragment(currentScore)
        findNavController(this).navigate(action)
    }
}