package com.alexiaapps.retrofit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexiaapps.retrofit.data.ObjectsRepository
import com.alexiaapps.retrofit.data.Result
import com.alexiaapps.retrofit.data.model.ObjectID
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val objectsRepository: ObjectsRepository
) : ViewModel() {

    private val _objects = MutableStateFlow<List<ObjectID>>(emptyList())
    val objects = _objects.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            objectsRepository.getObjectIDsList().collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { objectIDs ->
                            fetchObjectDetails(objectIDs.objectIDs)
                        }
                    }
                }
            }
        }
    }

    private fun fetchObjectDetails(ids: List<Int>) {
        viewModelScope.launch {
            val objectList = mutableListOf<ObjectID>()
            ids.forEach { id ->
                val objectResult = objectsRepository.getObjectByID(id)
                if (objectResult is Result.Success) {
                    objectResult.data?.let { objectList.add(it) }
                }
            }
            _objects.update { objectList }
        }
    }
}
