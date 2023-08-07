package com.jsonkile.electionapp.util

data class MockHeadline(
    val header: String,
    val content: String,
    val imageUrl: String,
    val dateAdded: String
)

val mockNews = listOf(
    MockHeadline(
        header = "Voters registration to end in 2 weeks",
        content = "The ongoing voters registration that started 3 years ago will conclude in 2 weeks time, the presidents of INEC says.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/election-d2d38.appspot.com/o/breaking-news-768x644-1.jpg.webp?alt=media&token=c650cd88-be3a-4512-b53f-4ab3b0e8d192",
        dateAdded = "24 Oct 2024"
    ),

    MockHeadline(
        header = "INEC apologises for \"glitch in registration servers\"",
        content = "Uproar against failure of people getting registered has led to the president of INEC publicly apologizing for the situation.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/election-d2d38.appspot.com/o/INEC-1.jpg.webp?alt=media&token=0a4fbefc-9f6b-458a-81f6-8975a7ddb73c",
        dateAdded = "20 Oct 2024"
    )
)