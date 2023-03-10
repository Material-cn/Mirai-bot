/*
 * THIS FILE IS PART OF lgz-bot PROJECT
 *
 * You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated) application.
 * Your modified application must also be licensed under the AGPLv3.
 *
 * Copyright (c) 2022 - now Guimc Team.
 */

package ltd.guimc.lgzbot.command

import kotlinx.coroutines.launch
import ltd.guimc.lgzbot.PluginMain
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.message.data.Image.Key.isUploaded
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients

object HttpCatCommand: SimpleCommand (
    owner = PluginMain,
    primaryName = "httpcat",
    description = "Funny Cat"
) {
    @Handler
    fun CommandSender.onHandler(code: Int) = ltd_guimc_command_httpcat(code)

    fun CommandSender.ltd_guimc_command_httpcat(code: Int) = launch{
        require(!(subject == null || bot == null))
        val httpclients = HttpClients.createDefault()
        val httpget = HttpGet("https://http.cat/$code")
        val entity = httpclients.execute(httpget).entity
        if (entity != null) {
            val inputstream = entity.content
            if (inputstream != null) {
                val image = subject!!.uploadImage(inputstream.toExternalResource())
                if (image.isUploaded(bot!!)) {
                    subject?.sendMessage(image)
                    return@launch
                }
            }
        }
        subject?.sendMessage("Oops, something went wrong.")
    }
}