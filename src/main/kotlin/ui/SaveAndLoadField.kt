package ui

import model.DataField
import model.getDataFieldHash
import ui.Settings.DEFAULT_CACHE_DIRECTORY
import ui.Settings.DEFAULT_SAVES_DIRECTORY
import ui.Settings.DEFAULT_SAVE_FILE_PATH
import ui.Settings.separator
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object Settings {
    const val DEFAULT_SAVES_DIRECTORY = "saves"
    private const val DEFAULT_SAVE_FILE_NAME = "default_save.txt"
    const val DEFAULT_CACHE_DIRECTORY = "cache"

    val separator : String = File.separator

    val DEFAULT_SAVE_FILE_PATH : String = "${DEFAULT_SAVES_DIRECTORY}$separator${DEFAULT_SAVE_FILE_NAME}"
}

fun saveFieldToDefaultFile(field: Field) {
    Files.createDirectories(Paths.get(".$separator${DEFAULT_SAVES_DIRECTORY}"))

    val file = File(DEFAULT_SAVE_FILE_PATH)
    if (!file.exists()) file.createNewFile()

    field.saveToFile(file.absolutePath)
}

fun checkDefaultSaveFileExists() : Boolean {
    val file = File(DEFAULT_SAVE_FILE_PATH)
    return file.exists()
}
fun loadFieldFromDefaultPath(field: Field) {
    if (!checkDefaultSaveFileExists()) throw Exception("Error: try to load default file, but it is not exists")
    field.loadFromFile(File(DEFAULT_SAVE_FILE_PATH).absolutePath)
}

fun createFolderForCache() {
    Files.createDirectories(Paths.get(".$separator$DEFAULT_CACHE_DIRECTORY"))
}

fun getPathForDataFieldCache(dataField: DataField) = DEFAULT_CACHE_DIRECTORY +
        "$separator${getDataFieldHash(dataField)}"
fun cacheDataField(dataField: DataField) : Boolean {
    createFolderForCache()

    val file = File(getPathForDataFieldCache(dataField))
    if (file.exists()) return false
    file.createNewFile()
    return false
}

fun checkDataFieldWasBefore(dataField: DataField) : Boolean {
    createFolderForCache()

    return File(getPathForDataFieldCache(dataField)).exists()
}

fun deleteDirectory(file: File) {
    if (file.isDirectory) {
        val contents = file.listFiles()
        if (contents!= null) {
            for (f in contents) {
                deleteDirectory(f)
            }
        }
    }
    file.delete()
}
fun clearCache() {
    deleteDirectory(File(DEFAULT_CACHE_DIRECTORY))
}