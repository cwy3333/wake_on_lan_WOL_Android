package com.example.wakeonlan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wakeonlan.WakeOnLanApp
import com.example.wakeonlan.data.Device
import com.example.wakeonlan.data.DeviceDao
import com.example.wakeonlan.wol.WakeOnLan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DeviceListState(
    val devices: List<Device> = emptyList(),
    val isLoading: Boolean = true,
    val wakeResult: String? = null
)

class DeviceViewModel(
    private val dao: DeviceDao
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceListState())
    val state: StateFlow<DeviceListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllDevices().collect { devices ->
                _state.update { it.copy(devices = devices, isLoading = false) }
            }
        }
    }

    fun addDevice(device: Device) {
        viewModelScope.launch {
            dao.insert(device)
        }
    }

    fun updateDevice(device: Device) {
        viewModelScope.launch {
            dao.update(device)
        }
    }

    fun deleteDevice(device: Device) {
        viewModelScope.launch {
            dao.delete(device)
        }
    }

    fun wakeDevice(device: Device) {
        viewModelScope.launch {
            val result = WakeOnLan.send(device.macAddress, device.ipAddress, device.port)
            val message = result.fold(
                onSuccess = { "唤醒包已发送" },
                onFailure = { "发送失败: ${it.message}" }
            )
            _state.update { it.copy(wakeResult = "${device.name}: $message") }
        }
    }

    fun clearWakeResult() {
        _state.update { it.copy(wakeResult = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeviceViewModel(WakeOnLanApp.instance.database.deviceDao()) as T
            }
        }
    }
}
