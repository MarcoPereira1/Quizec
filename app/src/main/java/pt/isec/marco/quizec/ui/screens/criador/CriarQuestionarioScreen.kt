package pt.isec.marco.quizec.ui.screens.criador

import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import pt.isec.marco.quizec.R
import pt.isec.marco.quizec.ui.screens.BackgroundWithImage
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Pergunta
import pt.isec.marco.quizec.ui.viewmodels.Questionario
import pt.isec.marco.quizec.utils.AMovServer
import pt.isec.marco.quizec.utils.FStorageUtil.Companion.getPerguntaByIdSuspend
import pt.isec.marco.quizec.utils.FileUtils
import java.io.File

@Composable
fun CriarQuestionarioScreen(
    viewModel: FirebaseViewModel,
    navController: NavHostController
) {
    var confirmaDialog by remember { mutableStateOf(false) }
    var mostraMsgSucesso by remember { mutableStateOf(false) }
    var nomeQuestionario by remember { mutableStateOf("") } // Variável para armazenar o nome do questionário
    var descricao by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var pergunta by remember { mutableStateOf<Pergunta?>(null) }
    val context = LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }
    val picture = remember { mutableStateOf<String?>(null) }
    val imagePath: String by lazy { FileUtils.getTempFilename(context) }
    BackgroundWithImage(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Cria questionário",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(32.dp))
                AdicionaImagens(picture, context, imagePath)
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = descricao,
                    onValueChange = { newText ->
                        descricao = newText
                    },
                    label = { Text("Descrição:") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.LightGray,
                        unfocusedContainerColor = Color.LightGray,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.Blue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()

                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("tipo-pergunta") {
                            popUpTo("tipo-pergunta") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RectangleShape
                ) {
                    Text("Criar nova pergunta")
                }
                Button(
                    onClick = {
                        navController.navigate("seleciona-perguntas") {
                            popUpTo("seleciona-perguntas") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RectangleShape
                ) {
                    Text("Adicionar perguntas já existentes")
                }
                Button(
                    onClick = {
                        navController.navigate("ver-questionario") {
                            popUpTo("ver-questionario") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RectangleShape
                ) {
                    Text("Ver questionário")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { confirmaDialog = true }
                ) {
                    Text("Terminar Questionário")
                }
            }
        }

        if (confirmaDialog) {
            val perguntasIds = viewModel.perguntas.value
            var perguntas by remember { mutableStateOf<List<Pergunta>>(emptyList()) }
            LaunchedEffect(perguntasIds) {
                perguntas = getPerguntasByIds(perguntasIds)
            }

            GuardaQuestionario(
                onConfirm = {
                    confirmaDialog = false
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

                                } else {
                                    error = context.getString(R.string.error_uploading_image)
                                    imageUrl = null
                                }
                                viewModel.addQuestionarioToFirestore(
                                    Questionario(
                                        id = "",
                                        idUtilizador = FirebaseAuth.getInstance().currentUser?.uid
                                            ?: "",
                                        descricao = nomeQuestionario,
                                        perguntas = perguntas,
                                        imagem = imageUrl ?: "",
                                        questRespondidosIds = emptyList(),
                                        questPartilhadosIds = emptyList()
                                    )
                                )
                            }
                        )
                    } else {
                        viewModel.addQuestionarioToFirestore(
                            Questionario(
                                id = "",
                                idUtilizador = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                descricao = nomeQuestionario,
                                perguntas = perguntas,
                                imagem = "",
                                questRespondidosIds = emptyList(),
                                questPartilhadosIds = emptyList()
                            )
                        )
                    }
                    viewModel.perguntas.value = emptyList()
                    mostraMsgSucesso = true
                },
                onDismiss = { confirmaDialog = false },
                nomeQuestionario = nomeQuestionario,
                onNomeChange = { nomeQuestionario = it }
            )
        }

        if (mostraMsgSucesso) {
            MsgSucesso(
                message = "Questionário criado com sucesso!",
                onDismiss = {
                    mostraMsgSucesso = false
                    navController.navigate("menu-criador") {
                        popUpTo("menu-criador") { inclusive = true }
                    }
                }
            )
        }
    }
}
@Composable
fun GuardaQuestionario(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    nomeQuestionario: String,
    onNomeChange: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Guardar Questionário",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "De um nome ao seu questionário:",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                TextField(
                    value = nomeQuestionario,
                    onValueChange = { onNomeChange(it) },
                    placeholder = { Text("Escreve aqui...") }
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(onClick = onConfirm) {
                        Text("Terminar")
                    }
                }
            }
        }
    }
}


@Composable
fun MsgSucesso(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sucesso",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}
suspend fun getPerguntasByIds(perguntasIds: List<String>): List<Pergunta> {
    val perguntas = mutableListOf<Pergunta>()
    for (perguntaId in perguntasIds) {
        val pergunta = getPerguntaByIdSuspend(perguntaId)
        if (pergunta != null) {
            perguntas.add(pergunta)
        }
    }
    return perguntas
}