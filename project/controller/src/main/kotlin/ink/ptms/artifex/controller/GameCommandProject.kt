package ink.ptms.artifex.controller

import ink.ptms.artifex.Artifex
import ink.ptms.artifex.controller.internal.*
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.submit
import taboolib.common5.Demand
import taboolib.module.lang.sendLang

object GameCommandProject {

    /**
     * 运行脚本工程
     */
    @CommandBody
    val run = subCommand {
        dynamic("project") {
            suggestion<ProxyCommandSender>(uncheck = true) { _, _ -> allScriptProjects() }
            execute<ProxyCommandSender> { sender, _, argument ->
                val file = scriptsFile.searchProject(argument)
                if (file?.exists() == true) {
                    submit(async = true) { runProject(file, sender) }
                } else {
                    sender.sendLang("command-project-not-found", argument)
                }
            }
            dynamic(commit = "args", optional = true) {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val demand = Demand("0 $argument")
                    val file = scriptsFile.searchProject(context.argument(-1))
                    if (file?.exists() == true) {
                        submit(async = true) { runProject(file, sender, compile = demand.tags.contains("C")) }
                    } else {
                        sender.sendLang("command-project-not-found", argument)
                    }
                }
            }
        }
    }

    /**
     * 释放脚本工程
     */
    @CommandBody
    val release = subCommand {
        dynamic("project") {
            suggestion<ProxyCommandSender>(uncheck = true) { _, _ -> allScriptProjects() }
            execute<ProxyCommandSender> { sender, _, argument ->
                val file = scriptsFile.searchProject(argument)
                if (file?.exists() == true) {
                    submit(async = true) { releaseProject(file, sender) }
                } else {
                    sender.sendLang("command-project-not-found", argument)
                }
            }
        }
    }

    /**
     * 重载脚本工程
     */
    @CommandBody
    val reload = subCommand {
        dynamic("project") {
            suggestion<ProxyCommandSender>(uncheck = true) { _, _ -> allScriptProjects() }
            execute<ProxyCommandSender> { sender, _, argument ->
                val file = scriptsFile.searchProject(argument)
                if (file?.exists() == true) {
                    submit(async = true) { reloadProject(file, sender) }
                } else {
                    sender.sendLang("command-project-not-found", argument)
                }
            }
            dynamic(commit = "args", optional = true) {
                execute<ProxyCommandSender> { sender, context, argument ->
                    val demand = Demand("0 $argument")
                    val file = scriptsFile.searchProject(context.argument(-1))
                    if (file?.exists() == true) {
                        submit(async = true) { reloadProject(file, sender, compile = demand.tags.contains("C")) }
                    } else {
                        sender.sendLang("command-project-not-found", argument)
                    }
                }
            }
        }
    }

    /**
     * 构建脚本工程
     */
    val build = subCommand {

    }

    @CommandBody
    val status = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            val projects = Artifex.api().getScriptProjectManager().getRunningProjects()
            if (projects.isEmpty()) {
                sender.sendLang("command-script-status-empty-project")
            } else {
                sender.sendLang("command-script-status-project")
                projects.sortedByDescending { it.isRunning() }.forEach { project ->
                    if (project.isRunning()) {
                        sender.sendLang("command-script-status-project-name-running", project.name(), project.file().name)
                    } else {
                        sender.sendLang("command-script-status-project-name", project.name(), project.file().name)
                    }
                }
            }
        }
    }
}