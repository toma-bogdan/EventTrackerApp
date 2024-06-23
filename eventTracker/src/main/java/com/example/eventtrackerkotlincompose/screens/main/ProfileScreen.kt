package com.example.eventtrackerkotlincompose.screens.main

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventtrackerkotlincompose.components.PasswordTextField
import com.example.eventtrackerkotlincompose.config.AppConfig
import com.example.eventtrackerkotlincompose.network.Role
import com.example.eventtrackerkotlincompose.viewModels.LoginStateViewModel
import com.example.eventtrackerkotlincompose.viewModels.ProfileViewModel
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    innerPadding: PaddingValues,
    onLogoutClick: () -> Unit
) {
    val user by viewModel.userDetails.collectAsState()
    val organizer by viewModel.organizer.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }
    var showChangePasswordModal by remember { mutableStateOf(false) }

    LaunchedEffect(selectedImageUri) {
        Log.d("din launch","din launch effect cu $selectedImageUri")
        selectedImageUri?.let {
            viewModel.changeProfileImage(it, context)
            selectedImageUri = null
        }

    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user == null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
            }
        } else {
            if (user!!.profile_image != null) {
                val correctUrl = user!!.profile_image!!.replace("localhost", AppConfig.SERVER_IP)
                AsyncImage(
                    model = correctUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { pickImageLauncher.launch("image/*") }
                    )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { pickImageLauncher.launch("image/*") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${user!!.firstName} ${user!!.lastName}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user!!.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ClickableText(
                text = AnnotatedString("Change Password"),
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                onClick = { showChangePasswordModal = true },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ClickableText(
                text = AnnotatedString("Logout"),
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
                onClick = {
                    viewModel.logout()
                    onLogoutClick()
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            if (organizer != null && user!!.role == Role.ORGANIZER) {
                if (viewModel.updatedOrganizer) {
                    Text(
                        text = "Organization Details saved!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Green,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                } else {
                    Text(
                        text = "Organization Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                var organizationName by remember { mutableStateOf(organizer!!.name) }
                var organizationDescription by remember { mutableStateOf(organizer!!.description) }

                OutlinedTextField(
                    value = organizationName,
                    onValueChange = { organizationName = it },
                    label = { Text("Organization Name") },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = organizationDescription,
                    onValueChange = { organizationDescription = it },
                    label = { Text("Organization Description") },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.updateOrganizer(organizationName, organizationDescription)
                    }
                },
                    colors = ButtonDefaults.buttonColors(Color(0xFF92A3FD))) {
                    Text(text = "Update Organization")
                }
            }
        }
    }

    if (showChangePasswordModal) {
        ChangePasswordModal(
            onDismissRequest = { showChangePasswordModal = false },
            onChangePassword = { oldPassword, newPassword, confirmNewPassword ->
                coroutineScope.launch {
                    showChangePasswordModal = !viewModel.changePassword(user!!.email, oldPassword, newPassword, confirmNewPassword)
                }
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun ChangePasswordModal(
    onDismissRequest: () -> Unit,
    onChangePassword: (oldPassword: String, newPassword: String, confirmNewPassword: String) -> Unit,
    viewModel: ProfileViewModel
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Change Password") },
        containerColor = Color(0xFFF7F8F8),
        text = {
            Column {
                if (viewModel.changePasswordError != null) {
                    Text(text = viewModel.changePasswordError!!, color = Color.Red)
                }
                PasswordTextField(labelValue = "Old Password", imgVector = Icons.Default.Lock) {
                    oldPassword = it
                }
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(labelValue = "New Password", imgVector = Icons.Default.Autorenew) {
                    newPassword = it
                }
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(labelValue = "Confirm New Password", imgVector = Icons.Default.Autorenew) {
                    confirmNewPassword = it
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onChangePassword(oldPassword, newPassword, confirmNewPassword) }
            ) {
                Text("Change Password")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
