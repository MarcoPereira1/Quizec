package pt.isec.marco.quizec.utils


import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pt.isec.marco.quizec.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.quizec.ui.viewmodels.Partilha
import pt.isec.marco.quizec.ui.viewmodels.Pergunta
import pt.isec.marco.quizec.ui.viewmodels.Questionario
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FStorageUtil {
    companion object {

        fun geraId(length: Int = 6): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return (1..length)
                .map { chars.random() }
                .joinToString("")
        }

        fun geraUnico(onComplete: (String) -> Unit) {
            val db = Firebase.firestore
            val newId = geraId()

            db.collection("Perguntas").document(newId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        geraId()
                    } else {
                        onComplete(newId)
                    }
                }
                .addOnFailureListener { e ->
                    println("Error checking document: $e")
                }
        }

        fun addPerguntaToFirestore(
            onResult: (Throwable?) -> Unit,
            pergunta: Pergunta,
            viewModel: FirebaseViewModel
        ) {
            val db = Firebase.firestore

            geraUnico { uniqueId ->
                pergunta.id = uniqueId

                val perguntaHash = hashMapOf(
                    "id" to pergunta.id,
                    "idUtilizador" to pergunta.idUtilizador,
                    "titulo" to pergunta.titulo,
                    "imagem" to pergunta.imagem,
                    "respostas" to pergunta.respostas,
                    "respostaCerta" to pergunta.respostaCerta,
                    "tipo" to pergunta.tipo
                )

                db.collection("Perguntas")
                    .document("pergunta_${pergunta.id}")
                    .set(perguntaHash)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            onResult(null)
                            viewModel.perguntas.value += pergunta.id
                        } else {
                            onResult(result.exception)
                        }
                    }
            }
        }

        fun getPerguntaById(id: String, onResult: (Pergunta?, Throwable?) -> Unit) {
            val db = Firebase.firestore

            val docRef = db.collection("Perguntas").document("pergunta_$id")

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val pergunta = Pergunta.fromFirestore(document)
                        onResult(pergunta, null) // Return the Pergunta
                    } else {
                        onResult(null, Throwable("Pergunta not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    onResult(null, exception)
                }
        }

        fun getQuestionarioById(id: String, onResult: (Questionario?, Throwable?) -> Unit) {
            val db = Firebase.firestore

            val docRef = db.collection("Questionarios").document("questionario_$id")

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val questionario = Questionario.fromFirestore(document)
                        onResult(questionario, null)
                    } else {
                        onResult(null, Throwable("Questionario not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    onResult(null, exception)
                }
        }

        fun getPartilhaById(id: String, onResult: (Partilha?, Throwable?) -> Unit) {
            val db = Firebase.firestore

            val docRef = db.collection("Partilhas").document("partilha_$id")

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val partilha = Partilha.fromFirestore(document)
                        onResult(partilha, null)
                    } else {
                        onResult(null, Throwable("Partilha not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    onResult(null, exception)
                }
        }
        fun getQuestionarioByPartilhaId(
            id: String,
            onResult: (Questionario?, Throwable?) -> Unit
        ) {
            val db = Firebase.firestore
            val docRef = db.collection("Partilhas").document("partilha_$id")

            docRef
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val idQuestionario = document.getString("idQuestionario")
                        if (idQuestionario != null) {
                            getQuestionarioById(idQuestionario, onResult)
                        } else {
                            onResult(null, Throwable("idQuestionario not found in Partilha document"))
                        }
                    } else {
                        onResult(null, Throwable("Partilha not found with id: $id"))
                    }
                }
                .addOnFailureListener { exception ->
                    onResult(null, exception)
                }
        }

        fun startQuestionariosObserver(userId: String, onNewValues: (List<Questionario>?, Throwable?) -> Unit) {
            stopObserver()
            val db = Firebase.firestore
            listenerRegistration = db.collection("Questionarios")
                .whereEqualTo("idUtilizador", userId)
                .addSnapshotListener { querySnapshot, e ->
                    if (e != null) {
                        onNewValues(null, e)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val questionarios = querySnapshot.documents.mapNotNull { doc ->
                            Questionario.fromFirestore(doc)
                        }
                        Log.i("Firestore", "$questionarios")
                        onNewValues(questionarios, null)
                    } else {
                        onNewValues(emptyList(), null)
                    }
                }
        }
        fun startPerguntasObserver(userId: String, onNewValues: (List<Pergunta>?, Throwable?) -> Unit) {
            stopObserver()
            val db = Firebase.firestore
            listenerRegistration = db.collection("Perguntas")
                .whereEqualTo("idUtilizador", userId)
                .addSnapshotListener { querySnapshot, e ->
                    if (e != null) {
                        onNewValues(null, e)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val perguntas = querySnapshot.documents.mapNotNull { doc ->
                            Pergunta.fromFirestore(doc)
                        }
                        Log.i("Firestore", "$perguntas")
                        onNewValues(perguntas, null)
                    } else {
                        onNewValues(emptyList(), null)
                    }
                }
        }
        suspend fun getQuestionarioByIdSuspend(id: String): Questionario? = suspendCoroutine { continuation ->
            getQuestionarioById(id) { questionario, _ ->
                continuation.resume(questionario)
            }
        }

        suspend fun getPerguntaByIdSuspend(id: String): Pergunta? = suspendCoroutine { continuation ->
            getPerguntaById(id) { pergunta, _ ->
                continuation.resume(pergunta)
            }
        }

        fun addQuestionarioToFirestore(onResult: (Throwable?) -> Unit, questionario: Questionario, viewModel: FirebaseViewModel) {
            val db = Firebase.firestore

            geraUnico { uniqueId ->
                questionario.id = uniqueId

                val questionarioHash = hashMapOf(
                    "id" to questionario.id,
                    "idUtilizador" to questionario.idUtilizador,
                    "descricao" to questionario.descricao,
                    "perguntas" to questionario.perguntas,
                    "imagem" to questionario.imagem
                )

                db.collection("Questionarios")
                    .document("questionario_${questionario.id}")
                    .set(questionarioHash)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            onResult(null)
                            viewModel.questionarios.value += questionario.id
                        } else {
                            onResult(result.exception)
                        }
                    }
            }
        }

        fun addPartilhaToFirestore(
            onResult: (Throwable?) -> Unit,
            partilha: Partilha,
            viewModel: FirebaseViewModel
        ) {
            val db = Firebase.firestore

            geraUnico { uniqueId ->
                partilha.id = uniqueId

                val partilhaHash = hashMapOf(
                    "id" to partilha.id,
                    "idQuestionario" to partilha.idQuestionario,
                    "tempoEspera" to partilha.tempoEspera,
                    "duracao" to partilha.duracao,
                    "respostaList" to partilha.respostaList,
                    "usersList" to partilha.usersList
                )

                db.collection("Partilhas")
                    .document("partilha_${partilha.id}")
                    .set(partilhaHash)
                    .addOnCompleteListener { result ->
                        Log.i(
                            "Firestore",
                            "addPartilhaToFirestore: Success? ${result.isSuccessful}"
                        )
                        if (result.isSuccessful) {
                            onResult(null)
                        } else {
                            onResult(result.exception)
                        }
                    }
            }
        }


        fun addRespostasToPartilha(
            idPartilha: String,
            respostaList: List<List<String>>,
            userId: String
        ) {
            val db = Firebase.firestore
            val partilhaDocRef = db.collection("Partilhas").document("partilha_$idPartilha")
            val respostaCollectionRef = partilhaDocRef.collection("entry_$userId")

            respostaList.forEachIndexed { index, resposta ->
                val respostaDocRef = respostaCollectionRef.document("resposta_$index")

                respostaDocRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            Log.d("Firestore", "Documento resposta_$index já existe, ignorando.")
                        } else {
                            val respostas = hashMapOf(
                                "respostas" to resposta
                            )

                            respostaDocRef.set(respostas)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Successfully added list to resposta_$index.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Erro ao adicionar resposta_$index", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao verificar documento resposta_$index", e)
                    }
            }
        }

        private var listenerRegistration: ListenerRegistration? = null


        fun stopObserver() {
            listenerRegistration?.remove()
        }


    }
}