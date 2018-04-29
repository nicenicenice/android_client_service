Инструкция:

1. SQLite БД на стороне сервера (сервиса). Можно использовать мою БД, можно создать свою, команды для создания БД и таблицы привожу ниже:

создать БД test в папке пользователя:
	sqlite3 test.db

для того, чтобы загрузить изображение, положите картинку в папку пользователя или измените путь до него в пункции readfile():

	CREATE TABLE test (rowid INTEGER PRIMARY KEY AUTOINCREMENT, num1 REAL, num2 REAL, num3 REAL, num4 REAL, picture BLOB);
	INSERT INTO test (num1, num2, num3, num4, picture) VALUES(1.1, 1.2, 1.3, 1.4, readfile('download.png'));
	INSERT INTO test (num1, num2, num3, num4, picture) VALUES(2.1, 2.2, 2.2, 2.4, readfile('download.png'));
	INSERT INTO test (num1, num2, num3, num4, picture) VALUES(3.1, 3.2, 3.3, 3.4, readfile('download.png'));


2. Путь до БД с данными нужно прописать тут (42 строка):
https://github.com/nicenicenice/android_client_service/blob/master/service_for_android/src/test/assignment/vista/DoctorScheduler.java

То есть, вместо "jdbc:sqlite:/Users/user/test.db", "jdbc:sqlite:*YOUR_OWN_PATH_TO_DB*";

3. Запускаем Tomcat для проекта service_for_android. 
Я это делаю из IntelliJ IDEA, просто щелкнув на кнопку запуск. 
После того, как задеплоится проект, json данные с сервиса можно будет получить по адресу:
http://10.0.2.2:8080/doctors_schedule/get_data - для эмулятора андроид

4. посля успешного деплоя и запуска Tomcat, в Android studio запускаем эмулятор android и запускаем проект:
Нажимаем на кнопку refresh, программа скачаем json, распарсит, создаст свою SQLite БД и запишет туда все полученные данные.

5. потом нажимаем на кнопку Data. 
Программа сделает выборку из внутренней БД и отобразит их в новой Активити.
