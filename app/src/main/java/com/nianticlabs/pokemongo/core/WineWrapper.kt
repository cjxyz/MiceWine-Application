package com.nianticlabs.pokemongo.core

import android.os.Build
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.wineDisksFolder
import com.nianticlabs.pokemongo.activities.MainActivity.Companion.winePrefix
import com.nianticlabs.pokemongo.core.EnvVars.getEnv
import com.nianticlabs.pokemongo.core.ShellLoader.runCommand
import com.nianticlabs.pokemongo.core.ShellLoader.runCommandWithOutput
import java.io.File

object WineWrapper {
    private var IS_BOX64 = if (Build.SUPPORTED_ABIS[0] == "x86_64") "" else "box64"

    fun wineServer(args: String) {
        runCommand(
            getEnv() + "$IS_BOX64 wineserver $args"
        )
    }

    fun waitFor(name: String) {
        while (!wine("tasklist", true).contains(name)) {
            Thread.sleep(100)
        }
    }

    fun wine(args: String) {
        runCommand(
            getEnv() + "WINEPREFIX=$winePrefix $IS_BOX64 wine $args"
        )
    }

    fun wine(args: String, retLog: Boolean): String {
        if (retLog) {
            return runCommandWithOutput(
                getEnv() + "BOX64_LOG=0 WINEPREFIX=$winePrefix $IS_BOX64 wine $args"
            )
        }
        return ""
    }

    fun wine(args: String, cwd: String) {
        runCommand(
            "cd $cwd;" +
                    getEnv() + "WINEPREFIX=$winePrefix $IS_BOX64 wine $args"
        )
    }

    fun clearDrives() {
        var letter = 'e'

        while (letter <= 'y') {
            val disk = File("$wineDisksFolder/$letter:")
            if (disk.exists()) {
                disk.delete()
            }
            letter++
        }
    }

    fun addDrive(path: String) {
        runCommand("ln -sf $path $wineDisksFolder/${getAvailableDisks()[0]}:")
    }

    fun getAvailableDisks(): List<String> {
        var letter = 'c'
        val availableDisks = mutableListOf<String>()

        while (letter <= 'z') {
            if (!File("$wineDisksFolder/$letter:").exists()) {
                availableDisks.add("$letter")
            }
            letter++
        }

        return availableDisks
    }

    fun extractIcon(exeFile: File, output: String) {
        if (exeFile.extension.lowercase() == "exe") {
            runCommand(
                getEnv() + "wrestool -x -t 14 '${exeFile.path}' > '$output'"
            )
        }
    }
}