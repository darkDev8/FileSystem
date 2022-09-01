package org.file.system

import org.apache.commons.io.FilenameUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors


enum class FileSystemDateTime {
    Date, Time, DateTime
}

enum class PermissionType {
    Owner, Group, Others, All
}

enum class ContentType {
    File, Folder, All
}

open class FileSystem(
    var path: String = System.getProperty("user.dir"), var safeFetch: Boolean = false,
    var pathNotFound: String = "Error"
) {

    companion object {
        @JvmStatic
        val WindowsExecutable = arrayOf("bat", "cmd", "exe", "msi")

        @JvmStatic
        val LinuxExecutable = arrayOf("sh", "run")

        @JvmStatic
        val MacExecutable = arrayOf("app", "dmg")

        @JvmStatic
        val audioFileExtensions = arrayOf(
            "mp3", "wav", "ogg", "mpa", "aac", "au", "m4a", "m4b", "mpc", "oga", "tta",
            "wma", "wv"
        )

        @JvmStatic
        val imageFileExtensions = arrayOf(
            "jpg", "jpeg", "jfif", "webp", "exif", "bmp", "png", "svg", "fig", "tiff",
            "gif"
        )

        @JvmStatic
        val videoFileExtensions = arrayOf(
            "mp4", "avi", "mkv", "flv", "3gp", "nsv", "webm", "vob", "gifv", "mov", "qt",
            "wmv", "viv", "amv", "m4p", "m4v", "mpg", "mp2", "mpv", "svi", "3g2", "f4v", "f4p", "f4a", "f4b"
        )

        @JvmStatic
        val textFileExtensions = arrayOf("txt", "xml", "fxml", "xmlns", "iml")

        @JvmStatic
        val documentFileExtensions = arrayOf(
            "doc", "html", "html", " docx", "odt", "pdf", "xml", "xmnl", "fxml",
            "json"
        )

        @JvmStatic
        val libraryFileExtensions = arrayOf("dll", "jar", "so", "pyd")

        @JvmStatic
        val archiveFileExtensions = arrayOf("zip", "rar", "7zip", "tar.gz", "gz", "rar4", "tar.xz")

        @JvmStatic
        val databaseFileExtensions = arrayOf("db", "sql3", "sql")

        @JvmStatic
        val fontFileExtensions = arrayOf("tf", "ttf")

        @JvmStatic
        val javaFileExtension = "java"

        @JvmStatic
        val csharpFileExtension = "cs"

        @JvmStatic
        val cFileExtension = "c"

        @JvmStatic
        val cppFileExtension = "cpp"

        @JvmStatic
        val pythonFileExtension = "py"

        @JvmStatic
        val javascriptFileExtension = "js"

        @JvmStatic
        val phpFileExtension = "php"

        @JvmStatic
        val goFileExtension = "go"

        @JvmStatic
        val rubyFileExtension = "rb"

        @JvmStatic
        val kotlinFileExtension = "kt"

        @JvmStatic
        val cHeaderFileExtension = "h"

        @JvmStatic
        val cppHeaderFileExtension = "hh"
    }

    fun getSize(): Long {
        if (safeFetch && !File(path).exists()) {
            return -1
        }

        if (File(path).isDirectory) {
            return File(path).walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        }

        return Files.size(Paths.get(path))
    }

    fun getModifyDateTime(fsDateTime: FileSystemDateTime): String {
        if (safeFetch && !File(path).exists()) {
            return pathNotFound
        }

        return when (fsDateTime) {
            FileSystemDateTime.Date ->
                formatAttributes(
                    "yyyy-MM-dd", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .lastModifiedTime()
                )
            FileSystemDateTime.Time ->
                formatAttributes(
                    "HH:mm:ss", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .lastModifiedTime()
                )

            FileSystemDateTime.DateTime ->
                formatAttributes(
                    "yyyy-MM-dd HH:mm:ss", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .lastModifiedTime()
                )
        }
    }

    fun getCreationDateTime(fsDateTime: FileSystemDateTime): String {
        if (safeFetch && !File(path).exists()) {
            return pathNotFound
        }

        return when (fsDateTime) {
            FileSystemDateTime.Date ->
                formatAttributes(
                    "yyyy-MM-dd", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .creationTime()
                )
            FileSystemDateTime.Time ->
                formatAttributes(
                    "HH:mm:ss", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .creationTime()
                )

            FileSystemDateTime.DateTime ->
                formatAttributes(
                    "yyyy-MM-dd HH:mm:ss", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .creationTime()
                )
        }
    }

    fun getAccessDateTime(fsDateTime: FileSystemDateTime): String {
        if (safeFetch && !File(path).exists()) {
            return pathNotFound
        }

        return when (fsDateTime) {
            FileSystemDateTime.Date ->
                formatAttributes(
                    "yyyy-MM-dd", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .lastAccessTime()
                )
            FileSystemDateTime.Time ->
                formatAttributes(
                    "HH:mm:ss", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .lastAccessTime()
                )

            FileSystemDateTime.DateTime ->
                formatAttributes(
                    "yyyy-MM-dd HH:mm:ss", Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
                        .lastAccessTime()
                )
        }
    }

    fun getOwner(): String {
        if (safeFetch && !File(path).exists()) {
            return pathNotFound
        }

        return Files.getOwner(Paths.get(path)).getName();
    }

    fun getType(): String {
        if (safeFetch && !File(path).exists()) {
            return pathNotFound
        }

        if (File(path).isDirectory) {
            return "Folder"
        }

        val array = arrayOf(
            WindowsExecutable,
            LinuxExecutable,
            MacExecutable,
            audioFileExtensions,
            imageFileExtensions,
            videoFileExtensions,
            textFileExtensions,
            documentFileExtensions,
            libraryFileExtensions,
            archiveFileExtensions,
            databaseFileExtensions,
            fontFileExtensions,
            javaFileExtension,
            csharpFileExtension,
            cFileExtension,
            cppFileExtension,
            pythonFileExtension,
            javascriptFileExtension,
            phpFileExtension,
            goFileExtension,
            rubyFileExtension,
            kotlinFileExtension,
            cHeaderFileExtension,
            cppHeaderFileExtension
        )

        val currentExtension = FilenameUtils.getExtension(path)
        var index: Int = -1

        for (i in 0 until array.size) {
            if (array[i] is Array<*>) {
                if ((array[i] as Array<*>).contains(currentExtension)) {
                    index = i
                    break
                }
            } else if (array[i] is String) {
                if ((array[i] as String) == currentExtension) {
                    index = i
                    break
                }
            }
        }

        return when (index) {
            0 -> "Windows executable file"
            1 -> "Linux executable file"
            2 -> "Mac executable file"
            3 -> "Audio file"
            4 -> "Image file"
            5 -> "Video file"
            6 -> "Text file"
            7 -> "Document file"
            8 -> "Library file"
            9 -> "Archive file"
            10 -> "Database file (script)"
            11 -> "Font file"
            12 -> "Java file"
            13 -> "Csharp file"
            14 -> "C file"
            15 -> "C++ file"
            16 -> "Python file"
            17 -> "Javascript file"
            18 -> "PHP file"
            19 -> "Go file"
            20 -> "Ruby file"
            21 -> "Kotlin file"
            22 -> "C header file"
            23 -> "C++ header file"
            else -> "Unknown file"
        }
    }

    fun getPermission(
        type: PermissionType = PermissionType.All,
        showException: Boolean = true,
        showInNumber: Boolean = true
    ): String {
        if (safeFetch && !File(path).exists()) {
            return pathNotFound
        }

        val processBuilder = ProcessBuilder()
        if (showInNumber) {
            processBuilder.command("/bin/bash", "-c", "stat -c \"%a\" $path")
        } else {
            processBuilder.command("/bin/bash", "-c", "stat -c \"%A\" $path")
        }

        try {
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val line: String = reader.readLine()

            process.waitFor()
            return if (showInNumber) when (type) {
                PermissionType.All -> line
                PermissionType.Owner -> line.get(0).toString()
                PermissionType.Group -> line.get(1).toString()
                PermissionType.Others -> line.get(2).toString()
            } else when (type) {
                PermissionType.All -> line
                PermissionType.Owner -> line.substring(1, 4)
                PermissionType.Group -> line.substring(4, 7)
                PermissionType.Others -> line.substring(7)
            }
        } catch (e: IOException) {
            if (showException) {
                e.printStackTrace()
            }
            return ""
        } catch (e: InterruptedException) {
            if (showException) {
                e.printStackTrace()
            }
            return ""
        }
    }

    fun countContent(type: ContentType = ContentType.All): Long {
        var count: Long = 0;

        if (safeFetch && !File(path).exists()) {
            return -1
        }

        if (!File(path).isDirectory) {
            return 1
        }

        when (type) {
            ContentType.All -> {
                for (pth in Files.newDirectoryStream(Paths.get(path))) {
                    if (Files.exists(pth)) {
                        count++
                    }
                }
            }

            ContentType.File -> {
                for (pth in Files.newDirectoryStream(Paths.get(path))) {
                    if (Files.exists(pth) && !Files.isDirectory(pth)) {
                        count++
                    }
                }
            }

            ContentType.Folder -> {
                for (pth in Files.newDirectoryStream(Paths.get(path))) {
                    if (Files.exists(pth) && Files.isDirectory(pth)) {
                        count++
                    }
                }
            }
        }

        return count
    }

    fun countSubContent(type: ContentType = ContentType.All): Long {
        if (safeFetch && !File(path).exists()) {
            return -1
        }

        if (!File(path).isDirectory) {
            return 1
        }

        return when (type) {
            ContentType.File ->
                Files.walk(Paths.get(path)).parallel().filter { p: Path ->
                    p.toFile().isFile
                }.count()

            ContentType.Folder -> (Files.walk(Paths.get(path)).parallel().filter { p: Path ->
                p.toFile().isDirectory
            }.count() - 1)

            ContentType.All ->
                Files.walk(Paths.get(path)).parallel().count() - 1
        }
    }

    fun getContent(type: ContentType = ContentType.All): MutableList<File> {
        if (safeFetch && !File(path).exists()) {
            return mutableListOf()
        }

        if (!File(path).isDirectory) {
            return mutableListOf()
        }

        val content: MutableList<File> = ArrayList()

        when (type) {
            ContentType.All -> {
                Files.list(Paths.get(path)).forEach { file: Path ->
                    if (Files.exists(file)) {
                        content.add(file.toFile())
                    }
                }
            }

            ContentType.Folder -> {
                Files.list(Paths.get(path)).forEach { file: Path ->
                    if (Files.exists(file) && Files.isDirectory(file)) {
                        content.add(file.toFile())
                    }
                }
            }

            ContentType.File -> {
                Files.list(Paths.get(path)).forEach { file: Path ->
                    if (Files.exists(file) && !Files.isDirectory(file)) {
                        content.add(file.toFile())
                    }
                }
            }
        }
        return content
    }

    fun getSubContent(type: ContentType = ContentType.All): MutableList<File> {
        if (safeFetch && !File(path).exists()) {
            return mutableListOf()
        }

        if (!File(path).isDirectory) {
            return mutableListOf()
        }

        return when (type) {
            ContentType.All ->
                Files.walk(Paths.get(path))
                    .map { obj: Path -> obj.toFile() }
                    .collect(Collectors.toList())

            ContentType.File ->

                Files.walk(Paths.get(path)).filter { path: Path -> !Files.isDirectory(path) }
                    .map { obj: Path -> obj.toFile() }
                    .collect(Collectors.toList())

            ContentType.Folder ->
                Files.walk(Paths.get(path)).filter { path: Path -> Files.isDirectory(path) }
                    .map { obj: Path -> obj.toFile() }
                    .collect(Collectors.toList())
        }
    }

    fun getInformation(): String {
        return "Path: $path\n" +
                "Size: ${getSize()}\n" +
                "Modify time: ${getModifyDateTime(FileSystemDateTime.DateTime)}\n" +
                "Creation time: ${getCreationDateTime(FileSystemDateTime.DateTime)}\n" +
                "Access time: ${getAccessDateTime(FileSystemDateTime.DateTime)}\n" +
                "Owner: ${getOwner()}\n" +
                "Permission: ${getPermission(PermissionType.All, showException = false, showInNumber = false)}" +
                " (${getPermission(PermissionType.All, showException = false, showInNumber = true)})\n" +
                "Type: ${getType()}\n"
    }

    private fun formatAttributes(pattern: String, time: FileTime): String {
        return SimpleDateFormat(pattern).format(Date(time.toMillis()))
    }
}