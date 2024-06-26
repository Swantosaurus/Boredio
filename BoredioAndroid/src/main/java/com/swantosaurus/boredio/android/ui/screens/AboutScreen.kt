package com.swantosaurus.boredio.android.ui.screens


import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.android.ui.util.ExpandableSection
import com.swantosaurus.boredio.screenViewModels.AboutViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(aboutViewModel: AboutViewModel = koinViewModel()) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = { Text(text = "About") }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            Section(title = stringResource(id = R.string.aboutScreenLicenseTitle)) {
                Description(text = stringResource(id = R.string.aboutScreenLicenseText))
            }

            Section(title = stringResource(id = R.string.aboutScreenDataSourcesTitle)) {
                Title(
                    text = stringResource(id = R.string.aboutScreenBoredApiTitle),
                    link = aboutViewModel.inAppUrls.boredApi,
                    padding = PaddingValues(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 16.dp)
                )
                Description(text = stringResource(id = R.string.aboutScreenBoredApiText))
                Title(
                    text = stringResource(id = R.string.aboutScreenOpenAiDalleTitle),
                    link = aboutViewModel.inAppUrls.dalleApi
                )
                Description(text = stringResource(id = R.string.aboutScreenOpenAiDalleText))
                Title(
                    text = stringResource(id = R.string.aboutScreenGoogleAPITitle),
                    link = aboutViewModel.inAppUrls.googleCould
                )
                Description(text = stringResource(id = R.string.aboutScreenGoogleAPIText))
            }

            Section(title = stringResource(id = R.string.aboutScreenPrivacyPolicyTitle)) {
                Description(text = stringResource(id = R.string.aboutScreenPrivacyPolicyText))
            }
        }
    }
}


@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val isExpandedTransitionState by remember {
        derivedStateOf {
            MutableTransitionState(isExpanded)
        }
    }

    ExpandableSection(modifier = Modifier
        .padding(8.dp)
        .clip(RoundedCornerShape(16.dp)),
        isExpanded = isExpandedTransitionState,
        toggleExpanded = { isExpanded = !isExpanded },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(8.dp)
            )
        }) {
        Column {
            content()
        }
    }
//    Column(Modifier.padding(8.dp)) {
//        Text(text = title, style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(8.dp))
//        content()
//    }
}

@Composable
private fun Title(text: String, link: String, padding: PaddingValues = PaddingValues(start = 8.dp, bottom = 8.dp, end = 16.dp, top = 16.dp)) {
    val ctx = LocalContext.current
    TextButton(
        onClick = {
            Intent(Intent.ACTION_VIEW, Uri.parse(link)).let {
                ctx.startActivity(it)
            }
        }, contentPadding = padding
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun Description(text: String, leftPadding: Dp = 16.dp, rightPadding: Dp = 8.dp) {
    Text(modifier = Modifier.padding(start = leftPadding, end = rightPadding), text = text)
}

