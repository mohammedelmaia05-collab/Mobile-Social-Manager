package com.example.planner

// 1. Every user has a role and belongs to a team
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Manager", // Manager, Designer, Copywriter
    val teamId: String = "",
    val profileImageUrl: String = ""
)

// 2. The Team object links the members
data class Team(
    val teamId: String = "",
    val managerUid: String = "",
    val members: List<String> = emptyList(), // List of UIDs
    val pendingInvites: List<String> = emptyList() // List of Emails
)

// 3. The Content Post
data class Post(
    val id: String = "",
    val teamId: String = "",
    val caption: String = "",
    val platform: String = "",
    val scheduledTimestamp: Long = 0L,
    val imageUrl: String = "",
    val status: String = "Draft", // Draft, Review, Approved, Scheduled
    val authorName: String = ""
)
