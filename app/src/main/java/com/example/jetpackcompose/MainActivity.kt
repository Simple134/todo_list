package com.example.jetpackcompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TodoApp()
                }
            }
        }
    }
}

@Composable
fun EditTodoDialog(
    item: TodoItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var newTitle by remember { mutableStateOf(item.title) }
    var newDescription by remember { mutableStateOf(item.description) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Edit Todo Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newDescription,
                    onValueChange = { newDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(newTitle, newDescription)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    item: TodoItem,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Delete To do Item") },
        text = { Text("Are you sure you want to delete this item?") },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("No")
            }
        }
    )
}


@Composable
fun TodoApp() {
    var todoItems by remember { mutableStateOf(listOf<TodoItem>()) }
    var currentId by remember { mutableStateOf(0) }
    var itemToEdit by remember { mutableStateOf<TodoItem?>(null) }
    var itemToDelete by remember { mutableStateOf<TodoItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TodoInput { title, description ->
            todoItems = todoItems + TodoItem(currentId++, title, description)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TodoList(
            items = todoItems,
            onToggleDone = { item ->
                todoItems = todoItems.map {
                    if (it.id == item.id) it.copy(isDone = !it.isDone) else it
                }
            },
            onEditItem = { item ->
                itemToEdit = item
            },
            onDeleteItem = { item ->
                itemToDelete = item
            }
        )

        itemToEdit?.let { item ->
            EditTodoDialog(
                item = item,
                onDismiss = { itemToEdit = null },
                onConfirm = { newTitle, newDescription ->
                    todoItems = todoItems.map {
                        if (it.id == item.id) it.copy(title = newTitle, description = newDescription) else it
                    }
                }
            )
        }

        itemToDelete?.let { item ->
            ConfirmDeleteDialog(
                item = item,
                onDismiss = { itemToDelete = null },
                onConfirm = {
                    todoItems = todoItems.filter { it.id != item.id }
                }
            )
        }
    }
}



@Composable
fun TodoList(items: List<TodoItem>, onToggleDone: (TodoItem) -> Unit, onEditItem: (TodoItem) -> Unit, onDeleteItem: (TodoItem) -> Unit) {
    LazyColumn {
        items(items) { item ->
            TodoRow(item, onToggleDone, onEditItem, onDeleteItem)
        }
    }
}

@Composable
fun TodoRow(item: TodoItem, onToggleDone: (TodoItem) -> Unit, onEditItem: (TodoItem) -> Unit, onDeleteItem: (TodoItem) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = item.isDone,
            onCheckedChange = { onToggleDone(item) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { onEditItem(item) }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { onDeleteItem(item) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun TodoInput(onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    onAdd(title, description)
                    title = ""
                    description = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
            //change 1
        ) {
            Text("Add To do")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackComposeTheme {
        TodoApp()
    }
}

