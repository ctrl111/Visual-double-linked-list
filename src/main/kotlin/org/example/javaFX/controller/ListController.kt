package org.example.javaFX.controller

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import org.example.impl.DoublyLinkedList
import org.example.impl.UserFactory
import org.example.interfaces.UserType
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ListController : Initializable {

    @FXML private lateinit var typeComboBox: ComboBox<String>
    @FXML private lateinit var valueTextField: TextField
    @FXML private lateinit var indexTextField: TextField
    @FXML private lateinit var listVisualization: HBox
    @FXML private lateinit var listTableView: TableView<Array<String>>
    @FXML private lateinit var indexColumn: TableColumn<Array<String>, String>
    @FXML private lateinit var valueColumn: TableColumn<Array<String>, String>
    @FXML private lateinit var typeColumn: TableColumn<Array<String>, String>
    @FXML private lateinit var statusTextArea: TextArea
    @FXML private lateinit var logTextArea: TextArea

    private lateinit var list: DoublyLinkedList
    private lateinit var factory: UserFactory
    private var currentType: UserType? = null
    private lateinit var tableData: ObservableList<Array<String>>
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        factory = UserFactory()
        list = DoublyLinkedList()
        tableData = FXCollections.observableArrayList()

        setupComboBox()
        setupTableView()
        updateStatus("Готов - выберите тип данных")
    }

    private fun setupComboBox() {
        typeComboBox.items = FXCollections.observableArrayList(factory.getTypeNameList())
        typeComboBox.selectionModel.selectedItemProperty().addListener { _, _, newVal ->
            if (newVal != null) {
                currentType = factory.getBuilderByName(newVal)
                list = DoublyLinkedList() // Создать новый пустой список
                updateStatus("Выбран тип данных: $newVal")
                updateListDisplay()
                log("Переключен на тип данных: $newVal")
            }
        }

        // Выбрать первый тип по умолчанию
        if (typeComboBox.items.isNotEmpty()) {
            typeComboBox.selectionModel.select(0)
        }
    }

    private fun setupTableView() {
        indexColumn.setCellValueFactory { cellData ->
            javafx.beans.property.SimpleStringProperty(cellData.value[0])
        }
        valueColumn.setCellValueFactory { cellData ->
            javafx.beans.property.SimpleStringProperty(cellData.value[1])
        }
        typeColumn.setCellValueFactory { cellData ->
            javafx.beans.property.SimpleStringProperty(cellData.value[2])
        }

        listTableView.items = tableData
    }

    @FXML
    private fun onAddToEnd() {
        if (currentType == null) {
            showAlert("Ошибка", "Сначала выберите тип данных")
            return
        }

        val value = valueTextField.text.trim()
        if (value.isEmpty()) {
            showAlert("Ошибка", "Введите значение элемента")
            return
        }

        try {
            val element = currentType!!.parseValue(value)
            list.add(element)
            updateListDisplay()
            log("Добавлено в конец: $value")
            valueTextField.clear()
        } catch (e: Exception) {
            showAlert("Ошибка", "Неверное значение элемента: ${e.message}")
        }
    }

    @FXML
    private fun onInsertAtIndex() {
        if (currentType == null) {
            showAlert("Ошибка", "Сначала выберите тип данных")
            return
        }

        val value = valueTextField.text.trim()
        val indexStr = indexTextField.text.trim()

        if (value.isEmpty() || indexStr.isEmpty()) {
            showAlert("Ошибка", "Введите значение элемента и индекс")
            return
        }

        try {
            val index = indexStr.toInt()
            val element = currentType!!.parseValue(value)
            list.insert(index, element)
            updateListDisplay()
            log("Вставлено по индексу $index: $value")
            valueTextField.clear()
            indexTextField.clear()
        } catch (e: NumberFormatException) {
            showAlert("Ошибка", "Неверный формат индекса")
        } catch (e: IndexOutOfBoundsException) {
            showAlert("Ошибка", "Индекс вне диапазона: ${e.message}")
        } catch (e: Exception) {
            showAlert("Ошибка", "Ошибка операции: ${e.message}")
        }
    }

    @FXML
    private fun onSearchElement() {
        if (currentType == null) {
            showAlert("Ошибка", "Сначала выберите тип данных")
            return
        }
        val value = valueTextField.text.trim()
        if (value.isEmpty()) {
            showAlert("Ошибка", "Введите значение элемента для поиска")
            return
        }
        val element = currentType!!.parseValue(value)

        val found = list.firstThat { obj ->
            currentType!!.getTypeComparator().compare(obj, element) == 0
        }

        if (found != null) {
            updateStatus("Найден элемент: $found")
            log("Найден элемент: $found")
        } else {
            updateStatus("Элемент не найден")
            log("Элемент не найден")
        }
    }

    @FXML
    private fun onRemoveElement() {
        val indexStr = indexTextField.text.trim()
        if (indexStr.isEmpty()) {
            showAlert("Ошибка", "Введите индекс для удаления")
            return
        }

        try {
            val index = indexStr.toInt()
            val removed = list.get(index)
            list.remove(index)
            updateListDisplay()
            log("Удален индекс $index: $removed")
            indexTextField.clear()
        } catch (e: NumberFormatException) {
            showAlert("Ошибка", "Неверный формат индекса")
        } catch (e: IndexOutOfBoundsException) {
            showAlert("Ошибка", "Индекс вне диапазона: ${e.message}")
        }
    }

    @FXML
    private fun onSortList() {
        if (currentType == null) {
            showAlert("Ошибка", "Сначала выберите тип данных")
            return
        }

        try {
            list.sort(currentType!!.getTypeComparator())
            updateListDisplay()
            log("Список отсортирован")
        } catch (e: Exception) {
            showAlert("Ошибка", "Ошибка сортировки: ${e.message}")
        }
    }

    @FXML
    private fun onClearList() {
        list = DoublyLinkedList()
        updateListDisplay()
        log("Список очищен")
    }

    @FXML
    private fun onExportToJson() {
        val fileChooser = FileChooser()
        fileChooser.title = "Экспорт в JSON"
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("JSON файлы", "*.json")
        )

        val file = fileChooser.showSaveDialog(null)
        if (file != null) {
            try {
                PrintWriter(file).use { writer ->
                    writer.println("{")
                    writer.println("  \"dataType\": \"${currentType?.typeName() ?: "Unknown"}\",")
                    writer.println("  \"elements\": [")

                    val elements = ArrayList<String>()
                    list.forEach { obj -> elements.add("    \"$obj\"") }
                    writer.println(elements.joinToString(",\n"))

                    writer.println("  ],")
                    writer.println("  \"count\": ${list.size()}")
                    writer.println("}")
                }

                log("Данные экспортированы в JSON: ${file.name}")
            } catch (e: IOException) {
                showAlert("Ошибка", "Ошибка экспорта файла: ${e.message}")
            }
        }
    }

    @FXML
    private fun onImportFromJson() {
        if (currentType == null) {
            showAlert("Ошибка", "Сначала выберите тип данных")
            return
        }

        val fileChooser = FileChooser()
        fileChooser.title = "Импорт из JSON"
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("JSON файлы", "*.json")
        )

        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            try {
                BufferedReader(FileReader(file)).use { reader ->
                    val jsonContent = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        jsonContent.append(line)
                    }

                    // Парсинг JSON
                    val success = parseJsonAndLoadData(jsonContent.toString())
                    if (success) {
                        updateListDisplay()
                        log("Данные импортированы из JSON: ${file.name} (${list.size()} элементов)")
                    } else {
                        showAlert("Ошибка", "Неверный формат JSON файла или несовпадение типов данных")
                    }
                }
            } catch (e: IOException) {
                showAlert("Ошибка", "Ошибка чтения JSON файла: ${e.message}")
            }
        }
    }

    @FXML
    private fun onExportToBinary() {
        if (currentType == null) {
            showAlert("Ошибка", "Сначала выберите тип данных")
            return
        }

        val fileChooser = FileChooser()
        fileChooser.title = "Экспорт в бинарный файл"
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("Бинарные файлы", "*.bin")
        )

        val file = fileChooser.showSaveDialog(null)
        if (file != null) {
            try {
                DataOutputStream(FileOutputStream(file)).use { dos ->
                    val dataType = currentType!!.typeName()
                    val dataTypeBytes = dataType.toByteArray(Charsets.UTF_8)

                    dos.writeInt(dataTypeBytes.size)
                    dos.write(dataTypeBytes)
                    dos.writeInt(list.size())

                    for (i in 0 until list.size()) {
                        val element = list.get(i)
                        val elementStr = element.toString()
                        val elementBytes = elementStr.toByteArray(Charsets.UTF_8)

                        dos.writeInt(elementBytes.size)
                        dos.write(elementBytes)
                    }
                }

                log("Данные экспортированы в бинарный файл: ${file.name}")
            } catch (e: IOException) {
                showAlert("Ошибка", "Ошибка экспорта в бинарный файл: ${e.message}")
            }
        }
    }

    @FXML
    private fun onImportFromBinary() {
        val fileChooser = FileChooser()
        fileChooser.title = "Импорт из бинарного файла"
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("Бинарные файлы", "*.bin")
        )

        val file = fileChooser.showOpenDialog(null)
        if (file != null) {
            try {
                DataInputStream(FileInputStream(file)).use { dis ->
                    // Чтение заголовка
                    val dataTypeLength = dis.readInt()
                    val dataTypeBytes = ByteArray(dataTypeLength)
                    dis.readFully(dataTypeBytes)
                    val dataType = String(dataTypeBytes, Charsets.UTF_8)

                    // Установка типа данных
                    typeComboBox.selectionModel.select(dataType)
                    currentType = factory.getBuilderByName(dataType)

                    // Чтение количества элементов
                    val elementCount = dis.readInt()
                    list = DoublyLinkedList()

                    // Чтение элементов
                    for (i in 0 until elementCount) {
                        val elementLength = dis.readInt()
                        val elementBytes = ByteArray(elementLength)
                        dis.readFully(elementBytes)
                        val elementStr = String(elementBytes, Charsets.UTF_8)

                        val element = currentType!!.parseValue(elementStr)
                        list.add(element)
                    }

                    updateListDisplay()
                    log("Данные импортированы из бинарного файла: ${file.name} (${list.size()} элементов)")
                }
            } catch (e: EOFException) {
                showAlert("Ошибка", "Неожиданный конец файла")
            } catch (e: IOException) {
                showAlert("Ошибка", "Ошибка чтения бинарного файла: ${e.message}")
            } catch (e: Exception) {
                showAlert("Ошибка", "Ошибка формата бинарного файла: ${e.message}")
            }
        }
    }

    private fun parseJsonAndLoadData(jsonContent: String): Boolean {
        return try {
            val content = jsonContent.trim()

            if (!content.startsWith("{") || !content.endsWith("}")) {
                return false
            }

            val dataTypeStart = content.indexOf("\"dataType\"")
            if (dataTypeStart == -1) return false

            val colonIndex = content.indexOf(":", dataTypeStart)
            val quoteStart = content.indexOf("\"", colonIndex)
            val quoteEnd = content.indexOf("\"", quoteStart + 1)

            val jsonDataType = content.substring(quoteStart + 1, quoteEnd)
            typeComboBox.selectionModel.select(jsonDataType)
            currentType = factory.getBuilderByName(jsonDataType)
            val currentDataType = currentType!!.typeName()

            // Проверка соответствия типов данных
            if (jsonDataType != currentDataType) {
                showAlert(
                    "Несовпадение типов данных",
                    "Тип данных в JSON файле: $jsonDataType\n" +
                            "Выбранный тип данных: $currentDataType\n" +
                            "Выберите правильный тип данных перед импортом"
                )
                return false
            }

            // Извлечение массива элементов
            val elementsStart = content.indexOf("\"elements\"")
            if (elementsStart == -1) return false

            val arrayStart = content.indexOf("[", elementsStart)
            val arrayEnd = content.indexOf("]", arrayStart)

            if (arrayStart == -1 || arrayEnd == -1) return false

            val elementsArray = content.substring(arrayStart + 1, arrayEnd).trim()

            list = DoublyLinkedList() // Очистить текущий список

            if (elementsArray.isNotEmpty()) {
                // Разделение элементов
                val elements = parseJsonArray(elementsArray)

                for (element in elements) {
                    var el = element.trim()
                    // Удаление кавычек
                    if (el.startsWith("\"") && el.endsWith("\"")) {
                        el = el.substring(1, el.length - 1)
                    }
                    if (el.isNotEmpty()) {
                        val obj = currentType!!.parseValue(el)
                        list.add(obj)
                    }
                }
            }

            true
        } catch (e: Exception) {
            System.err.println("Ошибка парсинга JSON: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun parseJsonArray(arrayContent: String): List<String> {
        val elements = ArrayList<String>()
        val currentElement = StringBuilder()
        var bracketCount = 0
        var inQuotes = false

        for (c in arrayContent) {
            when {
                c == '\"' -> inQuotes = !inQuotes
                c == '(' && !inQuotes -> bracketCount++
                c == ')' && !inQuotes -> bracketCount--
            }

            if (c == ',' && !inQuotes && bracketCount == 0) {
                // Найдена запятая на верхнем уровне - разделитель элементов
                elements.add(currentElement.toString().trim())
                currentElement.clear()
            } else {
                currentElement.append(c)
            }
        }

        // Добавить последний элемент
        if (currentElement.isNotEmpty()) {
            elements.add(currentElement.toString().trim())
        }

        return elements
    }

    private fun updateListDisplay() {
        tableData.clear()
        for (i in 0 until list.size()) {
            val element = list.get(i)
            tableData.add(
                arrayOf(
                    i.toString(),
                    element.toString(),
                    currentType?.typeName() ?: "Unknown"
                )
            )
        }

        updateVisualization()
        updateStatus("Размер списка: ${list.size()} элементов")
    }

    private fun updateVisualization() {
        listVisualization.children.clear()

        if (list.size() == 0) {
            val emptyText = Text("Список пуст")
            listVisualization.children.add(emptyText)
            return
        }

        for (i in 0 until list.size()) {
            val element = list.get(i)

            // Контейнер узла
            val nodeBox = VBox(3.0).apply {
                style = "-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 5; " +
                        "-fx-background-color: #E8F5E8; -fx-padding: 8; -fx-alignment: center;"
            }

            // Метка индекса
            val indexLabel = Label("[$i]")

            // Метка значения
            val valueLabel = Label(element.toString()).apply {
                style = "-fx-font-weight: bold;"
            }

            nodeBox.children.addAll(indexLabel, valueLabel)
            listVisualization.children.add(nodeBox)

            if (i < list.size() - 1) {
                val arrowRight = Label("→").apply {
                    style = "-fx-padding: 0 5;"
                }
                val arrowLeft = Label("←").apply {
                    style = "-fx-padding: 0 5;"
                }

                // Создать контейнер для вертикального расположения стрелок
                val arrowsContainer = VBox(2.0).apply {
                    alignment = Pos.CENTER
                    children.addAll(arrowRight, arrowLeft)
                }

                listVisualization.children.add(arrowsContainer)
            }
        }
    }

    private fun updateStatus(message: String) {
        statusTextArea.text = message
    }

    private fun log(message: String) {
        val timestamp = dateFormat.format(Date())
        logTextArea.appendText("[$timestamp] $message\n")
        logTextArea.scrollTop = Double.MAX_VALUE
    }

    private fun showAlert(title: String, message: String) {
        val alert = Alert(Alert.AlertType.ERROR).apply {
            this.title = title
            headerText = null
            contentText = message
        }
        alert.showAndWait()
        log("Ошибка: $message")
    }
}

