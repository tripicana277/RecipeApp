CREATE TABLE `material` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `RECIPENAME` varchar(255) NOT NULL,
  `MATERIAL` varchar(255) NOT NULL,
  `QUANTITY` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `fk_recipe_material` (`RECIPENAME`),
  CONSTRAINT `fk_recipe_material` FOREIGN KEY (`RECIPENAME`) REFERENCES `recipemain` (`RECIPENAME`) ON DELETE CASCADE
)