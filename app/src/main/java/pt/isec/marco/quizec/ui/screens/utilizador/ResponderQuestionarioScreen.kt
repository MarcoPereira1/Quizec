package pt.isec.marco.quizec.ui.screens.utilizador

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import pt.isec.marco.quizec.ui.screens.BackgroundWithImage
import pt.isec.marco.quizec.ui.screens.criador.MeteImagem
import pt.isec.marco.quizec.ui.screens.criador.TipoPerguntaCard
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Partilha
import pt.isec.marco.quizec.ui.viewmodels.Questionario
import pt.isec.marco.quizec.ui.viewmodels.Pergunta
import pt.isec.marco.quizec.utils.FStorageUtil
import pt.isec.marco.quizec.utils.FStorageUtil.Companion.addUserToPartilha

@SuppressLint("UnrememberedMutableState")
@Composable
fun ResponderQuestionarioScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController,
    idPartilha: String,
    tempoEspera: Int

) {
    Log.d("ResponderQuestionarioScreen", "idPartilha: $idPartilha")
    var questionario by remember { mutableStateOf<Questionario?>(null) }
    var perguntas by remember { mutableStateOf<List<Pergunta>>(emptyList()) }
    var partilha by remember { mutableStateOf<Partilha?>(null) }
    LaunchedEffect(idPartilha) {
        questionario = null
        FStorageUtil.getQuestionarioByPartilhaId(idPartilha) { questionarioaux, error ->
            if (error != null) {
            } else if (questionarioaux != null) {
                questionario = questionarioaux
                perguntas = questionario!!.perguntas
            }
        }
        FStorageUtil.getPartilhaById(idPartilha) { partilhaaux, error ->
            if (error != null) {
            } else if (partilhaaux != null) {
                partilha = partilhaaux
            }
        }
    }
    val picture =
        remember { mutableStateOf(questionario?.imagem) }
    if (!perguntas.isEmpty()) {
        val respostas = remember { mutableStateListOf<MutableList<String>>() }

        perguntas.forEach() {
            respostas.add(mutableStateListOf())
        }
        val pagerState = rememberPagerState(pageCount = {
            perguntas.size + 2
        })
        BackgroundWithImage(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Log.d("ResponderQuestionarioScreen", "idPartilha: $partilha")
                if (true) {
                    if (
                        false
//                    partilha!!.tempoEspera > 0
                    ) {
                        mostraTempoEspera(partilha!!)
                    } else {
                        Log.d("PagerState", "Current page: ${pagerState.currentPage}")
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            if (page == 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .padding(8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(135, 206, 250)
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth()
                                        ) {
                                            questionario?.let {
                                                Text(
                                                    text = "Questionário",
                                                    fontSize = 32.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    color = Color.Black
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = it.descricao,
                                                    fontSize = 20.sp,
                                                    textAlign = TextAlign.Center,
                                                    color = Color.DarkGray
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(32.dp))
                                            MeteImagem(picture)
                                        }
                                    }
                                }

                            } else if (page == pagerState.pageCount - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .padding(2.dp)
                                ) {
                                    val message by remember { mutableStateOf("") }

                                    Button(
                                        onClick = {
                                            FStorageUtil.addRespostasToPartilha(
                                                partilha!!.id,
                                                respostas,
                                                FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                            )

                                            addUserToPartilha(
                                                partilhaId = idPartilha,
                                                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                                onSuccess = {
                                                    Log.d("partilha","adicionada")
                                                            },
                                                onError = { exception ->
                                                    Log.d("partilha","nao adicionada")
                                                }
                                            )
                                            navController.navigate("menu-utilizador") {
                                                popUpTo("menu-utilizador") {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Finalizar")
                                    }
                                    if (message != "") {
                                        Text(message)
                                    }
                                }
                            } else {
                                val pergunta = perguntas[page - 1]
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .padding(2.dp)
                                ) {
                                    TipoPerguntaCard(
                                        pergunta, true, respostas[page - 1]
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { i ->
                        var isAnswered = false
                        if (i != 0 && i != pagerState.pageCount - 1) {
                            if (respostas[i - 1].isNotEmpty()) {
                                for (j in respostas[i - 1].indices) {
                                    if (respostas[i - 1][j] != "") {
                                        isAnswered = true
                                    }
                                }
                            }

                        }
                        val color = when {
                            isAnswered -> Color.Green
                            !isAnswered && pagerState.currentPage == i -> Color.DarkGray
                            i == 0 || i == pagerState.pageCount - 1 -> Color.White
                            else -> Color.Red
                        }
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
    else{
        BackgroundWithImage(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    questionario?.let {
                        Text(
                            text = it.descricao,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    questionario?.let {
                        Text(
                            text = "Este questionario nao tem perguntas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    MeteImagem(picture)
                }
            }
        }
    }
}
@Composable
fun mostraTempoEspera(
    partilha: Partilha
){
    Column{
        Text(text = "Tempo de espera: ${partilha.tempoEspera}")
        Text("Por favor aguarde")
    }
}
