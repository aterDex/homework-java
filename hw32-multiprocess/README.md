## hw32-multiprocess

### Структура проекта

Проект состоит из 3 модулей и двух разделяемых библиотек.

* **hw32-multiprocess/middle** - Центральный модуль, обеспечивает работу ядра MessageSystem.
* **hw32-multiprocess/front** - Модуль отвечает за Web интерфейс.
* **hw32-multiprocess/back** - Модуль отвечает за работу с базой данных.
* **hw32-multiprocess/common** - Библиотека содержит общие элементы.
* **hw31-messagingSystem/MessageSystem** - Библиотека ядра системы сообщений.

### Доступный протоколы

Реализованы следующие протоколы, которые можно использовать для связывания модулей:
* **tcp** - Профиль запуска **SignalTcp**, сервер NIO клиент IO. По умолчанию запускается на порту **15005**. Полезная нагрузка передается через стандартную сериализацию.
* **rmi** - Профиль запуска **RMI**, stub's используют любые не занятые порты, регистр поднимается один на порту **15000**. Полезная нагрузка передается через стандартную сериализацию.
* **gRPC** - Профиль запуска **gRPC**, запускается на порту **15010**. Полезная нагрузка передается через protobuf.

### База данных

База данных используется H2 mem, для развертывания нескольких back's предусмотрен
профиль **H2_DB_server**. Он запустит tcp сервер для текущей H2 mem на порту **9123** (server.db.port).

### Запуск примера

Модуль middle содержит тестовый пример, с 2 front и 2 back.

Самый простой вариант запуска (проверно на MacOS, Windows10 и DebianBuster) это команда:

> ./gradlew hw32-multiprocess:middle:run -Pexample

Она соберет все нужные зависимости, скопирует все исполняющие файлы в единую директорию 'build/lib'.
После чего выставит все нужные параметры (возьмет из gradle окружения) и запустит middle.

Прмер можно запустить вручную для этого в middle.jar нужно передать следующие параметры:

* **--start-example** без параметров, сообщает о том что нужно запустить тестовый пример.
* **--work-directory** директория где back.jar и front.jar, и в которую будут записаны файлы логов (перенаправлены outputStream процессов).
* **--java** путь к исполняющему файлу java.
* **--front** имя jar файла ответственного за front.
* **--back** имя jar файла ответственного за back.

В результате будет запущено 4 дополнительных процессов, со следующими параметрами:
* **back0** 
  * --spring.profiles.active=**gRPC**,H2_DB_server
* **front0**
  * --spring.profiles.active=**SignalTcp**
* **back1**
  * --message-system.frontend-service-client-name=frontend2
  * --message-system.database-service-client-name=back2
  * --spring.datasource.url=jdbc:h2:tcp://localhost:9123/mem:OtusExamplesDB
  * --spring.profiles.active=**SignalTcp**
* **front1**
  * --server.port=8081
  * --message-system.frontend-service-client-name=frontend2
  * --message-system.database-service-client-name=back2
  * --spring.profiles.active=**RMI**

Эти же настройки возможно использовать для запуска всех процессов по отдельности.

Для автоматического закрытия дополнительных процессов, после закрытия middle, используется исключение System.InputStream.read() у потомков.
