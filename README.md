# ЛР6 Псевдо-анонимайзер

## Задание
Разработать приложение, использующее технологии zookeeper, акка и позволяющее «анонимизировать» запрос.

Администратор запускает несколько серверов. В параметре командной строки он указывает порт для каждого.

Каждый сервер регистрируется в zookeeper - создает дочерний узел для узла /servers в содержимом которого хранит свой порт, а также подписывается на изменения servers.

В случае изменения списка серверов он перечитывается и отправляется актору
хранилищу конфигурации.

Клиент отправляет запрос вида -
http://localhost:8080/?url=http://rambler.ru&count=20
