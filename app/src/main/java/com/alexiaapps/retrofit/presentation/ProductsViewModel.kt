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

    private var allObjectIDs: List<Int> = emptyList()  // Lista completă de ID-uri
    private var currentPage = 0  // Indexul paginii curente
    private val pageSize = 10  // Numărul de obiecte pe pagină

    init {
        viewModelScope.launch {
            objectsRepository.getObjectIDsList().collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }
                    is Result.Success -> {
                        result.data?.let { objectIDs ->
                            allObjectIDs = objectIDs.objectIDs  // Salvăm toate ID-urile
                            loadNextPage()  // Încărcăm prima pagină
                        }
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        if (currentPage * pageSize >= allObjectIDs.size) return  // Verificăm dacă am terminat

        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allObjectIDs.size)
        val nextBatch = allObjectIDs.subList(startIndex, endIndex)

        fetchObjectDetails(nextBatch)
        currentPage++  // Incrementăm pagina
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
            _objects.update { currentList -> currentList + objectList }  // Adăugăm noile obiecte
        }
    }
}