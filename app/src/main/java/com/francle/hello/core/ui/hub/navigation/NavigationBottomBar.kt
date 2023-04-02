package com.francle.hello.core.ui.hub.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.francle.hello.core.ui.hub.navigation.destination.BottomItems

@Composable
fun NavigationBottomBar(
    modifier: Modifier,
    curRoute: String,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        BottomItems.values().forEach { bottomItems ->
            NavigationBarItem(
                selected = curRoute.startsWith(bottomItems.route),
                onClick = {
                    if (curRoute != bottomItems.route) {
                        onPopBackStack()
                        onNavigate(bottomItems.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = bottomItems.icon,
                        contentDescription = stringResource(id = bottomItems.contentDescription)
                    )
                },
                label = {
                    stringResource(id = bottomItems.contentDescription)
                },
                alwaysShowLabel = curRoute.startsWith(bottomItems.route),
                modifier = Modifier.animateContentSize(
                    animationSpec = SpringSpec(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }
    }
}
