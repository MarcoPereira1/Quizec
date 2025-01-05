package pt.isec.marco.quizec.ui.screens.criador

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Partilha


@Composable
fun PartilharQuestionarioScreen(
    viewModel: FirebaseViewModel,
    idQuestionario: String,
    navController: NavHostController
) {
    var tempoEspera by remember { mutableStateOf(0L) }
    var duracao by remember { mutableStateOf(0L) }
    val partilhaId by viewModel.partilhaId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = tempoEspera.toString(),
                isError = tempoEspera < 0,
                label = { Text("Tempo de Espera (em segundos):") },
                onValueChange = { newText ->
                    tempoEspera = newText.toLongOrNull() ?: 0L
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = duracao.toString(),
                isError = duracao <= 0,
                label = { Text("Duração do Questionário (em segundos):") },
                onValueChange = { newText ->
                    duracao = newText.toLongOrNull() ?: 0L
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        viewModel.addPartilhaToFirestore(
                            Partilha(
                                id = "",
                                idQuestionario = idQuestionario,
                                tempoEspera = tempoEspera,
                                duracao = duracao,
                                respostaList = emptyList(),
                                usersList = emptyList()
                            )
                        )
                        navController.navigate("menu-criador") {
                            popUpTo("menu-criador") {
                                inclusive = true
                            }
                        }
                    }

                ) {
                    Text("Começar Partilha")
                }
            }
            val partilhaSuccess by viewModel.partilhaSuccess.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(partilhaSuccess) {
                if (partilhaSuccess) {
                    Toast.makeText(context, "Partilha iniciada com sucesso!\nCodigo de partilha: $partilhaId", Toast.LENGTH_SHORT).show()
                    navController.navigate("menu-criador") {
                        popUpTo("menu-criador") { inclusive = true }
                    }
                    viewModel.resetSuccessState()
                }
            }
        }
    }
}
