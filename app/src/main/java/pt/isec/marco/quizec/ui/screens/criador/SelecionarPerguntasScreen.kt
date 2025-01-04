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

@Composable
fun SelecionarPerguntasScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController,
    showComplete: Boolean = false,
) {
    val perguntas by remember { viewModel.perguntasAux }
    var perguntasaux by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        viewModel.startPerguntasObserver()
    }

    if (perguntas.isEmpty()) {
        Text("Nenhuma pergunta disponível")
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                perguntas.forEach { pergunta ->
                    var isSelected by remember { mutableStateOf(false) }
                    var wasSelected by remember { mutableStateOf(false) }

                    if (viewModel.perguntas.value.contains(pergunta.id)) {
                        isSelected = false
                        wasSelected = true
                    }
                    val backgroundColor =
                        if (isSelected) Color.Green
                        else if(wasSelected) Color.Magenta
                        else Color(108, 147, 201, 255)

                    Box(
                        modifier = Modifier
                            .shadow(4.dp)
                            .background(backgroundColor)
                            .clickable {
                                isSelected = !isSelected
                                if (isSelected && !viewModel.perguntas.value.contains(pergunta.id)) {
                                    Log.d(
                                        "SelecionarPerguntasScreen",
                                        "Adicionou pergunta ${pergunta.id}"
                                    )
                                    perguntasaux += pergunta.id
                                    Log.d(
                                        "SelecionarPerguntasScreen",
                                        "Adicionou pergunta ${perguntasaux.size}"
                                    )
                                }
                                if (!isSelected) {
                                    Log.d(
                                        "SelecionarPerguntasScreen",
                                        "Removeu pergunta ${pergunta.id}"
                                    )
                                    perguntasaux -= pergunta.id
                                }
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pergunta: ${pergunta.titulo}",
                                        color = Color.Blue,
                                        modifier = Modifier
                                            .weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Seta para frente",
                                        tint = Color.Blue,
                                        modifier = Modifier
                                            .clickable {

                                            }
                                    )
                                }
                                TipoPerguntaCard(
                                    pergunta = pergunta,
                                    showComplete = showComplete
                                )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    viewModel.perguntas.value += perguntasaux
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
                Text("Adicionar perguntas")
            }
        }
    }
}