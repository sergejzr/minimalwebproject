-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jun 15, 2021 at 12:50 PM
-- Server version: 8.0.25-0ubuntu0.20.04.1
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mtgame`
--

-- --------------------------------------------------------

--
-- Table structure for table `tiq_application_property`
--

CREATE TABLE `tiq_application_property` (
  `key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tiq_image_log_suffixes`
--

CREATE TABLE `tiq_image_log_suffixes` (
  `id` int NOT NULL,
  `prefix` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tiq_image_log_suffixes`
--

INSERT INTO `tiq_image_log_suffixes` (`id`, `prefix`) VALUES
(1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tiq_user_log_p1`
--

CREATE TABLE `tiq_user_log_p1` (
  `id` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tiq_user_log_suffixes`
--

CREATE TABLE `tiq_user_log_suffixes` (
  `id` int NOT NULL,
  `prefix` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `tiq_user_log_suffixes`
--

INSERT INTO `tiq_user_log_suffixes` (`id`, `prefix`) VALUES
(1, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tiq_image_log_suffixes`
--
ALTER TABLE `tiq_image_log_suffixes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tiq_user_log_p1`
--
ALTER TABLE `tiq_user_log_p1`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tiq_user_log_suffixes`
--
ALTER TABLE `tiq_user_log_suffixes`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tiq_image_log_suffixes`
--
ALTER TABLE `tiq_image_log_suffixes`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `tiq_user_log_p1`
--
ALTER TABLE `tiq_user_log_p1`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tiq_user_log_suffixes`
--
ALTER TABLE `tiq_user_log_suffixes`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
