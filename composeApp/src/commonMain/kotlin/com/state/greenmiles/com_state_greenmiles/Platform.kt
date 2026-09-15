package com.state.greenmiles.com_state_greenmiles

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform