/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.home.popular

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import app.tivi.common.compose.EntryGrid
import app.tivi.common.compose.rememberFlowWithLifecycle

@Composable
fun Popular(navController: NavController) {
    Popular(
        viewModel = hiltViewModel(),
        navController = navController,
    )
}

@Composable
internal fun Popular(
    viewModel: PopularShowsViewModel,
    navController: NavController,
) {
    EntryGrid(
        lazyPagingItems = rememberFlowWithLifecycle(viewModel.pagedList).collectAsLazyPagingItems(),
        title = stringResource(R.string.discover_popular_title),
        onOpenShowDetails = { showId -> navController.navigate("show/$showId") }
    )
}
