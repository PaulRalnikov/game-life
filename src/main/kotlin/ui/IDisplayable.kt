package ui

import androidx.compose.runtime.Composable

interface IDisplayable  {
    @Composable
    fun display() : @Composable Unit
}