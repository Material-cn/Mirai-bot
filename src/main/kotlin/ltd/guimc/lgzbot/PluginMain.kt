/*
 * THIS FILE IS PART OF lgz-bot PROJECT
 *
 * You must disclose the source code of your modified work and the source code you took from this project. This means you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated) application.
 * Your modified application must also be licensed under the AGPLv3.
 *
 * Copyright (c) 2022 - now Guimc Team.
 */

package ltd.guimc.lgzbot


import ltd.guimc.lgzbot.command.*
import ltd.guimc.lgzbot.files.Config
import ltd.guimc.lgzbot.files.GithubSubConfig
import ltd.guimc.lgzbot.listener.message.GithubUrlListener
import ltd.guimc.lgzbot.listener.message.MessageFilter
import ltd.guimc.lgzbot.listener.multi.BakaListener
import ltd.guimc.lgzbot.listener.mute.AutoQuit
import ltd.guimc.lgzbot.listener.nudge.AntiNudgeSpam
import ltd.guimc.lgzbot.utils.RegexUtils.getDefaultPinyinRegex
import ltd.guimc.lgzbot.utils.RegexUtils.getDefaultRegex
import ltd.guimc.lgzbot.utils.RequestUtils
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.author
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.name
import net.mamoe.mirai.console.plugin.version
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.events.NudgeEvent

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        "ltd.guimc.lgzbot",
        "0.2.3",
        "LgzBot",
    ){
        author("BakaBotTeam")
    }
) {
    lateinit var bypassMute: Permission
    lateinit var blocked: Permission
    lateinit var nudgeMute: Permission
    lateinit var disableSpamCheck: Permission
    lateinit var disableADCheck: Permission
    lateinit var root: Permission
    lateinit var disableRoot: Permission
    lateinit var adRegex: Array<Regex>
    lateinit var adPinyinRegex: Array<Regex>

    override fun onEnable() {
        logger.info("$name v$version by $author Loading")
        adRegex = getDefaultRegex()
        adPinyinRegex = getDefaultPinyinRegex()

        registerPerms()
        registerCommands()
        registerEvents()
        Config.reload()
        GithubSubConfig.reload()
        logger.info("$name v$version by $author Loaded")
    }

    override fun onDisable() {
        logger.info("$name v$version by $author Disabling")
        Config.save()
        GithubSubConfig.save()
    }

    private fun registerPerms() = PermissionService.INSTANCE.run {
        root = register(PermissionId("lgzbot", "*"), "The root permission")
        bypassMute = register(PermissionId("lgzbot", "bypassmute"), "?????????????????????????????????", root)
        blocked = register(PermissionId("lgzbot", "blocked"), "??????????????????!", root)
        nudgeMute = register(PermissionId("lgzbot", "nudgemute"), "???????????????", root)

        disableRoot = register(PermissionId("lgzbot.disable", "*"), "The root permission", root)
        disableSpamCheck = register(PermissionId("lgzbot.disable", "spamcheck"), "????????????????????????", disableRoot)
        disableADCheck = register(PermissionId("lgzbot.disable", "adcheck"), "????????????????????????", disableRoot)
    }

    private fun registerCommands() = CommandManager.run {
        registerCommand(LGZBotCommand)
        registerCommand(MusicCommand)
        registerCommand(ACGCommand)
        registerCommand(RiskCommand)
        registerCommand(HttpCatCommand)
        registerCommand(GithubSubCommand)
        registerCommand(ToggleCheckCommand)
        registerCommand(ReviewCommand)
    }

    private fun registerEvents() = GlobalEventChannel.run {
        subscribeAlways<GroupMessageEvent>(priority = EventPriority.HIGHEST) { event -> MessageFilter.filter(event) }

        subscribeAlways<GroupMessageEvent> { event -> GithubUrlListener.onMessage(event) }

        subscribeAlways<BotInvitedJoinGroupRequestEvent> {
            // it.accept()
            require(it.invitor != null) { "Must have a invitor in BotInvitedJoinGroupRequestEvent" }

            it.invitor!!.sendMessage(
                "Event ID: ${it.eventId}"
            )
            it.invitor!!.sendMessage(
                "????????????????????????????????????????????????/????????????????????????????????????????????????????????????\n" +
                    "??????: ????????????????????? ??????????????????????????????????????????????????????!"
            )
            RequestUtils.Group.add(it)
        }

        subscribeAlways<NewFriendRequestEvent> {
            it.accept()
        }

        // Anti NudgeSpam
        subscribeAlways<NudgeEvent>(priority = EventPriority.HIGHEST) { e -> AntiNudgeSpam.onNudge(e) }

        // BakaListener
        registerListenerHost(BakaListener)
        registerListenerHost(AutoQuit)
    }
}
