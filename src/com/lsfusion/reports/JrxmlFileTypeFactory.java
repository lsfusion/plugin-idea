package com.lsfusion.reports;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class JrxmlFileTypeFactory extends FileTypeFactory {
  public void createFileTypes(@NotNull final FileTypeConsumer consumer) {
    consumer.consume(XmlFileType.INSTANCE, "jrxml");
  }
}
