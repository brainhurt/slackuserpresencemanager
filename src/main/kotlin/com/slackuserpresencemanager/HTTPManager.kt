package com.slackuserpresencemanager

import com.google.gson.Gson
import com.slackuserpresencemanager.slack.Presence
import org.apache.http.client.fluent.Request
import org.apache.http.client.utils.URIBuilder
import org.apache.logging.log4j.LogManager

import java.io.IOException
import java.net.URISyntaxException
import java.util.HashMap

/**
 * Created by Tharaka on 6/12/2017.
 * For project: SlackUserPresenceManager
 */
object HTTPManager {

    private val LOGGER = LogManager.getLogger(HTTPManager::class.java)

    private val BASE_URL = "https://slack.com/api/"

    private val RESOURCE_STATUS = "users.profile.set"

    private val RESOURCE_PRESENCE = "users.setPresence"

    private fun update(resource: String, key: String, value: String) {
        try {
            for (token in Main.getProperty("tokens").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                val uriBuilder = URIBuilder(BASE_URL + resource)
                uriBuilder.addParameter("token", token)
                uriBuilder.addParameter(key, value)
                Request.Get(uriBuilder.build())
                        .connectTimeout(1000)
                        .socketTimeout(1000)
                        .execute()
            }
        } catch (e: URISyntaxException) {
            LOGGER.error(e.message, e)
        } catch (e: IOException) {
            LOGGER.error(e.message, e)
        }

    }

    fun updateStatus(message: String, emoji: String) {
        val statusInfo = HashMap<String, String>()
        statusInfo.put("status_text", message)
        statusInfo.put("status_emoji", ":$emoji:")
        update(RESOURCE_STATUS, "profile", Gson().toJson(statusInfo))
    }

    fun updatePresence(presence: Presence) {
        update(RESOURCE_PRESENCE, "presence", presence.presenceString)
    }
}
