package cn.ianzb.miuixguitemplate.ui.screen.examples

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.ianzb.miuixguitemplate.R
import cn.ianzb.miuixguitemplate.ui.screen.subpage.BaseSubPageActivity
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card

/**
 * 示例二级页面：演示基于 [BaseSubPageActivity] 的独立 Activity 子页面。
 */
class ExampleSubPageActivity : BaseSubPageActivity() {

    override val titleRes: Int = R.string.example_arrow_sub_title

    @Composable
    override fun SubPageContent(
        isBlurEnabled: Boolean,
        contentPadding: PaddingValues,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
        ) {
            item {
                Card(modifier = Modifier.padding(horizontal = 12.dp).padding(top = 12.dp)) {
                    BasicComponent(
                        title = stringResource(R.string.example_arrow_sub_item),
                        summary = stringResource(R.string.example_arrow_sub_body),
                    )
                }
            }
        }
    }
}
