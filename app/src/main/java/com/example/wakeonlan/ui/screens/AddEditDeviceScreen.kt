package com.example.wakeonlan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wakeonlan.data.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDeviceDialog(
    device: Device? = null,
    onDismiss: () -> Unit,
    onSave: (Device) -> Unit
) {
    val isEdit = device != null
    var name by remember { mutableStateOf(device?.name ?: "") }
    var mac by remember { mutableStateOf(device?.macAddress ?: "") }
    var ip by remember { mutableStateOf(device?.ipAddress ?: "255.255.255.255") }
    var port by remember { mutableStateOf(device?.port?.toString() ?: "9") }
    var note by remember { mutableStateOf(device?.note ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var macError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEdit) "编辑设备" else "添加设备") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("设备名称 *") },
                    isError = nameError,
                    supportingText = if (nameError) {{ Text("名称不能为空") }} else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mac,
                    onValueChange = {
                        mac = it
                        macError = false
                    },
                    label = { Text("MAC 地址 *") },
                    placeholder = { Text("00:11:22:33:44:55") },
                    isError = macError,
                    supportingText = if (macError) {{ Text("请输入有效的 MAC 地址") }} else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = ip,
                        onValueChange = { ip = it },
                        label = { Text("IP 地址") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.weight(2f)
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("端口") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val trimmedName = name.trim()
                val trimmedMac = mac.trim()

                nameError = trimmedName.isEmpty()
                macError = trimmedMac.isEmpty() || !trimmedMac.matches(Regex("^([0-9A-Fa-f]{2}[:-]?){5}([0-9A-Fa-f]{2})$"))

                if (!nameError && !macError) {
                    onSave(
                        device?.copy(
                            name = trimmedName,
                            macAddress = trimmedMac,
                            ipAddress = ip.trim().ifBlank { "255.255.255.255" },
                            port = port.trim().toIntOrNull() ?: 9,
                            note = note.trim()
                        ) ?: Device(
                            name = trimmedName,
                            macAddress = trimmedMac,
                            ipAddress = ip.trim().ifBlank { "255.255.255.255" },
                            port = port.trim().toIntOrNull() ?: 9,
                            note = note.trim()
                        )
                    )
                }
            }) {
                Text(if (isEdit) "保存" else "添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
