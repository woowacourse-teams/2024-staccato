package com.on.staccato.presentation.main

sealed interface HomeRefresh {
    data object None : HomeRefresh

    data object Timeline : HomeRefresh

    data object Map : HomeRefresh

    data object All : HomeRefresh
}
