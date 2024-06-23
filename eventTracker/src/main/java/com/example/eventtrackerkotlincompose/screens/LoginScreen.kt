import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerkotlincompose.R
import com.example.eventtrackerkotlincompose.components.ButtonComponent
import com.example.eventtrackerkotlincompose.components.ClickableLoginTextComponent
import com.example.eventtrackerkotlincompose.components.CustomTextField
import com.example.eventtrackerkotlincompose.components.DividerTextComponent
import com.example.eventtrackerkotlincompose.components.HeadingTextComponent
import com.example.eventtrackerkotlincompose.components.NormalTextComponent
import com.example.eventtrackerkotlincompose.components.PasswordTextField
import com.example.eventtrackerkotlincompose.components.UnderLinedTextComponent
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.viewModels.LoginStateViewModel
import kotlinx.coroutines.launch


@Composable
fun Login(
    viewModel: LoginStateViewModel = viewModel(),
    onLoginSuccessful: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPassword: () -> Unit
)
{
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.isLoggedIn) {
        if (viewModel.isLoggedIn) {
            onLoginSuccessful()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column (modifier = Modifier.fillMaxSize()){
            NormalTextComponent(value = stringResource(id = R.string.login))
            HeadingTextComponent(value = stringResource(id = R.string.welcome))
            Spacer(modifier = Modifier.height(40.dp))

            if (viewModel.loginError != null) {
                Text(
                    text = "Email or password are incorrect!",
                    color = Color.Red,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
            CustomTextField(
                labelValue = "Email",
                imgVector = Icons.Rounded.Email,
                onTextSelected = {
                    email = it
                }
            )
            PasswordTextField(
                labelValue = "password",
                imgVector = Icons.Rounded.Lock,
                onTextSelected = {
                    password = it
                })

            Spacer(modifier = Modifier.height(40.dp))
            UnderLinedTextComponent(
                value = stringResource(id = R.string.forgot_password),
                onTextSelected = {onForgotPassword()})
            Spacer(modifier = Modifier.height(40.dp))

            if (viewModel.isLoading) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            } else {
                ButtonComponent(value = stringResource(id = R.string.login), onButtonClicked = {
                    coroutineScope.launch {
                        viewModel.login(email = email, password = password)
                    }
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = false) {
                onSignUpClick()
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview() {
    Login(viewModel<LoginStateViewModel>(), {}, {}, {})
}