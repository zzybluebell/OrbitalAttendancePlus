-- phpMyAdmin SQL Dump
-- version 4.7.9
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: 2018-08-02 03:23:41
-- 服务器版本： 5.7.21
-- PHP Version: 5.6.35

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mas`
--
CREATE DATABASE IF NOT EXISTS `mas` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `mas`;

-- --------------------------------------------------------

--
-- 表的结构 `attendance`
--

DROP TABLE IF EXISTS `attendance`;
CREATE TABLE IF NOT EXISTS `attendance` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ModuleId` int(11) NOT NULL,
  `Date` timestamp NOT NULL,
  `MacAddress` char(17) NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=29 DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- 表的结构 `module`
--

DROP TABLE IF EXISTS `module`;
CREATE TABLE IF NOT EXISTS `module` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ModuleCode` varchar(10) NOT NULL,
  `ModuleName` varchar(100) NOT NULL,
  `Date` int(1) NOT NULL,
  `BeginTime` time NOT NULL,
  `EndTime` time NOT NULL,
  `TeacherIC` char(9) NOT NULL,
  `AdminNo` text NOT NULL,
  `SheetId` text NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `module`
--

INSERT INTO `module` (`Id`, `ModuleCode`, `ModuleName`, `Date`, `BeginTime`, `EndTime`, `TeacherIC`, `AdminNo`, `SheetId`) VALUES
(1, 'AB1234', 'OOAD', 1, '08:00:00', '10:00:00', 'G1552076R', '160015Q,163690G,160018Y,160033G', '1nnXKjAJ9glV8qKBqJs0FIJQg79fNXM6t-iy-yyKyZcE'),
(3, 'CD5678', 'WDD', 1, '10:30:00', '12:00:00', 'Q1234567W', '160015Q,163690G,160048Y', '111cYImSM38gHw5OU9MeIPT2-DBijQKLh_EtdiP0-Bqk'),
(4, 'QW6789', 'OSWSD', 1, '12:00:00', '14:00:00', 'G1552076R', '163690G,160048Y,160015Q', '19-eb5WryTopNBPlk33nGWpk3y77J0ueK8jNMBVexSEQ'),
(6, 'TY9988', 'STAD', 1, '14:00:00', '16:00:00', 'G1552076R', '163690G,160048Y,160033G', '1N3yV2tgyKRlIdnmIgBBIJN_9yp1foasVz31e3T0uY1c'),
(7, 'CV6755', 'WAD', 1, '16:00:00', '18:00:00', 'Q1234567W', '163690G,160033G,160015Q', '1ORhqj7lBUTZKYQuZoTscjwVB665mj2vG2mX8OsxHoQ4'),
(9, 'KJ2321', 'ISS', 2, '08:00:00', '10:00:00', 'Q1234567W', '163690G,160033G,160074H', '1zTW5vHF9oEKQmlQQuiwxVTHGeDaQTJxts3gUvot8REI'),
(10, 'RT5624', 'CM1', 2, '12:00:00', '14:00:00', 'Q1234567W', '160015Q,163690G,160048Y', '1p7_hPQsqW8P-oFkMaFMNN0yNkKCXIZgfD4AlZ_RqmhY'),
(11, 'IO1367', 'TPSS', 3, '10:00:00', '12:00:00', 'Q1234567W', '160015Q,163690G,160018Y,160033G', '16bQAYp_UWd2XPlBrv4G8BbR0dxm5r3NB90KmzjX4zpc'),
(12, 'YJ6343', 'DMD', 3, '14:00:00', '16:00:00', 'Q1234567W', '160048Y,160015Q,160097L', '1RJsjMGSn6KzxQZmIfex3xYOhSzFiOB2ebOss-JK2O-g'),
(13, 'NT2342', 'DF', 3, '16:00:00', '18:00:00', 'Q1234567W', '160015Q,163690G,160048Y', '1cK9zuoVoOU1kIgttjAPNMVvlJW63RPcK0VrYczawa2U'),
(14, 'FJ1684', 'UXDM', 4, '08:00:00', '10:00:00', 'Q1234567W', '163690G,160048Y,160015Q', '1dg5ehOyasuoeA6nBljV-3rnaD_Q0PfR1BTnFkXvy83g'),
(15, 'SK3576', 'SEP', 4, '12:00:00', '14:00:00', 'Q1234567W', '163690G,160033G,160074H', '1VKL5r46XQJ4HfLzPJLoRnh5biZSq1jeqvy98RE1p__c'),
(16, 'TR2352', 'CM2', 4, '16:00:00', '18:00:00', 'Q1234567W', '160015Q,163690G,160018Y,160033G', '1GBalh2LntWRi6inhSalmfSXbODzKX8BqvR14QfUySPg'),
(17, 'BS3223', 'CSE', 5, '10:00:00', '12:00:00', 'Q1234567W', '160048Y,160015Q,160097L', '1xbxYGKTKQ2_ROh7w2o0q0BMJ1cuELMsgEBEn-3QSNqE'),
(18, 'ER3734', 'OOP', 5, '14:00:00', '16:00:00', 'Q1234567W', '163690G,160033G,160015Q', '1A9e-_6UMrT6nIjx_Nklv4llDAJNgntPFCaAp9X1FSR4'),
(19, 'HW5242', 'NT', 2, '10:00:00', '12:00:00', 'G1552076R', '160015Q,163690G,160048Y', '1R3wEH6GCJfDcZUZstL0edn8kAQ-xfKuhl7L0HzR4zKY'),
(20, 'SA4343', 'PMP', 2, '14:00:00', '16:00:00', 'G1552076R', '160048Y,160015Q,160097L', '1heICVUvDttc5_owbejUNzXDMGfr9oVNhpWfHOZwwlCA'),
(21, 'FT8743', 'NS', 2, '16:00:00', '18:00:00', 'G1552076R', '163690G,160033G,160074H', '1nwzw-d1tM-MUuCkIaLuR7MKU-0ucpSB6VJK2DpdX4eM'),
(22, 'WE8673', 'CS2', 3, '08:00:00', '10:00:00', 'G1552076R', '160015Q,163690G,160048Y', '1raJr3VTTTSt3FhI2EcZEgTPZMRj60PwQtB1RpTUZMSw'),
(23, 'GH3576', 'MAD', 3, '12:00:00', '14:00:00', 'G1552076R', '163690G,160033G,160015Q', '1kBbcijni5FegLjDXN7Z2z8Ecx9HgO0SdTBqo0wWnago'),
(24, 'AM7363', 'CS', 4, '10:00:00', '12:00:00', 'G1552076R', '160048Y,160015Q,160097L', '1xJX3HFSwCn3x9zdMtUYQTwie7R14Y6GbhiIXiLHe5-M'),
(25, 'AQ2246', 'GAB', 4, '14:00:00', '16:00:00', 'G1552076R', '163690G,160033G,160074H', '13nPWVKCn65qs9jNgnUyqIEYHKeKJOCR6j_sTUwTUJpk'),
(26, 'WJ6944', 'DSA', 5, '08:00:00', '10:00:00', 'G1552076R', '160015Q,163690G,160018Y,160033G', '1KNHa0_auou3bdAX5LpmQ-vzwk6PUIM86eOMzPDW-SuQ'),
(27, 'TY5257', 'HOA', 5, '12:00:00', '14:00:00', 'G1552076R', '160015Q,163690G,160048Y', '1f9te9zHtZhtESHHQk3KMsQoSxHOfLR6rttdxQYYdayg'),
(28, 'SY6476', 'AC', 5, '16:00:00', '18:00:00', 'G1552076R', '163690G,160048Y,160033G', '1MZDhA9MdDS83OmzKZBxlKPed4wKSBGBUYN-Wy_A6XOc');

-- --------------------------------------------------------

--
-- 表的结构 `notification`
--

DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ModuleCode` varchar(10) NOT NULL,
  `TeacherIC` char(9) NOT NULL,
  `Content` text NOT NULL,
  `TimeStamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `notification`
--

INSERT INTO `notification` (`Id`, `ModuleCode`, `TeacherIC`, `Content`, `TimeStamp`) VALUES
(1, 'AB1234', 'G1552076R', 'hahahahaha', '2018-07-24 13:11:01'),
(2, 'YJ6343', 'Q1234567W', 'fadsfdsafsdfsadfsadf', '2018-08-01 12:04:17');

-- --------------------------------------------------------

--
-- 表的结构 `spreadsheet`
--

DROP TABLE IF EXISTS `spreadsheet`;
CREATE TABLE IF NOT EXISTS `spreadsheet` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Email` varchar(50) NOT NULL,
  `SpreadSheetId` text NOT NULL,
  `SpreadSheetName` varchar(50) NOT NULL,
  `TimeStamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `spreadsheet`
--

INSERT INTO `spreadsheet` (`Id`, `Email`, `SpreadSheetId`, `SpreadSheetName`, `TimeStamp`) VALUES
(1, 'lsjs945743958@gmail.com', '1kUbPCAPnetm-SqDk7sGvx0UqUSlkevbBwEC1YhqjZrU', 'hu jia jun', '2018-06-20 18:10:53'),
(3, 'lsjs945743958@gmail.com', '1V2w3nKS1b3OUM1hMudBrAHW_dmIqv3u77xsvDltCdJo', 'NIHAOMA', '2018-06-21 01:31:46');

-- --------------------------------------------------------

--
-- 表的结构 `student`
--

DROP TABLE IF EXISTS `student`;
CREATE TABLE IF NOT EXISTS `student` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `AdminNo` char(7) NOT NULL,
  `Name` varchar(50) DEFAULT NULL,
  `SCG` varchar(50) DEFAULT NULL,
  `Image` varchar(100) DEFAULT NULL,
  `FirebaseToken` text NOT NULL,
  `TimeStamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `student`
--

INSERT INTO `student` (`Id`, `AdminNo`, `Name`, `SCG`, `Image`, `FirebaseToken`, `TimeStamp`) VALUES
(10, '160015Q', 'hi', 'SEG ', 'originals/160015Q.jpg', 'wretaggdasgfhgehjysjdyje', '2018-08-01 10:59:07');

-- --------------------------------------------------------

--
-- 表的结构 `teacher`
--

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE IF NOT EXISTS `teacher` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `TeacherIC` char(9) NOT NULL,
  `TeacherName` varchar(50) NOT NULL,
  `Password` varchar(50) NOT NULL,
  `Phone` char(8) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `TeacherImage` text NOT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- 转存表中的数据 `teacher`
--

INSERT INTO `teacher` (`Id`, `TeacherIC`, `TeacherName`, `Password`, `Phone`, `Email`, `TeacherImage`) VALUES
(1, 'G1552076R', 'HU JIA JUN', '123456', '93799009', 'G1552076R@nyp.edu.sg', 'teacherImages/G1552076R.jpg'),
(2, 'Q1234567W', 'Angela Quek', '123456', '97128841', 'Q1234567W@nyp.edu.sg', 'teacherImages/Q1234567W.jpg');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
