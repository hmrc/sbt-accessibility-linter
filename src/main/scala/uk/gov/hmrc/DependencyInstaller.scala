/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc

import sbt._

import java.io.File
import java.util.zip.{ZipEntry, ZipFile}
import scala.collection.JavaConverters._
import scala.io.Source

object DependencyInstaller {

  /**
   * Extract a subset of resources found in a JAR file to a directory.
   *
   * @param jarPath the path to the JAR file
   * @param entryPrefix only extract JAR entries with the given prefix
   * @param targetDirectory the destination directory as a [[File]]
   */
  def apply(jarPath: String, entryPrefix: String, targetDirectory: File): Unit = {
    jarEntries(jarPath, entryPrefix) foreach  { (ze: ZipEntry) =>
      writeResource("/" + ze.getName, targetDirectory)
    }
  }

  private def jarEntries(jarPath: String, entryPrefix: String): Seq[ZipEntry] = {
    val zipFile = new ZipFile(jarPath)
    zipFile.entries.asScala.toIndexedSeq filter { (ze: ZipEntry) =>
      ze.getName.startsWith(entryPrefix) && !ze.isDirectory
    }
  }

  private def writeResource(path: String, targetDirectory: File): Unit = {
    val source: Source = Source.fromInputStream(getClass.getResourceAsStream(path))

    try {
      IO.write(targetDirectory / path, source.mkString)
    } finally {
      source.close()
    }
  }
}
