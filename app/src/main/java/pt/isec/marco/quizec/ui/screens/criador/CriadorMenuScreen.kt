package pt.isec.marco.quizec.ui.screens.criador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pt.isec.marco.quizec.ui.screens.BackgroundWithImage
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel

@Composable
fun CriadorMenuScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController
) {
    BackgroundWithImage(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("User: ${viewModel.user.value?.email ?: ""}")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Menu Criador")
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("historico-questionarios") {
                        popUpTo("historico-questionarios") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text("Meus questionarios")
            }
            Button(
                onClick = {
                    navController.navigate("criar-questionario") {
                        popUpTo("criar-questionario") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text("Criar questionario")
            }
            Button(
                onClick = {
                    navController.navigate("partilhar-questionario") {
                        popUpTo("partilhar-questionario") {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text("Partilhar questionario")
            }
        }
    }
}