CREATE TABLE `howtomake` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `RECIPENAME` varchar(255) NOT NULL,
  `FILENAME2` varchar(255) NOT NULL,
  `HOWTOMAKE` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_recipe_howtomake` (`RECIPENAME`),
  CONSTRAINT `fk_recipe_howtomake` FOREIGN KEY (`RECIPENAME`) REFERENCES `recipemain` (`RECIPENAME`) ON DELETE CASCADE
)