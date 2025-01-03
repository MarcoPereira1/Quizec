package pt.isec.marco.quizec.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import pt.isec.marco.quizec.utils.FAuthUtil
import pt.isec.marco.quizec.utils.FStorageUtil

open class FirebaseViewModel : ViewModel() {
    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val perguntas = mutableStateOf(listOf<String>())
    val questionarios = mutableStateOf(listOf<String>())
    val questionariosRespondidos = mutableStateOf(listOf<String>())


    open val user : State<Utilizador?>
        get() = _user

    private val _error = mutableStateOf<String?>(null)
    open val error : State<String?>
        get() = _error

    fun createUserWithEmail(email: String, password: String) {
        if (email.isBlank() && password.isBlank())
            return;
        viewModelScope.launch {
            FAuthUtil.createUserWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isBlank() && password.isBlank())
            return;
        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    fun signOut() {
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }

    fun addPerguntaToFirestore(pergunta: Pergunta) {
        viewModelScope.launch {
            FStorageUtil.addPerguntaToFirestore({ exception ->
                _error.value = exception?.message
            }, pergunta, this@FirebaseViewModel)
        }
    }

    fun addQuestionarioToFirestore(questionario: Questionario) {
        viewModelScope.launch {
            FStorageUtil.addQuestionarioToFirestore({ exception ->
                _error.value = exception?.message
            }, questionario, this@FirebaseViewModel)
        }
    }

    fun addPartilhaToFirestore(partilha: Partilha) {
        viewModelScope.launch {
            FStorageUtil.addPartilhaToFirestore({ exception ->
                _error.value = exception?.message
            }, partilha, this@FirebaseViewModel)
        }
    }
    fun updateDataInFirestore() {
        viewModelScope.launch {
            //FirebaseUtils.updateDataInFirestore()
            FStorageUtil.updateDataInFirestoreTrans { exception ->
                _error.value = exception?.message
            }
        }
    }

    fun removeDataFromFirestore() {
        viewModelScope.launch {
            FStorageUtil.removeDataFromFirestore { exception ->
                _error.value = exception?.message
            }
        }
    }

    private val _questionariosAux = mutableStateOf<List<Questionario>>(emptyList())
    val questionariosAux: State<List<Questionario>> get() = _questionariosAux

    fun startQuestionariosObserver() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                FStorageUtil.startQuestionariosObserver(userId) { questionarios, throwable ->
                    if (throwable != null) {
                        Log.e("Firestore", "Erro ao observar questionários: ${throwable.message}")
                    } else {
                        questionarios?.let {
                            _questionariosAux.value = it
                        }
                    }
                }
            }
        } else {
            Log.e("Firestore", "Utilizador não autenticado")
        }
    }

    private val _perguntasAux = mutableStateOf<List<Pergunta>>(emptyList())
    val perguntasAux: State<List<Pergunta>> get() = _perguntasAux

    fun startPerguntasObserver() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                FStorageUtil.startPerguntasObserver(userId) { perguntas, throwable ->
                    if (throwable != null) {
                        Log.e("Firestore", "Erro ao observar perguntas: ${throwable.message}")
                    } else {
                        perguntas?.let {
                            _perguntasAux.value = it
                        }
                    }
                }
            }
        } else {
            Log.e("Firestore", "Utilizador não autenticado")
        }
    }

    fun stopObserver() {
        viewModelScope.launch {
            FStorageUtil.stopObserver()
        }
    }
}