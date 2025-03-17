package net.moviemate.app.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import net.moviemate.app.m.User

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _userLiveData = MutableLiveData<User?>()
    val userLiveData: LiveData<User?> get() = _userLiveData

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    private val _profileUpdateStatus = MutableLiveData<String>()
    val profileUpdateStatus: LiveData<String> get() = _profileUpdateStatus

    init {
        checkUserSession()
    }

    fun checkUserSession() {
        auth.currentUser?.let { firebaseUser ->
            fetchUserDetails(firebaseUser.uid)
        } ?: _userLiveData.postValue(null)
    }

    fun signUp(username: String, email: String, password: String, profileImageUri: Uri?) {
        viewModelScope.launch {
            try {
                // Step 1: Create user with email and password
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                firebaseUser?.let { user ->
                    val userId = user.uid
                    var imageUrl = ""

                    // Step 2: Upload profile image (if available)
                    profileImageUri?.let { uri ->
                        imageUrl = uploadProfileImage(userId, uri)
                    }

                    // Step 3: Create user data object
                    val userData = mapOf(
                        "userId" to userId,
                        "username" to username,
                        "email" to email,
                        "image" to imageUrl
                    )

                    // Step 4: Save user details to Firestore
                    saveUserToFirestore(userId, userData)

                    // Step 5: Update LiveData
                    _userLiveData.postValue(User(userId, email, imageUrl))
                }
            } catch (e: Exception) {
                _authStatus.postValue("Signup Failed: ${e.message}")
            }
        }
    }


    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                auth.currentUser?.let { fetchUserDetails(it.uid) }
            } catch (e: Exception) {
                _authStatus.postValue("Sign-in Failed: ${e.message}")
            }
        }
    }

    fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    _userLiveData.postValue(user)
                } else {
                    _authStatus.postValue("User not found")
                }
            } catch (e: Exception) {
                _authStatus.postValue("Error fetching user details: ${e.message}")
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    firestore.collection("users").document(user.uid)
                        .update("username", newUsername).await()
                    _profileUpdateStatus.postValue("Username updated successfully")
                }
            } catch (e: Exception) {
                _profileUpdateStatus.postValue("Failed to update username: ${e.message}")
            }
        }
    }

    fun updateProfileImage(profileImageUri: Uri) {
        viewModelScope.launch {
            try {
                auth.currentUser?.let { user ->
                    val imageUrl = uploadProfileImage(user.uid, profileImageUri)
                    firestore.collection("users").document(user.uid)
                        .update("image", imageUrl).await()
                    fetchUserDetails(user.uid)
                    _profileUpdateStatus.postValue("Profile image updated successfully")
                }
            } catch (e: Exception) {
                _profileUpdateStatus.postValue("Failed to update profile image: ${e.message}")
            }
        }
    }

    private suspend fun uploadProfileImage(uid: String, imageUri: Uri): String {
        return try {
            val storageRef = storage.reference.child("profile_images/$uid.jpg")
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            _profileUpdateStatus.postValue("Profile image upload failed: ${e.message}")
            ""
        }
    }

    private fun saveUserToFirestore(userId: String, userData: Map<String, Any>) {
        FirebaseFirestore.getInstance().collection("users")
            .document(userId)
            .set(userData)
            .addOnSuccessListener {
                _authStatus.postValue("Signup successful!")
            }
            .addOnFailureListener { e ->
                _authStatus.postValue("Failed to save user data: ${e.message}")
            }
    }

    fun signOut() {
        auth.signOut()
        _userLiveData.postValue(null)
    }
}
