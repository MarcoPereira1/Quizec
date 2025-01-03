package pt.isec.marco.quizec.ui.screens.utilizador

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import pt.isec.marco.quizec.ui.screens.BackgroundWithImage
import pt.isec.marco.quizec.ui.screens.criador.TipoPerguntaCard
import pt.isec.marco.quizec.ui.viewmodels.Pergunta
import pt.isec.marco.quizec.ui.viewmodels.Questionario
import pt.isec.marco.quizec.utils.FStorageUtil


@Composable
fun HistoricoQuestionarioRespondidosScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController,
    showComplete: Boolean
) {
    val questionariosIds = viewModel.questionariosRespondidos.value
    var questionarios by remember { mutableStateOf<List<Questionario>>(emptyList()) }

    BackgroundWithImage(
        modifier = Modifier.fillMaxSize()
    ) {
        LaunchedEffect(Unit) {
            questionarios = mutableListOf()

            questionariosIds.forEach { questionarioId ->
                FStorageUtil.getQuestionarioById(questionarioId) { questionario, _ ->
                    if (questionario != null) {

                        questionarios = questionarios + questionario
                    }

                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val pagerState = rememberPagerState(pageCount = {
                questionarios.size
            })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(2.dp)
                ) {
                    Card(
                        viewModel = viewModel,
                        questionario = questionarios[page],
                        showComplete = showComplete
                    )
                }
            }
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Card(
    viewModel: FirebaseViewModel,
    questionario: Questionario,
    showComplete: Boolean
) {
    if (questionario != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(255, 224, 192))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Título do Questionário: ${questionario.descricao}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                questionario.perguntas?.forEach { pergunta ->
                    Text("Pergunta: ${pergunta.titulo}")
                    TipoPerguntaCard(
                        pergunta = pergunta,
                        showComplete = showComplete,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
        Text("Nenhum questionário disponível")
    }
}

