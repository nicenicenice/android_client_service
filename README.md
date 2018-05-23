Инструкция:

1. SQLite БД на стороне сервера (сервиса). Можно использовать мою БД, можно создать свою, команды для создания БД и таблицы привожу ниже:

создать БД gr_overlays в папке пользователя:
	sqlite3 gr_overlays.db

для того, чтобы загрузить изображение, положите картинку в папку пользователя или измените путь до него в пункции readfile():
	
	DROP TABLE overlay;
	CREATE TABLE overlay (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		id_warehouse INTEGER,
		latLngBoundNEN REAL, 
		latLngBoundNEE REAL, 
		latLngBoundSWN REAL, 
		latLngBoundSWE REAL, 
		overlayPic BLOB,
		FOREIGN KEY(id_warehouse) REFERENCES warehouse(id_warehouse)
	);
	INSERT INTO overlay (id_warehouse, latLngBoundNEN, latLngBoundNEE, latLngBoundSWN, latLngBoundSWE, overlayPic) VALUES(1, 40.712216, -74.22655, 40.773941, -74.12544, readfile('newark_nj_1922.jpg'));
	INSERT INTO overlay (id_warehouse, latLngBoundNEN, latLngBoundNEE, latLngBoundSWN, latLngBoundSWE, overlayPic) VALUES(2, 40.712216, -74.22655, 40.773941, -74.12544, readfile('newark_nj_1922.jpg'));
	INSERT INTO overlay (id_warehouse, latLngBoundNEN, latLngBoundNEE, latLngBoundSWN, latLngBoundSWE, overlayPic) VALUES(3, 54.148969, 36.649708, 54.210695, 36.750818, readfile('newark_nj_1922.jpg'));
	

	CREATE TABLE product (
		id_product INTEGER PRIMARY KEY AUTOINCREMENT, 
		product_name TEXT
	);
	INSERT INTO product (product_name) VALUES('гвозди');
	INSERT INTO product (product_name) VALUES('дерево');
	INSERT INTO product (product_name) VALUES('болты');
	INSERT INTO product (product_name) VALUES('шурупы');

	CREATE TABLE warehouse (
		id_warehouse INTEGER PRIMARY KEY AUTOINCREMENT, 
		warehouse_name TEXT
	);
	INSERT INTO warehouse (warehouse_name) VALUES(1);
	INSERT INTO warehouse (warehouse_name) VALUES(2);
	INSERT INTO warehouse (warehouse_name) VALUES(3);
	
	CREATE TABLE slot (
		id_slot INTEGER PRIMARY KEY AUTOINCREMENT, 
		slot_name TEXT, 
		id_product INTEGER, 
		FOREIGN KEY(id_product) REFERENCES product(id_product)
	);
	INSERT INTO slot (slot_name, id_product) VALUES('10.Я1', 1);
	INSERT INTO slot (slot_name, id_product) VALUES('10.Я2', 2);
	INSERT INTO slot (slot_name, id_product) VALUES('10.Я3', 2);
	INSERT INTO slot (slot_name, id_product) VALUES('15.Я1', 3);
	INSERT INTO slot (slot_name, id_product) VALUES('15.Я2', 3);
	INSERT INTO slot (slot_name, id_product) VALUES('20.Я1', 4);
	INSERT INTO slot (slot_name, id_product) VALUES('20.Я2', 4);

	CREATE TABLE warehouse_slot (
		id INTEGER PRIMARY KEY AUTOINCREMENT, 
		id_warehouse INTEGER, 
		id_slot INTEGER, 
		FOREIGN KEY(id_warehouse) REFERENCES warehouse(id_warehouse),
		FOREIGN KEY(id_slot) REFERENCES slot(id_slot)
	);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(1, 1);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(1, 2);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(1, 3);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(2, 4);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(2, 5);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(3, 6);
	INSERT INTO warehouse_slot (id_warehouse, id_slot) VALUES(3, 7);


	SELECT * FROM slot INNER JOIN product ON slot.id_product = product.id_product WHERE id_slot IN (1,2,3,4,5);
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
