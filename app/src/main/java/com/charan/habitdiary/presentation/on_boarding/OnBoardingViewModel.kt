package com.charan.habitdiary.presentation.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.habitdiary.data.repository.DataStoreRepository
import com.charan.habitdiary.utils.getAppVersion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStoreRepo : DataStoreRepository
) : ViewModel(){
    private val _state = MutableStateFlow(OnBoardingState())
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<OnBoardingEffects>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event : OnBoardingEvent) {
        when(event) {
            OnBoardingEvent.GetStarted -> {

            }
            OnBoardingEvent.NextPage -> {
                onNextPage()
            }
            is OnBoardingEvent.PageChanged -> {
                onPageChange(event.page)
            }
        }
    }

    private fun onNextPage() =viewModelScope.launch{
        val nextPage = state.value.currentPage + 1
        if(nextPage == state.value.onBoardingPage.size){
            dataStoreRepo.setOnBoardingCompleted(true)
            dataStoreRepo.setLastScreenChangeLogVersion(getAppVersion())
            sendEvent(OnBoardingEffects.NavigateToHome)
            return@launch
        }
        sendEvent(OnBoardingEffects.OnScrollToPage(nextPage))
        onPageChange(nextPage)
    }

    private fun onPageChange(index : Int) {
        _state.update {
            it.copy(
                currentPage = index
            )
        }
    }

    private fun sendEvent(effect : OnBoardingEffects) = viewModelScope.launch {
       _effect.emit(effect)
    }
}