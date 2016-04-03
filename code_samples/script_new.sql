create database fresher_training;

use fresher_training;

CREATE TABLE students (
     student_id INT NOT NULL AUTO_INCREMENT,
     fullname CHAR(30) NOT NULL,
     sex CHAR(1) NOT NULL,
     address varchar(100),
     PRIMARY KEY (student_id)
);

insert into students (fullname, sex, address) values ('Nguyen An', 'F', 'Viet Name');
insert into students (fullname, sex, address) values ('Nguyen Hung', 'M', 'Viet Name');

select * from students;

------ ONE - TO - ONE -----------
--
-- Definition of table `stock`
--

DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `STOCK_CODE` varchar(10) NOT NULL,
  `STOCK_NAME` varchar(20) NOT NULL,
  PRIMARY KEY (`STOCK_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_NAME` (`STOCK_NAME`),
  UNIQUE KEY `UNI_STOCK_ID` (`STOCK_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;


--
-- Definition of table `stock_detail`
--
DROP TABLE IF EXISTS `stock_detail`;
CREATE TABLE  `stock_detail` (
  `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `COMP_NAME` varchar(100) NOT NULL,
  `COMP_DESC` varchar(255) DEFAULT NULL,
  `REMARK` varchar(255) DEFAULT NULL,
  `LISTED_DATE` date NOT NULL,
  PRIMARY KEY (`STOCK_ID`) USING BTREE,
  CONSTRAINT `FK_STOCK_ID` FOREIGN KEY (`STOCK_ID`) REFERENCES `stock` (`STOCK_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8;


select * from stock;
select * from stock_detail;

--------- ONE -- TO -- MANY -------
DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `STOCK_CODE` varchar(10) NOT NULL,
  `STOCK_NAME` varchar(20) NOT NULL,
  PRIMARY KEY (`STOCK_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_NAME` (`STOCK_NAME`),
  UNIQUE KEY `UNI_STOCK_ID` (`STOCK_CODE`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `stock_daily_record`;
CREATE TABLE  `stock_daily_record` (
  `RECORD_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `PRICE_OPEN` float(6,2) DEFAULT NULL,
  `PRICE_CLOSE` float(6,2) DEFAULT NULL,
  `PRICE_CHANGE` float(6,2) DEFAULT NULL,
  `VOLUME` bigint(20) unsigned DEFAULT NULL,
  `DATE` date NOT NULL,
  `STOCK_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`RECORD_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_DAILY_DATE` (`DATE`),
  KEY `FK_STOCK_TRANSACTION_STOCK_ID` (`STOCK_ID`),
  CONSTRAINT `FK_STOCK_TRANSACTION_STOCK_ID` FOREIGN KEY (`STOCK_ID`) 
  REFERENCES `stock` (`STOCK_ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

select * from stock;
select * from stock_daily_record;

--------- MANY -- TO -- MANY ---------

DROP TABLE IF EXISTS `stock`;
CREATE TABLE `stock` (
  `STOCK_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `STOCK_CODE` varchar(10) NOT NULL,
  `STOCK_NAME` varchar(20) NOT NULL,
  PRIMARY KEY (`STOCK_ID`) USING BTREE,
  UNIQUE KEY `UNI_STOCK_NAME` (`STOCK_NAME`),
  UNIQUE KEY `UNI_STOCK_ID` (`STOCK_CODE`) USING BTREE
);

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `CATEGORY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(10) NOT NULL,
  `DESC` varchar(255) NOT NULL,
  PRIMARY KEY (`CATEGORY_ID`) USING BTREE
);

DROP TABLE IF EXISTS `stock_category`;
CREATE TABLE  `stock_category` (
  `STOCK_ID` int(10) unsigned NOT NULL,
  `CATEGORY_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`STOCK_ID`,`CATEGORY_ID`),
  CONSTRAINT `FK_CATEGORY_ID` FOREIGN KEY (`CATEGORY_ID`) REFERENCES `category` (`CATEGORY_ID`),
  CONSTRAINT `FK_STOCK_ID` FOREIGN KEY (`STOCK_ID`) REFERENCES `stock` (`STOCK_ID`)
);

select * from stock;
select * from category;
select * from stock_category;

----------- HIBERNATE DAO SUPPORT ---------'
CREATE TABLE `EMPLOYEE` (
  `EMPLOYEE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) NOT NULL,
  `CONTACT` varchar(100),
  PRIMARY KEY (`EMPLOYEE_ID`) USING BTREE
);
