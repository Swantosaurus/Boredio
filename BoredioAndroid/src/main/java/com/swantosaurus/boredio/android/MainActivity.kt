package com.swantosaurus.boredio.android

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.swantosaurus.boredio.Greeting
import com.swantosaurus.boredio.SimpleCounterViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        //GreetingView(Greeting().greet())
                        val koin = getKoin()
                        Text(text = koin.get<Context>().packageName)
                        Counter()
                    }
                }
            }
        }
    }
}

@Composable
fun Counter(counterViewModel: SimpleCounterViewModel = koinViewModel()) {
    val cnt by counterViewModel.count.collectAsState()
    Text(text = "Counter: $cnt")
    Row {
        Button(onClick = { counterViewModel.increment() }) {
            Text(stringResource(R.string.plus))
        }
        Button(onClick = { counterViewModel.decrement() }) {
            Text("--")
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
