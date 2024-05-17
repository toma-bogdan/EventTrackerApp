import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch


@Composable
fun Login(
    onLoginSuccessful: () -> Unit,
    onSignUpClick: () -> Unit) {
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

            CustomTextField(labelValue = "email", imgVector = Icons.Rounded.Email)
            PasswordTextField(labelValue = "password", imgVector = Icons.Rounded.Lock)

            Spacer(modifier = Modifier.height(40.dp))
            UnderLinedTextComponent(value = stringResource(id = R.string.forgot_password))
            Spacer(modifier = Modifier.height(40.dp))

            ButtonComponent(value = stringResource(id = R.string.login), onButtonClicked = { onLoginSuccessful() })
            Spacer(modifier = Modifier.height(20.dp))
            DividerTextComponent()
            ClickableLoginTextComponent(tryingToLogin = false) {
                onSignUpClick()
            }
        }
    }
}

@Composable
fun LoginScreen() {

    // context
    val context = LocalContext.current
    // scope
    val scope = rememberCoroutineScope()
    // datastore Email
    val dataStore = UserDetailsStore(context)
    // get saved email
    val savedEmail = dataStore.getEmail.collectAsState(initial = "")

    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            modifier = Modifier
                .padding(16.dp, top = 30.dp),
            text = "Email",
            color = Color.Gray,
            fontSize = 12.sp
        )
        //email field
        OutlinedTextField(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
        )
        Spacer(modifier = Modifier.height(120.dp))
        // save button
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp),
            onClick = {
                //launch the class in a coroutine scope
                scope.launch {
                    dataStore.saveEmail(email)
                }
            },
        ) {
            // button text
            Text(
                text = "Save",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Text(
            text = savedEmail.value!!,
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
fun LoginPreview() {
    Login({}, {})
}