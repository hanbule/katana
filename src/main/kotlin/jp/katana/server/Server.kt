package jp.katana.server

import jp.katana.core.IServer
import jp.katana.core.IServerProperties
import jp.katana.core.ServerState
import jp.katana.i18n.I18n
import jp.katana.server.console.KatanaConsole
import org.apache.logging.log4j.LogManager
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File


class Server : IServer {
    companion object {
        init {
            System.setProperty("log4j.skipJansi", "false")
            System.setProperty("log4j.configurationFile", "log4j2-katana.xml")

            Thread.currentThread().name = I18n["katana.server.hostThread"]
        }
    }

    override val propertiesFile: File = File("properties.yml")
    override var serverProperties: IServerProperties? = null
        private set
    override var state: ServerState = ServerState.Stopped
        private set
    override val logger = LogManager.getLogger(Server::class.java)!!
    override val console = KatanaConsole(this)

    override var totalTick: Long = 0
        private set

    private val consoleThread = Thread { startConsole() }
    private val mainThread = Thread { startMainThread() }

    override fun start() {
        state = ServerState.Running
        consoleThread.name = I18n["katana.server.consoleThread"]
        consoleThread.start()

        logger.info(I18n["katana.server.starting"])

        try {
            loadServerProperties()
        } catch (e: Exception) {
            logger.error(e)
            return
        }

        mainThread.start()
    }

    override fun shutdown(): Boolean {
        state = ServerState.Stopping
        logger.info(I18n["katana.server.stopping"])

        try {
            saveServerProperties()
        } catch (e: Exception) {
            logger.error(e)
            return shutdownForce()
        }

        state = ServerState.Stopped
        logger.info(I18n["katana.server.stop"])
        return true
    }

    override fun shutdownForce(): Boolean {
        state = ServerState.Stopped
        return true
    }

    override fun update(tick: Long): Boolean {
        try {
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun startConsole() {
        try {
            console.start()
        } catch (e: Exception) {
            logger.error(e)
            shutdownForce()
        }
    }

    private fun loadServerProperties() {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(options)
        if (propertiesFile.exists()) {
            serverProperties = yaml.loadAs(propertiesFile.reader(), ServerProperties::class.java)
            logger.info(I18n["katana.server.file.load", propertiesFile.name])
        } else {
            serverProperties = ServerProperties()
            propertiesFile.createNewFile()
            yaml.dump(serverProperties, propertiesFile.writer())
            logger.info(I18n["katana.server.file.create", propertiesFile.name])
        }
    }

    private fun saveServerProperties() {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        val yaml = Yaml(options)
        yaml.dump(serverProperties, propertiesFile.writer())
        logger.info(I18n["katana.server.file.save", propertiesFile.name])
    }

    private fun startMainThread() {
        Thread.currentThread().name = I18n["katana.server.mainThread"]

        val tickRate = 20
        var now: Long
        var old: Long
        var diff = 0L
        var sleep: Long
        val rate = (1000 shl 16) / tickRate
        var sleepReal: Long
        while (state == ServerState.Running) {
            old = System.currentTimeMillis() shl 16

            update(totalTick++)

            now = System.currentTimeMillis() shl 16
            sleep = rate - (now - old) - diff
            if (sleep < 0x20000) sleep = 0x20000
            old = now
            try {
                sleepReal = sleep shr 16
                Thread.sleep(sleepReal)
                if (sleepReal <= 2)
                    logger.warn(I18n["katana.server.tickDelay"])
            } catch (e: Exception) {
                logger.warn(e)
            }
            now = System.currentTimeMillis() shl 16
            diff = now - old - sleep
        }
    }
}