package org.example.javaFX

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class Main : Application() {
    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(javaClass.getResource("/view/list_view.fxml"))
        val root: Parent = loader.load()

        val scene = Scene(root, 1000.0, 700.0)

        primaryStage.apply {
            title = "DataStructLab - Kotlin"
            this.scene = scene
            minWidth = 800.0
            minHeight = 600.0
            show()
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}

