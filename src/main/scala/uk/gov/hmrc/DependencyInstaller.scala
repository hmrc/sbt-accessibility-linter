/*
 * Copyright 2021 HM Revenue & Customs
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

  def apply(sourceDirectory: String, targetDirectory: File) = {
    val zf = new ZipFile(sourceDirectory)
    val zipEntries = zf.entries.asScala.toIndexedSeq
    zipEntries filter { (ze: ZipEntry) =>
      ze.getName.startsWith("javascripts") && !ze.getName.endsWith("/")
    } foreach  { (ze: ZipEntry) =>
      val fileName: String = ze.getName
      val source: Source = Source.fromInputStream(getClass.getResourceAsStream("/" + fileName))
      val outFile: File = targetDirectory / fileName
      IO.write(outFile, source.mkString)
    }
  }

}
