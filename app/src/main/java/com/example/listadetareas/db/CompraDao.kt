package com.example.listadetareas.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface CompraDao {

    @Query("SELECT * FROM compra ORDER BY realizada")
    fun findAll(): List<Compra>

    @Query("SELECT COUNT(*) FROM compra")
    fun contar(): Int

    @Insert
    fun insertar(tarea: Compra): Long

    @Update
    fun actualizar(tarea: Compra)

    @Delete
    fun eliminar(tarea: Compra)

}