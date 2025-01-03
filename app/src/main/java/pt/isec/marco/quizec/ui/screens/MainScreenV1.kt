package pt.isec.marco.quizec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import coil3.compose.AsyncImage
import pt.isec.marco.quizec.R

@Composable
fun QuizecScreen(
    modifier: Modifier = Modifier,
    viewModel: FirebaseViewModel,
    navController: NavHostController = rememberNavController()
) {
    BackgroundWithImage(
        modifier = Modifier.fillMaxSize()
    ) {
        val error by remember { viewModel.error }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("User: ${viewModel.user.value?.email ?: ""}")
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            if (error != null) {
                Text(text = "Error: $error", Modifier.background(Color(255, 0, 0, 100)))
                Spacer(modifier = Modifier.height(16.dp))
            }
            AsyncImage(
                model = R.drawable.quizec,
                contentScale = ContentScale.Crop,
                contentDescription = "Contact image",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .shadow(4.dp, RectangleShape)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(50.dp))
            Row {

                Button(
                    onClick = {
                        navController.navigate("menu-criador") {
                            popUpTo("menu-criador") {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(150.dp)
                        .padding(8.dp)
                        .shadow(4.dp, CircleShape),
                ) {
                    Text("Criador")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("menu-utilizador") {
                            popUpTo("menu-criador") {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(150.dp)
                        .padding(8.dp)
                        .shadow(4.dp, CircleShape),

                    )
                {
                    Text("Utilizador")
                }
            }

        }
    }
}

