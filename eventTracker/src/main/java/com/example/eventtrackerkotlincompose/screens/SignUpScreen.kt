package com.example.eventtrackerkotlincompose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerkotlincompose.R
import com.example.eventtrackerkotlincompose.components.ButtonComponent
import com.example.eventtrackerkotlincompose.components.ClickableLoginTextComponent
import com.example.eventtrackerkotlincompose.components.CustomTextField
import com.example.eventtrackerkotlincompose.components.DividerTextComponent
import com.example.eventtrackerkotlincompose.components.HeadingTextComponent
import com.example.eventtrackerkotlincompose.components.NormalTextComponent
import com.example.eventtrackerkotlincompose.components.PasswordTextField
import com.example.eventtrackerkotlincompose.network.User
import com.example.eventtrackerkotlincompose.viewModels.LoginStateViewModel
import com.example.eventtrackerkotlincompose.viewModels.SignUpStateViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUp (
    viewModel: SignUpStateViewModel = viewModel(),
    onSignUpComplete: () -> Unit,
    onLoginClick: () ->Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isOrganizer by remember { mutableStateOf(false) }
    var organizerName by remember { mutableStateOf("") }
    var organizerDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isRegistered) {
        if (viewModel.isRegistered) {
            onSignUpComplete()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column (modifier = Modifier.fillMaxSize()) {
            NormalTextComponent(value = stringResource(id = R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))
            Spacer(modifier = Modifier.height(20.dp))
            if (viewModel.registerError != null) {
                Text(
                    text = viewModel.registerError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
            CustomTextField(
                labelValue = "First Name",
                Icons.Rounded.Person,
                onTextSelected = { firstName = it }
            )
            CustomTextField(
                labelValue = "Last Name",
                Icons.Rounded.Person,
                onTextSelected = {lastName = it}
            )
            CustomTextField(
                labelValue = "Email",
                Icons.Rounded.Email,
                onTextSelected = {email = it}
            )
            PasswordTextField(
                labelValue = "Password",
                Icons.Rounded.Lock,
                onTextSelected = {password = it}
            )
            PasswordTextField(
                labelValue = "Confirm Password",
                Icons.Rounded.Lock,
                onTextSelected = {confirmPassword = it}
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isOrganizer,
                    onCheckedChange = { isOrganizer = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Create Organizer Account")
            }
            if (isOrganizer) {
                CustomTextField(
                    labelValue = "Organizer Name",
                    Icons.Rounded.Person,
                    onTextSelected = { organizerName = it }
                )
                CustomTextField(
                    labelValue = "Description (Optional)",
                    Icons.Rounded.Description,
                    onTextSelected = { organizerDescription = it }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            if (viewModel.isLoading) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            } else {
                ButtonComponent(value = "Register", onButtonClicked = {
                    coroutineScope.launch {
                        viewModel.register(firstName, lastName, email, password, confirmPassword, isOrganizer, organizerName, organizerDescription)
                    }
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent {
                onLoginClick()
            }
        }
    }
}



@Preview
@Composable
fun PreviewSignUpScreen() {
    val viewModel: SignUpStateViewModel = viewModel()
    SignUp(viewModel, {},{})
}