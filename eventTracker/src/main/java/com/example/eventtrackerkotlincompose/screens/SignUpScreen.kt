package com.example.eventtrackerkotlincompose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eventtrackerkotlincompose.R
import com.example.eventtrackerkotlincompose.components.ButtonComponent
import com.example.eventtrackerkotlincompose.components.ClickableLoginTextComponent
import com.example.eventtrackerkotlincompose.components.CustomTextField
import com.example.eventtrackerkotlincompose.components.DividerTextComponent
import com.example.eventtrackerkotlincompose.components.HeadingTextComponent
import com.example.eventtrackerkotlincompose.components.NormalTextComponent
import com.example.eventtrackerkotlincompose.components.PasswordTextField

@Composable
fun SignUp (
    onSignUpComplete: () -> Unit,
    onLoginClick: () ->Unit
) {
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
            CustomTextField(labelValue = "First Name", Icons.Rounded.Person)
            CustomTextField(labelValue = "Last Name", Icons.Rounded.Person)
            CustomTextField(labelValue = "Email", Icons.Rounded.Email)
            PasswordTextField(labelValue = "Password", Icons.Rounded.Lock)
            PasswordTextField(labelValue = "Confirm Password", Icons.Rounded.Lock)
            Spacer(modifier = Modifier.height(40.dp))
            ButtonComponent(value = "Register", onButtonClicked = {})
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent {

            }
        }

    }
}

@Preview
@Composable
fun PreviewSignUpScreen() {
    SignUp({},{})
}