package org.bread_experts_group.extensible_image_archiver

import java.nio.file.Path

interface Driver {
    val url: String

    fun run(outputDirectory: Path)
}