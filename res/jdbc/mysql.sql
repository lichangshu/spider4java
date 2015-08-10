-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.5.5-10.0.11-MariaDB - mariadb.org binary distribution
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  8.0.0.4396
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 导出 jspider 的数据库结构
CREATE DATABASE IF NOT EXISTS `jspider` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `jspider`;


-- 导出  表 jspider.jspider_content 结构
DROP TABLE IF EXISTS `jspider_content`;
CREATE TABLE IF NOT EXISTS `jspider_content` (
  `id` int(11) NOT NULL DEFAULT '0',
  `content` mediumblob
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_cookie 结构
DROP TABLE IF EXISTS `jspider_cookie`;
CREATE TABLE IF NOT EXISTS `jspider_cookie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  `domain` varchar(255) NOT NULL DEFAULT '',
  `path` varchar(255) NOT NULL DEFAULT '',
  `expires` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_decision 结构
DROP TABLE IF EXISTS `jspider_decision`;
CREATE TABLE IF NOT EXISTS `jspider_decision` (
  `resource` int(11) NOT NULL DEFAULT '0',
  `subject` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `comment` longtext NOT NULL,
  PRIMARY KEY (`resource`,`subject`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_decision_step 结构
DROP TABLE IF EXISTS `jspider_decision_step`;
CREATE TABLE IF NOT EXISTS `jspider_decision_step` (
  `resource` int(11) NOT NULL DEFAULT '0',
  `subject` int(11) NOT NULL DEFAULT '0',
  `sequence` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `rule` longtext NOT NULL,
  `decision` int(11) NOT NULL DEFAULT '0',
  `comment` longtext NOT NULL,
  PRIMARY KEY (`resource`,`subject`,`sequence`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_email_address 结构
DROP TABLE IF EXISTS `jspider_email_address`;
CREATE TABLE IF NOT EXISTS `jspider_email_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_email_address_reference 结构
DROP TABLE IF EXISTS `jspider_email_address_reference`;
CREATE TABLE IF NOT EXISTS `jspider_email_address_reference` (
  `resource` int(11) NOT NULL DEFAULT '0',
  `address` int(11) NOT NULL DEFAULT '0',
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`resource`,`address`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_folder 结构
DROP TABLE IF EXISTS `jspider_folder`;
CREATE TABLE IF NOT EXISTS `jspider_folder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parent` int(11) NOT NULL DEFAULT '0',
  `site` int(11) NOT NULL DEFAULT '0',
  `name` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_resource 结构
DROP TABLE IF EXISTS `jspider_resource`;
CREATE TABLE IF NOT EXISTS `jspider_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` longtext NOT NULL,
  `header` longtext,
  `state` int(11) NOT NULL DEFAULT '0',
  `httpstatus` int(11) NOT NULL DEFAULT '0',
  `site` int(11) NOT NULL DEFAULT '0',
  `timems` int(11) NOT NULL DEFAULT '0',
  `mimetype` varchar(255) DEFAULT NULL,
  `size` int(11) NOT NULL DEFAULT '0',
  `folder` int(11) NOT NULL DEFAULT '0',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_resource_reference 结构
DROP TABLE IF EXISTS `jspider_resource_reference`;
CREATE TABLE IF NOT EXISTS `jspider_resource_reference` (
  `referer` int(11) NOT NULL DEFAULT '0',
  `referee` int(11) NOT NULL DEFAULT '0',
  `count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`referer`,`referee`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。


-- 导出  表 jspider.jspider_site 结构
DROP TABLE IF EXISTS `jspider_site`;
CREATE TABLE IF NOT EXISTS `jspider_site` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(255) NOT NULL DEFAULT '',
  `port` int(11) NOT NULL DEFAULT '80',
  `robotstxthandled` tinyint(4) NOT NULL DEFAULT '0',
  `usecookies` tinyint(4) NOT NULL DEFAULT '0',
  `useproxy` tinyint(4) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `obeyrobotstxt` int(11) NOT NULL DEFAULT '0',
  `fetchrobotstxt` int(11) NOT NULL DEFAULT '0',
  `basesite` int(11) NOT NULL DEFAULT '0',
  `useragent` varchar(255) NOT NULL DEFAULT '',
  `handle` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 数据导出被取消选择。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
