package com.tabotabo.gorestapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tabotabo.gorestapp.core.domain.User
import com.tabotabo.gorestapp.presentation.viewmodel.UserScreenMode
import com.tabotabo.gorestapp.presentation.viewmodel.UserUiState
import com.tabotabo.gorestapp.presentation.viewmodel.UserViewModel
import com.tabotabo.gorestapp.ui.theme.GoRestAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoRestAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val state by userViewModel.userUiState.collectAsStateWithLifecycle()
    val screenMode by userViewModel.userScreenModeState.collectAsStateWithLifecycle()

    val loginScreenModeHandler = remember {
        { userViewModel.changeScreenMode(UserScreenMode.LOGIN) }
    }

    val registerScreenModeHandler = remember {
        { userViewModel.changeScreenMode(UserScreenMode.REGISTER) }
    }

    when (state) {
        UserUiState.Loading -> LoadingScreen(modifier)
        is UserUiState.Success -> UserHome(
            modifier = modifier,
            user = (state as UserUiState.Success).user
        )
        is UserUiState.Error -> LoginOrRegisterScreen(
            screenMode = screenMode,
            userViewModel = userViewModel,
            modifier = modifier,
            error = (state as UserUiState.Error).reason,
            onLoginScreenModeClicked = loginScreenModeHandler,
            onRegisterScreenModeClicked = registerScreenModeHandler
        )
        UserUiState.RequireUserInput -> LoginOrRegisterScreen(
            screenMode = screenMode,
            userViewModel = userViewModel,
            modifier = modifier,
            error = null,
            onLoginScreenModeClicked = loginScreenModeHandler,
            onRegisterScreenModeClicked = registerScreenModeHandler
        )
    }
}

@Composable
fun LoginOrRegisterScreen(
    screenMode: UserScreenMode,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    error: String? = null,
    onLoginScreenModeClicked: () -> Unit,
    onRegisterScreenModeClicked: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (screenMode) {
            UserScreenMode.LOGIN -> LoginScreen(
                userViewModel = userViewModel,
                onRegisterClicked = onRegisterScreenModeClicked,
                error = error
            )

            UserScreenMode.REGISTER -> RegisterScreen(
                userViewModel = userViewModel,
                onLoginClicked = onLoginScreenModeClicked,
                error = error
            )
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoginScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    onRegisterClicked: () -> Unit,
    error: String? = null
) {
    var username by remember { userViewModel.username }
    var password by remember { userViewModel.password }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { userViewModel.performLogin(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = onRegisterClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RegisterScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    onLoginClicked: () -> Unit,
    error: String? = null
) {
    var username by remember { userViewModel.username }
    var displayName by remember { userViewModel.name }
    var password by remember { userViewModel.password }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = username,
            onValueChange = {
                username = it
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = displayName,
            onValueChange = {
                displayName = it
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { userViewModel.performRegistration(username, displayName, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onLoginClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun UserHome(user: User, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            Text("Hi ${user.displayName}")
        }
    }
}