package pt.isec.marco.quizec.ui.screens.criador

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import pt.isec.marco.quizec.R
import pt.isec.marco.quizec.ui.viewmodels.Pergunta

sealed class ShowAnswer {
    object NotAnswered : ShowAnswer()
    data class BooleanAnswer(val value: Boolean) : ShowAnswer()
    data class IntAnswer(val value: Int?) : ShowAnswer()
    data class ListAnswer(val value: List<Int>) : ShowAnswer()
    data class StringAnswer(val value: String?) : ShowAnswer()
    data class ListStringAnswer(val value: List<String>) : ShowAnswer()
}


@Composable
fun TipoPerguntaCard(
    pergunta: Pergunta,
    showComplete: Boolean,
    resposta: MutableList<String>?=null,
) {
    var answer by remember { mutableStateOf<ShowAnswer?>(null) }

    answer = when (pergunta.tipo) {
        "P01" -> {
            when (pergunta.respostaCerta.getOrNull(0)) {
                "true" -> ShowAnswer.BooleanAnswer(true)
                "false" -> ShowAnswer.BooleanAnswer(false)
                else -> ShowAnswer.NotAnswered
            }
        }
        "P02" -> {
            val respostaIndex = pergunta.respostaCerta.getOrNull(0)?.toIntOrNull()
            if(respostaIndex == null){
                ShowAnswer.NotAnswered
            }else{
                ShowAnswer.IntAnswer(respostaIndex)
            }
        }
        "P03"  -> {
            val respostaIndex = pergunta.respostaCerta.mapNotNull { it.toIntOrNull() }
            if(respostaIndex.isEmpty()){
                ShowAnswer.NotAnswered
            }else{
                ShowAnswer.ListAnswer(respostaIndex)
            }
        }
        "P04" -> {
            if(pergunta.respostaCerta.isEmpty()){
                ShowAnswer.NotAnswered
            }else{
                val respostaIndex = List(pergunta.respostas.size/2) {-1}.toMutableList()
                for(i in 0 until pergunta.respostaCerta.size/2){
                    val index = pergunta.respostaCerta.indexOf(pergunta.respostas[i])
                    val index2 = pergunta.respostas.indexOf(pergunta.respostaCerta[index+pergunta.respostas.size/2])
                    respostaIndex[i] = index2 - pergunta.respostas.size/2 + 1
                }
                ShowAnswer.ListAnswer(respostaIndex)
            }

        }
        "P05" -> {
            if(pergunta.respostaCerta.isEmpty()){
                ShowAnswer.NotAnswered
            }else {
                val respostaIndex = List(pergunta.respostas.size) { -1 }.toMutableList()
                for (i in 0 until pergunta.respostaCerta.size) {
                    val index = pergunta.respostaCerta.indexOf(pergunta.respostas[i])
                    respostaIndex[index] = i + 1
                }
                ShowAnswer.ListAnswer(respostaIndex)
            }
        }
        "P06" -> {
            val frase = pergunta.respostas.getOrNull(0)
            var i = 1
            val result = StringBuilder()

            var j = 0

            if (frase != null) {
                for (char in frase) {
                    if (char == '_' && j < pergunta.respostaCerta.size) {
                        result.append("[$i. \"${pergunta.respostaCerta[j]}\"]")
                        i++
                        j++
                    } else {

                        result.append(char)
                    }
                }
            }
            ShowAnswer.StringAnswer(result.toString())
        }
        "P07" -> {
            val respostaStrings = pergunta.respostas.map { it }
            if(respostaStrings.isEmpty()){
                ShowAnswer.NotAnswered
            }else{
                ShowAnswer.ListStringAnswer(respostaStrings)
            }
        }
        "P08" -> {
            val respostaIndex = pergunta.respostas.getOrNull(0)?.toIntOrNull()
            ShowAnswer.IntAnswer(respostaIndex)
        }

        else -> ShowAnswer.NotAnswered
    }
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
        val picture = remember { mutableStateOf<String?>(pergunta.imagem) }
        if(picture.value != "" && showComplete){
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MeteImagem(picture)
            }
        }
        when(pergunta.tipo){
            "P01" -> PerguntaVF(pergunta, showComplete,answer,resposta)
            "P02" -> PerguntaEM(pergunta, showComplete,answer,false,resposta)
            "P03" -> PerguntaEM(pergunta, showComplete,answer,true,resposta)
            "P04" -> PerguntaCorrespondecia(pergunta,showComplete,answer,resposta)
            "P05" -> PerguntaOrdenacao(pergunta,showComplete,answer,resposta)
            "P06" -> PerguntaEspacosEmBranco(pergunta,showComplete,answer,resposta)
            "P07" -> PerguntaAssociacao(pergunta,showComplete,answer,resposta)
            "P08" -> PerguntaPalavras(pergunta,showComplete,answer,resposta)
            else -> {
                Text("Tipo de pergunta desconhecido")
            }
        }
    }
}

@Composable
fun PerguntaVF(
    pergunta: Pergunta,
    showComplete: Boolean,
    showAnswer: ShowAnswer?,
    resposta: MutableList<String>?=null
) {
    var showAnswerBoolean by remember { mutableStateOf<Boolean?>(null) }
    var isEnable by remember { mutableStateOf(true) }
    if(resposta == null){
        when (pergunta.respostaCerta.getOrNull(0)) {
            "true" -> showAnswerBoolean = true
            "false" -> showAnswerBoolean = false
        }
        isEnable = false
    }else{
        if(resposta.size >= 1){
            showAnswerBoolean = resposta[0].toBoolean()
        }
    }
    Text(stringResource(R.string.P01_name))
    Spacer(modifier = Modifier.height(16.dp))
    Text("Pergunta: ${pergunta.titulo}")
    if (showComplete) {
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isChecked by remember { mutableStateOf(showAnswerBoolean) }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Verdadeiro")
                    Checkbox(
                        checked = isChecked == true,
                        onCheckedChange = {
                            isChecked = if (it) true else null
                            if (it) {
                                resposta?.clear()
                                resposta?.add("true")
                            }else{
                                resposta?.clear()
                            }
                        },
                        enabled = isEnable,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Falso")
                    Checkbox(
                        checked = isChecked == false,
                        onCheckedChange = {
                            isChecked = if (it) false else null
                            if (it) {
                                resposta?.clear()
                                resposta?.add("false")
                            }else{
                                resposta?.clear()
                            }
                        },
                        enabled = isEnable,

                    )
                }
            }
        }
    }
}

@Composable
fun PerguntaEM(
    pergunta: Pergunta,
    showComplete: Boolean,
    showAnswer: ShowAnswer?,
    mults: Boolean,
    respostas: MutableList<String>?=null
) {

    var  selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var selectedAnswerList by remember { mutableStateOf<List<Int>?>(null) }
    var isEnable by remember { mutableStateOf(true) }

    if(respostas == null){
        selectedAnswerList = pergunta.respostaCerta.mapNotNull { it.toIntOrNull() }
        selectedAnswerIndex = pergunta.respostaCerta.getOrNull(0)?.toIntOrNull()
        isEnable = false
    }else {
        selectedAnswerList = respostas.mapNotNull { it.toIntOrNull() }
        selectedAnswerIndex = respostas.getOrNull(0)?.toIntOrNull()
        isEnable = true
    }

    if (mults) {
        Text(stringResource(R.string.P02_name))
    } else {
        Text(stringResource(R.string.P03_name))
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text("Pergunta: ${pergunta.titulo}")

    if (showComplete) {
        Spacer(modifier = Modifier.height(16.dp))

        var selected by remember { mutableStateOf<Int?>(selectedAnswerIndex) }
        var selectedMultiple by remember { mutableStateOf<List<Int>>(emptyList()) }

        pergunta.respostas.forEachIndexed { index, resposta ->
            Row(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = resposta,
                    fontSize = 16.sp
                )
                Checkbox(
                    checked = if (mults) {
                        selectedMultiple.contains(index) ||  selectedAnswerList?.contains(index) == true
                    } else {
                        selected == index
                    },
                    onCheckedChange = {
                            if (mults) {
                                selectedMultiple = if (it) {
                                    selectedMultiple + index
                                } else {
                                    selectedMultiple.filter { it != index }
                                }
                                respostas?.clear()
                                respostas?.addAll(selectedMultiple.map { it.toString() })
                            } else {
                                selected = if (it) index else null
                                if (it) {
                                    respostas?.clear()
                                    respostas?.add(index.toString())
                                }else{
                                    respostas?.clear()
                                }
                            }
                    },
                    enabled = isEnable
                )
            }
        }
    }
}

@Composable
fun PerguntaCorrespondecia(
    pergunta: Pergunta,
    showComplete: Boolean = false,
    showAnswer: ShowAnswer?,
    respostas: MutableList<String>?=null

){
    if (respostas != null) {
        while (respostas.size < pergunta.respostas.size / 2) {
            respostas.add("")
        }
    }
    val selectedAnswerList = when (showAnswer) {
        is ShowAnswer.ListAnswer -> showAnswer.value
        else ->  null
    }
    if(respostas == null){
        Text(stringResource(R.string.P04_name))
        Spacer(modifier = Modifier.height(16.dp))
    }

    Text("Pergunta: ${pergunta.titulo}")
    Spacer(modifier = Modifier.height(16.dp))
    var heigth by remember { mutableStateOf(58.dp) }
    if (showComplete) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                Text("Resposta:")
                for (i in 0 until pergunta.respostas.size / 2) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(heigth)
                                .padding(all = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "${('A' + i)}.",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = Color.Blue
                                )
                                Text(
                                    text = pergunta.respostas[i],
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(heigth)
                                .padding(all = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "${i + 1}. ",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = Color.Blue
                                )
                                Text(
                                    text = pergunta.respostas[i + pergunta.respostas.size / 2],
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Resposta:")
                for (i in 0 until pergunta.respostas.size / 2) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(heigth)
                                .padding(all = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + i).toString(),
                                fontSize = 16.sp,
                                color = Color.Blue
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))


                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(heigth)
                                .padding(all = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if(selectedAnswerList != null){
                                Text(
                                    text = "${selectedAnswerList[i]}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = Color.Blue
                                )
                            }else{
                                var upperCaseText by remember {
                                    mutableStateOf( "")
                                }
                                TextField(
                                    value = upperCaseText,
                                    onValueChange = { newText ->
                                        if (newText.isEmpty() || (newText.length == 1 && newText[0].isDigit())) {
                                            upperCaseText = newText
                                            respostas?.set(i, newText)
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(all = 1.dp)
//                                        .width(60.dp)
//                                        .height(60.dp)
//                                        .background(Color.LightGray),
                                            ,
                                    textStyle = TextStyle(
                                        textAlign = TextAlign.Center,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerguntaOrdenacao(
    pergunta: Pergunta,
    showComplete: Boolean = false,
    showAnswer: ShowAnswer?,
    respostas: MutableList<String>?=null

    ) {
    var heigth by remember { mutableStateOf(58.dp) }

    if (respostas != null) {
        while (respostas.size < pergunta.respostas.size) {
            respostas.add("")
        }
    }
    val selectedAnswerList = when (showAnswer) {
        is ShowAnswer.ListAnswer -> showAnswer.value
        else -> null
    }
    if(respostas == null){
        Text(stringResource(R.string.P05_name))
        Spacer(modifier = Modifier.height(16.dp))
    }

    Text("Pergunta: ${pergunta.titulo}")
    Spacer(modifier = Modifier.height(16.dp))
    if (showComplete) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                Text("Pergunta:")
                for (i in 0 until pergunta.respostas.size) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(heigth)
                                .padding(all = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically

                            ) {
                                Text(
                                    text = "${(i + 1)}.",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = Color.Blue
                                )
                                Text(
                                    text = pergunta.respostas[i],
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text("Ordem:")
                for (i in 0 until pergunta.respostas.size) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(heigth)
                                .padding(all = 3.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray)
                                .align(Alignment.CenterVertically),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedAnswerList != null) {
                                Text(
                                    text = "${selectedAnswerList[i]}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 8.dp),
                                    color = Color.Blue
                                )
                            } else {
                                var upperCaseText by remember {
                                    mutableStateOf( "")
                                }

                                TextField(
                                    value = upperCaseText,
                                    onValueChange = { newText ->
                                        if (newText.isEmpty() || (newText.length == 1 && newText[0].isDigit())) {
                                            upperCaseText = newText
                                            respostas?.set(i, newText)
                                        }
                                    },
                                    label = { Text("R:") },
                                    modifier = Modifier
                                        .padding(all = 1.dp)

                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerguntaEspacosEmBranco(
    pergunta: Pergunta,
    showComplete: Boolean = false,
    showAnswer: ShowAnswer?,
    resposta: MutableList<String>?=null
) {
    val selectedAnswerString = when (showAnswer) {
        is ShowAnswer.StringAnswer -> showAnswer.value
        else ->  ""
    }
    if(resposta == null){
        Text(stringResource(R.string.P06_name))
        Spacer(modifier = Modifier.height(16.dp))
    }
    Text("Pergunta: ${pergunta.titulo}")
    Spacer(modifier = Modifier.height(16.dp))

    if (showComplete) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){
            if (selectedAnswerString != null) {
                Text(selectedAnswerString)
            }
            T06_Opcoes(
                pergunta.respostaCerta.size,
                pergunta.respostaCerta,
                isNomeInvalidList = List(pergunta.respostaCerta.size) { false },
                onNomeChange = { _, _ -> }
            )
        }
    }
}

@Composable
fun PerguntaAssociacao(
    pergunta: Pergunta,
    showComplete: Boolean = false,
    showAnswer: ShowAnswer?,
    respostas: MutableList<String>?=null
) {
    val answerListStrings = when (showAnswer) {
        is ShowAnswer.ListStringAnswer -> showAnswer.value
        else -> null
    }

    val selectedAnswerIndex = answerListStrings?.getOrNull(1)?.toIntOrNull()

    val stringAnswer = answerListStrings?.getOrNull(0)
    if(respostas == null){
        Text(stringResource(R.string.P07_name))
        Spacer(modifier = Modifier.height(16.dp))
    }
    Text("Pergunta: ${pergunta.titulo}")
    val picture = remember { mutableStateOf<String?>(stringAnswer) }
    if( picture.value != ""){
        MeteImagem(picture)
        Spacer(modifier = Modifier.height(16.dp))
    }

    var selected by remember { mutableStateOf(selectedAnswerIndex) }

    Log.d("PerguntaAssociacao", "Resposta correta: $selectedAnswerIndex")
    pergunta.respostaCerta.forEachIndexed { index, resposta ->
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = resposta,
                fontSize = 16.sp
            )
            Checkbox(
                checked =
                selected == index ,
                onCheckedChange = {
                    selected = if (it) index else null
                    if(it){
                        respostas?.clear()
                        respostas?.add(index.toString())
                    }else{
                        respostas?.clear()
                    }
                },
                enabled = selectedAnswerIndex == null,
            )
        }
    }
}

@Composable
fun PerguntaPalavras(
    pergunta: Pergunta,
    showComplete: Boolean = false,
    showAnswer: ShowAnswer?,
    respostas: MutableList<String>?=null
){

    if (respostas != null) {
        while (respostas.size < pergunta.respostaCerta.size) {
            respostas.add("")
        }

    }else{

    }
    val selectedAnswerString = when (showAnswer) {
        is ShowAnswer.IntAnswer -> showAnswer.value
        else ->  -1
    }
    if(respostas == null){
        Text(stringResource(R.string.P08_name))
        Spacer(modifier = Modifier.height(16.dp))
    }

    Text("Pergunta: ${pergunta.titulo}")

    if (showComplete) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){

            if (selectedAnswerString != null) {
                if (respostas != null) {
                    T06_Opcoes(
                        selectedAnswerString,
                        respostas,
                        isNomeInvalidList = List(pergunta.respostaCerta.size) { false },
                        onNomeChange = {index, novoValor ->
                            respostas[index] = novoValor}
                    )
                }
            }
        }
    }

}


@Composable
    fun TipoPerguntaScreen(
        viewModel: FirebaseViewModel,
        navController: NavHostController,
        onPerguntaSelected: (Int) -> Unit
    ) {
        val tiposPerguntas = listOf(
            Pergunta(
                id = "Q1",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "A água ferve a 100°C?",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf(""),
                respostaCerta = listOf("true"),
                tipo = "P01"
            ),
            Pergunta(
                id = "Q2",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Qual é a capital da França?",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("Londres", "Berlim", "Paris", "Madrid"),
                respostaCerta = listOf("2"),
                tipo = "P02"
            ),
            Pergunta(
                id = "Q3",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Selecione os continentes",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("Ásia", "Europa", "Oceania", "Antártica", "Atlântico"),
                respostaCerta = listOf("2", "3"),
                tipo = "P03"
            ),
            Pergunta(
                id = "Q3",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Selecione os continentes",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("Ásia", "Europa", "Oceania", "Antártica", "Atlântico","MAreica"),
                respostaCerta = listOf("Ásia", "Europa", "Oceania", "Antártica", "Atlântico","MAreica"),
                tipo = "P04"
            ),
            Pergunta(
                id = "Q3",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Selecione os continentes",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("Ásia", "Europa", "Oceania", "Antártica"),
                respostaCerta = listOf(  "Oceania","Europa", "Antártica","Ásia"),
                tipo = "P05"
            ),
            Pergunta(
                id = "Q3",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Selecione os continentes",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("Estou na _ e vou para _ "),
                respostaCerta = listOf("Ásia", "Europa"),
                tipo = "P06"
            ),
            Pergunta(
                id = "Q3",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Selecione os continentes",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg","2"),
                respostaCerta = listOf("Ásia", "Europa", "Oceania", "Antártica"),
                tipo = "P07"
            ),
            Pergunta(
                id = "Q3",
                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                titulo = "Selecione os continentes",
                imagem = "http://amov.servehttp.com:11111/file/uploaded-1735618972183-file.jpg",
                respostas = listOf("2"),
                respostaCerta = listOf("Ásia", "Europa", "Oceania", "Antártica"),
                tipo = "P08"
            )
        )

        var selectedPage by remember { mutableStateOf(-1) }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val pagerState = rememberPagerState(pageCount = {
                tiposPerguntas.size
            })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val pergunta = tiposPerguntas[page]
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(2.dp)
                ) {
                    TipoPerguntaCard(
                        pergunta, true
                    )
                }
            }
            Button(
                onClick = {
                    selectedPage = pagerState.currentPage
                    onPerguntaSelected(selectedPage)
                    navController.navigate("criar-pergunta") {
                        popUpTo("criar-pergunta") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(64.dp)
            ) {
                Text("Escolher")
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

// TODO FINAL
// P06, equacao