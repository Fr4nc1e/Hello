package com.francle.hello.feature.pair.ui.event

sealed class PairEvent {
    object Pair : PairEvent()
    object ClickLike : PairEvent()
    object ClickDislike : PairEvent()
}
