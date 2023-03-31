package net.egork.telegram.svoyak.scheduler

import net.egork.telegram.svoyak.Utils
import net.egork.telegram.svoyak.data.User
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.util.*

/**
 * @author egor@egork.net
 */
class GameData {
    private var setId: String? = null
    private var topicCount = 6
    private var minPlayers = 3
    private var maxPlayers = 4
    private val players: MutableList<User> = ArrayList()
    private val spectators: MutableList<User> = ArrayList()
    var judge: User? = null
    var lastUpdated: Long
        private set

    init {
        lastUpdated = System.currentTimeMillis()
    }

    fun getSetId(): String? {
        return setId
    }

    fun setSetId(setId: String?) {
        this.setId = setId
        lastUpdated = System.currentTimeMillis()
    }

    fun getTopicCount(): Int {
        return topicCount
    }

    fun setTopicCount(topicCount: Int) {
        this.topicCount = topicCount
        lastUpdated = System.currentTimeMillis()
    }

    fun getMinPlayers(): Int {
        return minPlayers
    }

    fun setMinPlayers(minPlayers: Int) {
        this.minPlayers = minPlayers
        lastUpdated = System.currentTimeMillis()
    }

    fun getMaxPlayers(): Int {
        return maxPlayers
    }

    fun setMaxPlayers(maxPlayers: Int) {
        this.maxPlayers = maxPlayers
        lastUpdated = System.currentTimeMillis()
    }

    fun getPlayers(): List<User> {
        return Collections.unmodifiableList(players)
    }

    fun addPlayer(user: User) {
        unregister(user)
        players.add(user)
        lastUpdated = System.currentTimeMillis()
    }

    fun addSpectator(user: User) {
        unregister(user)
        spectators.add(user)
        lastUpdated = System.currentTimeMillis()
    }

    fun getSpectators(): List<User> {
        return Collections.unmodifiableList(spectators)
    }

    fun unregister(user: User) {
        spectators.remove(user)
        players.remove(user)
        lastUpdated = System.currentTimeMillis()
    }

    override fun toString(): String {
        return """
             ${if (setId == null) "Стандартная игра" else "Игра по пакету $setId"}
             Тем - $topicCount
             Игроков - $minPlayers-$maxPlayers
             Игроки: ${Utils.userList(players)}
             Зрители: ${Utils.userList(spectators)}
             """.trimIndent()
    }

    fun saveState(pw: PrintWriter) {
        pw.println("Game Data")
        GameChat.saveData(pw, "set id", setId)
        GameChat.saveData(pw, "topic count", topicCount)
        GameChat.saveData(pw, "players", players.size)
        for (player in players) {
            GameChat.saveData(pw, "player", player)
        }
        GameChat.saveData(pw, "spectators", spectators.size)
        for (spectator in spectators) {
            GameChat.saveData(pw, "spectator", spectator)
        }
        GameChat.saveNullableData(pw, "judge", judge)
    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun loadState(reader: BufferedReader?): GameData {
            GameChat.expectLabel(reader, "Game Data")
            return GameData().apply {
                setSetId(GameChat.readData(reader, "set id"))
                setTopicCount(GameChat.readData(reader, "topic count").toInt())

                val playerCount = GameChat.readData(reader, "players").toInt()
                for (i in 0 until playerCount) {
                    addPlayer(User.readUser(reader, "player"))
                }

                val specCount = GameChat.readData(reader, "spectators").toInt()
                for (i in 0 until specCount) {
                    addPlayer(User.readUser(reader, "spectator"))
                }

                judge = User.readNullableUser(reader, "judge")
            }
        }
    }
}
