package pt.isec.marco.quizec.ui.screens.criador

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import pt.isec.marco.quizec.R
import pt.isec.marco.quizec.ui.screens.BackgroundWithImage
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Pergunta
import pt.isec.marco.quizec.utils.AMovServer
import pt.isec.marco.quizec.utils.FileUtils
import java.io.File


@Composable
fun T01_PerguntaVF(
    isChecked: Boolean?,
    isCheckedInvalid: Boolean,
    onCheckedChange: (Boolean?) -> Unit
){
    Text("Solução:")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Verdadeiro")
            Checkbox(
                checked = isChecked == true,
                onCheckedChange = {
                    onCheckedChange(if (it) true else null)
                }
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Falso")
            Checkbox(
                checked = isChecked == false,
                onCheckedChange = {
                    onCheckedChange(if (it) false else null)
                }
            )
        }
    }
    if (isCheckedInvalid) {
        Text(
            text = "Por favor, selecione uma opção.",
            color = Color.Red,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun T02_PerguntaEM(
    selected: Int,
    isChecked: Int?,
    selectedInvalid: Boolean,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onCheckedChange: (Int?) -> Unit,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit
) {
    val butoesLista = (2..6).toList()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        BotoesLista(butoesLista, selected, onCountChange)
        Text("Solução:")
            T02_Opcoes(
                selected,
                isChecked,
                selectedInvalid,
                nomes,
                isNomeInvalidList,
                onCheckedChange,
                onNomeChange
            )
    }
}

@Composable
fun T03_PerguntaEM(
    selected: Int,
    isChecked: List<Int>,
    selectedInvalid: Boolean,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onCheckedChange: (List<Int>) -> Unit,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit
) {
    val butoesLista = (2..6).toList()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        BotoesLista(butoesLista, selected, onCountChange)
        Text("Solução:")
        T03_Opcoes(
            selected,
            isChecked,
            selectedInvalid,
            nomes,
            isNomeInvalidList,
            onCheckedChange,
            onNomeChange
        )
    }
}

@Composable
fun T04_PerguntaCorrespondecia(
    selected: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit
) {
    val butoesLista = (2..6).toList()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        BotoesLista(butoesLista, selected, onCountChange)
        Text("Solução:")
        T04_Opcoes(
            selected,
            nomes,
            isNomeInvalidList,
            onNomeChange
        )
    }
}

@Composable
fun T05_PerguntaOrdenacao(
    selected: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit
){
    val butoesLista = (2..6).toList()
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        BotoesLista(butoesLista, selected, onCountChange)
        Text("Solução:")
        T05_Opcoes(
            selected,
            nomes,
            isNomeInvalidList,
            onNomeChange
        )
    }
}

@Composable
fun T06_PerguntaEspacos(
    selected: Int,
    nomes: List<String>,
    frase: String,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit,
    onFraseChange: (String) -> Unit,
    isFraseInvalid: Boolean
){
    Column(
        modifier = Modifier.fillMaxWidth()

    ){
        Text("Solução:")
        OutlinedTextField(
            value = frase,
            onValueChange = { newText -> onFraseChange(newText) },
            label = { Text("Digite uma frase: ") },
            isError = isFraseInvalid,
            modifier = Modifier
                .padding(end = 8.dp)
        )
        val (newString, indices) = replaceMarkWithIndexedSpaces(frase)
        onCountChange(indices.size)
        if(nomes.isNotEmpty()){
            T06_Opcoes(
                indices.size,
                nomes,
                isNomeInvalidList,
                onNomeChange
            )
        }
    }

}
@Composable
fun T07_PerguntasAssociacao(
    selected: Int,
    nomes: List<String>,
    picture: MutableState<String?>,
    isNomeInvalidList: List<Boolean>,
    isPictureInvalid: Boolean,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit
){
    val butoesLista = (2..6).toList()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        BotoesLista(butoesLista, selected, onCountChange)
        Text("Solução:")
        T07_Opcoes(
            selected,
            picture,
            nomes,
            isPictureInvalid,
            isNomeInvalidList,
            onNomeChange,
        )
    }
}
@Composable
fun T08_PerguntaPalavras(
    selected: Int,
    nrRespostas: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit,
    onCountChange: (Int) -> Unit,
    onNrRespostasChange: (Int) -> Unit,
    adicinaPalavra: () -> Unit,
    removePalavra: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()

    ) {
        Text("Número de palavras permitidas: $nrRespostas")

        Slider(
            value = nrRespostas.toFloat(),
            onValueChange = { newValue ->
                onNrRespostasChange(newValue.toInt().coerceAtMost(nomes.size))
            },
            valueRange = 1f..nomes.size.toFloat(),
            steps = nomes.size - 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        if (nomes.isNotEmpty()) {
            Text("Respostas:")
            T06_Opcoes(
                nomes.size,
                nomes,
                isNomeInvalidList,
                onNomeChange
            )
        }
        Button(
            onClick = {
                adicinaPalavra()
                onCountChange(selected + 1)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Adicionar resposta")
        }
        Button(
            onClick = {
                removePalavra()
                onCountChange(selected - 1)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Remove resposta")
        }

    }
}

fun replaceMarkWithIndexedSpaces(inputString: String, mark: Char = '_'): Pair<String, List<Int>> {
    var counter = 1
    val indices = mutableListOf<Int>()

    val resultString = inputString.map { char ->
        if (char == mark) {
            indices.add(counter)
            "( $counter )".also { counter++ }
        } else {
            char.toString()
        }
    }.joinToString("")

    return Pair(resultString, indices)
}

@Composable
fun BotoesLista(
    butoesLista: List<Int> = (2..6).toList(),
    selected: Int? = null,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        butoesLista.forEach { num ->
            Button(
                onClick = {
                    onCountChange(num)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == num) Color.DarkGray else Color.LightGray
                )
            ) {
                Text(num.toString())
            }
        }
    }
}


@Composable
fun OutlinedTextField(
    text : String,
    i: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit
){
    OutlinedTextField(
        value = nomes[i],
        isError = isNomeInvalidList[i],
        label = { Text(text) },
        onValueChange = { newText ->
            onNomeChange(i, newText)
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun T02_Opcoes(
    countButton: Int,
    selected: Int?,
    isCheckedInvalid: Boolean,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onCheckedChange: (Int?) -> Unit,
    onNomeChange: (Int, String) -> Unit
) {
    for (i in 0 until countButton) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selected == i,
                onCheckedChange = {
                    onCheckedChange(if (it) i else null)
                }
            )
            OutlinedTextField("Resposta :", i, nomes, isNomeInvalidList, onNomeChange)
        }
        if (isCheckedInvalid && i == countButton - 1) {
            Text(
                text = "Por favor, selecione uma opção.",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}


@Composable
fun T03_Opcoes(
    countButton: Int,
    selected: List<Int>,
    isCheckedInvalid: Boolean,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onCheckedChange: (List<Int>) -> Unit,
    onNomeChange: (Int, String) -> Unit
) {
    for (i in 0 until countButton) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = selected.contains(i),
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        onCheckedChange(selected + i)
                    } else {
                        onCheckedChange(selected.filter { it != i })
                    }
                }
            )
            OutlinedTextField("Resposta: ", i, nomes, isNomeInvalidList, onNomeChange)
        }

        if (isCheckedInvalid && i == countButton - 1) {
            Text(
                text = "Por favor, selecione uma opção.",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun T04_Opcoes(
    countButton: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit
) {
    for (i in 0 until countButton) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nomes.getOrElse(i) { "" },
                onValueChange = { newText -> onNomeChange(i, newText) },
                label = { Text("${i + 1}. ") },
                isError = isNomeInvalidList.getOrElse(i) { false },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            OutlinedTextField(
                value = nomes.getOrElse(i + countButton) { "" },
                onValueChange = { newText -> onNomeChange(i + countButton, newText) },
                label = { Text("${('A' + i)}. ") },
                isError = isNomeInvalidList.getOrElse(i + countButton) { false },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun T05_Opcoes(
    countButton: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit
) {
    for (i in 0 until countButton) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nomes.getOrElse(i) { "" },
                onValueChange = { newText -> onNomeChange(i, newText) },
                label = { Text("${i + 1}. ") },
                isError = isNomeInvalidList.getOrElse(i) { false },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun T06_Opcoes(
    countButton: Int,
    nomes: List<String>,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit
) {
    Column {
        for (i in 0 until countButton step 3) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (j in 0 until 3) {
                    if (i + j < countButton) {
                        OutlinedTextField(
                            value = nomes.getOrElse(i + j) { "" },
                            onValueChange = { newText -> onNomeChange(i + j, newText) },
                            label = { Text("${i + j + 1}. ") },
                            isError = isNomeInvalidList.getOrElse(i + j) { false },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = if (j < 2) 8.dp else 0.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun T07_Opcoes(
    countButton: Int,
    picture: MutableState<String?>,
    nomes: List<String>,
    invalidPicture: Boolean,
    isNomeInvalidList: List<Boolean>,
    onNomeChange: (Int, String) -> Unit
) {
    val context = LocalContext.current
    val imagePath: String by lazy { FileUtils.getTempFilename(context) }

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color(108, 147, 201, 255))
            .border(
                width = 2.dp,
                color = if (invalidPicture) Color.Red else Color.Black,
                shape = RectangleShape
                )
            .padding(8.dp)
            .clip(
                RoundedCornerShape(8.dp)
            )
    ) {
        AdicionaImagens(picture, context, imagePath)

        if (invalidPicture) {
            Text(
                text = "Adiciona uma imagem",
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
    for (i in 0 until countButton) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = nomes.getOrElse(i ) { "" },
                onValueChange = { newText -> onNomeChange(i , newText) },
                label = { Text("${(i + 1)}. ") },
                isError = isNomeInvalidList.getOrElse(i ) { false },
            )
        }
    }
}


@Composable
fun CriarPerguntaScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController,
    tipoPerguntaSelecionada: Int
) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var pergunta by remember { mutableStateOf<Pergunta?>(null) }
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }
    val picture = remember { mutableStateOf<String?>(null) }
    val imagePath: String by lazy { FileUtils.getTempFilename(context) }

    var nome by remember { mutableStateOf("") }
    var isNomeInvalid by remember { mutableStateOf(false) }
    // P01 -------------
    var isChecked01 by remember { mutableStateOf<Boolean?>(null) }
    var isCheckedInvalid01 by remember { mutableStateOf(false) }
    // P02---------------
    var isChecked02 by remember { mutableStateOf<Int?>(null) }
    var isCheckedInvalid02 by remember { mutableStateOf(false) }
    var countButton by remember { mutableIntStateOf(2) }
    val nomes = remember(countButton) { mutableStateListOf(*Array(countButton) { "" }) }
    val isNomeInvalidList =
        remember(countButton) { mutableStateListOf(*Array(countButton) { false }) }
    // P03---------------
    var isChecked03 by remember { mutableStateOf<List<Int>>(emptyList()) }
    // P04---------------
    val nomes04 = remember(countButton) { mutableStateListOf(*Array(countButton * 2) { "" }) }
    val isNomeInvalidList04 =
        remember(countButton) { mutableStateListOf(*Array(countButton * 2) { false }) }
    val errorMessage by viewModel.error
    var isEntradaValida by remember { mutableStateOf(false) }
    // P06 ----------
    var frase by remember { mutableStateOf("") }
    var isFraseInvalid by remember { mutableStateOf(false) }
    // P07 ------------
    var pictureInvalid by remember { mutableStateOf(false) }
    val picture07 = remember { mutableStateOf<String?>(null) }
    var imageUrl07 by remember { mutableStateOf<String?>(null) }
    // P08 ---------
    var palavras08 = remember { mutableStateListOf("") }
    var isPalavras08Invalid = remember { mutableStateListOf(false) }
    var nrRespostas08 by remember { mutableIntStateOf(1) }

    fun adicinaPalavra() {
        palavras08.add("")
        isPalavras08Invalid.add(false)
    }

    fun removePalavra() {
        if (palavras08.isNotEmpty()) {
            val lastIndex = palavras08.size - 1
            palavras08.removeAt(lastIndex)
            isPalavras08Invalid.removeAt(lastIndex)
        }
    }

    fun validarP01(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }
        if (isChecked01 == null) {
            isCheckedInvalid01 = true
            isValid = false
        } else {
            isCheckedInvalid01 = false
        }
        return isValid
    }

    fun validarP02(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }

        if (isChecked02 == null) {
            isCheckedInvalid02 = true
            isValid = false
        } else {
            isCheckedInvalid02 = false
        }
        for (i in 0 until countButton) {
            if (nomes[i].isEmpty()) {
                isNomeInvalidList[i] = true
                isValid = false
            } else {
                isNomeInvalidList[i] = false
            }
        }
        return isValid
    }

    fun validarP03(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }
        if (isChecked03.isEmpty()) {
            isCheckedInvalid02 = true
            isValid = false
        } else {
            isCheckedInvalid02 = false
        }
        for (i in 0 until countButton) {
            if (nomes[i].isEmpty()) {
                isNomeInvalidList[i] = true
                isValid = false
            } else {
                isNomeInvalidList[i] = false
            }
        }
        return isValid
    }

    fun validarP04(): Boolean {

        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }
        for (i in 0 until countButton * 2) {
            if (nomes04[i].isEmpty()) {
                isNomeInvalidList04[i] = true
            } else {
                isNomeInvalidList04[i] = false
            }
        }
        return isValid
    }

    fun validarP05(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }
        for (i in 0 until countButton) {
            if (nomes[i].isEmpty()) {
                isNomeInvalidList[i] = true
            } else {
                isNomeInvalidList[i] = false
            }
        }
        return isValid
    }

    fun validarP06(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }
        for (i in 0 until nomes.size) {
            if (nomes[i].isEmpty()) {
                isNomeInvalidList[i] = true
                isValid = false
            } else {
                isNomeInvalidList[i] = false
            }
        }
        if (frase.isEmpty()) {
            isFraseInvalid = true
            isValid = false
        } else {
            isFraseInvalid = false
        }
        return isValid
    }

    fun validarP07(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        }
        for (i in 0 until nomes.size) {
            if (nomes[i].isEmpty()) {
                isNomeInvalidList[i] = true
                isValid = false
            } else {
                isNomeInvalidList[i] = false
            }
        }
        if (picture07.value == null) {
            pictureInvalid = true
            isValid = false
        } else {
            pictureInvalid = false
        }
        return isValid
    }

    fun validarP08(): Boolean {
        var isValid = true
        if (nome.isEmpty()) {
            isNomeInvalid = true
            isValid = false
        } else {
            isNomeInvalid = false
        }
        for (i in 0 until palavras08.size) {
            if (palavras08[i] == "") {
                isPalavras08Invalid[i] = true
                isValid = false
            } else {
                isPalavras08Invalid[i] = false
            }
        }
        return isValid
    }

    BackgroundWithImage(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AdicionaImagens(picture, context, imagePath)
            OutlinedTextField(
                value = nome,
                isError = isNomeInvalid,
                label = { Text("Pergunta:") },
                onValueChange = { newText ->
                    nome = newText
                    isNomeInvalid = false
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (isNomeInvalid) {
                Text(
                    text = "Este campo é obrigatório.",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            when (tipoPerguntaSelecionada) {
                0 -> {
                    T01_PerguntaVF(
                        isChecked = isChecked01,
                        isCheckedInvalid = isCheckedInvalid01,
                        onCheckedChange = { novoValor -> isChecked01 = novoValor }
                    )
                }

                1 -> {
                    T02_PerguntaEM(
                        selected = countButton,
                        isChecked = isChecked02,
                        selectedInvalid = isCheckedInvalid02,
                        onCheckedChange = { novoValor -> isChecked02 = novoValor },
                        nomes = nomes,
                        isNomeInvalidList = isNomeInvalidList,
                        onNomeChange = { index, novoValor ->
                            nomes[index] = novoValor
                            isNomeInvalidList[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor }
                    )
                }

                2 -> {
                    T03_PerguntaEM(
                        selected = countButton,
                        isChecked = isChecked03,
                        selectedInvalid = isCheckedInvalid02,
                        onCheckedChange = { novoValor -> isChecked03 = novoValor },
                        nomes = nomes,
                        isNomeInvalidList = isNomeInvalidList,
                        onNomeChange = { index, novoValor ->
                            nomes[index] = novoValor
                            isNomeInvalidList[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor })
                }

                3 -> {
                    T04_PerguntaCorrespondecia(
                        selected = countButton,
                        nomes = nomes04,
                        isNomeInvalidList = isNomeInvalidList04,
                        onNomeChange = { index, novoValor ->
                            nomes04[index] = novoValor
                            isNomeInvalidList04[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor }
                    )
                }

                4 -> {
                    T05_PerguntaOrdenacao(
                        selected = countButton,
                        nomes = nomes,
                        isNomeInvalidList = isNomeInvalidList,
                        onNomeChange = { index, novoValor ->
                            nomes[index] = novoValor
                            isNomeInvalidList[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor }
                    )
                }

                5 -> {
                    T06_PerguntaEspacos(
                        selected = countButton,
                        nomes = nomes,
                        frase = frase,
                        isNomeInvalidList = isNomeInvalidList,
                        isFraseInvalid = isFraseInvalid,
                        onNomeChange = { index, novoValor ->
                            nomes[index] = novoValor
                            isNomeInvalidList[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor },
                        onFraseChange = {
                            frase = it
                            isFraseInvalid = false
                        }
                    )
                }

                6 -> {
                    T07_PerguntasAssociacao(
                        selected = countButton,
                        picture = picture07,
                        nomes = nomes,
                        isPictureInvalid = pictureInvalid,
                        isNomeInvalidList = isNomeInvalidList,
                        onNomeChange = { index, novoValor ->
                            nomes[index] = novoValor
                            isNomeInvalidList[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor }
                    )
                }

                7 -> {
                    T08_PerguntaPalavras(
                        selected = countButton,
                        nrRespostas = nrRespostas08,
                        nomes = palavras08,
                        isNomeInvalidList = isPalavras08Invalid,
                        onNomeChange = { index, novoValor ->
                            palavras08[index] = novoValor
                            isNomeInvalidList[index] = false
                        },
                        onCountChange = { novoValor -> countButton = novoValor },
                        onNrRespostasChange = { novoValor -> nrRespostas08 = novoValor },
                        adicinaPalavra = {
                            adicinaPalavra()
                        },
                        removePalavra = {
                            removePalavra()
                        }
                    )
                }

                else -> {
                    Text("Tipo de pergunta desconhecido")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = {
                    when (tipoPerguntaSelecionada) {
                        0 -> {
                            isEntradaValida = validarP01()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = listOf(""),
                                respostaCerta = listOf(isChecked01.toString()),
                                tipo = "P01"
                            )
                        }

                        1 -> {
                            isEntradaValida = validarP02()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",

                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = nomes,
                                respostaCerta = listOf(isChecked02.toString()),
                                tipo = "P02"
                            )
                        }

                        2 -> {
                            val respostasCertas = List(isChecked03.size) { "" }.toMutableList()

                            if (isChecked03.isNotEmpty()) {
                                isChecked03.forEachIndexed { index, item ->
                                    respostasCertas[index] = isChecked03[index].toString()
                                }
                            }
                            isEntradaValida = validarP03()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = nomes,
                                respostaCerta = respostasCertas,
                                tipo = "P03"
                            )
                        }

                        3 -> {
                            val primeiraMetade = nomes04.take(nomes04.size / 2).shuffled()
                            val segundaMetade = nomes04.drop(nomes04.size / 2).shuffled()
                            val nomes04shuffled = primeiraMetade + segundaMetade
                            isEntradaValida = validarP04()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = nomes04shuffled,
                                respostaCerta = nomes04,
                                tipo = "P04"
                            )
                        }

                        4 -> {
                            isEntradaValida = validarP05()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = nomes.shuffled(),
                                respostaCerta = nomes,
                                tipo = "P05"
                            )

                        }

                        5 -> {
                            isEntradaValida = validarP06()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = listOf(frase),
                                respostaCerta = nomes,
                                tipo = "P06"
                            )
                        }

                        6 -> {
                            isEntradaValida = validarP07()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = listOf(picture07.value ?: "",),
                                respostaCerta = nomes,
                                tipo = "P07"
                            )
                        }

                        7 -> {
                            isEntradaValida = validarP08()
                            pergunta = Pergunta(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                titulo = nome,
                                imagem = picture.value ?: "",
                                respostas = listOf(nrRespostas08.toString()),
                                respostaCerta = palavras08,
                                tipo = "P08"
                            )
                        }
                    }
                    if (isEntradaValida) {
                        if (picture.value != null) {
                            val fileUri: Uri = Uri.fromFile(picture.value?.let { File(it) })
                            AMovServer.asyncUploadImage(
                                inputStream = context.contentResolver.openInputStream(fileUri)!!,
                                extension = "jpg",
                                onResult = { result ->
                                    Log.d("AMovServer", "Result: $result")
                                    if (result != null) {
                                        imageUrl = result
                                        error = null
                                        pergunta?.imagem = imageUrl ?: ""
                                    } else {
                                        error = context.getString(R.string.error_uploading_image)
                                        imageUrl = null
                                    }
                                    pergunta?.let {
                                        viewModel.addPerguntaToFirestore(
                                            it
                                        )
                                    }
                                }
                            )
                        } else {
                            if (picture07.value != null) {
                                val fileUri: Uri = Uri.fromFile(picture07.value?.let { File(it) })
                                AMovServer.asyncUploadImage(
                                    inputStream = context.contentResolver.openInputStream(fileUri)!!,
                                    extension = "jpg",
                                    onResult = { result ->
                                        Log.d("AMovServer", "Result: $result")
                                        if (result != null) {
                                            imageUrl07 = result
                                            error = null
                                            val novaLista = pergunta?.respostas?.toMutableList()
                                            novaLista?.set(0, imageUrl07 ?: "")
                                            pergunta?.respostas = novaLista?.toList()!!
                                        } else {
                                            error =
                                                context.getString(R.string.error_uploading_image)
                                            imageUrl07 = null
                                        }
                                        pergunta?.let {
                                            viewModel.addPerguntaToFirestore(
                                                it
                                            )
                                        }
                                    }
                                )
                            }
                            pergunta?.let {
                                viewModel.addPerguntaToFirestore(
                                    it
                                )
                            }
                        }
                        if (errorMessage == null) {
                            navController.navigate("criar-questionario") {
                                popUpTo("criar-questionario") { inclusive = true }
                            }
                        }
                    } else {
                        imageUrl?.let { url ->
                            val trimmedUrl = url.trimEnd('/')
                            val segments = trimmedUrl.split("/")
                            val filename = segments.lastOrNull() ?: ""
                            AMovServer.asyncDeleteFileFromServer(
                                fileName = filename,
                                serverUrl = trimmedUrl,
                                onResult = { result ->
                                    if (result) {
                                        error = null
                                        imageUrl = null
                                    } else {
                                        error = context.getString(R.string.error_deleting_image)
                                    }
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
fun AdicionaImagens(
    picture: MutableState<String?>,
    context: Context,
    imagePath: String
) {
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                picture.value = FileUtils.createFileFromUri(context, it)
            }
        }
    )

    val takePicture2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            picture.value = FileUtils.copyFile(context, imagePath)
        }
    }
    MeteImagem(picture)
    Spacer(Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                pickImage.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text("Select picture")
        }
        Button(
            onClick = {
                takePicture2.launch(
                    FileProvider.getUriForFile(
                        context,
                        "pt.isec.marco.quizec.android.fileprovider",
                        File(imagePath)
                    )
                )
            }
        ) {
            Text("Take picture")
        }
    }
}

@Composable
fun MeteImagem(picture: MutableState<String?>) {
    picture.value?.let { path ->
        AsyncImage(
            model = path,
            contentScale = ContentScale.Crop,
            contentDescription = "Pergunta image",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .clip(RectangleShape)
        )
    }
}


