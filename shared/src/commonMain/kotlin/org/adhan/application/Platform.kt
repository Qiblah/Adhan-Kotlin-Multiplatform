package org.adhan.application


expect val platform: String

class Greeting {
    fun greeting() = "Hello, $platform!"

}

