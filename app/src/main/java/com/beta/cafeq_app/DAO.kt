package com.beta.cafeq_app

import android.net.Uri
import com.beta.cafeq_app.data.Cafe
import com.beta.cafeq_app.data.Reservation
import com.beta.cafeq_app.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

object DAO {
    private val db = FirebaseDatabase.getInstance("https://cafeq-app-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val dbImg = FirebaseStorage.getInstance("gs://cafeq-app.appspot.com")
    private val databaseReferenceUser: DatabaseReference = db.getReference("Users")
    private val databaseReferenceCafe: DatabaseReference = db.getReference("Cafe")
    private val databaseReferenceReservation: DatabaseReference = db.getReference("Reservation")
    private val storageReferenceImgCafe: StorageReference = dbImg.getReference("Cafe")
    private val storageReferenceImgProfile: StorageReference = dbImg.getReference("Profile")

    fun addUser(user: User, idUser: String): Task<Void> {
        return databaseReferenceUser.child(idUser).setValue(user)
    }
    fun getSpecificUser(idUser: String): Query {
        return db.getReference("Users/$idUser").orderByKey()
    }
    fun updateUser(idUser: String,user: User){
        if (user.img !== "") {
            db.getReference("Users/$idUser").updateChildren(mapOf<String,String>("img" to user.img))
        }
        db.getReference("Users/$idUser").updateChildren(mapOf<String,String>("name" to user.name))
        db.getReference("Users/$idUser").updateChildren(mapOf<String,String>("gender" to user.gender))
        db.getReference("Users/$idUser").updateChildren(mapOf<String,String>("birthdate" to user.birthdate))
        db.getReference("Users/$idUser").updateChildren(mapOf<String,String>("email" to user.email))
        db.getReference("Users/$idUser").updateChildren(mapOf<String,String>("cell" to user.cell))
    }
    fun addCafe(cafe: Cafe): Task<Void> {
        return databaseReferenceCafe.push().setValue(cafe)
    }
    fun getCafe(): Query {
        return databaseReferenceCafe.orderByKey()
    }
    fun getSpecificCafe(idCafe: String): Query {
        return db.getReference("Cafe/$idCafe").orderByKey()
    }
    fun updateCafe(idCafe: String,latestChair: Int){
        db.getReference("Cafe/$idCafe").updateChildren(mapOf<String,Int>("chair" to latestChair))
    }
    fun addReservation(reservation: Reservation, idUser: String): Task<Void>{
        return databaseReferenceReservation.child(idUser).push().setValue(reservation)
    }
    fun getReservation(idUser: String): Query {
        return databaseReferenceReservation.child(idUser).orderByKey()
    }

    fun uploadImage(path: Uri, idImg: String): UploadTask {
        return storageReferenceImgCafe.child("images/$idImg").putFile(path)
    }
    fun uploadImageProfile(path: Uri, idImg: String): UploadTask {
        return storageReferenceImgProfile.child("images/$idImg").putFile(path)
    }
}
