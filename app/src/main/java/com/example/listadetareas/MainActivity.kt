package com.example.listadetareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listadetareas.db.AppDatabase
import com.example.listadetareas.db.Compra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ListaCompraUI()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCompraUI() {
    val contexto = LocalContext.current
    val (compras, setCompras) = remember { mutableStateOf(emptyList<Compra>()) }
    val alcanceCorrutina = rememberCoroutineScope()

    LaunchedEffect(compras) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(contexto).compraDao()
            setCompras(dao.findAll())
        }
    }

    var newCompraText by remember { mutableStateOf("") }
    var addingNewItem by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (compras.isEmpty() && !addingNewItem) {
            Text(
                text = "No hay productos que mostrar",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(compras) { compra ->
                    CompraItemUI(compra) {
                        setCompras(emptyList<Compra>())
                    }
                }
            }
        }

        if (addingNewItem) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Carro de compra",
                    modifier = Modifier.padding(bottom = 8.dp)
                        .size(200.dp)
                )
                TextField(
                    value = newCompraText,
                    onValueChange = { newCompraText = it },
                    label = { Text("Producto") },
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = {
                        if (newCompraText.isNotBlank()) {
                            val dao = AppDatabase.getInstance(contexto).compraDao()
                            val newCompra = Compra(0, newCompraText, false)
                            alcanceCorrutina.launch(Dispatchers.IO) {
                                dao.insertar(newCompra)
                                setCompras(dao.findAll())
                            }
                            newCompraText = ""
                            addingNewItem = false
                        }
                    }
                ) {
                    Text(text = "Crear")
                }
            }
        } else {
            FloatingActionButton(
                onClick = { addingNewItem = true },
                modifier = Modifier
                    .padding(16.dp)
                    .size(170.dp, 90.dp)
                    .align(Alignment.End)
            ) {
                Text(text = "+ Crear")
            }
        }
    }
}



@Composable
fun CompraItemUI(compra: Compra, onSave:() -> Unit = {} ){
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        if(compra.realizada){
            Icon(
                Icons.Filled.Check,
                contentDescription = "Compra realizada",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch (Dispatchers.IO){
                        val dao =   AppDatabase.getInstance(contexto).compraDao()
                        compra.realizada = false
                        dao.actualizar(compra)
                        onSave()
                    }
                }
            )
        }else{
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "compra por hacer",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch (Dispatchers.IO){
                        val dao =   AppDatabase.getInstance(contexto).compraDao()
                        compra.realizada = true
                        dao.actualizar(compra)
                        onSave()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = compra.compra,
            modifier = Modifier.weight(2f)
            )
        Icon(
            Icons.Filled.Delete,
            contentDescription = "eliminar  compra",
            modifier = Modifier.clickable {
                alcanceCorrutina.launch (Dispatchers.IO){
                    val dao =   AppDatabase.getInstance(contexto).compraDao()
                    dao.eliminar(compra)
                    onSave()
                }
            },
            tint = Color.Red
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompraItemUIPreview(){
    val compra = Compra(1, "producto", true)
    CompraItemUI(compra)


}

@Preview(showBackground = true)
@Composable
fun CompraItemUIPreview2(){
    val compra = Compra(1, "producto", false)
    CompraItemUI(compra)
}



