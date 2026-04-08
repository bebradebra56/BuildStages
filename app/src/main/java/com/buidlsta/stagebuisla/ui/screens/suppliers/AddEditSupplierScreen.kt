@file:OptIn(ExperimentalMaterial3Api::class)
package com.buidlsta.stagebuisla.ui.screens.suppliers

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.buidlsta.stagebuisla.ui.components.AppTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddEditSupplierScreen(navController: NavController, supplierId: Long? = null) {
    val vm: SuppliersViewModel = koinViewModel()
    val editSupplier by vm.editSupplier.collectAsState()
    val isEdit = supplierId != null

    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    LaunchedEffect(supplierId) { if (supplierId != null) vm.loadEditSupplier(supplierId) }
    LaunchedEffect(editSupplier) {
        editSupplier?.let { s -> name = s.name; contact = s.contact; email = s.email; phone = s.phone; address = s.address }
    }

    Scaffold(topBar = { AppTopBar(title = if (isEdit) "Edit Supplier" else "Add Supplier", onBack = { navController.popBackStack() }) }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Supplier name *") }, leadingIcon = { Icon(Icons.Default.Store, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact person") }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, leadingIcon = { Icon(Icons.Default.Phone, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Button(
                onClick = {
                    if (name.isBlank()) return@Button
                    if (isEdit && supplierId != null) vm.updateSupplier(supplierId, name, contact, email, phone, address)
                    else vm.addSupplier(name, contact, email, phone, address)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank()
            ) {
                Icon(if (isEdit) Icons.Default.Save else Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEdit) "Save Changes" else "Add Supplier")
            }
        }
    }
}
