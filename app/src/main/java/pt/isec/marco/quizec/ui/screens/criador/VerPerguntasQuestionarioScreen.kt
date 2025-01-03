package pt.isec.marco.quizec.ui.screens.criador

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Pergunta
import pt.isec.marco.quizec.utils.FStorageUtil

@Composable
fun VerQuestionarioScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController,
    showComplete: Boolean = false,
) {
    val perguntasIds = viewModel.perguntas.value
    var perguntas by remember { mutableStateOf<List<Pergunta>>(emptyList()) }
    var perguntaSelecionada by remember { mutableStateOf<Pergunta?>(null) }

    LaunchedEffect(perguntasIds) {
        perguntas = mutableListOf()

        perguntasIds.forEach { perguntaId ->
            FStorageUtil.getPerguntaById(perguntaId) { pergunta, _ ->
                if (pergunta != null) {

                    perguntas = perguntas + pergunta
                }

            }
        }
    }

    if (perguntaSelecionada != null) {
        TipoPerguntaCard(
            pergunta = perguntaSelecionada!!,
            showComplete = true
        )
    } else if (perguntas.isEmpty()) {
        Text("Nenhuma pergunta disponível")
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(perguntas) { pergunta ->
                    Box(
                        modifier = Modifier
                            .shadow(4.dp)
                            .clickable {
                                perguntaSelecionada = pergunta

                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .shadow(
                                    4.dp,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column {
                                TipoPerguntaCard(
                                    pergunta = pergunta,
                                    showComplete = showComplete,
                                )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    navController.navigate("criar-questionario") {
                        popUpTo("criar-questionario") {
                            inclusive = true
                        }
                    }

                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Voltar")
            }
        }
    }
}
