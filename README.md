# camelspringdemo
apache camel + spring boot + activemq + h2 db + mail

# Постановка задачи
Приложение читает файлы из папки средствами Apache Camel.
В зависимости от типа файла выполняются некоторые действия:
	- если файл имеет расширение txt -> отправлять файл в txtQueue Embedded ActiveMQ брокера;
	- если файл имеет расширение xml -> парсим файл с помощью jaxb и отправляем в xmlQueue Embedded ActiveMQ брокера;
	- если файл имеет любое другое расширение -> выбрасывать исключение и записывать файл в errorQueue Embedded ActiveMQ брокера;
	
Далее происходит вычитка очередей из Embedded ActiveMQ брокера:
	- txtQueue -> логируем тело сообщения, логи пишем в файл;
	- xmlQueue -> записываем тело сообщения в БД в таблицу body, заголовки логируем в таблицу headers.
	  Связь между таблицами осуществялется с помощью внешнего ключа;
	- errorQueue -> оставляем сопровождению ;)
	
При обработке каждого сотого файла отсылать письмо, содержащее количество файлов txt, количество файлов xml
и количество нераспознанных файлов;
Настройку, запуск, конфигурирование осуществлять при помощи средств Spring Framework.

# Пример ввода XML
```
<CATALOG>
	<CD>
		<TITLE>Empire Burlesque</TITLE>
		<ARTIST>Bob Dylan</ARTIST>
		<COUNTRY>USA</COUNTRY>
		<COMPANY>Columbia</COMPANY>
		<PRICE>10.90</PRICE>
		<YEAR>1985</YEAR>
	</CD>
	<CD>
		<TITLE>Hide your heart</TITLE>
		<ARTIST>Bonnie Tyler</ARTIST>
		<COUNTRY>UK</COUNTRY>
		<COMPANY>CBS Records</COMPANY>
		<PRICE>9.90</PRICE>
		<YEAR>1988</YEAR>
	</CD>
</CATALOG>
```
