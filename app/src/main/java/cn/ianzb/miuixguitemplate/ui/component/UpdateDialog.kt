package cn.ianzb.miuixguitemplate.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.UpdateChecker
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun UpdateDialog(
    info: UpdateChecker.UpdateInfo,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppDialog(
        show = true,
        title = stringResource(R.string.update_available_title),
        summary = stringResource(
            R.string.update_available_summary,
            info.currentVersion,
            info.latestVersion,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        if (info.releaseNotes.isNotBlank()) {
            MiuixText(
                text = info.releaseNotes,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 12.dp),
            )
        }
        AppDialogButtonRow {
            AppDialogButton(
                text = stringResource(R.string.update_later),
                onClick = onDismissRequest,
                modifier = Modifier.weight(1f),
            )
            AppDialogButton(
                text = stringResource(R.string.update_now),
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                primary = true,
            )
        }
    }
}
