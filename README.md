Инструкция:

1. SQLite БД на стороне сервера (сервиса). Можно использовать мою БД, можно создать свою, команды для создания БД и таблицы привожу ниже:

создать БД gr_overlays в папке пользователя:
	sqlite3 gr_overlays.db

для того, чтобы загрузить изображение, положите картинку в папку пользователя или измените путь до него в пункции readfile():

	CREATE TABLE gr_overlays (rowid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, latLngBoundNEN REAL, latLngBoundNEE REAL, latLngBoundSWN REAL, latLngBoundSWE REAL, overlayPic BLOB);
	INSERT INTO gr_overlays (name, latLngBoundNEN, latLngBoundNEE, latLngBoundSWN, latLngBoundSWE, overlayPic) VALUES('наземное наложение 1', 40.712216, -74.22655, 40.773941, -74.12544, readfile('newark_nj_1922.jpg'));
	INSERT INTO gr_overlays (name, latLngBoundNEN, latLngBoundNEE, latLngBoundSWN, latLngBoundSWE, overlayPic) VALUES('наземное наложение 2', 40.712216, -74.22655, 40.773941, -74.12544, readfile('newark_nj_1922.jpg'));
	INSERT INTO gr_overlays (name, latLngBoundNEN, latLngBoundNEE, latLngBoundSWN, latLngBoundSWE, overlayPic) VALUES('наземное наложение 3', 40.712216, -74.22655, 40.773941, -74.12544, readfile('newark_nj_1922.jpg'));



2. Путь до БД с данными нужно прописать тут (42 строка):
https://github.com/nicenicenice/android_client_service/blob/master/OverlayService/src/my/android/service/OverlayService.java

То есть, вместо "jdbc:sqlite:/Users/user/gr_overlays.db", "jdbc:sqlite:*YOUR_OWN_PATH_TO_DB*";

3. Запускаем Tomcat для проекта OverlayService. 
Я это делаю из IntelliJ IDEA, просто щелкнув на кнопку запуск. 
После того, как задеплоится проект, json данные с сервиса можно будет получить по адресу:
http://10.0.2.2:8080/overlay_service/get_data - для эмулятора андроид

4. посля успешного деплоя и запуска Tomcat, в Android studio запускаем эмулятор android и запускаем проект:
Нажимаем на кнопку refresh, программа скачаем json, распарсит, создаст свою SQLite БД и запишет туда все полученные данные.

5. потом нажимаем на кнопку Data. 
Программа сделает выборку из внутренней БД и отобразит их в новой Активити.
