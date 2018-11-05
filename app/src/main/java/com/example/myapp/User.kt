package com.example.myapp

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.annotation.Backlink



@Entity
data class User (
        @Id var id: Long = 0,
    var login: String? = null, //Логин
    var password: String? = null //Пароль


)
{
    @Backlink(to = "user")
    lateinit var flat: ToMany<Flat>
}