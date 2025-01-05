package pt.isec.marco.quizec.ui.screens.utilizador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pt.isec.marco.quizec.ui.screens.BackgroundWithImage
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Partilha
import pt.isec.marco.quizec.utils.FStorageUtil


@Composable
fun EntrarQuestionarioScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController
    ) {
    var partilha by remember { mutableStateOf<Partilha?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userInput by remember { mutableStateOf("") }
    BackgroundWithImage(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column {
                TextField(
                    value = userInput,
                    onValueChange = { newText ->
                        userInput = newText
                    },
                    label = { Text("Introduz codigo questionario") },
                    placeholder = { Text("Escreve aqui...") }
                )
                Text(errorMessage ?: "")
            }
            Button(
                onClick = {
                    errorMessage = null
                    // TODO verificar se o codigo é valido
                    FStorageUtil.getPartilhaById(userInput) { result, error ->
                        if (error != null) {
                            errorMessage = "Erro ao obter partilha: ${error.message}"
                        } else if (result != null) {
                            partilha = result
                            val tempoEspera = "60" // Exemplo de valor
                            navController.navigate(
                                "responder-questionario/${userInput}/$tempoEspera"
                            )
                        } else {
                            errorMessage = "Nenhuma partilha encontrada com este código."
                        }
                    }
//                navController.navigate("responder-questionario"){
//                    popUpTo("responder-questionario") {
//                        inclusive = true
//                    }
//                }
                }
            ) {
                Text("Entrar")
            }
        }
    }
}