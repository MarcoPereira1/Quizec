package pt.isec.marco.quizec.ui.screens.utilizador

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import pt.isec.marco.quizec.ui.screens.criador.MeteImagem
import pt.isec.marco.quizec.ui.screens.criador.TipoPerguntaCard
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Partilha
import pt.isec.marco.quizec.ui.viewmodels.Questionario
import pt.isec.marco.quizec.ui.viewmodels.Pergunta

@SuppressLint("UnrememberedMutableState")
@Composable
fun ResponderQuestionarioScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController,

) {
    val partilha = Partilha(
        "nome", "1223", 0, 1000
    )

    val perguntasList = listOf(
        Pergunta(
            id = "Q1",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "A água ferve a 100°C?",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf(""),
            respostaCerta = listOf(),
            tipo = "P01"
        ),
        Pergunta(
            id = "Q2",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Qual é a capital da França?",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf("Londres", "Berlim", "Paris", "Madrid"),
            respostaCerta = listOf(),
            tipo = "P02"
        ),
        Pergunta(
            id = "Q3",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Selecione os continentes",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf("Ásia", "Europa", "Oceania", "Antártica", "Atlântico"),
            respostaCerta = emptyList(),
            tipo = "P03"
        ),
        Pergunta(
            id = "Q4",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Selecione os continentes",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf("Ásia", "Europa", "Oceania", "Antártica", "Atlântico", "MAreica"),
            respostaCerta = emptyList(),
            tipo = "P04"
        ),
        Pergunta(
            id = "Q5",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Selecione os continentes",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf("Ásia", "Europa", "Oceania", "Antártica"),
            respostaCerta = listOf(),
            tipo = "P05"
        ),
        Pergunta(
            id = "Q6",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Selecione os continentes",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf("Estou na _ e vou para _ "),
            respostaCerta = listOf(),
            tipo = "P06"
        ),
        Pergunta(
            id = "Q7",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Selecione os continentes",
            imagem = "imagem_pergunta3",
            respostas = listOf("http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg"),
            respostaCerta = listOf("Ásia", "Europa", "Oceania", "Antártica"),
            tipo = "P07"
        ),
        Pergunta(
            id = "Q8",
            idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            titulo = "Selecione os continentes",
            imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
            respostas = listOf("2"),
            respostaCerta = listOf("Ásia", "Europa", "Oceania", "Antártica"),
            tipo = "P08"
        )
    )

    val questionario = Questionario(
        id = "123",
        idUtilizador = "idUtilz",
        descricao = "descricao",
        perguntas = perguntasList,
        imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
        questRespondidosIds = emptyList()
    )

    val respostas = remember { mutableStateListOf<MutableList<String>>() }
    for (i in perguntasList.indices) {
        respostas.add(mutableStateListOf())
    }

    val pagerState = rememberPagerState(pageCount = {
        perguntasList.size + 2
    })

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        if (partilha != null) {
            if (partilha.tempoEspera > 0) {
                mostraTempoEspera(partilha)
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    if(page == 0){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(2.dp)
                        ) {
                            val picture = remember { mutableStateOf<String?>(questionario.imagem) }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .padding(bottom = 16.dp)
                                    .verticalScroll(rememberScrollState()),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(255,224,192)
                                )
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Text(questionario.id)
                                    MeteImagem(picture)
                                    Text(questionario.descricao)
                                }
                            }
                        }

                    }else if(page == perguntasList.size + 2){
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(2.dp)
                        ) {
                            Button(
                                onClick = {
                                    for(i in respostas.indices){
                                        if(respostas[i].isEmpty()){
                                            // guarda o nr da pergunta
                                        }
                                    }
                                    // mostra um texto de confirmcao a dizer que se realmente quer
                                    // acabar o teste e se sim acaba o teste
                                }
                            ){
                                Text("Finalizar")
                            }
                        }
                    } else{
                        val pergunta = perguntasList[page-1]
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(2.dp)
                        ) {
                            TipoPerguntaCard(
                                pergunta, true, respostas[page-1]
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
                if(i != 0 && i != pagerState.pageCount-1){
                    if(respostas[i-1].isNotEmpty()){
                        isAnswered = true
                    }
                }

                val color = when {
                    isAnswered -> Color.Green
                    !isAnswered && pagerState.currentPage == i -> Color.DarkGray
                    i == 0 || i == pagerState.pageCount-1 -> Color.DarkGray
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

@Composable
fun mostraTempoEspera(
    partilha: Partilha
){
    Column{
        Text(text = "Tempo de espera: ${partilha.tempoEspera}")
        Text("Por favor aguarde")
    }
}
