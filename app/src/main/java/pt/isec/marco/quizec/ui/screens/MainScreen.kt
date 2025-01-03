package pt.isec.marco.quizec.ui.screens

import pt.isec.marco.quizec.ui.screens.criador.PartilharQuestionarioScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pt.isec.marco.quizec.ui.screens.criador.CriadorMenuScreen
import pt.isec.marco.quizec.ui.screens.criador.CriarPerguntaScreen
import pt.isec.marco.quizec.ui.screens.criador.CriarQuestionarioScreen
import pt.isec.marco.quizec.ui.screens.criador.HistoricoQuestionarioScreen
import pt.isec.marco.quizec.ui.screens.criador.SelecionarPerguntasScreen
import pt.isec.marco.quizec.ui.screens.criador.TipoPerguntaScreen
import pt.isec.marco.quizec.ui.screens.criador.VerQuestionarioScreen
import pt.isec.marco.quizec.ui.screens.utilizador.HistoricoQuestionarioRespondidosScreen
import pt.isec.marco.quizec.ui.screens.utilizador.QuestionarioScreen
import pt.isec.marco.quizec.ui.screens.utilizador.ResponderQuestionarioScreen
import pt.isec.marco.quizec.ui.screens.utilizador.UtilizadorMenuScreen
import pt.isec.marco.quizec.ui.theme.FirebaseTheme
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSignOut: () -> Unit,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    viewModel: FirebaseViewModel

) {
    val context = LocalContext.current
    val currentScreen by navController.currentBackStackEntryAsState()
    var tipoPerguntaSelecionada by remember { mutableStateOf(-1) }
    var showComplete by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }
    var codigoPartilha by remember { mutableStateOf("") }


    FirebaseTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Firebase App")
                    },
                    actions = {
                        when (currentScreen?.destination?.route) {
                            "ver-questionario", "historico-questionarios", "seleciona-perguntas" ,"historico-questionarios-respondidos"-> {
                                IconButton(
                                    onClick = {
                                        showComplete = !showComplete
                                    }
                                ) {
                                    Icon(
                                        if (showComplete) {
                                            Icons.Filled.KeyboardArrowUp
                                        } else {
                                            Icons.Filled.KeyboardArrowDown

                                        },
                                        contentDescription = "Show complete"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        showAnswer = !showAnswer
                                    }
                                ) {
                                    Icon(
                                        if (showAnswer) {
                                            Icons.Filled.Lock
                                        } else {
                                            Icons.Filled.Warning

                                        },
                                        contentDescription = "Show answer"
                                    )
                                }
                            }

                            else -> {
                                IconButton(
                                    onClick = {
                                        onSignOut()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sign out"
                                    )
                                }
                            }
                        }

                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                navController.navigate("firestore") {
                                    popUpTo("firestore") {
                                        inclusive = true
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Firestore",
                                tint = if (currentScreen?.destination?.route == "firestore") Color(
                                    0,
                                    128,
                                    0
                                ) else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                navController.navigate("intent") {
                                    popUpTo("intent") {
                                        inclusive = true
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Intent",
                                tint = if (currentScreen?.destination?.route == "intent") Color(
                                    0,
                                    128,
                                    0
                                ) else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "quizec",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = "quizec") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        QuizecScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
                composable(route = "menu-criador") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        CriadorMenuScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
                composable(route = "criar-questionario") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        CriarQuestionarioScreen(
                            viewModel = viewModel,
                            navController = navController
                        )

                    }
                }
                composable(route = "tipo-pergunta") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        TipoPerguntaScreen(
                            viewModel = viewModel,
                            navController = navController,
                            onPerguntaSelected = { index ->
                                tipoPerguntaSelecionada = index
                            }
                        )
                    }
                }
                composable(route = "criar-pergunta") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        CriarPerguntaScreen(
                            viewModel = viewModel,
                            navController = navController,
                            tipoPerguntaSelecionada = tipoPerguntaSelecionada
                        )
                    }
                }
                composable(route = "menu-utilizador") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        UtilizadorMenuScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
                composable(route = "questionario") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        QuestionarioScreen(
                            viewModel = viewModel,
                            navController = navController,
                            codigoPartilha = codigoPartilha
                        )
                    }
                }
                composable(route = "responder-questionario") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        ResponderQuestionarioScreen(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }

                composable(route = "seleciona-perguntas") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        SelecionarPerguntasScreen(
                            viewModel = viewModel,
                            navController = navController,
                            showComplete = showComplete
                        )
                    }
                }
                composable(route = "ver-questionario") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        VerQuestionarioScreen(
                            viewModel = viewModel,
                            navController = navController,
                            showComplete = showComplete
                        )
                    }
                }
                composable(route = "historico-questionarios") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        HistoricoQuestionarioScreen(
                            viewModel = viewModel,
                            navController = navController,
                            showComplete = showComplete
                        )
                    }
                }
                composable(
                    route = "partilhar-questionario/{idQuestionario}",
                    arguments = listOf(
                        navArgument("idQuestionario") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val idQuestionario = backStackEntry.arguments?.getString("idQuestionario") ?: ""
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(129, 162, 204, 255)) // Define a cor de fundo aqui
                    ) {
                        PartilharQuestionarioScreen(
                            viewModel = viewModel,
                            idQuestionario = idQuestionario
                        )
                    }
                }
                composable(route = "historico-questionarios-respondidos") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(108, 147, 201, 255))
                    ) {
                        HistoricoQuestionarioRespondidosScreen(
                            viewModel = viewModel,
                            navController = navController,
                            showComplete = showComplete
                        )
                    }
                }
            }
        }
    }
}