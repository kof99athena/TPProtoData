package com.anehta.tpprotodata

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.anehta.tpprotodata.ui.theme.TPProtoDataTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val Context.settingsUserData: DataStore<MyUserData> by dataStore(
        fileName = "userdata.pb", //로컬에 저장될 protobuf 파일명
        serializer = UserDataSerializer
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPProtoDataTheme {
                val dataStore = applicationContext.settingsUserData
                val savedUserName = dataStore.data.map { // MyUserData의 data인 name
                        settingsUserData ->
                    settingsUserData.name
                }.collectAsState(initial = "")

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = savedUserName.value,
                        modifier = Modifier.padding(innerPadding),
                        onSave = { userName ->
                            lifecycleScope.launch {
                                dataStore.updateData { currentNames ->
                                    currentNames.toBuilder()
                                        .setName(userName)
                                        .build()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onSave: (String) -> Unit) {
    var text by remember {
        mutableStateOf(name)
    }
    Column(Modifier.padding(all = 32.dp)) {
        TextField(value = text, onValueChange = { text = it })
        Button(onClick = { onSave(text) }) {
            Text(text = "save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TPProtoDataTheme {
        Greeting("Android") {}
    }
}