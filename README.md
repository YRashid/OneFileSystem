# OneFileSystem

#### Интерфейс:
1) Создать пустой файл:  
`void createFile(String fileName);`
2) Записать контент в существующий файл  
`void writeContent(String fileName, byte[] content);`
3) Прочитать контент.  
`byte[] readContent(String fileName);`
4) Удалить файл:  
`void deleteFile(String fileName);`

### Константы:
1) `MAX_FILES_COUNT` - максимальное количество файлов
2) `FILE_HEADER_SIZE` - размер заголовка файла
3) `MAX_FILE_SIZE` - максимальный размер одного файла (количество байт, которое резервируется под каждый файл)
4) `CONTENT_START_POSITION` - позиция в файле, с которой начинается контент файлов


#### Исключения:
1) `FileNotFoundException` - при вызове операции над несуществующим файлом.
2) `TooLargeFileException` - файл больше чем максимально разрешенный размер файла (Constants.MAX_FILE_SIZE)
3) `FileAlreadyExistsException` - при попытке создания уже существующего файла


#### Возможные улучшения:
1) Не ограничивать размер файлов.
2) Не резервировать лишнее место под каждый файл.
3) 