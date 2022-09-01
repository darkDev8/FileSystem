package org.file.system

fun main() {
    val fs = FileSystem("/bin", true)
    println("path: ${fs.path}")
    println("size: ${fs.getSize()} bytes")
    println("modify date: ${fs.getModifyDateTime(FileSystemDateTime.DateTime)}")
    println("create date: ${fs.getCreationDateTime(FileSystemDateTime.DateTime)}")
    println("access date: ${fs.getAccessDateTime(FileSystemDateTime.DateTime)}")
    println("owner: ${fs.getOwner()}")
    println("permission: ${fs.getPermission(PermissionType.All, showInNumber = false)} " +
            "(${fs.getPermission(showInNumber = true)})")
    println("type: ${fs.getType()}")
    println("content: ${fs.countContent()}")
    println("sub content: ${fs.countSubContent()}")

    fs.getContent(ContentType.File).forEach(System.out::println)
    println(fs.getContent().size)

    fs.getSubContent(ContentType.File).forEach(System.out::println)
    println(fs.getSubContent().size)
    println(fs.getInformation())

}