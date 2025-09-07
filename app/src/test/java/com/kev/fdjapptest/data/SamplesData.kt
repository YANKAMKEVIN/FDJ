package com.kev.fdjapptest.data

import com.kev.domain.model.League
import com.kev.domain.model.Team

val EPL = League(id = "123", name = "English Premier League", sport = "Soccer")
val L1  = League(id = "456", name = "French Ligue 1", sport = "Soccer")

val TEAMS_EPL = listOf(
  Team(id = "t1", name = "Arsenal", badgeUrl = null),
  Team(id = "t2", name = "Chelsea", badgeUrl = null)
)

val TEAMS_L1 = listOf(
  Team(id = "f1", name = "PSG", badgeUrl = null),
  Team(id = "f2", name = "Lyon", badgeUrl = null)
)
