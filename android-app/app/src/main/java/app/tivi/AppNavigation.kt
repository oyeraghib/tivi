// Copyright 2021, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package app.tivi

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import app.tivi.account.AccountUi
import app.tivi.common.compose.ui.androidMinWidthDialogSize
import app.tivi.episode.track.EpisodeTrack
import app.tivi.episodedetails.EpisodeDetails
import app.tivi.home.discover.Discover
import app.tivi.home.library.Library
import app.tivi.home.popular.PopularShows
import app.tivi.home.recommended.RecommendedShows
import app.tivi.home.search.Search
import app.tivi.home.trending.TrendingShows
import app.tivi.home.upnext.UpNext
import app.tivi.showdetails.details.ShowDetails
import app.tivi.showdetails.seasons.ShowSeasons
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

internal sealed class RootScreen(val route: String) {
    object Discover : RootScreen("discover")
    object Library : RootScreen("library")
    object UpNext : RootScreen("upnext")
    object Search : RootScreen("search")
}

private sealed class Screen(
    private val route: String,
) {
    fun createRoute(root: RootScreen) = "${root.route}/$route"

    object Discover : Screen("discover")
    object Trending : Screen("trending")
    object Library : Screen("library")
    object UpNext : Screen("upnext")
    object Popular : Screen("popular")

    object ShowDetails : Screen("show/{showId}") {
        fun createRoute(root: RootScreen, showId: Long): String {
            return "${root.route}/show/$showId"
        }
    }

    object EpisodeDetails : Screen("episode/{episodeId}") {
        fun createRoute(root: RootScreen, episodeId: Long): String {
            return "${root.route}/episode/$episodeId"
        }
    }

    object EpisodeTrack : Screen("episode/{episodeId}/track") {
        fun createRoute(root: RootScreen, episodeId: Long): String {
            return "${root.route}/episode/$episodeId/track"
        }
    }

    object ShowSeasons : Screen("show/{showId}/seasons?seasonId={seasonId}") {
        fun createRoute(
            root: RootScreen,
            showId: Long,
            seasonId: Long? = null,
        ): String {
            return "${root.route}/show/$showId/seasons".let {
                if (seasonId != null) "$it?seasonId=$seasonId" else it
            }
        }
    }

    object RecommendedShows : Screen("recommendedshows")
    object Search : Screen("search")
    object Account : Screen("account")
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController,
    composeScreens: ComposeScreens,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = RootScreen.Discover.route,
        enterTransition = { defaultTiviEnterTransition(initialState, targetState) },
        exitTransition = { defaultTiviExitTransition(initialState, targetState) },
        popEnterTransition = { defaultTiviPopEnterTransition() },
        popExitTransition = { defaultTiviPopExitTransition() },
        modifier = modifier,
    ) {
        addDiscoverTopLevel(navController, composeScreens, onOpenSettings)
        addLibraryTopLevel(navController, composeScreens, onOpenSettings)
        addUpNextTopLevel(navController, composeScreens, onOpenSettings)
        addSearchTopLevel(navController, composeScreens, onOpenSettings)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addDiscoverTopLevel(
    navController: NavController,
    composeScreens: ComposeScreens,
    openSettings: () -> Unit,
    rootScreen: RootScreen = RootScreen.Discover,
) {
    navigation(
        route = rootScreen.route,
        startDestination = Screen.Discover.createRoute(rootScreen),
    ) {
        addDiscover(navController, rootScreen, composeScreens.discover)
        addAccount(rootScreen, composeScreens.accountUi, openSettings)
        addShowDetails(navController, rootScreen, composeScreens.showDetails)
        addShowSeasons(navController, rootScreen, composeScreens.showSeasons)
        addEpisodeDetails(navController, rootScreen, composeScreens.episodeDetails)
        addEpisodeTrack(navController, rootScreen, composeScreens.episodeTrack)
        addRecommendedShows(navController, rootScreen, composeScreens.recommendedShows)
        addTrendingShows(navController, rootScreen, composeScreens.trendingShows)
        addPopularShows(navController, rootScreen, composeScreens.popularShows)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addLibraryTopLevel(
    navController: NavController,
    composeScreens: ComposeScreens,
    openSettings: () -> Unit,
    rootScreen: RootScreen = RootScreen.Library,
) {
    navigation(
        route = rootScreen.route,
        startDestination = Screen.Library.createRoute(rootScreen),
    ) {
        addLibrary(navController, rootScreen, composeScreens.library)
        addAccount(rootScreen, composeScreens.accountUi, openSettings)
        addShowDetails(navController, rootScreen, composeScreens.showDetails)
        addShowSeasons(navController, rootScreen, composeScreens.showSeasons)
        addEpisodeDetails(navController, rootScreen, composeScreens.episodeDetails)
        addEpisodeTrack(navController, rootScreen, composeScreens.episodeTrack)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addUpNextTopLevel(
    navController: NavController,
    composeScreens: ComposeScreens,
    openSettings: () -> Unit,
    rootScreen: RootScreen = RootScreen.UpNext,
) {
    navigation(
        route = rootScreen.route,
        startDestination = Screen.UpNext.createRoute(rootScreen),
    ) {
        addUpNext(navController, rootScreen, composeScreens.upNext)
        addAccount(rootScreen, composeScreens.accountUi, openSettings)
        addShowDetails(navController, rootScreen, composeScreens.showDetails)
        addShowSeasons(navController, rootScreen, composeScreens.showSeasons)
        addEpisodeDetails(navController, rootScreen, composeScreens.episodeDetails)
        addEpisodeTrack(navController, rootScreen, composeScreens.episodeTrack)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addSearchTopLevel(
    navController: NavController,
    composeScreens: ComposeScreens,
    openSettings: () -> Unit,
    rootScreen: RootScreen = RootScreen.Search,
) {
    navigation(
        route = rootScreen.route,
        startDestination = Screen.Search.createRoute(rootScreen),
    ) {
        addSearch(navController, rootScreen, composeScreens.search)
        addAccount(rootScreen, composeScreens.accountUi, openSettings)
        addShowDetails(navController, rootScreen, composeScreens.showDetails)
        addShowSeasons(navController, rootScreen, composeScreens.showSeasons)
        addEpisodeDetails(navController, rootScreen, composeScreens.episodeDetails)
        addEpisodeTrack(navController, rootScreen, composeScreens.episodeTrack)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addDiscover(
    navController: NavController,
    root: RootScreen,
    discover: Discover,
) {
    composable(
        route = Screen.Discover.createRoute(root),
        debugLabel = "Discover()",
    ) {
        discover(
            openTrendingShows = {
                navController.navigate(Screen.Trending.createRoute(root))
            },
            openPopularShows = {
                navController.navigate(Screen.Popular.createRoute(root))
            },
            openRecommendedShows = {
                navController.navigate(Screen.RecommendedShows.createRoute(root))
            },
            openShowDetails = { showId, seasonId, episodeId ->
                navController.navigateToShow(root, showId, seasonId, episodeId)
            },
            openUser = {
                navController.navigate(Screen.Account.createRoute(root))
            },
        )
    }
}

private fun NavController.navigateToShow(
    root: RootScreen,
    showId: Long,
    seasonId: Long? = null,
    episodeId: Long? = null,
) {
    navigate(Screen.ShowDetails.createRoute(root, showId))
    // If we have an season id, we also open that
    if (seasonId != null) {
        navigate(Screen.ShowSeasons.createRoute(root, showId, seasonId))
    }
    // If we have an episodeId, we also open that
    if (episodeId != null) {
        navigate(Screen.EpisodeDetails.createRoute(root, episodeId))
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addLibrary(
    navController: NavController,
    root: RootScreen,
    library: Library,
) {
    composable(
        route = Screen.Library.createRoute(root),
        debugLabel = "Library()",
    ) {
        library(
            openShowDetails = { showId ->
                navController.navigateToShow(root, showId)
            },
            openUser = {
                navController.navigate(Screen.Account.createRoute(root))
            },
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addUpNext(
    navController: NavController,
    root: RootScreen,
    upNext: UpNext,
) {
    composable(
        route = Screen.UpNext.createRoute(root),
        debugLabel = "UpNext()",
    ) {
        upNext(
            openShowDetails = { showId, seasonId, episodeId ->
                navController.navigateToShow(root, showId, seasonId, episodeId)
            },
            openUser = {
                navController.navigate(Screen.Account.createRoute(root))
            },
            openTrackEpisode = { episodeId ->
                navController.navigate(Screen.EpisodeTrack.createRoute(root, episodeId))
            },
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addSearch(
    navController: NavController,
    root: RootScreen,
    search: Search,
) {
    composable(Screen.Search.createRoute(root)) {
        search(
            openShowDetails = { showId ->
                navController.navigateToShow(root, showId)
            },
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addShowDetails(
    navController: NavController,
    root: RootScreen,
    showDetails: ShowDetails,
) {
    composable(
        route = Screen.ShowDetails.createRoute(root),
        debugLabel = "ShowDetails()",
        arguments = listOf(
            navArgument("showId") { type = NavType.LongType },
        ),
    ) {
        showDetails(
            navigateUp = navController::navigateUp,
            openShowDetails = { showId ->
                navController.navigateToShow(root, showId)
            },
            openEpisodeDetails = { episodeId ->
                navController.navigate(Screen.EpisodeDetails.createRoute(root, episodeId))
            },
            openSeasons = { showId, seasonId ->
                navController.navigate(Screen.ShowSeasons.createRoute(root, showId, seasonId))
            },
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addEpisodeDetails(
    navController: NavController,
    root: RootScreen,
    episodeDetails: EpisodeDetails,
) {
    composable(
        route = Screen.EpisodeDetails.createRoute(root),
        debugLabel = "EpisodeDetails()",
        arguments = listOf(
            navArgument("episodeId") { type = NavType.LongType },
        ),
    ) { backStackEntry ->
        val episodeId = backStackEntry.arguments!!.getLong("episodeId")

        episodeDetails(
            navigateUp = navController::navigateUp,
            navigateToTrack = {
                navController.navigate(
                    Screen.EpisodeTrack.createRoute(root, episodeId),
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@ExperimentalAnimationApi
private fun NavGraphBuilder.addEpisodeTrack(
    navController: NavController,
    root: RootScreen,
    episodeTrack: EpisodeTrack,
) {
    bottomSheet(
        route = Screen.EpisodeTrack.createRoute(root),
        debugLabel = "EpisodeTrack()",
        arguments = listOf(
            navArgument("episodeId") { type = NavType.LongType },
        ),
    ) {
        episodeTrack(
            navigateUp = navController::navigateUp,
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addRecommendedShows(
    navController: NavController,
    root: RootScreen,
    recommendedShows: RecommendedShows,
) {
    composable(
        route = Screen.RecommendedShows.createRoute(root),
        debugLabel = "RecommendedShows()",
    ) {
        recommendedShows(
            openShowDetails = { showId ->
                navController.navigateToShow(root, showId)
            },
            navigateUp = navController::navigateUp,
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addTrendingShows(
    navController: NavController,
    root: RootScreen,
    trendingShows: TrendingShows,
) {
    composable(
        route = Screen.Trending.createRoute(root),
        debugLabel = "TrendingShows()",
    ) {
        trendingShows(
            openShowDetails = { showId ->
                navController.navigateToShow(root, showId)
            },
            navigateUp = navController::navigateUp,
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addPopularShows(
    navController: NavController,
    root: RootScreen,
    popularShows: PopularShows,
) {
    composable(
        route = Screen.Popular.createRoute(root),
        debugLabel = "PopularShows()",
    ) {
        popularShows(
            openShowDetails = { showId ->
                navController.navigateToShow(root, showId)
            },
            navigateUp = navController::navigateUp,
        )
    }
}

private fun NavGraphBuilder.addAccount(
    root: RootScreen,
    accountUi: AccountUi,
    onOpenSettings: () -> Unit,
) {
    dialog(
        route = Screen.Account.createRoute(root),
        debugLabel = "AccountUi()",
        // Required due to https://issuetracker.google.com/issues/221643630
        dialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        accountUi(
            openSettings = onOpenSettings,
            modifier = Modifier
                // Required due to `usePlatformDefaultWidth = false` above
                .androidMinWidthDialogSize(clampMaxWidth = true),
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addShowSeasons(
    navController: NavController,
    root: RootScreen,
    showSeasons: ShowSeasons,
) {
    composable(
        route = Screen.ShowSeasons.createRoute(root),
        debugLabel = "ShowSeasons()",
        arguments = listOf(
            navArgument("showId") {
                type = NavType.LongType
            },
            navArgument("seasonId") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        showSeasons(
            navigateUp = navController::navigateUp,
            openEpisodeDetails = { episodeId ->
                navController.navigate(Screen.EpisodeDetails.createRoute(root, episodeId))
            },
            initialSeasonId = it.arguments?.getString("seasonId")?.toLong(),
        )
    }
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultTiviEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.Start)
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultTiviExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.Start)
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultTiviPopEnterTransition(): EnterTransition {
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.End)
}

@ExperimentalAnimationApi
private fun AnimatedContentScope<*>.defaultTiviPopExitTransition(): ExitTransition {
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End)
}
