package com.example.myapp

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne



@Entity
data class Flat(
        @Id var id: Long = 0,

        var street: String? = null,//Адрес
        var price: Double = 0.0,//Цена
        var area: Double = 0.0,//Площадь
        var room: Int = 0, // Количество комнат
        var floor: Int = 0//Этаж

)
{
    lateinit var user: ToOne<User>
}