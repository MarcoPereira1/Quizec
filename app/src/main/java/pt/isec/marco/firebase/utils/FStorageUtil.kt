package pt.isec.marco.firebase.utils

import android.content.res.AssetManager
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import pt.isec.marco.firebase.ui.viewmodels.FirebaseViewModel
import pt.isec.marco.firebase.ui.viewmodels.Pergunta
import java.io.IOException
import java.io.InputStream


class FStorageUtil {
    companion object {
        fun addDataToFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore

            val scores = hashMapOf(
                "nrgames" to 0,
                "topscore" to 0
            )
            db.collection("Scores").document("Level1").set(scores)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

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

        fun addPerguntaToFirestore(onResult: (Throwable?) -> Unit, pergunta: Pergunta, viewModel: FirebaseViewModel) {
            val db = Firebase.firestore

            geraUnico { uniqueId ->
                pergunta.id = uniqueId

                val perguntaHash = hashMapOf(
                    "id" to pergunta.id,
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

        fun updateDataInFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            v.get(Source.SERVER)
                .addOnSuccessListener {
                    val exists = it.exists()
                    Log.i("Firestore", "updateDataInFirestore: Success? $exists")
                    if (!exists) {
                        onResult(Exception("Doesn't exist"))
                        return@addOnSuccessListener
                    }
                    val value = it.getLong("nrgames") ?: 0
                    v.update("nrgames", value + 1)
                    onResult(null)
                }
                .addOnFailureListener { e ->
                    onResult(e)
                }
        }

        fun updateDataInFirestoreTrans(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            db.runTransaction { transaction ->
                val doc = transaction.get(v)
                if (doc.exists()) {
                    val newnrgames = (doc.getLong("nrgames") ?: 0) + 1
                    val newtopscore = (doc.getLong("topscore") ?: 0) + 100
                    transaction.update(v, "nrgames", newnrgames)
                    transaction.update(v, "topscore", newtopscore)
                    null
                } else
                    throw FirebaseFirestoreException(
                        "Doesn't exist",
                        FirebaseFirestoreException.Code.UNAVAILABLE
                    )
            }.addOnCompleteListener { result ->
                onResult(result.exception)
            }
        }

        fun removeDataFromFirestore(onResult: (Throwable?) -> Unit) {
            val db = Firebase.firestore
            val v = db.collection("Scores").document("Level1")

            v.delete()
                .addOnCompleteListener { onResult(it.exception) }
        }

        private var listenerRegistration: ListenerRegistration? = null

        fun startObserver(onNewValues: (Long, Long) -> Unit) {
            stopObserver()
            val db = Firebase.firestore
            listenerRegistration = db.collection("Scores").document("Level1")
                .addSnapshotListener { docSS, e ->
                    if (e != null) {
                        return@addSnapshotListener
                    }
                    if (docSS != null && docSS.exists()) {
                        val nrgames = docSS.getLong("nrgames") ?: 0
                        val topscore = docSS.getLong("topscore") ?: 0
                        Log.i("Firestore", "$nrgames : $topscore")
                        onNewValues(nrgames, topscore)
                    }
                }
        }

        fun stopObserver() {
            listenerRegistration?.remove()
        }

// Storage

        fun getFileFromAsset(assetManager: AssetManager, strName: String): InputStream? {
            var istr: InputStream? = null
            try {
                istr = assetManager.open(strName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return istr
        }

//https://firebase.google.com/docs/storage/android/upload-files

        fun uploadFile(inputStream: InputStream, imgFile: String) {
            val storage = Firebase.storage
            val ref1 = storage.reference
            val ref2 = ref1.child("images")
            val ref3 = ref2.child(imgFile)

            val uploadTask = ref3.putStream(inputStream)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref3.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    println(downloadUri.toString())
                    // something like:
                    //   https://firebasestorage.googleapis.com/v0/b/p0405ansamov.appspot.com/o/images%2Fimage.png?alt=media&token=302c7119-c3a9-426d-b7b4-6ab5ac25fed9
                } else {
                    // Handle failures
                    // ...
                }
            }


        }

    }
}