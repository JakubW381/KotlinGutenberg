package com.example.gutenberglibrary.Auth

interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val message: String): AuthResponse
}